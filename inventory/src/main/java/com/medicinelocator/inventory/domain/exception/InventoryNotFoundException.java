package com.medicinelocator.inventory.domain.exception;

import java.util.UUID;

public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(String message) {
        super(message);
    }

    public InventoryNotFoundException(UUID pharmacyId, UUID medicineId) {
        super("Inventory not found for pharmacyId: " + pharmacyId + " and medicineId: " + medicineId);
    }
}