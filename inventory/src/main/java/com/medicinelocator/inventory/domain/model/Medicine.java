package com.medicinelocator.inventory.domain.model;

import com.medicinelocator.inventory.domain.exception.MedicineNotFoundException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Medicine {

    private UUID id;
    private String name;
    private String genericName;
    private String brandName;
    private String description;
    private UUID categoryId;
    private boolean requiresPrescription;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Medicine() {
    }

    public Medicine(UUID id, String name, String genericName, String brandName,
                    String description, UUID categoryId, boolean requiresPrescription,
                    boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.genericName = genericName;
        this.brandName = brandName;
        this.description = description;
        this.categoryId = categoryId;
        this.requiresPrescription = requiresPrescription;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void updateDetails(String name, String genericName, String brandName,
                              String description, UUID categoryId, boolean requiresPrescription) {
        this.name = name;
        this.genericName = genericName;
        this.brandName = brandName;
        this.description = description;
        this.categoryId = categoryId;
        this.requiresPrescription = requiresPrescription;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public boolean isRequiresPrescription() { return requiresPrescription; }
    public void setRequiresPrescription(boolean requiresPrescription) { this.requiresPrescription = requiresPrescription; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}