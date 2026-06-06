package com.medicinelocator.prescription.application.service;

import com.medicinelocator.prescription.domain.model.Prescription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrescriptionService {

    Prescription save(Prescription prescription);

    Optional<Prescription> findById(UUID id);

    Prescription update(Prescription prescription);

    List<Prescription> findByCustomerId(UUID customerId);
}