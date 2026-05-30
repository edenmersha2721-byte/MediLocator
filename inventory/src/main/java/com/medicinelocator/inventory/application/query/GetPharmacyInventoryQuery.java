package com.medicinelocator.inventory.application.query;

import java.util.UUID;

public class GetPharmacyInventoryQuery {

    private final UUID pharmacyId;
    private final int page;
    private final int size;

    public GetPharmacyInventoryQuery(UUID pharmacyId, int page, int size) {
        this.pharmacyId = pharmacyId;
        this.page = page;
        this.size = size;
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public int getPage() { return page; }
    public int getSize() { return size; }
}