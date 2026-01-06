package com.devision.job_manager_notification.listener;

import com.devision.job_manager_notification.dto.request.CreateNotificationRequest;
import com.devision.job_manager_notification.enums.NotificationType;
import com.devision.job_manager_notification.event.CompanyAccountLockedEvent;
import com.devision.job_manager_notification.event.CompanyActivatedEvent;
import com.devision.job_manager_notification.event.CompanyRegisteredEvent;
import com.devision.job_manager_notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyEventListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "company.registered",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleCompanyRegistered(CompanyRegisteredEvent event) {
        try {
            log.info("Received CompanyRegisteredEvent for company: {}", event.getCompanyId());

            CreateNotificationRequest notification = CreateNotificationRequest.builder()
                    .userId(event.getCompanyId())
                    .type(NotificationType.ACCOUNT)
                    .title("Welcome to Job Manager!")
                    .message("Your account has been created successfully. Please check your email to activate your account.")
                    .referenceId(event.getCompanyId().toString())
                    .referenceType("COMPANY_REGISTRATION")
                    .build();

            notificationService.createNotification(notification);
            log.info("Notification created for company registration: {}", event.getCompanyId());
        } catch (Exception e) {
            log.error("Error processing CompanyRegisteredEvent for company: {}", event.getCompanyId(), e);
        }
    }

    @KafkaListener(
            topics = "company.activated",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleCompanyActivated(CompanyActivatedEvent event) {
        try {
            log.info("Received CompanyActivatedEvent for company: {}", event.getCompanyId());

            CreateNotificationRequest notification = CreateNotificationRequest.builder()
                    .userId(event.getCompanyId())
                    .type(NotificationType.ACCOUNT)
                    .title("Account Activated Successfully")
                    .message("Your account has been activated! You can now start using all features of Job Manager.")
                    .referenceId(event.getCompanyId().toString())
                    .referenceType("COMPANY_ACTIVATION")
                    .build();

            notificationService.createNotification(notification);
            log.info("Notification created for company activation: {}", event.getCompanyId());
        } catch (Exception e) {
            log.error("Error processing CompanyActivatedEvent for company: {}", event.getCompanyId(), e);
        }
    }

    @KafkaListener(
            topics = "company.account.locked",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleCompanyAccountLocked(CompanyAccountLockedEvent event) {
        try {
            log.info("Received CompanyAccountLockedEvent for company: {}", event.getCompanyId());

            CreateNotificationRequest notification = CreateNotificationRequest.builder()
                    .userId(event.getCompanyId())
                    .type(NotificationType.ALERT)
                    .title("Account Locked")
                    .message("Your account has been locked due to: " + event.getReason() + ". Please contact support for assistance.")
                    .referenceId(event.getCompanyId().toString())
                    .referenceType("COMPANY_ACCOUNT_LOCKED")
                    .build();

            notificationService.createNotification(notification);
            log.info("Notification created for company account locked: {}", event.getCompanyId());
        } catch (Exception e) {
            log.error("Error processing CompanyAccountLockedEvent for company: {}", event.getCompanyId(), e);
        }
    }
}
