package com.medicinelocator.search.infrastructure.persistence.repository;

import com.medicinelocator.search.application.service.SearchService;
import com.medicinelocator.search.domain.projection.NearbyMedicineProjection;
import com.medicinelocator.search.domain.projection.PharmacySearchView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class SearchRepositoryImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchRepositoryImpl.class);
    private static final double METRES_PER_KM = 1000.0;

    private final SearchJpaRepository jpaRepository;

    public SearchRepositoryImpl(SearchJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Page<NearbyMedicineProjection> searchMedicinesByName(String searchTerm,
                                                                Double latitude,
                                                                Double longitude,
                                                                Double radiusKm,
                                                                Boolean requiresPrescription,
                                                                String category,
                                                                Pageable pageable) {
        int limit  = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        if (latitude != null && longitude != null) {
            Double radiusMetres = radiusKm != null ? radiusKm * METRES_PER_KM : null;

            List<Object[]> rows = jpaRepository.searchMedicinesByNameWithLocation(
                    searchTerm, latitude, longitude, radiusMetres,
                    requiresPrescription, category, limit, offset);
            long total = jpaRepository.countSearchMedicinesByNameWithLocation(
                    searchTerm, latitude, longitude, radiusMetres,
                    requiresPrescription, category);

            return new PageImpl<>(rows.stream().map(this::mapToNearbyProjection).toList(),
                    pageable, total);
        } else {
            List<Object[]> rows = jpaRepository.searchMedicinesByNameNoLocation(
                    searchTerm, requiresPrescription, category, limit, offset);
            long total = jpaRepository.countSearchMedicinesByNameNoLocation(
                    searchTerm, requiresPrescription, category);

            return new PageImpl<>(rows.stream().map(this::mapToNearbyProjectionNoDistance).toList(),
                    pageable, total);
        }
    }

    @Override
    public Page<PharmacySearchView> searchNearbyPharmacies(double latitude,
                                                           double longitude,
                                                           double radiusKm,
                                                           Pageable pageable) {
        double radiusMetres = radiusKm * METRES_PER_KM;
        int limit  = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        List<Object[]> rows = jpaRepository.findNearbyPharmacies(
                latitude, longitude, radiusMetres, limit, offset);
        long total = jpaRepository.countNearbyPharmacies(latitude, longitude, radiusMetres);

        return new PageImpl<>(rows.stream().map(this::mapToPharmacyView).toList(), pageable, total);
    }

    @Override
    public Page<NearbyMedicineProjection> searchByPrescriptionMedicines(List<String> medicineNames,
                                                                        Double latitude,
                                                                        Double longitude,
                                                                        Double radiusKm,
                                                                        Pageable pageable) {
        String[] namesArray = medicineNames.toArray(new String[0]);
        Double radiusMetres = (radiusKm != null) ? radiusKm * METRES_PER_KM : null;
        int limit  = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        List<Object[]> rows = jpaRepository.searchByPrescriptionMedicines(
                namesArray, latitude, longitude, radiusMetres, limit, offset);
        long total = jpaRepository.countByPrescriptionMedicines(
                namesArray, latitude, longitude, radiusMetres);

        return new PageImpl<>(rows.stream().map(this::mapToNearbyProjection).toList(),
                pageable, total);
    }

    private NearbyMedicineProjection mapToNearbyProjection(Object[] row) {
        return new NearbyMedicineProjection(
                toUUID(row[0]),                      // medicine_id
                toString(row[1]),                    // medicine_name
                toString(row[2]),                    // generic_name
                toString(row[3]),                    // brand_name
                toString(row[4]),                    // category
                toBoolean(row[5]),                   // requires_prescription
                toBigDecimal(row[6]),                // price
                toInt(row[7]),                       // stock_quantity
                toBoolean(row[8]),                   // available
                toUUID(row[9]),                      // pharmacy_id
                toString(row[10]),                   // pharmacy_name
                toString(row[11]),                   // address
                toString(row[12]),                   // city
                toDouble(row[13]),                   // latitude
                toDouble(row[14]),                   // longitude
                toDouble(row[15])                    // distance_meters
        );
    }

    private NearbyMedicineProjection mapToNearbyProjectionNoDistance(Object[] row) {
        return new NearbyMedicineProjection(
                toUUID(row[0]),
                toString(row[1]),
                toString(row[2]),
                toString(row[3]),
                toString(row[4]),
                toBoolean(row[5]),
                toBigDecimal(row[6]),
                toInt(row[7]),
                toBoolean(row[8]),
                toUUID(row[9]),
                toString(row[10]),
                toString(row[11]),
                toString(row[12]),
                toDouble(row[13]),
                toDouble(row[14]),
                0.0  // no distance when no location provided
        );
    }

    private PharmacySearchView mapToPharmacyView(Object[] row) {
        return new PharmacySearchView(
                toUUID(row[0]),       // pharmacy_id
                toString(row[1]),     // pharmacy_name
                toString(row[2]),     // address
                toString(row[3]),     // city
                toDouble(row[4]),     // latitude
                toDouble(row[5]),     // longitude
                toDouble(row[6]),     // distance_meters
                toInt(row[7])         // available_count
        );
    }


    private UUID toUUID(Object o) {
        if (o == null) return null;
        return (o instanceof UUID u) ? u : UUID.fromString(o.toString());
    }

    private String toString(Object o) {
        return o != null ? o.toString() : null;
    }

    private boolean toBoolean(Object o) {
        if (o == null) return false;
        return (o instanceof Boolean b) ? b : Boolean.parseBoolean(o.toString());
    }

    private BigDecimal toBigDecimal(Object o) {
        if (o == null) return BigDecimal.ZERO;
        return (o instanceof BigDecimal bd) ? bd : new BigDecimal(o.toString());
    }

    private int toInt(Object o) {
        if (o == null) return 0;
        return (o instanceof Number n) ? n.intValue() : Integer.parseInt(o.toString());
    }

    private double toDouble(Object o) {
        if (o == null) return 0.0;
        return (o instanceof Number n) ? n.doubleValue() : Double.parseDouble(o.toString());
    }
}