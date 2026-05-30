package com.medicinelocator.inventory.application.command.handler;

import com.medicinelocator.inventory.application.command.RemoveMedicineCommand;
import com.medicinelocator.inventory.application.service.MedicineService;
import com.medicinelocator.inventory.domain.exception.MedicineNotFoundException;
import com.medicinelocator.inventory.domain.model.Medicine;
import com.medicinelocator.inventory.infrastructure.redis.InventoryCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RemoveMedicineHandler {

    private static final Logger log = LoggerFactory.getLogger(RemoveMedicineHandler.class);

    private final MedicineService medicineService;
    private final InventoryCacheService cacheService;

    public RemoveMedicineHandler(MedicineService medicineService,
                                 InventoryCacheService cacheService) {
        this.medicineService = medicineService;
        this.cacheService = cacheService;
    }

    @Transactional
    public void handle(RemoveMedicineCommand command) {
        Medicine medicine = medicineService.findById(command.getMedicineId())
                .orElseThrow(() -> new MedicineNotFoundException(command.getMedicineId()));

        if (medicineService.hasInventory(command.getMedicineId())) {
            // Soft-delete: deactivate instead of physical delete
            medicine.deactivate();
            medicineService.update(medicine);
            log.info("Medicine deactivated (has inventory): id={}", command.getMedicineId());
        } else {
            medicine.deactivate();
            medicineService.update(medicine);
            log.info("Medicine deactivated: id={}", command.getMedicineId());
        }

        cacheService.evictMedicineCache(command.getMedicineId());
    }
}