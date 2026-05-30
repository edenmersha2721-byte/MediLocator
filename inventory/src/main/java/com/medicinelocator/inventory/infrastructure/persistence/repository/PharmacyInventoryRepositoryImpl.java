package com.medicinelocator.inventory.infrastructure.persistence.repository;

import com.medicinelocator.inventory.application.service.PharmacyInventoryService;
import com.medicinelocator.inventory.domain.model.PharmacyInventory;
import com.medicinelocator.inventory.infrastructure.persistence.entity.PharmacyInventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PharmacyInventoryRepositoryImpl implements PharmacyInventoryService {

    private final PharmacyInventoryJpaRepository jpaRepository;

    public PharmacyInventoryRepositoryImpl(PharmacyInventoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PharmacyInventory save(PharmacyInventory inventory) {
        PharmacyInventoryEntity entity = toEntity(inventory);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<PharmacyInventory> findByPharmacyIdAndMedicineId(UUID pharmacyId, UUID medicineId) {
        return jpaRepository.findByPharmacyIdAndMedicineId(pharmacyId, medicineId).map(this::toDomain);
    }

    @Override
    public boolean existsByPharmacyIdAndMedicineId(UUID pharmacyId, UUID medicineId) {
        return jpaRepository.existsByPharmacyIdAndMedicineId(pharmacyId, medicineId);
    }

    @Override
    public PharmacyInventory update(PharmacyInventory inventory) {
        PharmacyInventoryEntity entity = jpaRepository.findById(inventory.getId())
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + inventory.getId()));
        updateEntity(entity, inventory);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Page<PharmacyInventory> findByPharmacyId(UUID pharmacyId, Pageable pageable) {
        return jpaRepository.findByPharmacyId(pharmacyId, pageable).map(this::toDomain);
    }

    @Override
    public Page<PharmacyInventory> findAvailableByMedicineId(UUID medicineId, Pageable pageable) {
        return jpaRepository.findByMedicineIdAndAvailableTrue(medicineId, pageable).map(this::toDomain);
    }

    @Override
    public List<PharmacyInventory> findAllByMedicineId(UUID medicineId) {
        return jpaRepository.findByMedicineId(medicineId).stream().map(this::toDomain).toList();
    }

    private PharmacyInventoryEntity toEntity(PharmacyInventory inventory) {
        PharmacyInventoryEntity entity = new PharmacyInventoryEntity();
        entity.setId(inventory.getId());
        entity.setPharmacyId(inventory.getPharmacyId());
        entity.setMedicineId(inventory.getMedicineId());
        entity.setQuantity(inventory.getQuantity());
        entity.setUnitPrice(inventory.getUnitPrice());
        entity.setAvailable(inventory.isAvailable());
        return entity;
    }

    private void updateEntity(PharmacyInventoryEntity entity, PharmacyInventory inventory) {
        entity.setQuantity(inventory.getQuantity());
        entity.setUnitPrice(inventory.getUnitPrice());
        entity.setAvailable(inventory.isAvailable());
    }

    private PharmacyInventory toDomain(PharmacyInventoryEntity entity) {
        return new PharmacyInventory(
                entity.getId(),
                entity.getPharmacyId(),
                entity.getMedicineId(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.isAvailable(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}