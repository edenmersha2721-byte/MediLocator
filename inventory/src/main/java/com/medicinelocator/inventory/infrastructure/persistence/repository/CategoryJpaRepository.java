package com.medicinelocator.inventory.infrastructure.persistence.repository;

import com.medicinelocator.inventory.infrastructure.persistence.entity.MedicineCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryJpaRepository extends JpaRepository<MedicineCategoryEntity, UUID> {

    boolean existsByNameIgnoreCase(String name);

    Optional<MedicineCategoryEntity> findByNameIgnoreCase(String name);

    List<MedicineCategoryEntity> findByActiveTrue();
}