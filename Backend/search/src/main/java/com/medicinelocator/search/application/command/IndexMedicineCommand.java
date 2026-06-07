package com.medicinelocator.search.application.command;

import java.math.BigDecimal;
import java.util.UUID;


public class IndexMedicineCommand {

    private final UUID medicineId;
    private final String medicineName;
    private final String genericName;
    private final String brandName;
    private final String category;
    private final String description;
    private final boolean requiresPrescription;
    private final BigDecimal price;
    private final int stockQuantity;
    private final boolean available;
    private final boolean active;
    private final UUID pharmacyId;
    private final String pharmacyName;
    private final String address;
    private final String city;
    private final double latitude;
    private final double longitude;

    public IndexMedicineCommand(UUID medicineId, String medicineName, String genericName,
                                String brandName, String category, String description,
                                boolean requiresPrescription, BigDecimal price,
                                int stockQuantity, boolean available, boolean active,
                                UUID pharmacyId, String pharmacyName, String address,
                                String city, double latitude, double longitude) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.genericName = genericName;
        this.brandName = brandName;
        this.category = category;
        this.description = description;
        this.requiresPrescription = requiresPrescription;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.available = available;
        this.active = active;
        this.pharmacyId = pharmacyId;
        this.pharmacyName = pharmacyName;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UUID getMedicineId() { return medicineId; }
    public String getMedicineName() { return medicineName; }
    public String getGenericName() { return genericName; }
    public String getBrandName() { return brandName; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public boolean isRequiresPrescription() { return requiresPrescription; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public boolean isAvailable() { return available; }
    public boolean isActive() { return active; }
    public UUID getPharmacyId() { return pharmacyId; }
    public String getPharmacyName() { return pharmacyName; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}