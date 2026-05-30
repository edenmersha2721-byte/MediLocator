package com.medicinelocator.inventory.application.command.handler;

import com.medicinelocator.inventory.application.command.UpdateMedicineCommand;
import com.medicinelocator.inventory.application.service.CategoryService;
import com.medicinelocator.inventory.application.service.MedicineService;
import com.medicinelocator.inventory.domain.exception.CategoryNotFoundException;
import com.medicinelocator.inventory.domain.exception.MedicineNotFoundException;
import com.medicinelocator.inventory.domain.model.Medicine;
import com.medicinelocator.inventory.infrastructure.redis.InventoryCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateMedicineHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateMedicineHandler.class);

    private final MedicineService medicineService;
    private final CategoryService categoryService;
    private final InventoryCacheService cacheService;

    public UpdateMedicineHandler(MedicineService medicineService,
                                 CategoryService categoryService,
                                 InventoryCacheService cacheService) {
        this.medicineService = medicineService;
        this.categoryService = categoryService;
        this.cacheService = cacheService;
    }

    @Transactional
    public Medicine handle(UpdateMedicineCommand command) {
        Medicine medicine = medicineService.findById(command.getMedicineId())
                .orElseThrow(() -> new MedicineNotFoundException(command.getMedicineId()));

        if (!categoryService.existsById(command.getCategoryId())) {
            throw new CategoryNotFoundException(command.getCategoryId());
        }

        medicine.updateDetails(
                command.getName(),
                command.getGenericName(),
                command.getBrandName(),
                command.getDescription(),
                command.getCategoryId(),
                command.isRequiresPrescription()
        );
        medicine.setActive(command.isActive());

        Medicine updated = medicineService.update(medicine);
        cacheService.evictMedicineCache(updated.getId());
        log.info("Medicine updated: id={}", updated.getId());
        return updated;
    }
}