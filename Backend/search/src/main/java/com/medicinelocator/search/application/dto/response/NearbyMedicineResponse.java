package com.medicinelocator.search.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "A medicine result with distance from user's location")
public class NearbyMedicineResponse {

    @Schema(description = "Unique identifier of the medicine record", example = "a1b2c3...")
    private UUID medicineId;

    @Schema(description = "Medicine name as defined by the pharmacy", example = "Paracetamol 500mg")
    private String medicineName;

    @Schema(description = "Generic (scientific) name", example = "Acetaminophen")
    private String genericName;

    @Schema(description = "Brand name", example = "Tylenol")
    private String brandName;

    @Schema(description = "Local category label", example = "ANALGESIC")
    private String category;

    @Schema(description = "Whether a prescription is required")
    private boolean requiresPrescription;

    @Schema(description = "Price set by the pharmacy", example = "25.00")
    private BigDecimal price;

    @Schema(description = "Current stock quantity", example = "100")
    private int stockQuantity;

    @Schema(description = "Whether the medicine is currently available")
    private boolean available;

    @Schema(description = "ID of the pharmacy that owns this medicine")
    private UUID pharmacyId;

    @Schema(description = "Pharmacy display name", example = "ABC Pharmacy")
    private String pharmacyName;

    @Schema(description = "Pharmacy street address", example = "123 Healthcare Blvd, Suite A")
    private String address;

    @Schema(description = "Pharmacy city", example = "Addis Ababa")
    private String city;

    @Schema(description = "Pharmacy latitude for map rendering", example = "8.9912")
    private double latitude;

    @Schema(description = "Pharmacy longitude for map rendering", example = "38.7634")
    private double longitude;

    @Schema(description = "Distance from user to this pharmacy in metres", example = "450.0")
    private double distanceMeters;

    public NearbyMedicineResponse() {
    }

    public UUID getMedicineId() { return medicineId; }
    public void setMedicineId(UUID medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isRequiresPrescription() { return requiresPrescription; }
    public void setRequiresPrescription(boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

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
}