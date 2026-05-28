package com.medicinelocator.auth.infrastructure.persistence.repository;

import com.medicinelocator.auth.application.service.PharmacyService;
import com.medicinelocator.auth.domain.model.Pharmacy;
import com.medicinelocator.auth.infrastructure.persistence.entity.PharmacyEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PharmacyRepositoryImpl implements PharmacyService {

    private final PharmacyJpaRepository jpaRepository;

    public PharmacyRepositoryImpl(PharmacyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Pharmacy save(Pharmacy pharmacy) {
        PharmacyEntity entity = toEntity(pharmacy);
        PharmacyEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Pharmacy> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<Pharmacy> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Pharmacy update(Pharmacy pharmacy) {
        PharmacyEntity entity = jpaRepository.findById(pharmacy.getId())
                .orElseThrow(() -> new IllegalArgumentException("Pharmacy not found: " + pharmacy.getId()));
        updateEntity(entity, pharmacy);
        PharmacyEntity updated = jpaRepository.save(entity);
        return toDomain(updated);
    }

    private PharmacyEntity toEntity(Pharmacy pharmacy) {
        PharmacyEntity entity = new PharmacyEntity();
        entity.setId(pharmacy.getId());
        entity.setEmail(pharmacy.getEmail());
        entity.setPasswordHash(pharmacy.getPasswordHash());
        entity.setPharmacyName(pharmacy.getPharmacyName());
        entity.setLicenseNumber(pharmacy.getLicenseNumber());
        entity.setPhoneNumber(pharmacy.getPhoneNumber());
        entity.setAddress(pharmacy.getAddress());
        entity.setCity(pharmacy.getCity());
        entity.setLatitude(pharmacy.getLatitude());
        entity.setLongitude(pharmacy.getLongitude());
        entity.setAccountStatus(pharmacy.getAccountStatus());
        entity.setPharmacyStatus(pharmacy.getPharmacyStatus());
        entity.setEmailVerified(pharmacy.isEmailVerified());
        entity.setFailedLoginAttempts(pharmacy.getFailedLoginAttempts());
        entity.setLockedUntil(pharmacy.getLockedUntil());
        return entity;
    }

    private void updateEntity(PharmacyEntity entity, Pharmacy pharmacy) {
        entity.setEmail(pharmacy.getEmail());
        entity.setPasswordHash(pharmacy.getPasswordHash());
        entity.setPharmacyName(pharmacy.getPharmacyName());
        entity.setLicenseNumber(pharmacy.getLicenseNumber());
        entity.setPhoneNumber(pharmacy.getPhoneNumber());
        entity.setAddress(pharmacy.getAddress());
        entity.setCity(pharmacy.getCity());
        entity.setLatitude(pharmacy.getLatitude());
        entity.setLongitude(pharmacy.getLongitude());
        entity.setAccountStatus(pharmacy.getAccountStatus());
        entity.setPharmacyStatus(pharmacy.getPharmacyStatus());
        entity.setEmailVerified(pharmacy.isEmailVerified());
        entity.setFailedLoginAttempts(pharmacy.getFailedLoginAttempts());
        entity.setLockedUntil(pharmacy.getLockedUntil());
    }

    private Pharmacy toDomain(PharmacyEntity entity) {
        return new Pharmacy(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getPharmacyName(),
                entity.getLicenseNumber(),
                entity.getPhoneNumber(),
                entity.getAddress(),
                entity.getCity(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getAccountStatus(),
                entity.getPharmacyStatus(),
                entity.isEmailVerified(),
                entity.getFailedLoginAttempts(),
                entity.getLockedUntil(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}