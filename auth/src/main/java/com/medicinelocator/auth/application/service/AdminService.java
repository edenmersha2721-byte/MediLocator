package com.medicinelocator.auth.application.service;

import com.medicinelocator.auth.domain.model.Admin;

import java.util.Optional;
import java.util.UUID;

public interface AdminService {

    Optional<Admin> findByEmail(String email);

    Optional<Admin> findById(UUID id);
}