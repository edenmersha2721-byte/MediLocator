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
    private final PharmacyInventoryJpaRepository inventoryJpaRepository;

    public MedicineRepositoryImpl(MedicineJpaRepository jpaRepository,
                                  PharmacyInventoryJpaRepository inventoryJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.inventoryJpaRepository = inventoryJpaRepository;
    }

    @Override
    public Medicine save(Medicine medicine) {
        MedicineEntity entity = toEntity(medicine);
        MedicineEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Medicine> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Medicine update(Medicine medicine) {
        MedicineEntity entity = jpaRepository.findById(medicine.getId())
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found: " + medicine.getId()));
        updateEntity(entity, medicine);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Page<Medicine> searchMedicines(String name, String brandName, String genericName,
                                          UUID categoryId, Boolean requiresPrescription,
                                          Boolean activeOnly, Pageable pageable) {
        return jpaRepository.searchMedicines(name, brandName, genericName,
                        categoryId, requiresPrescription, activeOnly, pageable)
                .map(this::toDomain);
    }

    @Override
    public boolean hasInventory(UUID medicineId) {
        return inventoryJpaRepository.existsByMedicineId(medicineId);
    }

    private MedicineEntity toEntity(Medicine medicine) {
        MedicineEntity entity = new MedicineEntity();
        entity.setId(medicine.getId());
        entity.setName(medicine.getName());
        entity.setGenericName(medicine.getGenericName());
        entity.setBrandName(medicine.getBrandName());
        entity.setDescription(medicine.getDescription());
        entity.setCategoryId(medicine.getCategoryId());
        entity.setRequiresPrescription(medicine.isRequiresPrescription());
        entity.setActive(medicine.isActive());
        return entity;
    }

    private void updateEntity(MedicineEntity entity, Medicine medicine) {
        entity.setName(medicine.getName());
        entity.setGenericName(medicine.getGenericName());
        entity.setBrandName(medicine.getBrandName());
        entity.setDescription(medicine.getDescription());
        entity.setCategoryId(medicine.getCategoryId());
        entity.setRequiresPrescription(medicine.isRequiresPrescription());
        entity.setActive(medicine.isActive());
    }

    private Medicine toDomain(MedicineEntity entity) {
        return new Medicine(
                entity.getId(),
                entity.getName(),
                entity.getGenericName(),
                entity.getBrandName(),
                entity.getDescription(),
                entity.getCategoryId(),
                entity.isRequiresPrescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}