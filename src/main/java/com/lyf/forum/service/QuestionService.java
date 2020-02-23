package com.lyf.forum.service;

import com.lyf.forum.dto.PaginationDTO;
import com.lyf.forum.dto.QuestionDTO;
import com.lyf.forum.exception.CustomizeErrorCode;
import com.lyf.forum.exception.CustomizeException;
import com.lyf.forum.mapper.QuestionExtMapper;
import com.lyf.forum.mapper.QuestionMapper;
import com.lyf.forum.mapper.UserMapper;
import com.lyf.forum.model.Question;
import com.lyf.forum.model.QuestionExample;
import com.lyf.forum.model.User;
import com.lyf.forum.model.UserExample;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = (int)questionMapper.countByExample(new QuestionExample());
        // 数据库中没有 question 直接返回
        if (totalCount<=0)
            return paginationDTO;

        paginationDTO.setPagination(totalCount, page, size);

        if (page < 1) {
            page = 1;
        } else if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }


        Integer offset = size * (page - 1);

        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(new QuestionExample(),new RowBounds(offset,size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questionList) {
            UserExample userExample=new UserExample();
            userExample.createCriteria().andIdEqualTo(question.getCreator());
            List<User> user=userMapper.selectByExample(userExample);
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user.get(0));
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        QuestionExample questionExample=new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);
        if (totalCount==0)
            return paginationDTO;
        paginationDTO.setPagination(totalCount, page, size);

        if (page < 1) {
            page = 1;
        } else if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        Integer offset = size * (page - 1);

        List<Question> questionList = questionMapper.selectByExampleWithRowbounds(new QuestionExample(),new RowBounds(offset,size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questionList) {
            UserExample userExample=new UserExample();
            userExample.createCriteria().andIdEqualTo(question.getCreator());
            List<User> user=userMapper.selectByExample(userExample);
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user.get(0));
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);
        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question question=questionMapper.selectByPrimaryKey(id);
        if (question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO=new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        UserExample userExample=new UserExample();
        userExample.createCriteria().andIdEqualTo(question.getCreator());
        List<User> user=userMapper.selectByExample(userExample);
        questionDTO.setUser(user.get(0));
        return questionDTO;
    }

    public void createOrUpdate(Question question) {

        if (question.getId()==null){
            // 创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(System.currentTimeMillis());
            question.setCommentCount(0);
            question.setLikeCount(0);
            question.setViewCount(0);
            questionMapper.insert(question);
        }else {
            // 更新
            question.setGmtModified(System.currentTimeMillis());
            QuestionExample questionExample=new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());
            Integer updated=questionMapper.updateByExampleSelective(question,questionExample);
            if (updated!=1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Integer id){
        questionExtMapper.incView(id);
    }
}
