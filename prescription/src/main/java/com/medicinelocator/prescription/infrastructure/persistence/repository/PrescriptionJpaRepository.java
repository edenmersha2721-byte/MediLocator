package com.medicinelocator.prescription.infrastructure.persistence.repository;

import com.medicinelocator.prescription.infrastructure.persistence.entity.PrescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionJpaRepository extends JpaRepository<PrescriptionEntity, UUID> {

    List<PrescriptionEntity> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    @Query("SELECT p FROM PrescriptionEntity p LEFT JOIN FETCH p.items WHERE p.id = :id")
    Optional<PrescriptionEntity> findByIdWithItems(@Param("id") UUID id);
}