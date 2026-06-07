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
     * Restructured evaluation statements to prevent Hibernate from defaulting parameters to bytea.
     */
    @Query("""
            SELECT m FROM MedicineEntity m
            WHERE m.active = true
            AND (LOWER(m.medicineName) LIKE LOWER(CONCAT('%', CAST(:medicineName AS string), '%')) 
                 OR CAST(:medicineName AS string) IS NULL)
            AND (LOWER(m.brandName) LIKE LOWER(CONCAT('%', CAST(:brandName AS string), '%')) 
                 OR CAST(:brandName AS string) IS NULL)
            AND (LOWER(m.genericName) LIKE LOWER(CONCAT('%', CAST(:genericName AS string), '%')) 
                 OR CAST(:genericName AS string) IS NULL)
            AND (LOWER(m.category) LIKE LOWER(CONCAT('%', CAST(:category AS string), '%')) 
                 OR CAST(:category AS string) IS NULL)
            AND (:availableOnly IS NULL OR m.available = :availableOnly)
            AND (:requiresPrescription IS NULL OR m.requiresPrescription = :requiresPrescription)
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