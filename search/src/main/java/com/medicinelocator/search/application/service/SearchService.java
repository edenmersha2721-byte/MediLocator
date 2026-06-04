package com.medicinelocator.search.application.service;

import com.medicinelocator.search.domain.projection.NearbyMedicineProjection;
import com.medicinelocator.search.domain.projection.PharmacySearchView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Application-layer interface for the search read model.
 * All implementations must be read-only — no mutations allowed.
 */
public interface SearchService {

    /**
     * Full-text + trigram fuzzy search across all pharmacy medicines.
     * Results ordered by relevance when no location is provided,
     * or by distance when location is provided.
     */
    Page<NearbyMedicineProjection> searchMedicinesByName(String searchTerm,
                                                         Double latitude,
                                                         Double longitude,
                                                         Double radiusKm,
                                                         Boolean requiresPrescription,
                                                         String category,
                                                         Pageable pageable);

    /**
     * Discover pharmacies within a radius, sorted by distance.
     * Returns pharmacies that have at least one available medicine.
     */
    Page<PharmacySearchView> searchNearbyPharmacies(double latitude,
                                                    double longitude,
                                                    double radiusKm,
                                                    Pageable pageable);

    /**
     * Batch search for multiple medicine names from a prescription.
     * Returns each medicine name with the nearest matching pharmacies.
     */
    Page<NearbyMedicineProjection> searchByPrescriptionMedicines(List<String> medicineNames,
                                                                 Double latitude,
                                                                 Double longitude,
                                                                 Double radiusKm,
                                                                 Pageable pageable);
}