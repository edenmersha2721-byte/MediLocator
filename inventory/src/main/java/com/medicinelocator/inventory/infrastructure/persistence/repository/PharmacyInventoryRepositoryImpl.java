package com.medicinelocator.inventory.infrastructure.persistence.repository;

import com.medicinelocator.inventory.application.service.PharmacyInventoryService;
import com.medicinelocator.inventory.domain.model.Medicine;
import com.medicinelocator.inventory.domain.model.PharmacyInventory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PharmacyInventoryRepositoryImpl implements PharmacyInventoryService {

    private final MedicineJpaRepository medicineJpaRepository;
    private final MedicineRepositoryImpl medicineRepository;

    public PharmacyInventoryRepositoryImpl(MedicineJpaRepository medicineJpaRepository,
                                           MedicineRepositoryImpl medicineRepository) {
        this.medicineJpaRepository = medicineJpaRepository;
        this.medicineRepository = medicineRepository;
    }

    @Override
    public PharmacyInventory getPharmacyInventorySummary(UUID pharmacyId) {
        // Fetch first 1000 active medicines for summary (use paged API for larger sets)
        List<Medicine> medicines = medicineRepository
                .findByPharmacyId(pharmacyId, false, PageRequest.of(0, 1000))
                .getContent();

        return new PharmacyInventory(pharmacyId, medicines.size(), medicines);
    }
}