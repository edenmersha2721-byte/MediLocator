package com.medicinelocator.inventory.application.service;

import com.medicinelocator.inventory.domain.model.PharmacyInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PharmacyInventoryService {

    PharmacyInventory save(PharmacyInventory inventory);

    Optional<PharmacyInventory> findByPharmacyIdAndMedicineId(UUID pharmacyId, UUID medicineId);

    boolean existsByPharmacyIdAndMedicineId(UUID pharmacyId, UUID medicineId);

    PharmacyInventory update(PharmacyInventory inventory);

    Page<PharmacyInventory> findByPharmacyId(UUID pharmacyId, Pageable pageable);

    Page<PharmacyInventory> findAvailableByMedicineId(UUID medicineId, Pageable pageable);

    List<PharmacyInventory> findAllByMedicineId(UUID medicineId);
}