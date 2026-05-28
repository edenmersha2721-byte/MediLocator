package com.medicinelocator.auth.application.query;

import com.medicinelocator.auth.domain.enums.Role;

import java.util.UUID;

public class GetCurrentUserQuery {

    private final UUID userId;
    private final Role role;

    public GetCurrentUserQuery(UUID userId, Role role) {
        this.userId = userId;
        this.role = role;
    }

    public UUID getUserId() { return userId; }
    public Role getRole() { return role; }
}