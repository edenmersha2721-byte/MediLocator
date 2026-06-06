package com.medicinelocator.prescription.application.dto.response;

import java.util.List;
import java.util.UUID;

public class ExtractedMedicinesResponse {

    private UUID prescriptionId;
    private List<String> extractedMedicines;
    private Object pharmacyResults;
    private String message;

    public ExtractedMedicinesResponse() {
    }

    public UUID getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(UUID prescriptionId) { this.prescriptionId = prescriptionId; }

    public List<String> getExtractedMedicines() { return extractedMedicines; }
    public void setExtractedMedicines(List<String> extractedMedicines) {
        this.extractedMedicines = extractedMedicines;
    }

    public Object getPharmacyResults() { return pharmacyResults; }
    public void setPharmacyResults(Object pharmacyResults) { this.pharmacyResults = pharmacyResults; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}