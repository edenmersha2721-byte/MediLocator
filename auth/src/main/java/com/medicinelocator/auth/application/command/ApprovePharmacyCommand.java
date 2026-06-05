package com.medicinelocator.auth.application.command;

import java.util.UUID;

public class ApprovePharmacyCommand {
    private final UUID pharmacyId;
    private final boolean approve;

    public ApprovePharmacyCommand(UUID pharmacyId, boolean approve) {
        this.pharmacyId = pharmacyId;
        this.approve = approve;
    }

    public UUID getPharmacyId() {
        return pharmacyId;
    }

    public boolean isApprove() {
        return approve;
    }
}