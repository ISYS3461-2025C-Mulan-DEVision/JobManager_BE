package com.devision.job_manager_notification.controller;

import com.devision.job_manager_notification.dto.request.CreateNotificationRequest;
import com.devision.job_manager_notification.dto.response.ApiResponse;
import com.devision.job_manager_notification.dto.response.NotificationResponse;
import com.devision.job_manager_notification.dto.response.NotificationSummaryResponse;
import com.devision.job_manager_notification.enums.NotificationStatus;
import com.devision.job_manager_notification.enums.NotificationType;
import com.devision.job_manager_notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {
        log.info("Creating notification for user: {}, type: {}", request.getUserId(), request.getType());
        ApiResponse<NotificationResponse> response = notificationService.createNotification(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(
            @PathVariable UUID notificationId) {
        log.info("Retrieving notification: {}", notificationId);
        ApiResponse<NotificationResponse> response = notificationService.getNotificationById(notificationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUserNotifications(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        log.info("Retrieving notifications for user: {}", userId);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        ApiResponse<Page<NotificationResponse>> response = notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAllUserNotifications(
            @PathVariable UUID userId) {
        log.info("Retrieving all notifications for user: {}", userId);
        ApiResponse<List<NotificationResponse>> response = notificationService.getAllUserNotifications(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUserNotificationsByStatus(
            @PathVariable UUID userId,
            @PathVariable NotificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Retrieving {} notifications for user: {}", status, userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ApiResponse<Page<NotificationResponse>> response = notificationService.getUserNotificationsByStatus(userId, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUserNotificationsByType(
            @PathVariable UUID userId,
            @PathVariable NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Retrieving {} type notifications for user: {}", type, userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        ApiResponse<Page<NotificationResponse>> response = notificationService.getUserNotificationsByType(userId, type, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<ApiResponse<NotificationSummaryResponse>> getUserNotificationSummary(
            @PathVariable UUID userId) {
        log.info("Retrieving notification summary for user: {}", userId);
        ApiResponse<NotificationSummaryResponse> response = notificationService.getUserNotificationSummary(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable UUID notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        ApiResponse<NotificationResponse> response = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(
            @PathVariable UUID userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        ApiResponse<String> response = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(
            @PathVariable UUID notificationId) {
        log.info("Deleting notification: {}", notificationId);
        ApiResponse<String> response = notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteAllUserNotifications(
            @PathVariable UUID userId) {
        log.info("Deleting all notifications for user: {}", userId);
        ApiResponse<String> response = notificationService.deleteAllUserNotifications(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<ApiResponse<String>> cleanupOldNotifications(
            @RequestParam(defaultValue = "30") int daysOld) {
        log.info("Cleaning up notifications older than {} days", daysOld);
        ApiResponse<String> response = notificationService.cleanupOldNotifications(daysOld);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Service is running");
    }
}
