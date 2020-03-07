package com.lyf.forum.controller;

import com.lyf.forum.dto.CommentDTO;
import com.lyf.forum.dto.QuestionDTO;
import com.lyf.forum.enums.CommentTypeEnum;
import com.lyf.forum.service.CommentService;
import com.lyf.forum.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;
    @GetMapping("/question/{id}")
    public String question(@PathVariable("id")Long id, Model model){
        QuestionDTO questionDTO=questionService.getById(id);
        // 根据标签查询相关问题
        List<QuestionDTO> relatedQuestions=questionService.selectRelated(questionDTO);
        model.addAttribute("relatedQuestions",relatedQuestions);
        model.addAttribute("question",questionDTO);
        // 增加阅读数
        questionService.incView(id);

        List<CommentDTO> commentDTOS=commentService.listByTargetId(id, CommentTypeEnum.QUESTION);
        model.addAttribute("comments",commentDTOS);
        return "question";
    }
}
