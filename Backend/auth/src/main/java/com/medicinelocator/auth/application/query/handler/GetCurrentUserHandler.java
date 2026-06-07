package com.medicinelocator.auth.application.query.handler;

import com.medicinelocator.auth.application.dto.response.UserProfileResponse;
import com.medicinelocator.auth.application.query.GetCurrentUserQuery;
import com.medicinelocator.auth.application.service.AdminService;
import com.medicinelocator.auth.application.service.CustomerService;
import com.medicinelocator.auth.application.service.PharmacyService;
import com.medicinelocator.auth.domain.enums.Role;
import com.medicinelocator.auth.domain.model.Admin;
import com.medicinelocator.auth.domain.model.Customer;
import com.medicinelocator.auth.domain.model.Pharmacy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class GetCurrentUserHandler {

    private final CustomerService customerService;
    private final PharmacyService pharmacyService;
    private final AdminService adminService;

    public GetCurrentUserHandler(CustomerService customerService,
                                 PharmacyService pharmacyService,
                                 AdminService adminService) {
        this.customerService = customerService;
        this.pharmacyService = pharmacyService;
        this.adminService = adminService;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse handle(GetCurrentUserQuery query) {
        if (query.getRole() == Role.CUSTOMER) {
            Customer customer = customerService.findById(query.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
            return new UserProfileResponse(
                    customer.getId(),
                    customer.getEmail(),
                    Role.CUSTOMER.name(),
                    customer.getFirstName() + " " + customer.getLastName(),
                    customer.isEmailVerified()
            );
        }

        if (query.getRole() == Role.PHARMACY) {
            Pharmacy pharmacy = pharmacyService.findById(query.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Pharmacy not found"));
            return new UserProfileResponse(
                    pharmacy.getId(),
                    pharmacy.getEmail(),
                    Role.PHARMACY.name(),
                    pharmacy.getPharmacyName(),
                    pharmacy.isEmailVerified()
            );
        }

        if (query.getRole() == Role.ADMIN) {
            Admin admin = adminService.findById(query.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
            return new UserProfileResponse(
                    admin.getId(),
                    admin.getEmail(),
                    Role.ADMIN.name(),
                    admin.getFirstName() + " " + admin.getLastName(),
                    true
            );
        }

        throw new IllegalArgumentException("Unknown role: " + query.getRole());
    }
}