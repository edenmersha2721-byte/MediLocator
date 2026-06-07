package com.medicinelocator.auth.application.command.handler;

import com.medicinelocator.auth.application.command.LogoutCommand;
import com.medicinelocator.auth.application.service.TokenService;
import com.medicinelocator.auth.domain.model.RefreshToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LogoutHandler {

    private static final Logger log = LoggerFactory.getLogger(LogoutHandler.class);

    private final TokenService tokenService;

    public LogoutHandler(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Transactional
    public void handle(LogoutCommand command) {
        if (command.getAccessToken() != null && !command.getAccessToken().isBlank()) {
            long remainingTtl = tokenService.getAccessTokenRemainingTtl(command.getAccessToken());
            if (remainingTtl > 0) {
                tokenService.blacklistAccessToken(command.getAccessToken(), remainingTtl);
            }
        }

        if (command.getRefreshToken() != null && !command.getRefreshToken().isBlank()) {
            try {
                RefreshToken refreshToken = tokenService.findRefreshToken(command.getRefreshToken());
                tokenService.revokeRefreshToken(refreshToken.getTokenHash());
            } catch (Exception e) {
                log.warn("Refresh token not found during logout: {}", e.getMessage());
            }
        }

        log.info("User logged out successfully");
    }
}