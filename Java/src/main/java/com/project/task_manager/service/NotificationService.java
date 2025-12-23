package com.project.task_manager.service;

import com.project.task_manager.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getNotificationsForUser(Long userId);

    List<NotificationResponse> getUnreadNotificationsForUser(Long userId);

    void markAsRead(Long notificationId);

    Long countUnreadNotifications(Long userId);
}
