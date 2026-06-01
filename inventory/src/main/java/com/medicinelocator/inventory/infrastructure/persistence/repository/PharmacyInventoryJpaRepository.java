package com.medicinelocator.inventory.infrastructure.persistence.repository;

import com.medicinelocator.inventory.infrastructure.persistence.entity.MedicineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Kept to satisfy the folder-structure contract.
 * In this pharmacy-owned model, all inventory queries go through
 * {@link MedicineJpaRepository} — the single source of truth.
 *
 * This repository is a thin alias for pharmacy-scoped count/existence checks
 * that aren't already on MedicineJpaRepository.
 */
@Repository
public interface PharmacyInventoryJpaRepository extends JpaRepository<MedicineEntity, UUID> {

    long countByPharmacyIdAndActiveTrue(UUID pharmacyId);

    boolean existsByPharmacyId(UUID pharmacyId);
}