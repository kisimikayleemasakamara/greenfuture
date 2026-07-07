package com.ewomen.greenfuture.repository;

import com.ewomen.greenfuture.entity.Notification;
import com.ewomen.greenfuture.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUser(User user);
}
