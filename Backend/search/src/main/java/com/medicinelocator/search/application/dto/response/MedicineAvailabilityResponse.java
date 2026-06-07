package com.medicinelocator.search.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Simple medicine availability response without distance data")
public class MedicineAvailabilityResponse {

    @Schema(description = "Medicine record ID")
    private UUID medicineId;

    @Schema(description = "Medicine name as listed by the pharmacy")
    private String medicineName;

    @Schema(description = "Generic name")
    private String genericName;

    @Schema(description = "Brand name")
    private String brandName;

    @Schema(description = "Price")
    private BigDecimal price;

    @Schema(description = "Stock quantity")
    private int stockQuantity;

    @Schema(description = "Availability status")
    private boolean available;

    @Schema(description = "Pharmacy ID")
    private UUID pharmacyId;

    @Schema(description = "Pharmacy name")
    private String pharmacyName;

    @Schema(description = "Pharmacy address")
    private String address;

    @Schema(description = "City")
    private String city;

    @Schema(description = "Latitude")
    private double latitude;

    @Schema(description = "Longitude")
    private double longitude;

    public MedicineAvailabilityResponse() {
    }

    public UUID getMedicineId() { return medicineId; }
    public void setMedicineId(UUID medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

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
}