package com.medicinelocator.inventory.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 150, message = "Category name must be between 2 and 150 characters")
    private String name;

    @Size(max = 500)
    private String description;

    private boolean active;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}