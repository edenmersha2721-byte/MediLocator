package com.medicinelocator.inventory.infrastructure.persistence.repository;

import com.medicinelocator.inventory.infrastructure.persistence.entity.MedicineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicineJpaRepository extends JpaRepository<MedicineEntity, UUID> {

    Optional<MedicineEntity> findByIdAndPharmacyId(UUID id, UUID pharmacyId);

    boolean existsByPharmacyIdAndMedicineNameIgnoreCase(UUID pharmacyId, String medicineName);

    Page<MedicineEntity> findByPharmacyIdAndActiveTrue(UUID pharmacyId, Pageable pageable);

    Page<MedicineEntity> findByPharmacyIdAndActiveTrueAndAvailableTrue(UUID pharmacyId, Pageable pageable);

    /**
     * Cross-pharmacy search using PostgreSQL LIKE on indexed columns.
     * All parameters are optional — null means "no filter applied".
     */
    @Query("""
            SELECT m FROM MedicineEntity m
            WHERE m.active = true
            AND (:medicineName IS NULL
                 OR LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :medicineName, '%')))
            AND (:brandName IS NULL
                 OR LOWER(m.brandName) LIKE LOWER(CONCAT('%', :brandName, '%')))
            AND (:genericName IS NULL
                 OR LOWER(m.genericName) LIKE LOWER(CONCAT('%', :genericName, '%')))
            AND (:category IS NULL
                 OR LOWER(m.category) LIKE LOWER(CONCAT('%', :category, '%')))
            AND (:availableOnly IS NULL OR m.available = :availableOnly)
            AND (:requiresPrescription IS NULL
                 OR m.requiresPrescription = :requiresPrescription)
            """)
    Page<MedicineEntity> searchAcrossPharmacies(
            @Param("medicineName") String medicineName,
            @Param("brandName") String brandName,
            @Param("genericName") String genericName,
            @Param("category") String category,
            @Param("availableOnly") Boolean availableOnly,
            @Param("requiresPrescription") Boolean requiresPrescription,
            Pageable pageable
    );
}