package com.medicinelocator.prescription.application.command;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class UploadPrescriptionCommand {

    private final UUID customerId;
    private final MultipartFile file;
    private final Double latitude;
    private final Double longitude;
    private final Double radiusKm;

    public UploadPrescriptionCommand(UUID customerId, MultipartFile file,
                                     Double latitude, Double longitude, Double radiusKm) {
        this.customerId = customerId;
        this.file = file;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKm = radiusKm;
    }

    public UUID getCustomerId() { return customerId; }
    public MultipartFile getFile() { return file; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Double getRadiusKm() { return radiusKm; }
}