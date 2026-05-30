package com.medicinelocator.inventory.application.service;

import com.medicinelocator.inventory.domain.model.MedicineCategoryDomain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryService {

    MedicineCategoryDomain save(MedicineCategoryDomain category);

    Optional<MedicineCategoryDomain> findById(UUID id);

    boolean existsByName(String name);

    boolean existsById(UUID id);

    MedicineCategoryDomain update(MedicineCategoryDomain category);

    List<MedicineCategoryDomain> findAll(boolean activeOnly);
}