package com.medicinelocator.prescription.domain.exception;

import java.util.UUID;

public class PrescriptionNotFoundException extends RuntimeException {

    public PrescriptionNotFoundException(UUID id) {
        super("Prescription not found with id: " + id);
    }

    public PrescriptionNotFoundException(String message) {
        super(message);
    }
}