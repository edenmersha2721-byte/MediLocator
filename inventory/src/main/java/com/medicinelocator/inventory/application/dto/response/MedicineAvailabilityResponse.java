package com.medicinelocator.inventory.application.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class MedicineAvailabilityResponse {

    private UUID pharmacyId;
    private UUID medicineId;
    private String medicineName;
    private int quantity;
    private BigDecimal unitPrice;
    private boolean available;

    public MedicineAvailabilityResponse() {
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(UUID pharmacyId) { this.pharmacyId = pharmacyId; }

    public UUID getMedicineId() { return medicineId; }
    public void setMedicineId(UUID medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}