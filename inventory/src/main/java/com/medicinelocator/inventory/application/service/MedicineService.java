package com.medicinelocator.inventory.application.service;

import com.medicinelocator.inventory.domain.model.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MedicineService {

    Medicine save(Medicine medicine);

    Optional<Medicine> findById(UUID id);

    boolean existsById(UUID id);

    Medicine update(Medicine medicine);

    Page<Medicine> searchMedicines(String name, String brandName, String genericName,
                                   UUID categoryId, Boolean requiresPrescription,
                                   Boolean activeOnly, Pageable pageable);

    boolean hasInventory(UUID medicineId);
}