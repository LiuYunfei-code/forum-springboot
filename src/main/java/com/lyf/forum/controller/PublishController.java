package com.lyf.forum.controller;

import com.lyf.forum.dto.QuestionDTO;
import com.lyf.forum.mapper.QuestionMapper;
import com.lyf.forum.model.Question;
import com.lyf.forum.model.User;
import com.lyf.forum.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionService questionService;
    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    @GetMapping("/publish/{id}")
    public String edit(@PathVariable("id")Integer id,Model model){
        QuestionDTO question=questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        return "publish";

    }
    @PostMapping("/publish")
    public String doPublish(@RequestParam("title")String title,
                            @RequestParam("description") String description,
                            @RequestParam("tag")String tag,
                            @RequestParam(value = "id",required = false)Integer id,
                            HttpServletRequest request,
                            Model model){


        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);

        User user = (User) request.getSession().getAttribute("user");
        if (user==null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }
        if (title.equals("")){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if (description.equals("")){
            model.addAttribute("error","问题补充不能为空");
            return "publish";
        }
        if (tag.equals("")){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }
        Question question=new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setCreator(user.getId());
        question.setTag(tag);
        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";



    }
}
