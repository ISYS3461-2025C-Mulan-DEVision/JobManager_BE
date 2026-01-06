package com.devision.job_manager_notification.service;

import com.devision.job_manager_notification.dto.request.CreateNotificationRequest;
import com.devision.job_manager_notification.dto.response.ApiResponse;
import com.devision.job_manager_notification.dto.response.NotificationResponse;
import com.devision.job_manager_notification.dto.response.NotificationSummaryResponse;
import com.devision.job_manager_notification.enums.NotificationStatus;
import com.devision.job_manager_notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    ApiResponse<NotificationResponse> createNotification(CreateNotificationRequest request);

    ApiResponse<NotificationResponse> getNotificationById(UUID notificationId);

    ApiResponse<Page<NotificationResponse>> getUserNotifications(UUID userId, Pageable pageable);

    ApiResponse<Page<NotificationResponse>> getUserNotificationsByStatus(UUID userId, NotificationStatus status, Pageable pageable);

    ApiResponse<Page<NotificationResponse>> getUserNotificationsByType(UUID userId, NotificationType type, Pageable pageable);

    ApiResponse<List<NotificationResponse>> getAllUserNotifications(UUID userId);

    ApiResponse<NotificationResponse> markAsRead(UUID notificationId);

    ApiResponse<String> markAllAsRead(UUID userId);

    ApiResponse<String> deleteNotification(UUID notificationId);

    ApiResponse<String> deleteAllUserNotifications(UUID userId);

    ApiResponse<NotificationSummaryResponse> getUserNotificationSummary(UUID userId);

    ApiResponse<String> cleanupOldNotifications(int daysOld);
}
