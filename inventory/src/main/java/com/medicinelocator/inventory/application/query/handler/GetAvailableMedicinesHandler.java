package com.medicinelocator.inventory.application.query.handler;

import com.medicinelocator.inventory.application.dto.response.MedicineAvailabilityResponse;
import com.medicinelocator.inventory.application.dto.response.PagedResponse;
import com.medicinelocator.inventory.application.mapper.InventoryMapper;
import com.medicinelocator.inventory.application.query.GetAvailableMedicinesQuery;
import com.medicinelocator.inventory.application.service.PharmacyInventoryService;
import com.medicinelocator.inventory.domain.model.PharmacyInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetAvailableMedicinesHandler {

    private final PharmacyInventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    public GetAvailableMedicinesHandler(PharmacyInventoryService inventoryService,
                                        InventoryMapper inventoryMapper) {
        this.inventoryService = inventoryService;
        this.inventoryMapper = inventoryMapper;
    }

    @Transactional(readOnly = true)
    public PagedResponse<MedicineAvailabilityResponse> handle(GetAvailableMedicinesQuery query) {
        PageRequest pageable = PageRequest.of(query.getPage(), query.getSize());
        Page<PharmacyInventory> page = inventoryService.findAvailableByMedicineId(query.getMedicineId(), pageable);

        return new PagedResponse<>(
                page.getContent().stream().map(inventoryMapper::toMedicineAvailabilityResponse).toList(),
                query.getPage(),
                query.getSize(),
                page.getTotalElements()
        );
    }
}