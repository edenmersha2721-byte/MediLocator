package com.medicinelocator.prescription.application.dto.response;

import java.util.List;
import java.util.UUID;

public class CustomerPrescriptionsResponse {

    private UUID customerId;
    private int totalCount;
    private List<PrescriptionResponse> prescriptions;

    public CustomerPrescriptionsResponse() {
    }

    public CustomerPrescriptionsResponse(UUID customerId, int totalCount,
                                         List<PrescriptionResponse> prescriptions) {
        this.customerId = customerId;
        this.totalCount = totalCount;
        this.prescriptions = prescriptions;
    }

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public List<PrescriptionResponse> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(List<PrescriptionResponse> prescriptions) {
        this.prescriptions = prescriptions;
    }
}