package com.medicinelocator.inventory.application.command.handler;

import com.medicinelocator.inventory.application.command.CreateCategoryCommand;
import com.medicinelocator.inventory.application.service.CategoryService;
import com.medicinelocator.inventory.domain.model.MedicineCategoryDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CreateCategoryHandler {

    private static final Logger log = LoggerFactory.getLogger(CreateCategoryHandler.class);

    private final CategoryService categoryService;

    public CreateCategoryHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Transactional
    public MedicineCategoryDomain handle(CreateCategoryCommand command) {
        if (categoryService.existsByName(command.getName())) {
            throw new IllegalArgumentException("Category with name '" + command.getName() + "' already exists");
        }

        MedicineCategoryDomain category = new MedicineCategoryDomain(
                UUID.randomUUID(),
                command.getName(),
                command.getDescription(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        MedicineCategoryDomain saved = categoryService.save(category);
        log.info("Category created: id={} name={}", saved.getId(), saved.getName());
        return saved;
    }
}