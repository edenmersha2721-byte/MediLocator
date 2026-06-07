package com.medicinelocator.inventory.application.query;

import java.util.UUID;

public class GetPharmacyInventoryQuery {

    private final UUID pharmacyId;
    private final boolean availableOnly;
    private final int page;
    private final int size;

    public GetPharmacyInventoryQuery(UUID pharmacyId, boolean availableOnly, int page, int size) {
        this.pharmacyId = pharmacyId;
        this.availableOnly = availableOnly;
        this.page = page;
        this.size = size;
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public boolean isAvailableOnly() { return availableOnly; }
    public int getPage() { return page; }
    public int getSize() { return size; }
}