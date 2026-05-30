package com.medicinelocator.inventory.application.command;

import java.util.UUID;

public class UpdateCategoryCommand {

    private final UUID categoryId;
    private final String name;
    private final String description;
    private final boolean active;

    public UpdateCategoryCommand(UUID categoryId, String name, String description, boolean active) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public UUID getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
}