package com.medicinelocator.inventory.infrastructure.security;

import com.medicinelocator.inventory.domain.exception.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    /**
     * Enforces that the caller is either:
     * - The pharmacy that owns the resource (PHARMACY role + matching userId), OR
     * - An admin (who cannot modify medicines, but may read for moderation)
     *
     * For write operations, admins are NOT allowed — only the owning pharmacy.
     */
    public void requirePharmacyOwnership(CurrentUser currentUser, UUID pharmacyId) {
        if (!currentUser.isPharmacy()) {
            throw new AccessDeniedException(
                    "Access denied: only PHARMACY accounts can manage medicines");
        }
        if (!currentUser.getUserId().equals(pharmacyId)) {
            throw new AccessDeniedException(
                    "Access denied: you can only manage your own medicines. "
                            + "Attempted pharmacyId: " + pharmacyId);
        }
    }

    /**
     * Allows any authenticated user (CUSTOMER, PHARMACY, ADMIN).
     * Called on read-only / search endpoints.
     */
    public void requireAuthenticated(CurrentUser currentUser) {
        if (currentUser.getUserId() == null) {
            throw new AccessDeniedException("Access denied: authentication required");
        }
        String role = currentUser.getRole();
        if (!RoleConstants.ROLE_CUSTOMER.equals(role)
                && !RoleConstants.ROLE_PHARMACY.equals(role)
                && !RoleConstants.ROLE_ADMIN.equals(role)) {
            throw new AccessDeniedException("Access denied: unknown role '" + role + "'");
        }
    }
}