package com.medicinelocator.inventory.application.command.handler;

import com.medicinelocator.inventory.application.command.DeductStockCommand;
import com.medicinelocator.inventory.application.service.PharmacyInventoryService;
import com.medicinelocator.inventory.domain.exception.InventoryNotFoundException;
import com.medicinelocator.inventory.domain.model.PharmacyInventory;
import com.medicinelocator.inventory.infrastructure.redis.InventoryCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeductStockHandler {

    private static final Logger log = LoggerFactory.getLogger(DeductStockHandler.class);

    private final PharmacyInventoryService inventoryService;
    private final InventoryCacheService cacheService;

    public DeductStockHandler(PharmacyInventoryService inventoryService,
                              InventoryCacheService cacheService) {
        this.inventoryService = inventoryService;
        this.cacheService = cacheService;
    }

    @Transactional
    public PharmacyInventory handle(DeductStockCommand command) {
        PharmacyInventory inventory = inventoryService
                .findByPharmacyIdAndMedicineId(command.getPharmacyId(), command.getMedicineId())
                .orElseThrow(() -> new InventoryNotFoundException(command.getPharmacyId(), command.getMedicineId()));

        inventory.deductStock(command.getAmount());
        PharmacyInventory updated = inventoryService.update(inventory);
        cacheService.evictInventoryCache(command.getPharmacyId());
        log.info("Stock deducted: pharmacyId={} medicineId={} amount={}",
                command.getPharmacyId(), command.getMedicineId(), command.getAmount());
        return updated;
    }
}