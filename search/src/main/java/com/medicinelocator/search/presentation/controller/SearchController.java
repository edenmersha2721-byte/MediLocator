package com.medicinelocator.search.presentation.controller;

import com.medicinelocator.search.application.dto.request.MedicineSearchRequest;
import com.medicinelocator.search.application.dto.request.NearbySearchRequest;
import com.medicinelocator.search.application.dto.request.PrescriptionSearchRequest;
import com.medicinelocator.search.application.dto.response.NearbyMedicineResponse;
import com.medicinelocator.search.application.dto.response.PagedResponse;
import com.medicinelocator.search.application.dto.response.PharmacySearchResult;
import com.medicinelocator.search.application.mapper.SearchMapper;
import com.medicinelocator.search.application.query.SearchByPrescriptionQuery;
import com.medicinelocator.search.application.query.SearchMedicineByNameQuery;
import com.medicinelocator.search.application.query.SearchNearbyPharmaciesQuery;
import com.medicinelocator.search.application.query.handler.SearchByPrescriptionHandler;
import com.medicinelocator.search.application.query.handler.SearchMedicineByNameHandler;
import com.medicinelocator.search.application.query.handler.SearchNearbyPharmaciesHandler;
import com.medicinelocator.search.infrastructure.security.CurrentUser;
import com.medicinelocator.search.infrastructure.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Search Controller — pure read-only CQRS query side.
 *
 * Endpoints:
 *   GET  /api/v1/search                  → medicine search (name + optional geo)
 *   GET  /api/v1/search/nearby           → nearby pharmacy discovery
 *   POST /api/v1/search/prescription     → batch prescription medicine search
 *
 * This controller contains NO commands, NO mutations, NO state changes.
 * Identity is validated via gateway-forwarded headers.
 */
@RestController
@RequestMapping("/api/v1/search")
@Validated
@Tag(name = "Search", description = "Read-only medicine and pharmacy discovery endpoints")
public class SearchController {

    private final SearchMedicineByNameHandler searchMedicineByNameHandler;
    private final SearchNearbyPharmaciesHandler searchNearbyPharmaciesHandler;
    private final SearchByPrescriptionHandler searchByPrescriptionHandler;
    private final SearchMapper searchMapper;
    private final CurrentUserProvider currentUserProvider;

