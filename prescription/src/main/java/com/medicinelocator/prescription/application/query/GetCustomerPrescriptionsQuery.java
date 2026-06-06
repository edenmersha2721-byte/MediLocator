package com.medicinelocator.prescription.application.query;

import java.util.UUID;

public class GetCustomerPrescriptionsQuery {

    private final UUID customerId;
    private final UUID requestingUserId;
    private final String requestingRole;

    public GetCustomerPrescriptionsQuery(UUID customerId, UUID requestingUserId,
                                         String requestingRole) {
        this.customerId = customerId;
        this.requestingUserId = requestingUserId;
        this.requestingRole = requestingRole;
    }

    public UUID getCustomerId() { return customerId; }
    public UUID getRequestingUserId() { return requestingUserId; }
    public String getRequestingRole() { return requestingRole; }
}