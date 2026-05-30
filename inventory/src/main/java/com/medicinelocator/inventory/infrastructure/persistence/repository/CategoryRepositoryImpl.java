package com.medicinelocator.inventory.infrastructure.persistence.repository;

import com.medicinelocator.inventory.application.service.CategoryService;
import com.medicinelocator.inventory.domain.model.MedicineCategoryDomain;
import com.medicinelocator.inventory.infrastructure.persistence.entity.MedicineCategoryEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryRepositoryImpl implements CategoryService {

    private final CategoryJpaRepository jpaRepository;

    public CategoryRepositoryImpl(CategoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MedicineCategoryDomain save(MedicineCategoryDomain category) {
        MedicineCategoryEntity entity = toEntity(category);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<MedicineCategoryDomain> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public MedicineCategoryDomain update(MedicineCategoryDomain category) {
        MedicineCategoryEntity entity = jpaRepository.findById(category.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + category.getId()));
        entity.setName(category.getName());
        entity.setDescription(category.getDescription());
        entity.setActive(category.isActive());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<MedicineCategoryDomain> findAll(boolean activeOnly) {
        if (activeOnly) {
            return jpaRepository.findByActiveTrue().stream().map(this::toDomain).toList();
        }
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private MedicineCategoryEntity toEntity(MedicineCategoryDomain category) {
        MedicineCategoryEntity entity = new MedicineCategoryEntity();
        entity.setId(category.getId());
        entity.setName(category.getName());
        entity.setDescription(category.getDescription());
        entity.setActive(category.isActive());
        return entity;
    }

    private MedicineCategoryDomain toDomain(MedicineCategoryEntity entity) {
        return new MedicineCategoryDomain(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}