package com.medicinelocator.auth.application.service;

import com.medicinelocator.auth.domain.model.Pharmacy;

import java.util.Optional;
import java.util.UUID;

public interface PharmacyService {

    Pharmacy save(Pharmacy pharmacy);

    Optional<Pharmacy> findByEmail(String email);

    Optional<Pharmacy> findById(UUID id);

    boolean existsByEmail(String email);

    Pharmacy update(Pharmacy pharmacy);
}