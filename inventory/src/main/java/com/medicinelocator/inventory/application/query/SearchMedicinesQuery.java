package com.medicinelocator.inventory.application.query;

import java.util.UUID;

public class SearchMedicinesQuery {

    private final String name;
    private final String brandName;
    private final String genericName;
    private final UUID categoryId;
    private final Boolean requiresPrescription;
    private final Boolean activeOnly;
    private final int page;
    private final int size;

    public SearchMedicinesQuery(String name, String brandName, String genericName,
                                UUID categoryId, Boolean requiresPrescription,
                                Boolean activeOnly, int page, int size) {
        this.name = name;
        this.brandName = brandName;
        this.genericName = genericName;
        this.categoryId = categoryId;
        this.requiresPrescription = requiresPrescription;
        this.activeOnly = activeOnly;
        this.page = page;
        this.size = size;
    }

    public String getName() { return name; }
    public String getBrandName() { return brandName; }
    public String getGenericName() { return genericName; }
    public UUID getCategoryId() { return categoryId; }
    public Boolean getRequiresPrescription() { return requiresPrescription; }
    public Boolean getActiveOnly() { return activeOnly; }
    public int getPage() { return page; }
    public int getSize() { return size; }
}