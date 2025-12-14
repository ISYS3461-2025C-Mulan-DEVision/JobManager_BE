package com.devision.job_manager_company.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Company entity - represents company profile/business data.
 * The ID matches the CompanyAccount ID from the Auth service.
 */
@Entity
@Table(name = "company")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    @Id
    // ID is assigned from Auth service (no auto-generation)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 32)
    private String phone;

    @Column(name = "street_address", length = 255)
    private String streetAddress;

    @Column(length = 128)
    private String city;

    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CompanyProfile profile;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
