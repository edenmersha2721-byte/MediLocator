package com.medicinelocator.inventory.application.command;

import java.util.UUID;

public class RemoveMedicineCommand {

    private final UUID pharmacyId;
    private final UUID medicineId;

    public RemoveMedicineCommand(UUID pharmacyId, UUID medicineId) {
        this.pharmacyId = pharmacyId;
        this.medicineId = medicineId;
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public UUID getMedicineId() { return medicineId; }
}