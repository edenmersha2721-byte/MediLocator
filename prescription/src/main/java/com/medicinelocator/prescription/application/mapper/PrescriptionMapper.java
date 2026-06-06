package com.medicinelocator.prescription.application.mapper;

import com.medicinelocator.prescription.application.dto.response.PrescriptionResponse;
import com.medicinelocator.prescription.domain.model.Prescription;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionMapper {

    public PrescriptionResponse toPrescriptionResponse(Prescription prescription) {
        PrescriptionResponse response = new PrescriptionResponse();
        response.setPrescriptionId(prescription.getId());
        response.setCustomerId(prescription.getCustomerId());
        response.setStatus(prescription.getStatus());
        response.setExtractedMedicines(prescription.getMedicineNames());
        response.setImageUrl(prescription.getImageUrl());
        response.setCreatedAt(prescription.getCreatedAt());
        return response;
    }
}