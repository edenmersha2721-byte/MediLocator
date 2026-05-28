package com.medicinelocator.auth.domain.exception;

public class PharmacyNotApprovedException extends RuntimeException {

    public PharmacyNotApprovedException(String message) {
        super(message);
    }
}