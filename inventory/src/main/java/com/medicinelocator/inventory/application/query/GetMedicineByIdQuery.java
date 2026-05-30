package com.medicinelocator.inventory.application.query;

import java.util.UUID;

public class GetMedicineByIdQuery {

    private final UUID medicineId;

    public GetMedicineByIdQuery(UUID medicineId) {
        this.medicineId = medicineId;
    }

    public UUID getMedicineId() { return medicineId; }
}