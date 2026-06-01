package com.medicinelocator.inventory.application.service;

import com.medicinelocator.inventory.domain.model.PharmacyInventory;

import java.util.UUID;


public interface PharmacyInventoryService {

    PharmacyInventory getPharmacyInventorySummary(UUID pharmacyId);
}