package com.lyf.forum.controller;

import com.lyf.forum.dto.PaginationDTO;
import com.lyf.forum.dto.QuestionDTO;
import com.lyf.forum.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class IndexController {
    final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(@RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "size", defaultValue = "16") Integer size, Model model) {

        PaginationDTO<QuestionDTO> paginationDTO = questionService.list(page, size);
        model.addAttribute("pagination", paginationDTO);
        logger.info("running");


        return "index";
    }
}
