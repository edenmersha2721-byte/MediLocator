package com.medicinelocator.search.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "A pharmacy result with location data for map rendering")
public class PharmacySearchResult {

    @Schema(description = "Pharmacy unique identifier")
    private UUID pharmacyId;

    @Schema(description = "Pharmacy display name", example = "ABC Pharmacy")
    private String pharmacyName;

    @Schema(description = "Pharmacy street address", example = "123 Healthcare Blvd, Suite A")
    private String address;

    @Schema(description = "City where pharmacy is located", example = "Addis Ababa")
    private String city;

    @Schema(description = "Latitude for map marker", example = "8.9912")
    private double latitude;

    @Schema(description = "Longitude for map marker", example = "38.7634")
    private double longitude;

    @Schema(description = "Distance in metres from the user's location", example = "1200.0")
    private double distanceMeters;

    @Schema(description = "Number of available medicines at this pharmacy", example = "42")
    private int availableMedicineCount;

    public PharmacySearchResult() {
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(UUID pharmacyId) { this.pharmacyId = pharmacyId; }

    public String getPharmacyName() { return pharmacyName; }
    public void setPharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getDistanceMeters() { return distanceMeters; }
    public void setDistanceMeters(double distanceMeters) { this.distanceMeters = distanceMeters; }

    public int getAvailableMedicineCount() { return availableMedicineCount; }
    public void setAvailableMedicineCount(int availableMedicineCount) {
        this.availableMedicineCount = availableMedicineCount;
    }
}