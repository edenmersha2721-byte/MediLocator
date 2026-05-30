package com.medicinelocator.inventory.application.dto.response;

import java.util.List;
import java.util.UUID;

public class PharmacyInventoryResponse {

    private UUID pharmacyId;
    private int totalItems;
    private List<InventoryItemResponse> items;

    public PharmacyInventoryResponse() {
    }

    public PharmacyInventoryResponse(UUID pharmacyId, int totalItems, List<InventoryItemResponse> items) {
        this.pharmacyId = pharmacyId;
        this.totalItems = totalItems;
        this.items = items;
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(UUID pharmacyId) { this.pharmacyId = pharmacyId; }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    public List<InventoryItemResponse> getItems() { return items; }
    public void setItems(List<InventoryItemResponse> items) { this.items = items; }
}