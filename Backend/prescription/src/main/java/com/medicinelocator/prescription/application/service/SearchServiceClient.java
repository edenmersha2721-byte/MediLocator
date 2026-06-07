package com.medicinelocator.prescription.application.service;

import java.util.List;

public interface SearchServiceClient {

    Object searchMedicines(List<String> medicineNames,
                           Double latitude,
                           Double longitude,
                           Double radiusKm);
}