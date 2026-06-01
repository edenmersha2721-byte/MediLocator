package com.medicinelocator.inventory.application.query;

import java.util.UUID;

public class GetMedicineByIdQuery {

    private final UUID pharmacyId;
    private final UUID medicineId;

    public GetMedicineByIdQuery(UUID pharmacyId, UUID medicineId) {
        this.pharmacyId = pharmacyId;
        this.medicineId = medicineId;
    }

    public UUID getPharmacyId() { return pharmacyId; }
    public UUID getMedicineId() { return medicineId; }
}