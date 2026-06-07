package com.medicinelocator.prescription.application.query.handler;

import com.medicinelocator.prescription.application.dto.response.CustomerPrescriptionsResponse;
import com.medicinelocator.prescription.application.dto.response.PrescriptionResponse;
import com.medicinelocator.prescription.application.mapper.PrescriptionMapper;
import com.medicinelocator.prescription.application.query.GetCustomerPrescriptionsQuery;
import com.medicinelocator.prescription.application.service.PrescriptionService;
import com.medicinelocator.prescription.domain.exception.InvalidPrescriptionException;
import com.medicinelocator.prescription.domain.model.Prescription;
import com.medicinelocator.prescription.infrastructure.security.RoleConstants;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class GetCustomerPrescriptionsHandler {

    private final PrescriptionService prescriptionService;
    private final PrescriptionMapper prescriptionMapper;

    public GetCustomerPrescriptionsHandler(PrescriptionService prescriptionService,
                                           PrescriptionMapper prescriptionMapper) {
        this.prescriptionService = prescriptionService;
        this.prescriptionMapper = prescriptionMapper;
    }

    @Transactional(readOnly = true)
    public CustomerPrescriptionsResponse handle(GetCustomerPrescriptionsQuery query) {
        // Authorization: customers can only view their own prescriptions; admins view any
        boolean isAdmin = RoleConstants.ROLE_ADMIN.equals(query.getRequestingRole());
        boolean isSelf  = query.getCustomerId().equals(query.getRequestingUserId());

        if (!isAdmin && !isSelf) {
            throw new InvalidPrescriptionException(
                    "Access denied: you can only view your own prescriptions");
        }

        List<Prescription> prescriptions = prescriptionService.findByCustomerId(query.getCustomerId());
        List<PrescriptionResponse> responses = prescriptions.stream()
                .map(prescriptionMapper::toPrescriptionResponse)
                .toList();

        return new CustomerPrescriptionsResponse(
                query.getCustomerId(),
                responses.size(),
                responses
        );
    }
}