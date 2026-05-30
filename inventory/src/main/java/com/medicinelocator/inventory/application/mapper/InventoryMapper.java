package com.medicinelocator.inventory.application.mapper;

import com.medicinelocator.inventory.application.command.AddInventoryCommand;
import com.medicinelocator.inventory.application.command.UpdateStockCommand;
import com.medicinelocator.inventory.application.dto.request.AddInventoryRequest;
import com.medicinelocator.inventory.application.dto.request.UpdateStockRequest;
import com.medicinelocator.inventory.application.dto.response.InventoryItemResponse;
import com.medicinelocator.inventory.application.dto.response.MedicineAvailabilityResponse;
import com.medicinelocator.inventory.domain.model.PharmacyInventory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InventoryMapper {

    public AddInventoryCommand toAddInventoryCommand(UUID pharmacyId, AddInventoryRequest request) {
        return new AddInventoryCommand(
                pharmacyId,
                request.getMedicineId(),
                request.getQuantity(),
                request.getUnitPrice()
        );
    }

    public UpdateStockCommand toUpdateStockCommand(UUID pharmacyId, UpdateStockRequest request) {
        return new UpdateStockCommand(
                pharmacyId,
                request.getMedicineId(),
                request.getQuantity(),
                request.getUnitPrice()
        );
    }

    public InventoryItemResponse toInventoryItemResponse(PharmacyInventory inventory) {
        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(inventory.getId());
        response.setPharmacyId(inventory.getPharmacyId());
        response.setMedicineId(inventory.getMedicineId());
        response.setQuantity(inventory.getQuantity());
        response.setUnitPrice(inventory.getUnitPrice());
        response.setAvailable(inventory.isAvailable());
        response.setUpdatedAt(inventory.getUpdatedAt());
        return response;
    }

    public MedicineAvailabilityResponse toMedicineAvailabilityResponse(PharmacyInventory inventory) {
        MedicineAvailabilityResponse response = new MedicineAvailabilityResponse();
        response.setPharmacyId(inventory.getPharmacyId());
        response.setMedicineId(inventory.getMedicineId());
        response.setQuantity(inventory.getQuantity());
        response.setUnitPrice(inventory.getUnitPrice());
        response.setAvailable(inventory.isAvailable());
        return response;
    }
}