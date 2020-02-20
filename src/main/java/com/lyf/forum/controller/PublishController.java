package com.lyf.forum.controller;

import com.lyf.forum.mapper.QuestionMapper;
import com.lyf.forum.model.Question;
import com.lyf.forum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    private QuestionMapper questionMapper;
    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }
    @PostMapping("/publish")
    public String doPublish(@RequestParam("title")String title,
                            @RequestParam("description") String description,
                            @RequestParam("tag")String tag,
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
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(System.currentTimeMillis());
        question.setCreator(user.getId());
        question.setTag(tag);
        questionMapper.create(question);

        return "index";



    }
}
