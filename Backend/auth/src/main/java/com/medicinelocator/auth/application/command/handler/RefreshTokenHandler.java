package com.medicinelocator.auth.application.command.handler;

import com.medicinelocator.auth.application.command.RefreshTokenCommand;
import com.medicinelocator.auth.application.dto.response.AuthResponse;
import com.medicinelocator.auth.application.service.TokenService;
import com.medicinelocator.auth.domain.exception.InvalidTokenException;
import com.medicinelocator.auth.domain.model.RefreshToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RefreshTokenHandler {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenHandler.class);

    private final TokenService tokenService;

    @Value("${jwt.access-token-expiration:900000}")
    private long accessTokenExpiration;

    public RefreshTokenHandler(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Transactional
    public AuthResponse handle(RefreshTokenCommand command) {
        RefreshToken existingToken;

        try {
            existingToken = tokenService.findRefreshToken(command.getRefreshToken());
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        if (existingToken.isUsed()) {
            // Reuse detected — revoke all tokens for this user (token family revocation)
            tokenService.revokeAllUserRefreshTokens(existingToken.getUserId(), existingToken.getUserRole());
            log.warn("Refresh token reuse detected for userId={}, revoking all tokens",
                    existingToken.getUserId());
            throw new InvalidTokenException("Refresh token reuse detected. Please login again.");
        }

        if (!existingToken.isValid()) {
            throw new InvalidTokenException("Refresh token is expired or revoked");
        }

        // Mark existing token as used (rotation)
        existingToken.setUsed(true);
        tokenService.revokeRefreshToken(existingToken.getTokenHash());

        // Issue new token pair
        String newAccessToken = tokenService.generateAccessToken(
                existingToken.getUserId(),
                null,
                existingToken.getUserRole()
        );
        RefreshToken newRefreshToken = tokenService.generateRefreshToken(
                existingToken.getUserId(),
                existingToken.getUserRole()
        );

        log.info("Refresh token rotated for userId={}", existingToken.getUserId());
        return new AuthResponse(
                newAccessToken,
                newRefreshToken.getTokenHash(),
                "Bearer",
                accessTokenExpiration,
                existingToken.getUserRole().name()
        );
    }
}