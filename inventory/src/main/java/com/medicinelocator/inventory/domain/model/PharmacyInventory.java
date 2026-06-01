package com.medicinelocator.inventory.domain.model;

import java.util.List;
import java.util.UUID;

public class PharmacyInventory {

    private UUID pharmacyId;
    private int totalItems;
    private List<Medicine> medicines;

    public PharmacyInventory(UUID pharmacyId, int totalItems, List<Medicine> medicines) {
        this.pharmacyId = pharmacyId;
        this.totalItems = totalItems;
        this.medicines = medicines;
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public int getTotalItems() { return totalItems; }
    public List<Medicine> getMedicines() { return medicines; }
}