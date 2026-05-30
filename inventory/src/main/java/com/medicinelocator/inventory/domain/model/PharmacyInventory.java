package com.medicinelocator.inventory.domain.model;

import com.medicinelocator.inventory.domain.exception.InsufficientStockException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PharmacyInventory {

    private UUID id;
    private UUID pharmacyId;
    private UUID medicineId;
    private int quantity;
    private BigDecimal unitPrice;
    private boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PharmacyInventory() {
    }

    public PharmacyInventory(UUID id, UUID pharmacyId, UUID medicineId, int quantity,
                             BigDecimal unitPrice, boolean available,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.pharmacyId = pharmacyId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.available = available;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateStock(int newQuantity, BigDecimal newUnitPrice) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (newUnitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        this.quantity = newQuantity;
        this.unitPrice = newUnitPrice;
        this.available = newQuantity > 0;
        this.updatedAt = LocalDateTime.now();
    }

    public void deductStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deduction amount must be positive");
        }
        if (this.quantity < amount) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available: " + this.quantity + ", Requested: " + amount);
        }
        this.quantity -= amount;
        this.available = this.quantity > 0;
        this.updatedAt = LocalDateTime.now();
    }

    public void setAvailability(boolean available) {
        if (available && this.quantity == 0) {
            throw new IllegalArgumentException("Cannot mark available when quantity is zero");
        }
        this.available = available;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(UUID pharmacyId) { this.pharmacyId = pharmacyId; }

    public UUID getMedicineId() { return medicineId; }
    public void setMedicineId(UUID medicineId) { this.medicineId = medicineId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}