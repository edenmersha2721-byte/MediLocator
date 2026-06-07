package com.medicinelocator.inventory.domain.exception;

public class DuplicateMedicineException extends RuntimeException {

    public DuplicateMedicineException(String message) {
        super(message);
    }
}