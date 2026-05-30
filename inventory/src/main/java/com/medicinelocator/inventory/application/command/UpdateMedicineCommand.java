package com.medicinelocator.inventory.application.command;

import java.util.UUID;

public class UpdateMedicineCommand {

    private final UUID medicineId;
    private final String name;
    private final String genericName;
    private final String brandName;
    private final String description;
    private final UUID categoryId;
    private final boolean requiresPrescription;
    private final boolean active;

    public UpdateMedicineCommand(UUID medicineId, String name, String genericName, String brandName,
                                 String description, UUID categoryId,
                                 boolean requiresPrescription, boolean active) {
        this.medicineId = medicineId;
        this.name = name;
        this.genericName = genericName;
        this.brandName = brandName;
        this.description = description;
        this.categoryId = categoryId;
        this.requiresPrescription = requiresPrescription;
        this.active = active;
    }

    public UUID getMedicineId() { return medicineId; }
    public String getName() { return name; }
    public String getGenericName() { return genericName; }
    public String getBrandName() { return brandName; }
    public String getDescription() { return description; }
    public UUID getCategoryId() { return categoryId; }
    public boolean isRequiresPrescription() { return requiresPrescription; }
    public boolean isActive() { return active; }
}