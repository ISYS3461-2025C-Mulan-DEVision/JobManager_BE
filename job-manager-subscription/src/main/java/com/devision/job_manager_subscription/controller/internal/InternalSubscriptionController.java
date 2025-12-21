package com.devision.job_manager_subscription.controller.internal;

import com.devision.job_manager_subscription.dto.ApiResponse;
import com.devision.job_manager_subscription.dto.internal.CreateSubscriptionRequest;
import com.devision.job_manager_subscription.dto.internal.SubscriptionResponse;
import com.devision.job_manager_subscription.dto.internal.UpdateSubscriptionRequest;
import com.devision.job_manager_subscription.service.impl.SubscriptionServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/api/subscriptions")
@RequiredArgsConstructor
public class InternalSubscriptionController {

    private final SubscriptionServiceImpl subscriptionServiceImpl;

    // Get all subscriptions (admin)
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getAll() {
        List<SubscriptionResponse> subscriptions = subscriptionServiceImpl.getAll();
        return ResponseEntity.ok(ApiResponse.success("Subscriptions retrieved", subscriptions));
    }

    // Get subscription by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getById(@PathVariable UUID id) {
        SubscriptionResponse subscription = subscriptionServiceImpl.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription retrieved", subscription));
    }

    // Get subscription by company ID
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> getByCompanyId(@PathVariable UUID companyId) {
        SubscriptionResponse subscription = subscriptionServiceImpl.getByCompanyId(companyId);
        return ResponseEntity.ok(ApiResponse.success("Subscription retrieved", subscription));
    }

    // Check if a company is premium
    @GetMapping("/company/{companyId}/premium")
    public ResponseEntity<ApiResponse<Boolean>> isPremium(@PathVariable UUID companyId) {
        boolean isPremium = subscriptionServiceImpl.isPremium(companyId);
        return ResponseEntity.ok(ApiResponse.success("Premium status checked", isPremium));
    }

    // Create a new subscription
    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionResponse>> create(
            @Valid @RequestBody CreateSubscriptionRequest request) {
        SubscriptionResponse subscription = subscriptionServiceImpl.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscription created", subscription));
    }

    // Update an existing subscription
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSubscriptionRequest request) {
        SubscriptionResponse subscription = subscriptionServiceImpl.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Subscription updated", subscription));
    }

    // Activate a subscription
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> activate(@PathVariable UUID id) {
        SubscriptionResponse subscription = subscriptionServiceImpl.activate(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription activated", subscription));
    }

    // Deactivate a subscription
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> deactivate(@PathVariable UUID id) {
        SubscriptionResponse subscription = subscriptionServiceImpl.deactivate(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription deactivated", subscription));
    }

    // Cancel a subscription
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> cancel(@PathVariable UUID id) {
        SubscriptionResponse subscription = subscriptionServiceImpl.cancel(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription cancelled", subscription));
    }

    // Delete a subscription
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        subscriptionServiceImpl.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription deleted", null));
    }
}
