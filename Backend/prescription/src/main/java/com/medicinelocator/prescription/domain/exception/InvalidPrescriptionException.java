package com.medicinelocator.prescription.domain.exception;

public class InvalidPrescriptionException extends RuntimeException {

    public InvalidPrescriptionException(String message) {
        super(message);
    }
}