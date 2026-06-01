package com.medicinelocator.inventory.infrastructure.security;

import com.medicinelocator.inventory.domain.exception.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GatewayHeaderExtractor {

    private static final Logger log = LoggerFactory.getLogger(GatewayHeaderExtractor.class);

    private static final String HEADER_USER_ID    = "X-User-Id";
    private static final String HEADER_USER_ROLE  = "X-User-Role";
    private static final String HEADER_USER_EMAIL = "X-User-Email";

    public CurrentUser extract(HttpServletRequest request) {
        String userIdStr = request.getHeader(HEADER_USER_ID);
        String role      = request.getHeader(HEADER_USER_ROLE);
        String email     = request.getHeader(HEADER_USER_EMAIL);

        if (userIdStr == null || userIdStr.isBlank()) {
            throw new AccessDeniedException("Missing X-User-Id header. Request must pass through the API Gateway.");
        }

        try {
            UUID userId = UUID.fromString(userIdStr);
            return new CurrentUser(userId, role != null ? role : "", email != null ? email : "");
        } catch (IllegalArgumentException e) {
            log.warn("Invalid X-User-Id header value: {}", userIdStr);
            throw new AccessDeniedException("Invalid identity header received from gateway");
        }
    }
}