package com.ewomen.greenfuture.controller;

import com.ewomen.greenfuture.entity.Notification;
import com.ewomen.greenfuture.entity.User;
import com.ewomen.greenfuture.service.NotificationService;
import com.ewomen.greenfuture.service.UserService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(
            NotificationService notificationService,
            UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('can_view_dashboard')")
    @GetMapping
    public List<Notification> getMyNotifications(Authentication authentication) {

        String email = authentication.getName();

        User user = userService.findByEmail(email);

        return notificationService.getUserNotifications(user);
    }
}