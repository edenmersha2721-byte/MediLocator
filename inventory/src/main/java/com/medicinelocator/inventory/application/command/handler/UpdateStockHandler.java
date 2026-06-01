package com.medicinelocator.inventory.application.command.handler;

import com.medicinelocator.inventory.application.command.UpdateStockCommand;
import com.medicinelocator.inventory.application.service.MedicineService;
import com.medicinelocator.inventory.domain.exception.MedicineNotFoundException;
import com.medicinelocator.inventory.domain.model.Medicine;
import com.medicinelocator.inventory.infrastructure.redis.InventoryCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateStockHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateStockHandler.class);

    private final MedicineService medicineService;
    private final InventoryCacheService cacheService;

    public UpdateStockHandler(MedicineService medicineService,
                              InventoryCacheService cacheService) {
        this.medicineService = medicineService;
        this.cacheService = cacheService;
    }

    @Transactional
    public Medicine handle(UpdateStockCommand command) {
        Medicine medicine = medicineService
                .findByIdAndPharmacyId(command.getMedicineId(), command.getPharmacyId())
                .orElseThrow(() -> new MedicineNotFoundException(
                        command.getPharmacyId(), command.getMedicineId()));

        medicine.updateDetails(
                command.getMedicineName(),
                command.getGenericName(),
                command.getBrandName(),
                command.getCategory(),
                command.getDescription(),
                command.getPrice(),
                command.getStockQuantity(),
                command.isRequiresPrescription(),
                command.getExpiryDate()
        );

        Medicine updated = medicineService.update(medicine);
        cacheService.evictPharmacyInventoryCache(command.getPharmacyId());
        log.info("Medicine updated: id={} pharmacyId={}", updated.getId(), updated.getPharmacyId());
        return updated;
    }
}