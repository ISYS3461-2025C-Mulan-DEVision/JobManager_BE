package com.devision.job_manager_subscription.repository;

import com.devision.job_manager_subscription.model.CompanySubscription;
import com.devision.job_manager_subscription.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanySubscriptionRepository extends JpaRepository<CompanySubscription, UUID> {

    // Find subscription by company ID
    Optional<CompanySubscription> findByCompanyId(UUID companyId);

    // Find the latest active subscription for a company
    @Query("SELECT s FROM CompanySubscription s WHERE s.companyId = :companyId AND s.status = :status ORDER BY s.startAt DESC")
    Optional<CompanySubscription> findLatestByCompanyIdAndStatus(
            @Param("companyId") UUID companyId,
            @Param("status") SubscriptionStatus status
    );

    // Find all subscriptions for a company
    List<CompanySubscription> findAllByCompanyIdOrderByStartAtDesc(UUID companyId);

    // Check if a company has any active subscription
    boolean existsByCompanyIdAndStatus(UUID companyId, SubscriptionStatus status);

    // Find all active subscriptions (for batch processing)
    List<CompanySubscription> findAllByStatus(SubscriptionStatus status);
}
