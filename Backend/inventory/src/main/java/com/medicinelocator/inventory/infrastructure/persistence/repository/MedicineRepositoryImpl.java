package com.medicinelocator.inventory.infrastructure.persistence.repository;

import com.medicinelocator.inventory.application.service.MedicineService;
import com.medicinelocator.inventory.domain.model.Medicine;
import com.medicinelocator.inventory.infrastructure.persistence.entity.MedicineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class MedicineRepositoryImpl implements MedicineService {

    private final MedicineJpaRepository jpaRepository;

    public MedicineRepositoryImpl(MedicineJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Medicine save(Medicine medicine) {
        MedicineEntity entity = toEntity(medicine);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Medicine> findByIdAndPharmacyId(UUID medicineId, UUID pharmacyId) {
        return jpaRepository.findByIdAndPharmacyId(medicineId, pharmacyId).map(this::toDomain);
    }

    @Override
    public boolean existsByPharmacyIdAndMedicineName(UUID pharmacyId, String medicineName) {
        return jpaRepository.existsByPharmacyIdAndMedicineNameIgnoreCase(pharmacyId, medicineName);
    }

    @Override
    public Medicine update(Medicine medicine) {
        MedicineEntity entity = jpaRepository.findById(medicine.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Medicine not found for update: " + medicine.getId()));
        mapDomainToEntity(medicine, entity);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deleteById(UUID medicineId) {
        jpaRepository.deleteById(medicineId);
    }

    @Override
    public Page<Medicine> findByPharmacyId(UUID pharmacyId, boolean availableOnly, Pageable pageable) {
        if (availableOnly) {
            return jpaRepository
                    .findByPharmacyIdAndActiveTrueAndAvailableTrue(pharmacyId, pageable)
                    .map(this::toDomain);
        }
        return jpaRepository.findByPharmacyIdAndActiveTrue(pharmacyId, pageable).map(this::toDomain);
    }

    @Override
    public Page<Medicine> searchAcrossPharmacies(String medicineName, String brandName,
                                                 String genericName, String category,
                                                 Boolean availableOnly,
                                                 Boolean requiresPrescription,
                                                 Pageable pageable) {
        return jpaRepository.searchAcrossPharmacies(
                        medicineName, brandName, genericName,
                        category, availableOnly, requiresPrescription, pageable)
                .map(this::toDomain);
    }

    // ─── Mapping ──────────────────────────────────────────────────────────────

    private MedicineEntity toEntity(Medicine medicine) {
        MedicineEntity entity = new MedicineEntity();
        entity.setId(medicine.getId());
        mapDomainToEntity(medicine, entity);
        return entity;
    }

    private void mapDomainToEntity(Medicine medicine, MedicineEntity entity) {
        entity.setPharmacyId(medicine.getPharmacyId());
        entity.setMedicineName(medicine.getMedicineName());
        entity.setGenericName(medicine.getGenericName());
        entity.setBrandName(medicine.getBrandName());
        entity.setCategory(medicine.getCategory());
        entity.setDescription(medicine.getDescription());
        entity.setPrice(medicine.getPrice());
        entity.setStockQuantity(medicine.getStockQuantity());
        entity.setAvailable(medicine.isAvailable());
        entity.setRequiresPrescription(medicine.isRequiresPrescription());
        entity.setExpiryDate(medicine.getExpiryDate());
        entity.setActive(medicine.isActive());
    }

    private Medicine toDomain(MedicineEntity entity) {
        return new Medicine(
                entity.getId(),
                entity.getPharmacyId(),
                entity.getMedicineName(),
                entity.getGenericName(),
                entity.getBrandName(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStockQuantity(),
                entity.isAvailable(),
                entity.isRequiresPrescription(),
                entity.getExpiryDate(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}