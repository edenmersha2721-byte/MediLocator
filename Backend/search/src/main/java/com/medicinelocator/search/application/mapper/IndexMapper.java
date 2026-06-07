package com.medicinelocator.search.application.mapper;

import com.medicinelocator.search.application.command.IndexMedicineCommand;
import com.medicinelocator.search.application.dto.request.IndexMedicineRequest;
import org.springframework.stereotype.Component;

@Component
public class IndexMapper {

    public IndexMedicineCommand toCommand(IndexMedicineRequest request) {
        return new IndexMedicineCommand(
                request.getMedicineId(),
                request.getMedicineName(),
                request.getGenericName(),
                request.getBrandName(),
                request.getCategory(),
                request.getDescription(),
                request.isRequiresPrescription(),
                request.getPrice(),
                request.getStockQuantity(),
                request.isAvailable(),
                request.isActive(),
                request.getPharmacyId(),
                request.getPharmacyName(),
                request.getAddress(),
                request.getCity(),
                request.getLatitude(),
                request.getLongitude()
        );
    }
}