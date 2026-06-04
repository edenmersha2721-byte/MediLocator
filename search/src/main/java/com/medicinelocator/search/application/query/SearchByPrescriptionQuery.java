package com.medicinelocator.search.application.query;

import java.util.List;

/**
 * CQRS query: called by the Prescription Service via REST.
 * Accepts a list of medicine names extracted from a prescription
 * and returns pharmacies that stock each medicine nearby.
 */
public class SearchByPrescriptionQuery {

    private final List<String> medicineNames;
    private final Double latitude;
    private final Double longitude;
    private final Double radiusKm;
    private final int page;
    private final int size;

    public SearchByPrescriptionQuery(List<String> medicineNames, Double latitude,
                                     Double longitude, Double radiusKm, int page, int size) {
        this.medicineNames = medicineNames;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKm = radiusKm;
        this.page = page;
        this.size = size;
    }

    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }

    public List<String> getMedicineNames() { return medicineNames; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Double getRadiusKm() { return radiusKm; }
    public int getPage() { return page; }
    public int getSize() { return size; }
}