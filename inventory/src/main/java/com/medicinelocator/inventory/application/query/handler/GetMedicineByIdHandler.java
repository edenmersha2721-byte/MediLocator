package com.medicinelocator.inventory.application.query.handler;

import com.medicinelocator.inventory.application.dto.response.MedicineResponse;
import com.medicinelocator.inventory.application.mapper.MedicineMapper;
import com.medicinelocator.inventory.application.query.GetMedicineByIdQuery;
import com.medicinelocator.inventory.application.service.MedicineService;
import com.medicinelocator.inventory.domain.exception.MedicineNotFoundException;
import com.medicinelocator.inventory.domain.model.Medicine;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetMedicineByIdHandler {

    private final MedicineService medicineService;
    private final MedicineMapper medicineMapper;

    public GetMedicineByIdHandler(MedicineService medicineService,
                                  MedicineMapper medicineMapper) {
        this.medicineService = medicineService;
        this.medicineMapper = medicineMapper;
    }

    @Transactional(readOnly = true)
    public MedicineResponse handle(GetMedicineByIdQuery query) {
        Medicine medicine = medicineService
                .findByIdAndPharmacyId(query.getMedicineId(), query.getPharmacyId())
                .orElseThrow(() -> new MedicineNotFoundException(
                        query.getPharmacyId(), query.getMedicineId()));

        return medicineMapper.toMedicineResponse(medicine);
    }
}