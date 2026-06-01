package com.medicinelocator.inventory.application.query.handler;

import com.medicinelocator.inventory.application.dto.response.MedicineSearchResponse;
import com.medicinelocator.inventory.application.dto.response.PagedResponse;
import com.medicinelocator.inventory.application.mapper.MedicineMapper;
import com.medicinelocator.inventory.application.query.SearchMedicinesQuery;
import com.medicinelocator.inventory.application.service.MedicineService;
import com.medicinelocator.inventory.domain.model.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SearchMedicinesHandler {

    private final MedicineService medicineService;
    private final MedicineMapper medicineMapper;

    public SearchMedicinesHandler(MedicineService medicineService,
                                  MedicineMapper medicineMapper) {
        this.medicineService = medicineService;
        this.medicineMapper = medicineMapper;
    }

    @Transactional(readOnly = true)
    public PagedResponse<MedicineSearchResponse> handle(SearchMedicinesQuery query) {
        PageRequest pageable = PageRequest.of(query.getPage(), query.getSize());
        Page<Medicine> page = medicineService.searchAcrossPharmacies(
                query.getMedicineName(),
                query.getBrandName(),
                query.getGenericName(),
                query.getCategory(),
                query.getAvailableOnly(),
                query.getRequiresPrescription(),
                pageable
        );

        return new PagedResponse<>(
                page.getContent().stream().map(medicineMapper::toMedicineSearchResponse).toList(),
                query.getPage(),
                query.getSize(),
                page.getTotalElements()
        );
    }
}