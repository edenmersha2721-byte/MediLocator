package com.medicinelocator.search.domain.exception;

public class InvalidSearchRequestException extends RuntimeException {

    public InvalidSearchRequestException(String message) {
        super(message);
    }
}