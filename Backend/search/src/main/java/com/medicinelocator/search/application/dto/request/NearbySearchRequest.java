package com.medicinelocator.search.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request for nearby pharmacy discovery")
public class NearbySearchRequest {

    @NotNull(message = "Latitude is required for nearby search")
    @DecimalMin(value = "-90.0")  @DecimalMax(value = "90.0")
    @Schema(description = "User's current latitude", example = "8.9912")
    private Double lat;

    @NotNull(message = "Longitude is required for nearby search")
    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    @Schema(description = "User's current longitude", example = "38.7634")
    private Double lng;

    @DecimalMin(value = "0.1") @DecimalMax(value = "200.0")
    @Schema(description = "Search radius in kilometres", example = "10.0")
    private Double radiusKm = 10.0;

    @Min(0)
    @Schema(example = "0")
    private int page = 0;

    @Min(1) @Max(100)
    @Schema(example = "20")
    private int size = 20;

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public Double getRadiusKm() { return radiusKm; }
    public void setRadiusKm(Double radiusKm) { this.radiusKm = radiusKm; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
}