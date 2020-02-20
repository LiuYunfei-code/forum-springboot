package com.lyf.forum.controller;

import com.lyf.forum.dto.PaginationDTO;
import com.lyf.forum.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {


    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(@RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "size",defaultValue = "16")Integer size,
            HttpServletRequest request, Model model) {

        PaginationDTO paginationDTO=questionService.list(page,size);
        model.addAttribute("pagination", paginationDTO);

        return "index";
    }
}
