package com.medicinelocator.search.domain.exception;

public class NoPharmaciesFoundException extends RuntimeException {

    public NoPharmaciesFoundException(String message) {
        super(message);
    }

    public static NoPharmaciesFoundException forMedicine(String medicineName, double radiusKm) {
        return new NoPharmaciesFoundException(
                "No pharmacies found carrying '" + medicineName
                        + "' within " + radiusKm + " km of your location");
    }

    public static NoPharmaciesFoundException nearby(double radiusKm) {
        return new NoPharmaciesFoundException(
                "No pharmacies found within " + radiusKm + " km of your location");
    }
}