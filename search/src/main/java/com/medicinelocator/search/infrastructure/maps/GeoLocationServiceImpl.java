package com.medicinelocator.search.infrastructure.maps;

import com.medicinelocator.search.application.service.GeoLocationService;
import com.medicinelocator.search.domain.exception.InvalidSearchRequestException;
import org.springframework.stereotype.Component;

@Component
public class GeoLocationServiceImpl implements GeoLocationService {

    private static final double METRES_PER_KM = 1000.0;

    @Override
    public double toMetres(double radiusKm) {
        if (radiusKm <= 0) {
            throw new InvalidSearchRequestException("Radius must be greater than 0 km");
        }
        return radiusKm * METRES_PER_KM;
    }

    @Override
    public boolean isValidCoordinate(double latitude, double longitude) {
        return latitude >= -90.0 && latitude <= 90.0
                && longitude >= -180.0 && longitude <= 180.0;
    }

    /**
     * Builds a PostGIS POINT WKT string.
     * PostGIS convention: POINT(longitude latitude) — longitude comes FIRST.
     */
    @Override
    public String toPostGisPoint(double longitude, double latitude) {
        return String.format("POINT(%f %f)", longitude, latitude);
    }
}