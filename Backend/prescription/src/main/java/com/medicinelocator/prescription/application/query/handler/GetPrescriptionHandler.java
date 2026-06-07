package com.medicinelocator.prescription.application.query.handler;

import com.medicinelocator.prescription.application.dto.response.PrescriptionResponse;
import com.medicinelocator.prescription.application.mapper.PrescriptionMapper;
import com.medicinelocator.prescription.application.query.GetPrescriptionQuery;
import com.medicinelocator.prescription.application.service.PrescriptionService;
import com.medicinelocator.prescription.domain.exception.PrescriptionNotFoundException;
import com.medicinelocator.prescription.domain.model.Prescription;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetPrescriptionHandler {

    private final PrescriptionService prescriptionService;
    private final PrescriptionMapper prescriptionMapper;

    public GetPrescriptionHandler(PrescriptionService prescriptionService,
                                  PrescriptionMapper prescriptionMapper) {
        this.prescriptionService = prescriptionService;
        this.prescriptionMapper = prescriptionMapper;
    }

    @Transactional(readOnly = true)
    public PrescriptionResponse handle(GetPrescriptionQuery query) {
        Prescription prescription = prescriptionService.findById(query.getPrescriptionId())
                .orElseThrow(() -> new PrescriptionNotFoundException(query.getPrescriptionId()));

        return prescriptionMapper.toPrescriptionResponse(prescription);
    }
}