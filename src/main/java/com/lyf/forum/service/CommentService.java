package com.lyf.forum.service;

import com.lyf.forum.dto.CommentDTO;
import com.lyf.forum.enums.CommentTypeEnum;
import com.lyf.forum.enums.NotificationStatusEnum;
import com.lyf.forum.enums.NotificationTypeEnum;
import com.lyf.forum.exception.CustomizeErrorCode;
import com.lyf.forum.exception.CustomizeException;
import com.lyf.forum.mapper.*;
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
    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 保存评论
     *
     * @param comment
     * @param commentator
     */
    @Transactional
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) { // 回复评论
            // 查询原评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            // 根据原评论的 parentId 查询原问题
            Question dbQuestion = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if (dbQuestion == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
//            保存评论
            commentMapper.insert(comment);

            Notification notification = new Notification();
            notification.setNotifier(comment.getCommentator());
            notification.setNotifierName(commentator.getName());
            notification.setReceiver(dbComment.getCommentator());
            notification.setOuterId(dbComment.getParentId());
            notification.setOuterTitle(dbQuestion.getTitle());
            notification.setType(NotificationTypeEnum.REPLY_COMMENT.getType());
            notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
            notification.setGmtCreate(System.currentTimeMillis());
            notificationMapper.insert(notification);
        } else {  // 回复问题
            // 查询原问题
            Question dbQuestion = questionMapper.selectByPrimaryKey(comment.getParentId());
            // 判断原问题是否存在
            if (dbQuestion == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            // 保存评论
            commentMapper.insert(comment);
//            增加评论数
            questionExtMapper.incCommentCount(comment.getParentId());

            // 创建通知
            Notification notification = new Notification();
            notification.setNotifier(comment.getCommentator());
            notification.setReceiver(dbQuestion.getCreator());
            notification.setOuterId(dbQuestion.getId());
            notification.setOuterTitle(dbQuestion.getTitle());
            notification.setNotifierName(commentator.getName());
            notification.setType(NotificationTypeEnum.REPLY_QUESTION.getType());
            notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
            notification.setGmtCreate(System.currentTimeMillis());
            notificationMapper.insert(notification);
        }
    }

    /**
     * 获取评论列表
     *
     * @param parentId
     * @param commentTypeEnum
     * @return
     */
    public List<CommentDTO> listByTargetId(Long parentId, CommentTypeEnum commentTypeEnum) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(parentId)
                .andTypeEqualTo(commentTypeEnum.getType());
        // 按创建时间倒序
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        // 获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        // 获取评论人并转换为Map
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        // 转换 comment 为 commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
