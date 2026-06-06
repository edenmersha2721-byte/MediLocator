package com.medicinelocator.prescription.application.dto.response;

import com.medicinelocator.prescription.domain.enums.PrescriptionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PrescriptionResponse {

    private UUID prescriptionId;
    private UUID customerId;
    private PrescriptionStatus status;
    private List<String> extractedMedicines;
    private String imageUrl;
    private LocalDateTime createdAt;

    public PrescriptionResponse() {
    }

    public UUID getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(UUID prescriptionId) { this.prescriptionId = prescriptionId; }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public PrescriptionStatus getStatus() { return status; }
    public void setStatus(PrescriptionStatus status) { this.status = status; }

    public List<String> getExtractedMedicines() { return extractedMedicines; }
    public void setExtractedMedicines(List<String> extractedMedicines) {
        this.extractedMedicines = extractedMedicines;
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}