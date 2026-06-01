package com.medicinelocator.inventory.domain.exception;

import java.util.UUID;

public class MedicineNotFoundException extends RuntimeException {

    public MedicineNotFoundException(String message) {
        super(message);
    }

    public MedicineNotFoundException(UUID medicineId) {
        super("Medicine not found with id: " + medicineId);
    }

    public MedicineNotFoundException(UUID pharmacyId, UUID medicineId) {
        super("Medicine not found with id: " + medicineId + " for pharmacy: " + pharmacyId);
    }
}