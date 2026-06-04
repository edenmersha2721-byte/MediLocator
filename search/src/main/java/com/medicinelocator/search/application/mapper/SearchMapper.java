package com.medicinelocator.search.application.mapper;

import com.medicinelocator.search.application.dto.request.MedicineSearchRequest;
import com.medicinelocator.search.application.dto.request.NearbySearchRequest;
import com.medicinelocator.search.application.dto.request.PrescriptionSearchRequest;
import com.medicinelocator.search.application.dto.response.MedicineAvailabilityResponse;
import com.medicinelocator.search.application.dto.response.NearbyMedicineResponse;
import com.medicinelocator.search.application.dto.response.PharmacySearchResult;
import com.medicinelocator.search.application.query.SearchByPrescriptionQuery;
import com.medicinelocator.search.application.query.SearchMedicineByNameQuery;
import com.medicinelocator.search.application.query.SearchNearbyPharmaciesQuery;
import com.medicinelocator.search.domain.projection.MedicineSearchView;
import com.medicinelocator.search.domain.projection.NearbyMedicineProjection;
import com.medicinelocator.search.domain.projection.PharmacySearchView;
import org.springframework.stereotype.Component;

@Component
public class SearchMapper {

    // ─── Request → Query ─────────────────────────────────────────────────────

    public SearchMedicineByNameQuery toSearchMedicineByNameQuery(MedicineSearchRequest request) {
        return new SearchMedicineByNameQuery(
                request.getQuery().trim(),
                request.getLat(),
                request.getLng(),
                request.getRadiusKm(),
                request.getRequiresPrescription(),
                request.getCategory() != null ? request.getCategory().trim() : null,
                request.getPage(),
                request.getSize()
        );
    }

    public SearchNearbyPharmaciesQuery toSearchNearbyPharmaciesQuery(NearbySearchRequest request) {
        return new SearchNearbyPharmaciesQuery(
                request.getLat(),
                request.getLng(),
                request.getRadiusKm() != null ? request.getRadiusKm() : 10.0,
                request.getPage(),
                request.getSize()
        );
    }

    public SearchByPrescriptionQuery toSearchByPrescriptionQuery(PrescriptionSearchRequest request) {
        return new SearchByPrescriptionQuery(
                request.getMedicineNames(),
                request.getLat(),
                request.getLng(),
                request.getRadiusKm() != null ? request.getRadiusKm() : 10.0,
                request.getPage(),
                request.getSize()
        );
    }

    // ─── Projection → Response ────────────────────────────────────────────────

    public NearbyMedicineResponse toNearbyMedicineResponse(NearbyMedicineProjection projection) {
        NearbyMedicineResponse response = new NearbyMedicineResponse();
        response.setMedicineId(projection.getMedicineId());
        response.setMedicineName(projection.getMedicineName());
        response.setGenericName(projection.getGenericName());
        response.setBrandName(projection.getBrandName());
        response.setCategory(projection.getCategory());
        response.setRequiresPrescription(projection.isRequiresPrescription());
        response.setPrice(projection.getPrice());
        response.setStockQuantity(projection.getStockQuantity());
        response.setAvailable(projection.isAvailable());
        response.setPharmacyId(projection.getPharmacyId());
        response.setPharmacyName(projection.getPharmacyName());
        response.setAddress(projection.getAddress());
        response.setCity(projection.getCity());
        response.setLatitude(projection.getLatitude());
        response.setLongitude(projection.getLongitude());
        response.setDistanceMeters(projection.getDistanceMeters());
        return response;
    }

    public PharmacySearchResult toPharmacySearchResult(PharmacySearchView view) {
        PharmacySearchResult result = new PharmacySearchResult();
        result.setPharmacyId(view.getPharmacyId());
        result.setPharmacyName(view.getPharmacyName());
        result.setAddress(view.getAddress());
        result.setCity(view.getCity());
        result.setLatitude(view.getLatitude());
        result.setLongitude(view.getLongitude());
        result.setDistanceMeters(view.getDistanceMeters());
        result.setAvailableMedicineCount(view.getAvailableMedicineCount());
        return result;
    }

    public MedicineAvailabilityResponse toMedicineAvailabilityResponse(MedicineSearchView view) {
        MedicineAvailabilityResponse response = new MedicineAvailabilityResponse();
        response.setMedicineId(view.getMedicineId());
        response.setMedicineName(view.getMedicineName());
        response.setGenericName(view.getGenericName());
        response.setBrandName(view.getBrandName());
        response.setPrice(view.getPrice());
        response.setStockQuantity(view.getStockQuantity());
        response.setAvailable(view.isAvailable());
        response.setPharmacyId(view.getPharmacyId());
        response.setPharmacyName(view.getPharmacyName());
        response.setAddress(view.getAddress());
        response.setCity(view.getCity());
        response.setLatitude(view.getLatitude());
        response.setLongitude(view.getLongitude());
        return response;
    }
}