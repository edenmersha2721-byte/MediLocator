package com.medicinelocator.inventory.application.mapper;

import com.medicinelocator.inventory.application.command.AddMedicineCommand;
import com.medicinelocator.inventory.application.command.UpdateStockCommand;
import com.medicinelocator.inventory.application.dto.request.AddMedicineRequest;
import com.medicinelocator.inventory.application.dto.request.UpdateStockRequest;
import com.medicinelocator.inventory.application.dto.response.MedicineResponse;
import com.medicinelocator.inventory.application.dto.response.MedicineSearchResponse;
import com.medicinelocator.inventory.domain.model.Medicine;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MedicineMapper {

    public AddMedicineCommand toAddMedicineCommand(UUID pharmacyId, AddMedicineRequest request) {
        return new AddMedicineCommand(
                pharmacyId,
                request.getMedicineName().trim(),
                trimOrNull(request.getGenericName()),
                trimOrNull(request.getBrandName()),
                trimOrNull(request.getCategory()),
                trimOrNull(request.getDescription()),
                request.getPrice(),
                request.getStockQuantity(),
                request.isRequiresPrescription(),
                request.getExpiryDate()
        );
    }

    public UpdateStockCommand toUpdateStockCommand(UUID pharmacyId, UUID medicineId,
                                                   UpdateStockRequest request) {
        return new UpdateStockCommand(
                pharmacyId,
                medicineId,
                request.getMedicineName().trim(),
                trimOrNull(request.getGenericName()),
                trimOrNull(request.getBrandName()),
                trimOrNull(request.getCategory()),
                trimOrNull(request.getDescription()),
                request.getPrice(),
                request.getStockQuantity(),
                request.isRequiresPrescription(),
                request.getExpiryDate()
        );
    }

    public MedicineResponse toMedicineResponse(Medicine medicine) {
        MedicineResponse response = new MedicineResponse();
        response.setId(medicine.getId());
        response.setPharmacyId(medicine.getPharmacyId());
        response.setMedicineName(medicine.getMedicineName());
        response.setGenericName(medicine.getGenericName());
        response.setBrandName(medicine.getBrandName());
        response.setCategory(medicine.getCategory());
        response.setDescription(medicine.getDescription());
        response.setPrice(medicine.getPrice());
        response.setStockQuantity(medicine.getStockQuantity());
        response.setAvailable(medicine.isAvailable());
        response.setRequiresPrescription(medicine.isRequiresPrescription());
        response.setExpiryDate(medicine.getExpiryDate());
        response.setActive(medicine.isActive());
        response.setCreatedAt(medicine.getCreatedAt());
        response.setUpdatedAt(medicine.getUpdatedAt());
        return response;
    }

    public MedicineSearchResponse toMedicineSearchResponse(Medicine medicine) {
        MedicineSearchResponse response = new MedicineSearchResponse();
        response.setMedicineId(medicine.getId());
        response.setPharmacyId(medicine.getPharmacyId());
        response.setMedicineName(medicine.getMedicineName());
        response.setGenericName(medicine.getGenericName());
        response.setBrandName(medicine.getBrandName());
        response.setCategory(medicine.getCategory());
        response.setPrice(medicine.getPrice());
        response.setStockQuantity(medicine.getStockQuantity());
        response.setAvailable(medicine.isAvailable());
        response.setRequiresPrescription(medicine.isRequiresPrescription());
        response.setExpiryDate(medicine.getExpiryDate());
        return response;
    }

    private String trimOrNull(String value) {
        return value != null ? value.trim() : null;
    }
}