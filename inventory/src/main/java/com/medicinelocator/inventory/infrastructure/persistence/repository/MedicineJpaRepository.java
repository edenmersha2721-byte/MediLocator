package com.medicinelocator.inventory.infrastructure.persistence.repository;

import com.medicinelocator.inventory.infrastructure.persistence.entity.MedicineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MedicineJpaRepository extends JpaRepository<MedicineEntity, UUID> {

    @Query("""
            SELECT m FROM MedicineEntity m
            WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:brandName IS NULL OR LOWER(m.brandName) LIKE LOWER(CONCAT('%', :brandName, '%')))
            AND (:genericName IS NULL OR LOWER(m.genericName) LIKE LOWER(CONCAT('%', :genericName, '%')))
            AND (:categoryId IS NULL OR m.categoryId = :categoryId)
            AND (:requiresPrescription IS NULL OR m.requiresPrescription = :requiresPrescription)
            AND (:activeOnly IS NULL OR m.active = :activeOnly)
            """)
    Page<MedicineEntity> searchMedicines(
            @Param("name") String name,
            @Param("brandName") String brandName,
            @Param("genericName") String genericName,
            @Param("categoryId") UUID categoryId,
            @Param("requiresPrescription") Boolean requiresPrescription,
            @Param("activeOnly") Boolean activeOnly,
            Pageable pageable
    );

    boolean existsByCategoryId(UUID categoryId);
}