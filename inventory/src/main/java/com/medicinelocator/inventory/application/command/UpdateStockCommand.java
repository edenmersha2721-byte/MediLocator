package com.medicinelocator.inventory.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public class UpdateStockCommand {

    private final UUID pharmacyId;
    private final UUID medicineId;
    private final int quantity;
    private final BigDecimal unitPrice;

    public UpdateStockCommand(UUID pharmacyId, UUID medicineId, int quantity, BigDecimal unitPrice) {
        this.pharmacyId = pharmacyId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public UUID getMedicineId() { return medicineId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}