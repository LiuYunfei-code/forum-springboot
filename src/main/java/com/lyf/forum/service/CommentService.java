package com.lyf.forum.service;

import com.lyf.forum.dto.CommentDTO;
import com.lyf.forum.enums.CommentTypeEnum;
import com.lyf.forum.exception.CustomizeErrorCode;
import com.lyf.forum.exception.CustomizeException;
import com.lyf.forum.mapper.CommentMapper;
import com.lyf.forum.mapper.QuestionExtMapper;
import com.lyf.forum.mapper.QuestionMapper;
import com.lyf.forum.mapper.UserMapper;
import com.lyf.forum.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;
    @Transactional
    public void insert(Comment comment){
        if (comment.getParentId()==null || comment.getParentId()==0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType()==CommentTypeEnum.COMMENT.getType()){
            // 回复评论
            Comment dbComment=commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment==null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
        }else {
            // 回复问题
            Question dbQuestion=questionMapper.selectByPrimaryKey(comment.getParentId());
            if (dbQuestion==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            questionExtMapper.incCommentCount(comment.getParentId());
        }
    }

    /**
     * 获取评论列表
     * @param parentId
     * @param commentTypeEnum
     * @return
     */
    public List<CommentDTO> listByTargetId(Long parentId, CommentTypeEnum commentTypeEnum) {
        CommentExample commentExample=new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(parentId)
                .andTypeEqualTo(commentTypeEnum.getType());
        // 按创建时间倒序
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments=commentMapper.selectByExample(commentExample);
        if (comments.size() == 0){
            return new ArrayList<>();
        }
        // 获取去重的评论人
        Set<Long> commentators=comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds=new ArrayList<>();
        userIds.addAll(commentators);

        // 获取评论人并转换为Map
        UserExample userExample=new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users=userMapper.selectByExample(userExample);
        Map<Long,User> userMap=users.stream().collect(Collectors.toMap(user -> user.getId(),user -> user));

        // 转换 comment 为 commentDTO
        List<CommentDTO> commentDTOS=comments.stream().map(comment -> {
            CommentDTO commentDTO=new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;


    }
}