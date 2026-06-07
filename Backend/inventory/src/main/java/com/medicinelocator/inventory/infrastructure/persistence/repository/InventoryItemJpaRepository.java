package com.medicinelocator.inventory.infrastructure.persistence.repository;

import com.medicinelocator.inventory.infrastructure.persistence.entity.MedicineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * InventoryItem queries — projections onto MedicineEntity filtered by
 * availability and expiry. Kept as a separate interface per the folder contract.
 */
@Repository
public interface InventoryItemJpaRepository extends JpaRepository<MedicineEntity, UUID> {

    Page<MedicineEntity> findByPharmacyIdAndAvailableTrueAndActiveTrue(UUID pharmacyId,
                                                                       Pageable pageable);

    List<MedicineEntity> findByExpiryDateBeforeAndActiveTrue(LocalDate date);

    List<MedicineEntity> findByPharmacyIdAndStockQuantityLessThanAndActiveTrue(UUID pharmacyId,
                                                                               int threshold);
}