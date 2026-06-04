package com.medicinelocator.search.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Batch medicine search triggered by prescription service")
public class PrescriptionSearchRequest {

    @NotEmpty(message = "At least one medicine name is required")
    @Schema(description = "List of medicine names extracted from prescription",
            example = "[\"Amoxicillin\", \"Paracetamol\"]")
    private List<String> medicineNames;

    @DecimalMin(value = "-90.0") @DecimalMax(value = "90.0")
    @Schema(description = "User's latitude", example = "8.9912")
    private Double lat;

    @DecimalMin(value = "-180.0") @DecimalMax(value = "180.0")
    @Schema(description = "User's longitude", example = "38.7634")
    private Double lng;

    @DecimalMin(value = "0.1") @DecimalMax(value = "200.0")
    @Schema(description = "Radius filter in kilometres", example = "10.0")
    private Double radiusKm = 10.0;

    @Min(0)
    private int page = 0;

    @Min(1) @Max(100)
    private int size = 20;

    public List<String> getMedicineNames() { return medicineNames; }
    public void setMedicineNames(List<String> medicineNames) { this.medicineNames = medicineNames; }

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