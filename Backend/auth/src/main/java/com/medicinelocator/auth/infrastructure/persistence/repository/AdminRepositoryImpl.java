package com.medicinelocator.auth.infrastructure.persistence.repository;

import com.medicinelocator.auth.application.service.AdminService;
import com.medicinelocator.auth.domain.model.Admin;
import com.medicinelocator.auth.infrastructure.persistence.entity.AdminEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AdminRepositoryImpl implements AdminService {

    private final AdminJpaRepository jpaRepository;

    public AdminRepositoryImpl(AdminJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<Admin> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private Admin toDomain(AdminEntity entity) {
        return new Admin(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getAccountStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}