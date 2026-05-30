package com.medicinelocator.inventory.infrastructure.security;

import com.medicinelocator.inventory.domain.exception.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    public void requireAdmin(CurrentUser currentUser) {
        if (!currentUser.isAdmin()) {
            throw new AccessDeniedException("Access denied: ADMIN role required");
        }
    }

    public void requirePharmacy(CurrentUser currentUser) {
        if (!currentUser.isPharmacy()) {
            throw new AccessDeniedException("Access denied: PHARMACY role required");
        }
    }

    public void requirePharmacyOrAdmin(CurrentUser currentUser) {
        if (!currentUser.isPharmacy() && !currentUser.isAdmin()) {
            throw new AccessDeniedException("Access denied: PHARMACY or ADMIN role required");
        }
    }

    public void requirePharmacyOwnership(CurrentUser currentUser, UUID pharmacyId) {
        if (currentUser.isAdmin()) {
            return;
        }
        if (!currentUser.isPharmacy()) {
            throw new AccessDeniedException("Access denied: PHARMACY role required");
        }
        if (!currentUser.getUserId().equals(pharmacyId)) {
            throw new AccessDeniedException(
                    "Access denied: You can only manage your own pharmacy inventory");
        }
    }
}