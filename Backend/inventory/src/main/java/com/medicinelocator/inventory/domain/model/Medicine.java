package com.medicinelocator.inventory.domain.model;

import com.medicinelocator.inventory.domain.exception.InsufficientStockException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Pharmacy-owned medicine aggregate.
 * A medicine belongs exclusively to ONE pharmacy.
 * There is no global medicine catalog in this system.
 */
public class Medicine {

    private UUID id;
    private UUID pharmacyId;
    private String medicineName;
    private String genericName;
    private String brandName;
    private String category;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private boolean available;
    private boolean requiresPrescription;
    private LocalDate expiryDate;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Medicine() {
    }

    public Medicine(UUID id, UUID pharmacyId, String medicineName, String genericName,
                    String brandName, String category, String description,
                    BigDecimal price, int stockQuantity, boolean available,
                    boolean requiresPrescription, LocalDate expiryDate,
                    boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.pharmacyId = pharmacyId;
        this.medicineName = medicineName;
        this.genericName = genericName;
        this.brandName = brandName;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.available = available;
        this.requiresPrescription = requiresPrescription;
        this.expiryDate = expiryDate;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ─── Domain business methods ──────────────────────────────────────────────

    public void updateDetails(String medicineName, String genericName, String brandName,
                              String category, String description, BigDecimal price,
                              int stockQuantity, boolean requiresPrescription, LocalDate expiryDate) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.medicineName = medicineName;
        this.genericName = genericName;
        this.brandName = brandName;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.available = stockQuantity > 0;
        this.requiresPrescription = requiresPrescription;
        this.expiryDate = expiryDate;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStock(int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.stockQuantity = newQuantity;
        this.available = newQuantity > 0;
        this.updatedAt = LocalDateTime.now();
    }

    public void deductStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deduction amount must be positive");
        }
        if (this.stockQuantity < amount) {
            throw new InsufficientStockException(
                    "Insufficient stock for medicine '" + medicineName
                            + "'. Available: " + stockQuantity + ", Requested: " + amount);
        }
        this.stockQuantity -= amount;
        this.available = this.stockQuantity > 0;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.available = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean belongsTo(UUID pharmacyId) {
        return this.pharmacyId.equals(pharmacyId);
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

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

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

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

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}