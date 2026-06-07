package com.medicinelocator.prescription.infrastructure.security;

import java.util.UUID;

public class CurrentUser {

    private final UUID userId;
    private final String role;
    private final String email;

    public CurrentUser(UUID userId, String role, String email) {
        this.userId = userId;
        this.role = role;
        this.email = email;
    }

    public boolean isAdmin()    { return RoleConstants.ROLE_ADMIN.equals(role); }
    public boolean isPharmacy() { return RoleConstants.ROLE_PHARMACY.equals(role); }
    public boolean isCustomer() { return RoleConstants.ROLE_CUSTOMER.equals(role); }

    public UUID getUserId() { return userId; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
}