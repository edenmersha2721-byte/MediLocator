package com.medicinelocator.search.application.service;

/**
 * Application-layer interface for geo utilities.
 * Implementations calculate distances and validate coordinate boundaries.
 * The backend NEVER detects GPS location — coordinates always come from the frontend.
 */
public interface GeoLocationService {

    /**
     * Convert radius in kilometres to metres.
     */
    double toMetres(double radiusKm);

    /**
     * Validate that coordinates are within legal bounds.
     */
    boolean isValidCoordinate(double latitude, double longitude);

    /**
     * Build a PostGIS POINT literal string for a given lon/lat.
     * PostGIS convention: longitude first, latitude second.
     */
    String toPostGisPoint(double longitude, double latitude);
}