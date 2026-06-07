package com.medicinelocator.inventory.application.service;

import com.medicinelocator.inventory.domain.model.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MedicineService {

    Medicine save(Medicine medicine);

    Optional<Medicine> findByIdAndPharmacyId(UUID medicineId, UUID pharmacyId);

    boolean existsByPharmacyIdAndMedicineName(UUID pharmacyId, String medicineName);

    Medicine update(Medicine medicine);

    void deleteById(UUID medicineId);

    Page<Medicine> findByPharmacyId(UUID pharmacyId, boolean availableOnly, Pageable pageable);

    Page<Medicine> searchAcrossPharmacies(String medicineName, String brandName,
                                          String genericName, String category,
                                          Boolean availableOnly, Boolean requiresPrescription,
                                          Pageable pageable);
}