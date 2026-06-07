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
        Medicine medicine = medicineService
                .findByIdAndPharmacyId(command.getMedicineId(), command.getPharmacyId())
                .orElseThrow(() -> new MedicineNotFoundException(
                        command.getPharmacyId(), command.getMedicineId()));

        medicine.deactivate();
        medicineService.update(medicine);
        cacheService.evictPharmacyInventoryCache(command.getPharmacyId());
        log.info("Medicine removed (deactivated): medicineId={} pharmacyId={}",
                command.getMedicineId(), command.getPharmacyId());
    }
}