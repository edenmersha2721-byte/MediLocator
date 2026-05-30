package com.medicinelocator.inventory.application.command.handler;

import com.medicinelocator.inventory.application.command.AddInventoryCommand;
import com.medicinelocator.inventory.application.service.MedicineService;
import com.medicinelocator.inventory.application.service.PharmacyInventoryService;
import com.medicinelocator.inventory.domain.exception.DuplicateInventoryException;
import com.medicinelocator.inventory.domain.exception.MedicineNotFoundException;
import com.medicinelocator.inventory.domain.model.PharmacyInventory;
import com.medicinelocator.inventory.infrastructure.redis.InventoryCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AddInventoryHandler {

    private static final Logger log = LoggerFactory.getLogger(AddInventoryHandler.class);

    private final PharmacyInventoryService inventoryService;
    private final MedicineService medicineService;
    private final InventoryCacheService cacheService;

    public AddInventoryHandler(PharmacyInventoryService inventoryService,
                               MedicineService medicineService,
                               InventoryCacheService cacheService) {
        this.inventoryService = inventoryService;
        this.medicineService = medicineService;
        this.cacheService = cacheService;
    }

    @Transactional
    public PharmacyInventory handle(AddInventoryCommand command) {
        if (!medicineService.existsById(command.getMedicineId())) {
            throw new MedicineNotFoundException(command.getMedicineId());
        }

        if (inventoryService.existsByPharmacyIdAndMedicineId(command.getPharmacyId(), command.getMedicineId())) {
            throw new DuplicateInventoryException(
                    "Inventory already exists for pharmacyId: " + command.getPharmacyId()
                            + " and medicineId: " + command.getMedicineId()
            );
        }

        boolean available = command.getQuantity() > 0;

        PharmacyInventory inventory = new PharmacyInventory(
                UUID.randomUUID(),
                command.getPharmacyId(),
                command.getMedicineId(),
                command.getQuantity(),
                command.getUnitPrice(),
                available,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        PharmacyInventory saved = inventoryService.save(inventory);
        cacheService.evictInventoryCache(command.getPharmacyId());
        log.info("Inventory added: pharmacyId={} medicineId={} qty={}",
                command.getPharmacyId(), command.getMedicineId(), command.getQuantity());
        return saved;
    }
}