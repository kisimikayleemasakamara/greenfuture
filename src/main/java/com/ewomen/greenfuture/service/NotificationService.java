package com.ewomen.greenfuture.service;

import com.ewomen.greenfuture.dto.NotificationMessage;
import com.ewomen.greenfuture.entity.Notification;
import com.ewomen.greenfuture.entity.User;
import com.ewomen.greenfuture.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(
            NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Notification createNotification(
            String title,
            String message,
            User user) {

        Notification notification = new Notification();

        notification.setTitle(title);
        notification.setMessage(message);
        notification.setUser(user);

        Notification savedNotification = notificationRepository.save(notification);

        messagingTemplate.convertAndSend(
                "/topic/notifications",
                new NotificationMessage(
                        savedNotification.getTitle(),
                        savedNotification.getMessage()));

        return savedNotification;
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUser(user);
    }
}