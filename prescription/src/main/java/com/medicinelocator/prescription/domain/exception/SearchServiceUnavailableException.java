package com.medicinelocator.prescription.domain.exception;

public class SearchServiceUnavailableException extends RuntimeException {

    public SearchServiceUnavailableException(String message) {
        super(message);
    }

    public SearchServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}