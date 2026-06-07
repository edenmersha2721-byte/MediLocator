package com.medicinelocator.inventory.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class UpdateStockCommand {

    private final UUID pharmacyId;
    private final UUID medicineId;
    private final String medicineName;
    private final String genericName;
    private final String brandName;
    private final String category;
    private final String description;
    private final BigDecimal price;
    private final int stockQuantity;
    private final boolean requiresPrescription;
    private final LocalDate expiryDate;

    public UpdateStockCommand(UUID pharmacyId, UUID medicineId, String medicineName,
                              String genericName, String brandName, String category,
                              String description, BigDecimal price, int stockQuantity,
                              boolean requiresPrescription, LocalDate expiryDate) {
        this.pharmacyId = pharmacyId;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.genericName = genericName;
        this.brandName = brandName;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.requiresPrescription = requiresPrescription;
        this.expiryDate = expiryDate;
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public UUID getMedicineId() { return medicineId; }
    public String getMedicineName() { return medicineName; }
    public String getGenericName() { return genericName; }
    public String getBrandName() { return brandName; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public boolean isRequiresPrescription() { return requiresPrescription; }
    public LocalDate getExpiryDate() { return expiryDate; }
}