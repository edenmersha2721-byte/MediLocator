package com.medicinelocator.inventory.infrastructure.persistence.repository;

import com.medicinelocator.inventory.infrastructure.persistence.entity.PharmacyInventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PharmacyInventoryJpaRepository extends JpaRepository<PharmacyInventoryEntity, UUID> {

    Optional<PharmacyInventoryEntity> findByPharmacyIdAndMedicineId(UUID pharmacyId, UUID medicineId);

    boolean existsByPharmacyIdAndMedicineId(UUID pharmacyId, UUID medicineId);

    Page<PharmacyInventoryEntity> findByPharmacyId(UUID pharmacyId, Pageable pageable);

    Page<PharmacyInventoryEntity> findByMedicineIdAndAvailableTrue(UUID medicineId, Pageable pageable);

    List<PharmacyInventoryEntity> findByMedicineId(UUID medicineId);

    boolean existsByMedicineId(UUID medicineId);
}