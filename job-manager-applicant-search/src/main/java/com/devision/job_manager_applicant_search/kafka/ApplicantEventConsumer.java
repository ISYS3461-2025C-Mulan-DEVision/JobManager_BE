package com.devision.job_manager_applicant_search.kafka;

import com.devision.job_manager_applicant_search.event.ApplicantProfileUpdatedEvent;
import com.devision.job_manager_applicant_search.service.impl.MatchingServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for applicant profile update events.
 * When an applicant updates their profile, this consumer triggers the matching algorithm
 * to find search profiles that match and sends notifications to companies.
 * 
 * TODO – Applicant Data Dependency:
 * The exact structure of the event payload is owned by the Job Applicant team.
 * This consumer must be updated if the event schema changes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicantEventConsumer {

    private final MatchingServiceImpl matchingServiceImpl;

    /**
     * Consumes applicant profile updated events and triggers matching.
     * 
     * @param event the applicant profile updated event
     */
    @KafkaListener(
            topics = "applicant.profile.updated",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onApplicantProfileUpdated(ApplicantProfileUpdatedEvent event) {
        log.info("Received applicant profile updated event: {}", event.getApplicantId());
        
        try {
            matchingServiceImpl.processApplicantUpdate(event);
            log.info("Successfully processed applicant update: {}", event.getApplicantId());
        } catch (Exception e) {
            log.error("Error processing applicant update for {}: {}", 
                    event.getApplicantId(), e.getMessage(), e);
            // In production, consider dead-letter queue or retry mechanism
        }
    }
}
