package com.medicinelocator.inventory.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Lightweight projection used in cross-pharmacy search results.
 * Carries enough data to answer: "which pharmacy has this medicine, at what price?"
 */
public class InventoryItem {

    private UUID medicineId;
    private UUID pharmacyId;
    private String medicineName;
    private String genericName;
    private String brandName;
    private String category;
    private BigDecimal price;
    private int stockQuantity;
    private boolean available;
    private boolean requiresPrescription;
    private LocalDate expiryDate;

    public InventoryItem() {
    }

    public InventoryItem(UUID medicineId, UUID pharmacyId, String medicineName, String genericName,
                         String brandName, String category, BigDecimal price,
                         int stockQuantity, boolean available,
                         boolean requiresPrescription, LocalDate expiryDate) {
        this.medicineId = medicineId;
        this.pharmacyId = pharmacyId;
        this.medicineName = medicineName;
        this.genericName = genericName;
        this.brandName = brandName;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.available = available;
        this.requiresPrescription = requiresPrescription;
        this.expiryDate = expiryDate;
    }

    public UUID getMedicineId() { return medicineId; }
    public void setMedicineId(UUID medicineId) { this.medicineId = medicineId; }

    public UUID getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(UUID pharmacyId) { this.pharmacyId = pharmacyId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public boolean isRequiresPrescription() { return requiresPrescription; }
    public void setRequiresPrescription(boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}