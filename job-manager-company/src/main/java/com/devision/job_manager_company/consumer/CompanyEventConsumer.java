package com.devision.job_manager_company.consumer;

import com.devision.job_manager_company.event.CompanyRegisteredEvent;
import com.devision.job_manager_company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyEventConsumer {

    private final CompanyService companyService;

    @KafkaListener(
            topics = "company.registered",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleCompanyRegistered(CompanyRegisteredEvent event) {
        log.info("Received CompanyRegisteredEvent for company ID: {}", event.getCompanyId());
        try {
            companyService.createCompanyFromEvent(event);
            log.info("Successfully processed CompanyRegisteredEvent for company ID: {}", event.getCompanyId());
        } catch (Exception e) {
            log.error("Failed to process CompanyRegisteredEvent for company ID: {}", event.getCompanyId(), e);
            throw e; // Rethrow to trigger Kafka retry/DLQ
        }
    }
}
