package com.medicinelocator.inventory.application.command.handler;

import com.medicinelocator.inventory.application.command.DeductStockCommand;
import com.medicinelocator.inventory.application.service.MedicineService;
import com.medicinelocator.inventory.domain.exception.MedicineNotFoundException;
import com.medicinelocator.inventory.domain.model.Medicine;
import com.medicinelocator.inventory.infrastructure.redis.InventoryCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeductStockHandler {

    private static final Logger log = LoggerFactory.getLogger(DeductStockHandler.class);

    private final MedicineService medicineService;
    private final InventoryCacheService cacheService;

    public DeductStockHandler(MedicineService medicineService,
                              InventoryCacheService cacheService) {
        this.medicineService = medicineService;
        this.cacheService = cacheService;
    }

    @Transactional
    public Medicine handle(DeductStockCommand command) {
        Medicine medicine = medicineService
                .findByIdAndPharmacyId(command.getMedicineId(), command.getPharmacyId())
                .orElseThrow(() -> new MedicineNotFoundException(
                        command.getPharmacyId(), command.getMedicineId()));

        medicine.deductStock(command.getAmount());
        Medicine updated = medicineService.update(medicine);
        cacheService.evictPharmacyInventoryCache(command.getPharmacyId());
        log.info("Stock deducted: medicineId={} pharmacyId={} amount={}",
                command.getMedicineId(), command.getPharmacyId(), command.getAmount());
        return updated;
    }
}