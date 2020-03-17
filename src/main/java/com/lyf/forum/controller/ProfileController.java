package com.lyf.forum.controller;

import com.lyf.forum.dto.NotificationDTO;
import com.lyf.forum.dto.PaginationDTO;
import com.lyf.forum.dto.QuestionDTO;
import com.lyf.forum.model.User;
import com.lyf.forum.service.NotificationService;
import com.lyf.forum.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable("action") String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(value = "page", defaultValue = "1") Integer page,
                          @RequestParam(value = "size", defaultValue = "10") Integer size) {
        User user = (User) request.getSession().getAttribute("user");

        if ("questions".equals(action)) {  // 我的问题

            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            // 获取我的问题分页信息
            PaginationDTO<QuestionDTO> paginationDTO=questionService.list(user.getId(), page, size);
            model.addAttribute("pagination",paginationDTO);
        } else if ("replies".equals(action)) { // 回复通知

            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "我的通知");
            // 获取我的回复分页信息
            PaginationDTO<NotificationDTO> paginationDTO=notificationService.list(user.getId(),page,size);
            Long unreadCount=notificationService.unreadCount(user.getId());
            model.addAttribute("pagination",paginationDTO);
            model.addAttribute("unreadCount",unreadCount);
        }
        return "profile";

    }
}
