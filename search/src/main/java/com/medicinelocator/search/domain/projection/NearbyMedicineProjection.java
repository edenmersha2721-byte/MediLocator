package com.medicinelocator.search.domain.projection;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Combined geo + stock projection.
 * Returned for queries that search a medicine name AND filter by proximity.
 * Carries distance from the user's location (computed by PostGIS ST_DistanceSphere).
 */
public class NearbyMedicineProjection {

    private final UUID medicineId;
    private final String medicineName;
    private final String genericName;
    private final String brandName;
    private final String category;
    private final boolean requiresPrescription;
    private final BigDecimal price;
    private final int stockQuantity;
    private final boolean available;
    private final UUID pharmacyId;
    private final String pharmacyName;
    private final String address;
    private final String city;
    private final double latitude;
    private final double longitude;
    private final double distanceMeters;

    public NearbyMedicineProjection(UUID medicineId, String medicineName, String genericName,
                                    String brandName, String category, boolean requiresPrescription,
                                    BigDecimal price, int stockQuantity, boolean available,
                                    UUID pharmacyId, String pharmacyName, String address,
                                    String city, double latitude, double longitude,
                                    double distanceMeters) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.genericName = genericName;
        this.brandName = brandName;
        this.category = category;
        this.requiresPrescription = requiresPrescription;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.available = available;
        this.pharmacyId = pharmacyId;
        this.pharmacyName = pharmacyName;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceMeters = distanceMeters;
    }

    public UUID getMedicineId() { return medicineId; }
    public String getMedicineName() { return medicineName; }
    public String getGenericName() { return genericName; }
    public String getBrandName() { return brandName; }
    public String getCategory() { return category; }
    public boolean isRequiresPrescription() { return requiresPrescription; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public boolean isAvailable() { return available; }
    public UUID getPharmacyId() { return pharmacyId; }
    public String getPharmacyName() { return pharmacyName; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getDistanceMeters() { return distanceMeters; }
}