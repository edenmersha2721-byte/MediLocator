package com.medicinelocator.inventory.application.command.handler;

import com.medicinelocator.inventory.application.command.UpdateStockCommand;
import com.medicinelocator.inventory.application.service.MedicineService;
import com.medicinelocator.inventory.application.service.PharmacyInventoryService;
import com.medicinelocator.inventory.domain.exception.InventoryNotFoundException;
import com.medicinelocator.inventory.domain.exception.MedicineNotFoundException;
import com.medicinelocator.inventory.domain.model.PharmacyInventory;
import com.medicinelocator.inventory.infrastructure.redis.InventoryCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateStockHandler {

    private static final Logger log = LoggerFactory.getLogger(UpdateStockHandler.class);

    private final PharmacyInventoryService inventoryService;
    private final MedicineService medicineService;
    private final InventoryCacheService cacheService;

    public UpdateStockHandler(PharmacyInventoryService inventoryService,
                              MedicineService medicineService,
                              InventoryCacheService cacheService) {
        this.inventoryService = inventoryService;
        this.medicineService = medicineService;
        this.cacheService = cacheService;
    }

    @Transactional
    public PharmacyInventory handle(UpdateStockCommand command) {
        if (!medicineService.existsById(command.getMedicineId())) {
            throw new MedicineNotFoundException(command.getMedicineId());
        }

        PharmacyInventory inventory = inventoryService
                .findByPharmacyIdAndMedicineId(command.getPharmacyId(), command.getMedicineId())
                .orElseThrow(() -> new InventoryNotFoundException(command.getPharmacyId(), command.getMedicineId()));

        inventory.updateStock(command.getQuantity(), command.getUnitPrice());
        PharmacyInventory updated = inventoryService.update(inventory);
        cacheService.evictInventoryCache(command.getPharmacyId());
        log.info("Stock updated: pharmacyId={} medicineId={} qty={}",
                command.getPharmacyId(), command.getMedicineId(), command.getQuantity());
        return updated;
    }
}