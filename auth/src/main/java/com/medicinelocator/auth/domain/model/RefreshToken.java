package com.medicinelocator.auth.domain.model;

import com.medicinelocator.auth.domain.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshToken {

    private UUID id;
    private String tokenHash;
    private UUID userId;
    private Role userRole;
    private boolean revoked;
    private boolean used;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public RefreshToken() {
    }

    public RefreshToken(UUID id, String tokenHash, UUID userId, Role userRole,
                        boolean revoked, boolean used, LocalDateTime expiresAt,
                        LocalDateTime createdAt) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.userRole = userRole;
        this.revoked = revoked;
        this.used = used;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !used && !isExpired();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Role getUserRole() { return userRole; }
    public void setUserRole(Role userRole) { this.userRole = userRole; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}