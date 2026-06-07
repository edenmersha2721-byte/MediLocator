package com.medicinelocator.inventory.application.mapper;

import com.medicinelocator.inventory.application.dto.response.InventoryItemResponse;
import com.medicinelocator.inventory.domain.model.InventoryItem;
import com.medicinelocator.inventory.domain.model.Medicine;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryItemResponse toInventoryItemResponse(InventoryItem item) {
        InventoryItemResponse response = new InventoryItemResponse();
        response.setMedicineId(item.getMedicineId());
        response.setPharmacyId(item.getPharmacyId());
        response.setMedicineName(item.getMedicineName());
        response.setGenericName(item.getGenericName());
        response.setBrandName(item.getBrandName());
        response.setCategory(item.getCategory());
        response.setPrice(item.getPrice());
        response.setStockQuantity(item.getStockQuantity());
        response.setAvailable(item.isAvailable());
        response.setRequiresPrescription(item.isRequiresPrescription());
        response.setExpiryDate(item.getExpiryDate());
        return response;
    }

    public InventoryItem toInventoryItem(Medicine medicine) {
        return new InventoryItem(
                medicine.getId(),
                medicine.getPharmacyId(),
                medicine.getMedicineName(),
                medicine.getGenericName(),
                medicine.getBrandName(),
                medicine.getCategory(),
                medicine.getPrice(),
                medicine.getStockQuantity(),
                medicine.isAvailable(),
                medicine.isRequiresPrescription(),
                medicine.getExpiryDate()
        );
    }
}