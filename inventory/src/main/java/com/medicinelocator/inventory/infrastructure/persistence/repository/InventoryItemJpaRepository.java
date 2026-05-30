package com.medicinelocator.inventory.infrastructure.persistence.repository;

import com.medicinelocator.inventory.infrastructure.persistence.entity.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryItemJpaRepository extends JpaRepository<InventoryItemEntity, UUID> {

    List<InventoryItemEntity> findByPharmacyIdAndMedicineId(UUID pharmacyId, UUID medicineId);

    List<InventoryItemEntity> findByExpiryDateBefore(LocalDate date);

    List<InventoryItemEntity> findByPharmacyIdAndAvailableTrue(UUID pharmacyId);
}