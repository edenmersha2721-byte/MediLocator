package com.medicinelocator.inventory.application.command;

import java.util.UUID;

public class DeductStockCommand {

    private final UUID pharmacyId;
    private final UUID medicineId;
    private final int amount;

    public DeductStockCommand(UUID pharmacyId, UUID medicineId, int amount) {
        this.pharmacyId = pharmacyId;
        this.medicineId = medicineId;
        this.amount = amount;
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public UUID getMedicineId() { return medicineId; }
    public int getAmount() { return amount; }
}