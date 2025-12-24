package com.devision.job_manager_auth.service.internal.impl;

import com.devision.job_manager_auth.config.jwe.JweProperties;
import com.devision.job_manager_auth.entity.CompanyAccount;
import com.devision.job_manager_auth.service.internal.JweTokenService;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class JweTokenServiceImpl implements JweTokenService {
    private final JweProperties jweProperties;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JweTokenServiceImpl(
            JweProperties jweProperties,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.jweProperties = jweProperties;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Override
    public String generateAccessToken(CompanyAccount account) {
        return generateToken(account, accessTokenExpiration, "ACCESS");
    }

    String generateRefreshToken(CompanyAccount account);

    Long validateAccessToken(String token);

    UUID validateRefreshToken(String token);

    void revokeToken(String token);

    boolean isTokenRevoked(String token);

    String extractTokenFromHeader(String authHeader);

    Claims extractAllClaims(String token);

    private String generateToken(CompanyAccount account, long expirationMs, String tokenType) {
        try {
            Date now = new Date();
            Date expiration = new Date(now.getTime() + expirationMs);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .jwtID(UUID.randomUUID().toString())
                    .subject(account.getId().toString())
                    .issuer("job-manager-auth")
                    .issueTime(now)
                    .expirationTime(expiration)
                    .claim(CLAIM_USER_ID, account.getId().toString())
                    .claim(CLAIM_EMAIL, account.getEmail())
                    .claim(CLAIM_ROLE, account.getRole().name())
                    .claim(CLAIM_COUNTRY_CODE, account.getCountry().getCode())
                    .claim(CLAIM_TOKEN_TYPE, tokenType)
                    .build();

            // Create the JWE header
            JWEHeader header = new JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM)
                    .contentType("JWT")
                    .build();

            // Create encrypted JWT
            EncryptedJWT encryptedJWT = new EncryptedJWT(header, claimsSet);

            // Encrypt with the public key
            RSAEncrypter encrypter = new RSAEncrypter(jweProperties.getPublicKey());
            encryptedJWT.encrypt(encrypter);

            return encryptedJWT.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate JWE token", e);
        }
    }
}
