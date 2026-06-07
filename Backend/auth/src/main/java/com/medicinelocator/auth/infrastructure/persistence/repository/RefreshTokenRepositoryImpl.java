package com.medicinelocator.auth.infrastructure.persistence.repository;

import com.medicinelocator.auth.domain.enums.Role;
import com.medicinelocator.auth.domain.exception.InvalidTokenException;
import com.medicinelocator.auth.domain.model.RefreshToken;
import com.medicinelocator.auth.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RefreshTokenRepositoryImpl {

    private final RefreshTokenJpaRepository jpaRepository;

    public RefreshTokenRepositoryImpl(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public RefreshToken save(RefreshToken token) {
        RefreshTokenEntity entity = toEntity(token);
        RefreshTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    public RefreshToken findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash)
                .map(this::toDomain)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));
    }

    @Transactional
    public void revokeByTokenHash(String tokenHash) {
        jpaRepository.revokeByTokenHash(tokenHash);
    }

    @Transactional
    public void revokeAllByUserIdAndRole(UUID userId, Role role) {
        jpaRepository.revokeAllByUserIdAndRole(userId, role);
    }

    private RefreshTokenEntity toEntity(RefreshToken token) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(token.getId());
        entity.setTokenHash(token.getTokenHash());
        entity.setUserId(token.getUserId());
        entity.setUserRole(token.getUserRole());
        entity.setRevoked(token.isRevoked());
        entity.setUsed(token.isUsed());
        entity.setExpiresAt(token.getExpiresAt());
        return entity;
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getTokenHash(),
                entity.getUserId(),
                entity.getUserRole(),
                entity.isRevoked(),
                entity.isUsed(),
                entity.getExpiresAt(),
                entity.getCreatedAt()
        );
    }
}