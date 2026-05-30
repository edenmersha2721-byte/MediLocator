package com.medicinelocator.inventory.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class AddMedicineRequest {

    @NotBlank(message = "Medicine name is required")
    @Size(min = 2, max = 255, message = "Medicine name must be between 2 and 255 characters")
    private String name;

    @Size(max = 255, message = "Generic name must not exceed 255 characters")
    private String genericName;

    @Size(max = 255, message = "Brand name must not exceed 255 characters")
    private String brandName;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    private boolean requiresPrescription;

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
    public void setRequiresPrescription(boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }
}