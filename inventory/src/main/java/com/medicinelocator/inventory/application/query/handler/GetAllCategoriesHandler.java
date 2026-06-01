package com.medicinelocator.inventory.application.query.handler;

import com.medicinelocator.inventory.application.dto.response.CategoryResponse;
import com.medicinelocator.inventory.application.mapper.MedicineMapper;
import com.medicinelocator.inventory.application.query.GetAllCategoriesQuery;
import com.medicinelocator.inventory.application.service.CategoryService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class GetAllCategoriesHandler {

    private final CategoryService categoryService;
    private final MedicineMapper medicineMapper;

    public GetAllCategoriesHandler(CategoryService categoryService, MedicineMapper medicineMapper) {
        this.categoryService = categoryService;
        this.medicineMapper = medicineMapper;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> handle(GetAllCategoriesQuery query) {
        List<MedicineCategoryDomain> categories = categoryService.findAll(query.isActiveOnly());
        return categories.stream().map(medicineMapper::toCategoryResponse).toList();
    }
}