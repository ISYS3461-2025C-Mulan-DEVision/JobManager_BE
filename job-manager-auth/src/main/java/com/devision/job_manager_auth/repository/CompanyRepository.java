package com.devision.job_manager_auth.repository;

import com.devision.job_manager_auth.entity.Company;
import com.devision.job_manager_auth.entity.Country;
import com.devision.job_manager_auth.entity.SsoProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    /**
     * REGISTRATION QUERIES
     */

    // 1. find company by email
    Optional<Company> findByEmail(String email);

    // 2. check if email already exists
    boolean existsByEmail(String email);

    // 3. find company by activation token
    Optional<Company> findByActivationToken(String activationToken);

    /**
     * SSO QUERIES
     */

    // 1. find company by SSO provider and provider ID
    Optional<Company> findBySsoProviderAndSsoProviderId(SsoProvider ssoProvider, String ssoProviderId);

    // 2. Check if SSO account already exists
    boolean existsBySsoProviderAndSsoProviderId(SsoProvider ssoProvider, String ssoProviderId);

    /**
     * LOGIN AND SECURITY QUERIES
     */

    // 1. Find activated company by email
    Optional<Company> findByEmailAndIsActivatedTrue(String email);

    // 2. Find by email and not locked for login validation
    Optional<Company> findByEmailAndIsLockedFalse(String email);

    // 3. For secure login
    Optional<Company> findByEmailAndIsActivatedTrueAndIsLockedFalse(String email);

    /**
     * BRUTE-FORCE PROTECTION
     */

    // 1. Increase failed time
    @Modifying
    @Query("UPDATE Company c SET c.failedLoginAttempts = c.failedLoginAttempts + 1, " +
        "c.lastFailedLoginTime = :failedTime WHERE c.email = :email")
    void incrementFailedLoginAttempts(
            @Param("email") String email,
            @Param("failedTime") LocalDateTime failedTime
    );

    // 2. Reset when logging in successfully
    @Modifying
    @Query("UPDATE Company c SET c.failedLoginAttempts = 0, c.lastFailedLoginTime = null " +
    "WHERE c.email = :email")
    void resetFailedLoginAttempts(
            @Param("email") String email
    );

    // 3. Lock account
    @Modifying
    @Query("UPDATE Company c SET c.isLocked = true WHERE c.email = :email")
    void lockAccount(
            @Param("email") String email
    );

    // 4. Unlock account
    @Modifying
    @Query("UPDATE Company c SET c.isLocked = false, c.failedLoginAttempts = 0 WHERE c.email = :email")
    void unlockAccount(
            @Param("email") String email
    );

    // 5. Activate account
    @Modifying
    @Query("UPDATE Company c SET c.isActivated = true, c.activationToken = null, " + "c.activationTokenExpiry = null WHERE c.email = :email")
    void activateAccount(
            @Param("email") String email
    );

    /**
     * SHARDING QUERIES
     */

    // 1. find all companies by country
    List<Company> findByCountry(Country country);

    // 2. Count companies by country
    long countByCountry(Country country);

}
