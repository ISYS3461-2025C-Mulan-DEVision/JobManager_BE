package com.devision.job_manager_auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(length = 255)
    private String name;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Country country;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String street;

    @Column(length = 100)
    private String city;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role = Role.COMPANY; // COMPANY or ADMIN

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private SsoProvider ssoProvider; // GOOGLE, MICROSOFT, FACEBOOK, GITHUB, NONE

    @Column(length = 255)
    private String ssoProviderId; // this ID is provided by the SSO Provider

    @Column(nullable = false)
    private Boolean isActivated = false;

    @Column(length = 255)
    private String activationToken;

    @Column
    private LocalDateTime activationTokenExpiry;

    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column
    private LocalDateTime lastFailedLoginTime;

    @Column(nullable = false)
    private Boolean isLocked = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
