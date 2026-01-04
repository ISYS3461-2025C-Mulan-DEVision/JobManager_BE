package com.devision.job_manager_subscription.service.impl;

import com.devision.job_manager_subscription.dto.internal.request.CreateSubscriptionRequest;
import com.devision.job_manager_subscription.dto.internal.response.SubscriptionResponse;
import com.devision.job_manager_subscription.dto.internal.request.UpdateSubscriptionRequest;
import com.devision.job_manager_subscription.event.SubscriptionUpdatedEvent;
import com.devision.job_manager_subscription.exception.SubscriptionAlreadyExistsException;
import com.devision.job_manager_subscription.exception.SubscriptionNotFoundException;
import com.devision.job_manager_subscription.kafka.SubscriptionEventProducer;
import com.devision.job_manager_subscription.model.CompanySubscription;
import com.devision.job_manager_subscription.model.SubscriptionStatus;
import com.devision.job_manager_subscription.repository.CompanySubscriptionRepository;
import com.devision.job_manager_subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final CompanySubscriptionRepository subscriptionRepository;
    private final SubscriptionEventProducer eventProducer;

    /**
     * Checks if a company currently has premium access.
     * This is the main method used by other services to validate premium status.
     *
     * @param companyId the company UUID
     * @return true if the company has an active, non-expired subscription
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isPremium(UUID companyId) {
        return subscriptionRepository.findByCompanyId(companyId)
                .map(CompanySubscription::isPremium)
                .orElse(false);
    }

    /**
     * Gets the subscription for a company by company ID.
     *
     * @param companyId the company UUID
     * @return the subscription response
     * @throws SubscriptionNotFoundException if no subscription exists
     */
    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getByCompanyId(UUID companyId) {
        CompanySubscription subscription = subscriptionRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found for company: " + companyId));
        return SubscriptionResponse.fromEntity(subscription);
    }

    /**
     * Gets a subscription by its ID.
     *
     * @param id the subscription UUID
     * @return the subscription response
     * @throws SubscriptionNotFoundException if subscription not found
     */
    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getById(UUID id) {
        CompanySubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found: " + id));
        return SubscriptionResponse.fromEntity(subscription);
    }

    /**
     * Gets all subscriptions (for admin purposes).
     *
     * @return list of all subscriptions
     */
    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getAll() {
        return subscriptionRepository.findAll().stream()
                .map(SubscriptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new subscription for a company.
     *
     * @param request the create request
     * @return the created subscription response
     * @throws SubscriptionAlreadyExistsException if company already has a subscription
     */
    @Override
    public SubscriptionResponse create(CreateSubscriptionRequest request) {
        // Check if company already has a subscription
        if (subscriptionRepository.findByCompanyId(request.getCompanyId()).isPresent()) {
            throw new SubscriptionAlreadyExistsException(
                    "Subscription already exists for company: " + request.getCompanyId());
        }

        LocalDateTime startAt = request.getStartAt() != null 
                ? request.getStartAt() 
                : LocalDateTime.now();

        CompanySubscription subscription = CompanySubscription.builder()
                .companyId(request.getCompanyId())
                .status(request.getStatus() != null ? request.getStatus() : SubscriptionStatus.ACTIVE)
                .startAt(startAt)
                .endAt(request.getEndAt())
                .build();

        subscription = subscriptionRepository.save(subscription);
        log.info("Created subscription for company: {}", request.getCompanyId());

        // Publish event for other services
        publishSubscriptionEvent(subscription);

        return SubscriptionResponse.fromEntity(subscription);
    }

    /**
     * Updates an existing subscription.
     *
     * @param id the subscription UUID
     * @param request the update request
     * @return the updated subscription response
     * @throws SubscriptionNotFoundException if subscription not found
     */
    @Override
    public SubscriptionResponse update(UUID id, UpdateSubscriptionRequest request) {
        CompanySubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found: " + id));

        if (request.getStatus() != null) {
            subscription.setStatus(request.getStatus());
        }
        if (request.getStartAt() != null) {
            subscription.setStartAt(request.getStartAt());
        }
        if (request.getEndAt() != null) {
            subscription.setEndAt(request.getEndAt());
        }

        subscription = subscriptionRepository.save(subscription);
        log.info("Updated subscription: {}", id);

        // Publish event for other services
        publishSubscriptionEvent(subscription);

        return SubscriptionResponse.fromEntity(subscription);
    }

    /**
     * Activates a subscription.
     *
     * @param id the subscription UUID
     * @return the updated subscription response
     */
    @Override
    public SubscriptionResponse activate(UUID id) {
        return updateStatus(id, SubscriptionStatus.ACTIVE);
    }

    /**
     * Deactivates a subscription.
     *
     * @param id the subscription UUID
     * @return the updated subscription response
     */
    @Override
    public SubscriptionResponse deactivate(UUID id) {
        return updateStatus(id, SubscriptionStatus.INACTIVE);
    }

    /**
     * Cancels a subscription.
     *
     * @param id the subscription UUID
     * @return the updated subscription response
     */
    @Override
    public SubscriptionResponse cancel(UUID id) {
        return updateStatus(id, SubscriptionStatus.CANCELLED);
    }

    /**
     * Deletes a subscription.
     *
     * @param id the subscription UUID
     * @throws SubscriptionNotFoundException if subscription not found
     */
    @Override
    public void delete(UUID id) {
        CompanySubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found: " + id));

        subscriptionRepository.delete(subscription);
        log.info("Deleted subscription: {}", id);

        // Publish event indicating the subscription is no longer premium
        SubscriptionUpdatedEvent event = SubscriptionUpdatedEvent.fromSubscription(
                subscription.getCompanyId(),
                SubscriptionStatus.CANCELLED,
                null,
                false
        );
        eventProducer.publishSubscriptionUpdated(event);
    }

    private SubscriptionResponse updateStatus(UUID id, SubscriptionStatus status) {
        CompanySubscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not found: " + id));

        subscription.setStatus(status);
        subscription = subscriptionRepository.save(subscription);
        log.info("Updated subscription {} status to: {}", id, status);

        // Publish event for other services
        publishSubscriptionEvent(subscription);

        return SubscriptionResponse.fromEntity(subscription);
    }

    private void publishSubscriptionEvent(CompanySubscription subscription) {
        SubscriptionUpdatedEvent event = SubscriptionUpdatedEvent.fromSubscription(
                subscription.getCompanyId(),
                subscription.getStatus(),
                subscription.getEndAt(),
                subscription.isPremium()
        );
        eventProducer.publishSubscriptionUpdated(event);
    }
}
