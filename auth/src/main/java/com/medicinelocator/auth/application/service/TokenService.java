package com.medicinelocator.auth.application.service;

import com.medicinelocator.auth.domain.enums.Role;
import com.medicinelocator.auth.domain.model.RefreshToken;

import java.util.UUID;

public interface TokenService {

    String generateAccessToken(UUID userId, String email, Role role);

    RefreshToken generateRefreshToken(UUID userId, Role role);

    RefreshToken findRefreshToken(String rawToken);

    void revokeRefreshToken(String tokenHash);

    void revokeAllUserRefreshTokens(UUID userId, Role role);

    void blacklistAccessToken(String accessToken, long remainingTtlMillis);

    boolean isAccessTokenBlacklisted(String accessToken);

    long getAccessTokenRemainingTtl(String accessToken);
}