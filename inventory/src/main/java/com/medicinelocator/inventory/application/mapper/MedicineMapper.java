package com.medicinelocator.inventory.application.mapper;

import com.medicinelocator.inventory.application.command.AddMedicineCommand;
import com.medicinelocator.inventory.application.command.CreateCategoryCommand;
import com.medicinelocator.inventory.application.command.UpdateCategoryCommand;
import com.medicinelocator.inventory.application.command.UpdateMedicineCommand;
import com.medicinelocator.inventory.application.dto.request.AddMedicineRequest;
import com.medicinelocator.inventory.application.dto.request.CreateCategoryRequest;
import com.medicinelocator.inventory.application.dto.request.UpdateCategoryRequest;
import com.medicinelocator.inventory.application.dto.request.UpdateMedicineRequest;
import com.medicinelocator.inventory.application.dto.response.CategoryResponse;
import com.medicinelocator.inventory.application.dto.response.MedicineResponse;
import com.medicinelocator.inventory.domain.model.Medicine;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MedicineMapper {

    public AddMedicineCommand toAddMedicineCommand(AddMedicineRequest request) {
        return new AddMedicineCommand(
                request.getName().trim(),
                request.getGenericName() != null ? request.getGenericName().trim() : null,
                request.getBrandName() != null ? request.getBrandName().trim() : null,
                request.getDescription() != null ? request.getDescription().trim() : null,
                request.getCategoryId(),
                request.isRequiresPrescription()
        );
    }

    public UpdateMedicineCommand toUpdateMedicineCommand(UUID medicineId, UpdateMedicineRequest request) {
        return new UpdateMedicineCommand(
                medicineId,
                request.getName().trim(),
                request.getGenericName() != null ? request.getGenericName().trim() : null,
                request.getBrandName() != null ? request.getBrandName().trim() : null,
                request.getDescription() != null ? request.getDescription().trim() : null,
                request.getCategoryId(),
                request.isRequiresPrescription(),
                request.isActive()
        );
    }

    public CreateCategoryCommand toCreateCategoryCommand(CreateCategoryRequest request) {
        return new CreateCategoryCommand(
                request.getName().trim(),
                request.getDescription() != null ? request.getDescription().trim() : null
        );
    }

    public UpdateCategoryCommand toUpdateCategoryCommand(UUID categoryId, UpdateCategoryRequest request) {
        return new UpdateCategoryCommand(
                categoryId,
                request.getName().trim(),
                request.getDescription() != null ? request.getDescription().trim() : null,
                request.isActive()
        );
    }

    public MedicineResponse toMedicineResponse(Medicine medicine) {
        MedicineResponse response = new MedicineResponse();
        response.setId(medicine.getId());
        response.setName(medicine.getName());
        response.setGenericName(medicine.getGenericName());
        response.setBrandName(medicine.getBrandName());
        response.setDescription(medicine.getDescription());
        response.setCategoryId(medicine.getCategoryId());
        response.setRequiresPrescription(medicine.isRequiresPrescription());
        response.setActive(medicine.isActive());
        response.setCreatedAt(medicine.getCreatedAt());
        response.setUpdatedAt(medicine.getUpdatedAt());
        return response;
    }

    public CategoryResponse toCategoryResponse(MedicineCategoryDomain category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setActive(category.isActive());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        return response;
    }
}