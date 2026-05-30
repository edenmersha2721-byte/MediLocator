package com.medicinelocator.inventory.application.command;

import java.util.UUID;

public class RemoveMedicineCommand {

    private final UUID medicineId;

    public RemoveMedicineCommand(UUID medicineId) {
        this.medicineId = medicineId;
    }

    public UUID getMedicineId() { return medicineId; }
}