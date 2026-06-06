package com.medicinelocator.prescription.domain.model;

import java.util.UUID;


public class ExtractedMedicine {

    private UUID id;
    private UUID prescriptionId;
    private String medicineName;

    public ExtractedMedicine() {
    }

    public ExtractedMedicine(UUID id, UUID prescriptionId, String medicineName) {
        this.id = id;
        this.prescriptionId = prescriptionId;
        this.medicineName = medicineName;
    }

    public static ExtractedMedicine of(UUID prescriptionId, String medicineName) {
        return new ExtractedMedicine(UUID.randomUUID(), prescriptionId, medicineName);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(UUID prescriptionId) { this.prescriptionId = prescriptionId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
}