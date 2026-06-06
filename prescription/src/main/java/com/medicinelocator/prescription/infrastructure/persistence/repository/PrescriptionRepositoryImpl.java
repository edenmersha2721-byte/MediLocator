package com.medicinelocator.prescription.infrastructure.persistence.repository;

import com.medicinelocator.prescription.application.service.PrescriptionService;
import com.medicinelocator.prescription.domain.model.ExtractedMedicine;
import com.medicinelocator.prescription.domain.model.Prescription;
import com.medicinelocator.prescription.infrastructure.persistence.entity.PrescriptionEntity;
import com.medicinelocator.prescription.infrastructure.persistence.entity.PrescriptionItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PrescriptionRepositoryImpl implements PrescriptionService {

    private final PrescriptionJpaRepository jpaRepository;

    public PrescriptionRepositoryImpl(PrescriptionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Prescription save(Prescription prescription) {
        PrescriptionEntity entity = toEntity(prescription);
        PrescriptionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Prescription> findById(UUID id) {
        return jpaRepository.findByIdWithItems(id).map(this::toDomain);
    }

    @Override
    public Prescription update(Prescription prescription) {
        PrescriptionEntity entity = jpaRepository.findById(prescription.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Prescription not found for update: " + prescription.getId()));
        updateEntity(entity, prescription);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<Prescription> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    // ─── Mapping ──────────────────────────────────────────────────────────────

    private PrescriptionEntity toEntity(Prescription prescription) {
        PrescriptionEntity entity = new PrescriptionEntity();
        entity.setId(prescription.getId());
        mapDomainToEntity(prescription, entity);
        return entity;
    }

    private void updateEntity(PrescriptionEntity entity, Prescription prescription) {
        mapDomainToEntity(prescription, entity);
    }

    private void mapDomainToEntity(Prescription prescription, PrescriptionEntity entity) {
        entity.setCustomerId(prescription.getCustomerId());
        entity.setImageUrl(prescription.getImageUrl());
        entity.setRawText(prescription.getRawText());
        entity.setStatus(prescription.getStatus());
        entity.setLatitude(prescription.getLatitude());
        entity.setLongitude(prescription.getLongitude());

        // Sync items
        entity.getItems().clear();
        for (ExtractedMedicine medicine : prescription.getExtractedMedicines()) {
            PrescriptionItemEntity item = new PrescriptionItemEntity();
            item.setId(medicine.getId());
            item.setPrescription(entity);
            item.setMedicineName(medicine.getMedicineName());
            entity.getItems().add(item);
        }
    }

    private Prescription toDomain(PrescriptionEntity entity) {
        List<ExtractedMedicine> medicines = entity.getItems().stream()
                .map(item -> new ExtractedMedicine(
                        item.getId(),
                        entity.getId(),
                        item.getMedicineName()))
                .toList();

        return new Prescription(
                entity.getId(),
                entity.getCustomerId(),
                entity.getImageUrl(),
                entity.getRawText(),
                entity.getStatus(),
                entity.getLatitude(),
                entity.getLongitude(),
                medicines,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}