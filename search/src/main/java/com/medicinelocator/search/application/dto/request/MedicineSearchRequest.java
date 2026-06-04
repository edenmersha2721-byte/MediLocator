package com.medicinelocator.search.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request parameters for medicine search across all pharmacies")
public class MedicineSearchRequest {

    @NotBlank(message = "Search query is required")
    @Size(min = 1, max = 200, message = "Search query must be between 1 and 200 characters")
    @Schema(description = "Medicine name or partial name to search (supports fuzzy matching)",
            example = "paracitamol")
    private String query;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0",  message = "Latitude must be between -90 and 90")
    @Schema(description = "User's current latitude", example = "8.9912")
    private Double lat;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0",  message = "Longitude must be between -180 and 180")
    @Schema(description = "User's current longitude", example = "38.7634")
    private Double lng;

    @DecimalMin(value = "0.1", message = "Radius must be at least 0.1 km")
    @DecimalMax(value = "200.0", message = "Radius cannot exceed 200 km")
    @Schema(description = "Search radius in kilometres. If omitted, no radius filter is applied.",
            example = "5.0")
    private Double radiusKm;

    @Schema(description = "Filter by prescription requirement", example = "false")
    private Boolean requiresPrescription;

    @Schema(description = "Filter by specific category", example = "ANALGESIC")
    private String category;

    @Min(value = 0, message = "Page must be zero or greater")
    @Schema(description = "Zero-based page number", example = "0")
    private int page = 0;

    @Min(value = 1) @Max(value = 100)
    @Schema(description = "Number of results per page", example = "20")
    private int size = 20;

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public Double getRadiusKm() { return radiusKm; }
    public void setRadiusKm(Double radiusKm) { this.radiusKm = radiusKm; }

    public Boolean getRequiresPrescription() { return requiresPrescription; }
    public void setRequiresPrescription(Boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}