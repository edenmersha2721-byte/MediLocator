package com.medicinelocator.inventory.application.query;

public class GetAllCategoriesQuery {

    private final boolean activeOnly;

    public GetAllCategoriesQuery(boolean activeOnly) {
        this.activeOnly = activeOnly;
    }

    public boolean isActiveOnly() { return activeOnly; }
}