    public SearchController(SearchMedicineByNameHandler searchMedicineByNameHandler,
                            SearchNearbyPharmaciesHandler searchNearbyPharmaciesHandler,
                            SearchByPrescriptionHandler searchByPrescriptionHandler,
                            SearchMapper searchMapper,
                            CurrentUserProvider currentUserProvider) {
        this.searchMedicineByNameHandler = searchMedicineByNameHandler;
        this.searchNearbyPharmaciesHandler = searchNearbyPharmaciesHandler;
        this.searchByPrescriptionHandler = searchByPrescriptionHandler;
        this.searchMapper = searchMapper;
        this.currentUserProvider = currentUserProvider;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIMARY SEARCH: GET /api/v1/search
    // Fuzzy medicine name search across all pharmacy inventories.
    // Optionally filtered by user coordinates + radius.
    // Results ordered by proximity when coordinates are provided.
    // ─────────────────────────────────────────────────────────────────────────

    @Operation(
            summary = "Search medicines across all pharmacies",
            description = """
                    Searches medicine names across all pharmacy inventories.
                    Supports fuzzy/misspelling-tolerant matching via PostgreSQL pg_trgm.
                    When lat/lng are provided, results are ordered by distance (nearest first).
                    When radiusKm is also provided, only pharmacies within the radius are returned.
                    Only active, available medicines with stock > 0 are returned.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorDetails"))),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    public ResponseEntity<PagedResponse<NearbyMedicineResponse>> searchMedicines(
            @Parameter(description = "Medicine name to search (supports fuzzy matching)",
                    example = "paracitamol", required = true)
            @RequestParam @NotBlank(message = "Search query is required")
            @Size(min = 1, max = 200) String query,

            @Parameter(description = "User latitude for proximity sorting", example = "8.9912")
            @RequestParam(required = false)
            @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,

            @Parameter(description = "User longitude for proximity sorting", example = "38.7634")
            @RequestParam(required = false)
            @DecimalMin("-180.0") @DecimalMax("180.0") Double lng,

            @Parameter(description = "Search radius in kilometres (requires lat/lng)",
                    example = "5.0")
            @RequestParam(required = false)
            @DecimalMin("0.1") @DecimalMax("200.0") Double radiusKm,

            @Parameter(description = "Filter by prescription requirement")
            @RequestParam(required = false) Boolean requiresPrescription,

            @Parameter(description = "Filter by medicine category", example = "ANALGESIC")
            @RequestParam(required = false) String category,

            @Parameter(description = "Zero-based page number", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Results per page (max 100)", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        CurrentUser currentUser = currentUserProvider.getCurrentUser();

        MedicineSearchRequest request = new MedicineSearchRequest();
        request.setQuery(query);
        request.setLat(lat);
        request.setLng(lng);
        request.setRadiusKm(radiusKm);
        request.setRequiresPrescription(requiresPrescription);
        request.setCategory(category);
        request.setPage(page);
        request.setSize(size);

        SearchMedicineByNameQuery searchQuery = searchMapper.toSearchMedicineByNameQuery(request);
        return ResponseEntity.ok(searchMedicineByNameHandler.handle(searchQuery));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NEARBY PHARMACIES: GET /api/v1/search/nearby
    // Discover pharmacies near user's location regardless of medicine.
    // Results ordered by distance (nearest first).
    // ─────────────────────────────────────────────────────────────────────────

    @Operation(
            summary = "Find nearby pharmacies",
            description = """
                    Returns pharmacies within the specified radius of the user's coordinates.
                    Results are sorted by distance from nearest to farthest.
                    Each result includes address, coordinates (for map rendering), and
                    the count of available medicines at that pharmacy.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nearby pharmacies returned"),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates or radius"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/nearby")
    public ResponseEntity<PagedResponse<PharmacySearchResult>> searchNearbyPharmacies(
            @Parameter(description = "User latitude", example = "8.9912", required = true)
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double lat,

            @Parameter(description = "User longitude", example = "38.7634", required = true)
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double lng,

            @Parameter(description = "Search radius in kilometres", example = "10.0")
            @RequestParam(defaultValue = "10.0")
            @DecimalMin("0.1") @DecimalMax("200.0") double radiusKm,

            @Parameter(description = "Zero-based page number", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Results per page (max 100)", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        currentUserProvider.getCurrentUser();

        NearbySearchRequest request = new NearbySearchRequest();
        request.setLat(lat);
        request.setLng(lng);
        request.setRadiusKm(radiusKm);
        request.setPage(page);
        request.setSize(size);

        SearchNearbyPharmaciesQuery query = searchMapper.toSearchNearbyPharmaciesQuery(request);
        return ResponseEntity.ok(searchNearbyPharmaciesHandler.handle(query));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRESCRIPTION SEARCH: POST /api/v1/search/prescription
    // Called by Prescription Service via internal REST call.
    // Accepts multiple medicine names from a prescription and finds nearby
    // pharmacies stocking each medicine.
    // ─────────────────────────────────────────────────────────────────────────

    @Operation(
            summary = "Batch medicine search for prescription",
            description = """
                    Called by the Prescription Service after OCR processing.
                    Accepts a list of medicine names extracted from a prescription
                    and returns pharmacies near the user that stock each medicine.
                    Supports fuzzy matching for names that may have OCR errors.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prescription medicines found"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PostMapping("/prescription")
    public ResponseEntity<PagedResponse<NearbyMedicineResponse>> searchByPrescription(
            @Valid @RequestBody PrescriptionSearchRequest request) {

        currentUserProvider.getCurrentUser();

        SearchByPrescriptionQuery query = searchMapper.toSearchByPrescriptionQuery(request);
        return ResponseEntity.ok(searchByPrescriptionHandler.handle(query));
    }
}