package com.medicinelocator.inventory.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class InventoryItem {

    private UUID medicineId;
    private String medicineName;
    private String brandName;
    private int quantity;
    private BigDecimal unitPrice;
    private boolean available;
    private LocalDate expiryDate;

    public InventoryItem() {
    }

    public InventoryItem(UUID medicineId, String medicineName, String brandName,
                         int quantity, BigDecimal unitPrice, boolean available, LocalDate expiryDate) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.brandName = brandName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.available = available;
        this.expiryDate = expiryDate;
    }

    public UUID getMedicineId() { return medicineId; }
    public void setMedicineId(UUID medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}