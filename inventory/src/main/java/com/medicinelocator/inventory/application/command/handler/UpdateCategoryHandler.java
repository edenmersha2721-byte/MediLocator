package com.medicinelocator.inventory.application.command.handler;

import com.medicinelocator.inventory.application.command.UpdateCategoryCommand;
import com.medicinelocator.inventory.application.service.CategoryService;
import com.medicinelocator.inventory.domain.exception.CategoryNotFoundException;
import com.medicinelocator.inventory.domain.model.MedicineCategoryDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateCategoryHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateCategoryHandler.class);

    private final CategoryService categoryService;

    public UpdateCategoryHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Transactional
    public MedicineCategoryDomain handle(UpdateCategoryCommand command) {
        MedicineCategoryDomain category = categoryService.findById(command.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.getCategoryId()));

        category.setName(command.getName());
        category.setDescription(command.getDescription());
        category.setActive(command.isActive());

        MedicineCategoryDomain updated = categoryService.update(category);
        log.info("Category updated: id={}", updated.getId());
        return updated;
    }
}