package com.medicinelocator.prescription.domain.model;

import com.medicinelocator.prescription.domain.enums.PrescriptionStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class Prescription {

    private UUID id;
    private UUID customerId;
    private String imageUrl;
    private String rawText;
    private PrescriptionStatus status;
    private Double latitude;
    private Double longitude;
    private List<ExtractedMedicine> extractedMedicines;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Prescription() {
        this.extractedMedicines = new ArrayList<>();
    }

    public Prescription(UUID id, UUID customerId, String imageUrl, String rawText,
                        PrescriptionStatus status, Double latitude, Double longitude,
                        List<ExtractedMedicine> extractedMedicines,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.imageUrl = imageUrl;
        this.rawText = rawText;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.extractedMedicines = extractedMedicines != null
                ? new ArrayList<>(extractedMedicines) : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ─── Domain behaviour ─────────────────────────────────────────────────────

    public void markProcessing() {
        this.status = PrescriptionStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    public void completeProcessing(String rawText, List<ExtractedMedicine> medicines) {
        this.rawText = rawText;
        this.extractedMedicines = new ArrayList<>(medicines);
        this.status = PrescriptionStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = PrescriptionStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return PrescriptionStatus.COMPLETED.equals(this.status);
    }

    public boolean hasMedicines() {
        return !this.extractedMedicines.isEmpty();
    }

    public List<String> getMedicineNames() {
        return extractedMedicines.stream()
                .map(ExtractedMedicine::getMedicineName)
                .toList();
    }

    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public PrescriptionStatus getStatus() { return status; }
    public void setStatus(PrescriptionStatus status) { this.status = status; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public List<ExtractedMedicine> getExtractedMedicines() {
        return Collections.unmodifiableList(extractedMedicines);
    }

    public void setExtractedMedicines(List<ExtractedMedicine> extractedMedicines) {
        this.extractedMedicines = new ArrayList<>(extractedMedicines);
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}