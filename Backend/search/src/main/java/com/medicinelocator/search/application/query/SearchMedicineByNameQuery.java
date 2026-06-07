package com.medicinelocator.search.application.query;

/**
 * CQRS query: search for a medicine by name (or partial/misspelled name)
 * across all pharmacy inventories, optionally filtered by location radius.
 */
public class SearchMedicineByNameQuery {

    private final String searchTerm;
    private final Double latitude;
    private final Double longitude;
    private final Double radiusKm;
    private final Boolean requiresPrescription;
    private final String category;
    private final int page;
    private final int size;

    public SearchMedicineByNameQuery(String searchTerm, Double latitude, Double longitude,
                                     Double radiusKm, Boolean requiresPrescription,
                                     String category, int page, int size) {
        this.searchTerm = searchTerm;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKm = radiusKm;
        this.requiresPrescription = requiresPrescription;
        this.category = category;
        this.page = page;
        this.size = size;
    }

    public boolean hasLocation() {
        return latitude != null && longitude != null;
    }

    public boolean hasRadiusFilter() {
        return hasLocation() && radiusKm != null;
    }

    public String getSearchTerm() { return searchTerm; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Double getRadiusKm() { return radiusKm; }
    public Boolean getRequiresPrescription() { return requiresPrescription; }
    public String getCategory() { return category; }
    public int getPage() { return page; }
    public int getSize() { return size; }
}