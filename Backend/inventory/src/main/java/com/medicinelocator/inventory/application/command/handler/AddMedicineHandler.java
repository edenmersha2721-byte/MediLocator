package com.medicinelocator.inventory.application.command.handler;

import com.medicinelocator.inventory.application.command.AddMedicineCommand;
import com.medicinelocator.inventory.application.service.MedicineService;
import com.medicinelocator.inventory.domain.exception.DuplicateMedicineException;
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
    private final InventoryCacheService cacheService;

    public AddMedicineHandler(MedicineService medicineService,
                              InventoryCacheService cacheService) {
        this.medicineService = medicineService;
        this.cacheService = cacheService;
    }

    @Transactional
    public Medicine handle(AddMedicineCommand command) {
        if (medicineService.existsByPharmacyIdAndMedicineName(
                command.getPharmacyId(), command.getMedicineName())) {
            throw new DuplicateMedicineException(
                    "Medicine '" + command.getMedicineName()
                            + "' already exists in your inventory");
        }

        boolean available = command.getStockQuantity() > 0;

        Medicine medicine = new Medicine(
                UUID.randomUUID(),
                command.getPharmacyId(),
                command.getMedicineName(),
                command.getGenericName(),
                command.getBrandName(),
                command.getCategory(),
                command.getDescription(),
                command.getPrice(),
                command.getStockQuantity(),
                available,
                command.isRequiresPrescription(),
                command.getExpiryDate(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Medicine saved = medicineService.save(medicine);
        cacheService.evictPharmacyInventoryCache(command.getPharmacyId());
        log.info("Medicine added: id={} pharmacyId={} name={}",
                saved.getId(), saved.getPharmacyId(), saved.getMedicineName());
        return saved;
    }
}