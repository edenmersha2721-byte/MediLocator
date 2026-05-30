package com.medicinelocator.inventory.application.command.handler;

import com.medicinelocator.inventory.application.command.AddMedicineCommand;
import com.medicinelocator.inventory.application.service.CategoryService;
import com.medicinelocator.inventory.application.service.MedicineService;
import com.medicinelocator.inventory.domain.exception.CategoryNotFoundException;
import com.medicinelocator.inventory.domain.model.Medicine;
import com.medicinelocator.inventory.infrastructure.redis.InventoryCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AddMedicineHandler {

    private static final Logger log = LoggerFactory.getLogger(AddMedicineHandler.class);

    private final MedicineService medicineService;
    private final CategoryService categoryService;
    private final InventoryCacheService cacheService;

    public AddMedicineHandler(MedicineService medicineService,
                              CategoryService categoryService,
                              InventoryCacheService cacheService) {
        this.medicineService = medicineService;
        this.categoryService = categoryService;
        this.cacheService = cacheService;
    }

    @Transactional
    public Medicine handle(AddMedicineCommand command) {
        if (!categoryService.existsById(command.getCategoryId())) {
            throw new CategoryNotFoundException(command.getCategoryId());
        }

        Medicine medicine = new Medicine(
                UUID.randomUUID(),
                command.getName(),
                command.getGenericName(),
                command.getBrandName(),
                command.getDescription(),
                command.getCategoryId(),
                command.isRequiresPrescription(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Medicine saved = medicineService.save(medicine);
        cacheService.evictMedicineCache(saved.getId());
        log.info("Medicine added: id={} name={}", saved.getId(), saved.getName());
        return saved;
    }
}