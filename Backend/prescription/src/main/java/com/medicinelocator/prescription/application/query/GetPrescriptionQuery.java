package com.medicinelocator.prescription.application.query;

import java.util.UUID;

public class GetPrescriptionQuery {

    private final UUID prescriptionId;
    private final UUID requestingUserId;

    public GetPrescriptionQuery(UUID prescriptionId, UUID requestingUserId) {
        this.prescriptionId = prescriptionId;
        this.requestingUserId = requestingUserId;
    }

    public UUID getPrescriptionId() { return prescriptionId; }
    public UUID getRequestingUserId() { return requestingUserId; }
}