package com.medicinelocator.inventory.application.command;

import java.util.UUID;

public class AddMedicineCommand {

    private final String name;
    private final String genericName;
    private final String brandName;
    private final String description;
    private final UUID categoryId;
    private final boolean requiresPrescription;

    public AddMedicineCommand(String name, String genericName, String brandName,
                              String description, UUID categoryId, boolean requiresPrescription) {
        this.name = name;
        this.genericName = genericName;
        this.brandName = brandName;
        this.description = description;
        this.categoryId = categoryId;
        this.requiresPrescription = requiresPrescription;
    }

    public String getName() { return name; }
    public String getGenericName() { return genericName; }
    public String getBrandName() { return brandName; }
    public String getDescription() { return description; }
    public UUID getCategoryId() { return categoryId; }
    public boolean isRequiresPrescription() { return requiresPrescription; }
}