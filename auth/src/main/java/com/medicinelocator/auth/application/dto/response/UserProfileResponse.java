package com.medicinelocator.auth.application.dto.response;

import java.util.UUID;

public class UserProfileResponse {

    private UUID id;
    private String email;
    private String role;
    private String displayName;
    private boolean emailVerified;

    public UserProfileResponse(UUID id, String email, String role, String displayName, boolean emailVerified) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.displayName = displayName;
        this.emailVerified = emailVerified;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getDisplayName() { return displayName; }
    public boolean isEmailVerified() { return emailVerified; }
}