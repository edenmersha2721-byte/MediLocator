package com.medicinelocator.auth.infrastructure.persistence.repository;

import com.medicinelocator.auth.infrastructure.persistence.entity.PharmacyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PharmacyJpaRepository extends JpaRepository<PharmacyEntity, UUID> {

    Optional<PharmacyEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}