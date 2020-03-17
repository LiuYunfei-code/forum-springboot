package com.lyf.forum.controller;

import com.lyf.forum.model.Notification;
import com.lyf.forum.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @RequestMapping("read")
    public String read(@RequestParam("id")Long id){
        Notification notification=notificationService.read(id);
        return "redirect:/question/"+notification.getOuterId();
    }
}
