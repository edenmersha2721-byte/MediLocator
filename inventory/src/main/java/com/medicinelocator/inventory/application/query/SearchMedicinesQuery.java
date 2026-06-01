package com.medicinelocator.inventory.application.query;

public class SearchMedicinesQuery {

    private final String medicineName;
    private final String brandName;
    private final String genericName;
    private final String category;
    private final Boolean availableOnly;
    private final Boolean requiresPrescription;
    private final int page;
    private final int size;

    public SearchMedicinesQuery(String medicineName, String brandName, String genericName,
                                String category, Boolean availableOnly,
                                Boolean requiresPrescription, int page, int size) {
        this.medicineName = medicineName;
        this.brandName = brandName;
        this.genericName = genericName;
        this.category = category;
        this.availableOnly = availableOnly;
        this.requiresPrescription = requiresPrescription;
        this.page = page;
        this.size = size;
    }

    public String getMedicineName() { return medicineName; }
    public String getBrandName() { return brandName; }
    public String getGenericName() { return genericName; }
    public String getCategory() { return category; }
    public Boolean getAvailableOnly() { return availableOnly; }
    public Boolean getRequiresPrescription() { return requiresPrescription; }
    public int getPage() { return page; }
    public int getSize() { return size; }
}