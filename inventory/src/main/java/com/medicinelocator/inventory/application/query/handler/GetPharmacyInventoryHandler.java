package com.medicinelocator.inventory.application.query.handler;

import com.medicinelocator.inventory.application.dto.response.MedicineResponse;
import com.medicinelocator.inventory.application.dto.response.PharmacyInventoryResponse;
import com.medicinelocator.inventory.application.mapper.MedicineMapper;
import com.medicinelocator.inventory.application.query.GetPharmacyInventoryQuery;
import com.medicinelocator.inventory.application.service.MedicineService;
import com.medicinelocator.inventory.domain.model.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class GetPharmacyInventoryHandler {

    private final MedicineService medicineService;
    private final MedicineMapper medicineMapper;

    public GetPharmacyInventoryHandler(MedicineService medicineService,
                                       MedicineMapper medicineMapper) {
        this.medicineService = medicineService;
        this.medicineMapper = medicineMapper;
    }

    @Transactional(readOnly = true)
    public PharmacyInventoryResponse handle(GetPharmacyInventoryQuery query) {
        PageRequest pageable = PageRequest.of(query.getPage(), query.getSize());
        Page<Medicine> page = medicineService.findByPharmacyId(
                query.getPharmacyId(), query.isAvailableOnly(), pageable);

        List<MedicineResponse> responses = page.getContent()
                .stream()
                .map(medicineMapper::toMedicineResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) page.getTotalElements() / query.getSize());

        return new PharmacyInventoryResponse(
                query.getPharmacyId(),
                responses.size(),
                query.getPage(),
                query.getSize(),
                page.getTotalElements(),
                totalPages,
                responses
        );
    }
}