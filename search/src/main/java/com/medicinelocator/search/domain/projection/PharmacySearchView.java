package com.medicinelocator.search.domain.projection;

import java.util.UUID;

/**
 * Read-model projection for a pharmacy's location and identity.
 * Used in nearby pharmacy searches when no specific medicine is searched.
 */
public class PharmacySearchView {

    private final UUID pharmacyId;
    private final String pharmacyName;
    private final String address;
    private final String city;
    private final double latitude;
    private final double longitude;
    private final double distanceMeters;
    private final int availableMedicineCount;

    public PharmacySearchView(UUID pharmacyId, String pharmacyName, String address,
                              String city, double latitude, double longitude,
                              double distanceMeters, int availableMedicineCount) {
        this.pharmacyId = pharmacyId;
        this.pharmacyName = pharmacyName;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceMeters = distanceMeters;
        this.availableMedicineCount = availableMedicineCount;
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public String getPharmacyName() { return pharmacyName; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getDistanceMeters() { return distanceMeters; }
    public int getAvailableMedicineCount() { return availableMedicineCount; }
}