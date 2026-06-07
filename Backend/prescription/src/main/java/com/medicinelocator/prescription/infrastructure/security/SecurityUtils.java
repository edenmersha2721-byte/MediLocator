package com.medicinelocator.prescription.infrastructure.security;

import com.medicinelocator.prescription.domain.exception.InvalidPrescriptionException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    public void requireCustomerOrAdmin(CurrentUser currentUser) {
        if (!currentUser.isCustomer() && !currentUser.isAdmin()) {
            throw new InvalidPrescriptionException(
                    "Access denied: only CUSTOMER or ADMIN can upload prescriptions");
        }
    }

    public void requireCustomerOwnershipOrAdmin(CurrentUser currentUser, UUID ownerId) {
        if (currentUser.isAdmin()) {
            return;
        }
        if (!currentUser.getUserId().equals(ownerId)) {
            throw new InvalidPrescriptionException(
                    "Access denied: you can only access your own prescriptions");
        }
    }
}