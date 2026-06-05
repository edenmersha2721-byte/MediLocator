package com.medicinelocator.auth.application.command.handler;

import com.medicinelocator.auth.application.command.ApprovePharmacyCommand;
import com.medicinelocator.auth.domain.enums.AccountStatus;
import com.medicinelocator.auth.domain.enums.PharmacyStatus;
import com.medicinelocator.auth.domain.model.Pharmacy;
import com.medicinelocator.auth.application.service.PharmacyService;
import com.medicinelocator.auth.infrastructure.persistence.repository.PharmacyRepositoryImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class ApprovePharmacyHandler {
    private final PharmacyRepositoryImpl pharmacyRepository;

    public ApprovePharmacyHandler(PharmacyRepositoryImpl pharmacyRepository) {
        this.pharmacyRepository = pharmacyRepository;
    }

    public void handle(ApprovePharmacyCommand command) {
        // Retrieve the domain model through your domain infrastructure layer
        Pharmacy pharmacy = pharmacyRepository.findById(command.getPharmacyId())
                .orElseThrow(() -> new IllegalArgumentException("Pharmacy not found with ID: " + command.getPharmacyId()));

        if (command.isApprove()) {
            pharmacy.setPharmacyStatus(PharmacyStatus.APPROVED);
            pharmacy.setAccountStatus(AccountStatus.ACTIVE);
        } else {
            pharmacy.setPharmacyStatus(PharmacyStatus.REJECTED);
            // Lock or suspend the account from executing login flows
            pharmacy.setAccountStatus(AccountStatus.LOCKED);
        }

        pharmacy.setUpdatedAt(LocalDateTime.now());

        // Save the updated domain root back into your persistence engine
        pharmacyRepository.save(pharmacy);
    }
}