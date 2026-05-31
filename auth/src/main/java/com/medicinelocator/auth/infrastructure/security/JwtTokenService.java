package com.medicinelocator.auth.infrastructure.security;

import com.medicinelocator.auth.application.service.TokenService;
import com.medicinelocator.auth.domain.enums.Role;
import com.medicinelocator.auth.domain.model.RefreshToken;
import com.medicinelocator.auth.infrastructure.persistence.repository.RefreshTokenRepositoryImpl;
import com.medicinelocator.auth.infrastructure.redis.RedisTokenStore;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenService implements TokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);

    private final JwtProperties jwtProperties;
    private final RefreshTokenRepositoryImpl refreshTokenRepository;
    private final RedisTokenStore redisTokenStore;

    public JwtTokenService(JwtProperties jwtProperties,
                           RefreshTokenRepositoryImpl refreshTokenRepository,
                           RedisTokenStore redisTokenStore) {
        this.jwtProperties = jwtProperties;
        this.refreshTokenRepository = refreshTokenRepository;
        this.redisTokenStore = redisTokenStore;
    }

    @Override
    public String generateAccessToken(UUID userId, String email, Role role) {
        SecretKey key = getSigningKey();
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role.name())
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtProperties.getAccessTokenExpiration()))
                .signWith(key)
                .compact();
    }

    @Override
    public RefreshToken generateRefreshToken(UUID userId, Role role) {
        String rawToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000);

        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID(),
                rawToken,
                userId,
                role,
                false,
                false,
                expiresAt,
                LocalDateTime.now()
        );

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        // Return a copy with the raw token (not the hash) for initial delivery
        return new RefreshToken(
                saved.getId(),
                rawToken,
                saved.getUserId(),
                saved.getUserRole(),
                saved.isRevoked(),
                saved.isUsed(),
                saved.getExpiresAt(),
                saved.getCreatedAt()
        );
    }

    @Override
    public RefreshToken findRefreshToken(String rawToken) {
        return refreshTokenRepository.findByTokenHash(rawToken);
    }

    @Override
    public void revokeRefreshToken(String tokenHash) {
        refreshTokenRepository.revokeByTokenHash(tokenHash);
    }

    @Override
    public void revokeAllUserRefreshTokens(UUID userId, Role role) {
        refreshTokenRepository.revokeAllByUserIdAndRole(userId, role);
    }

    @Override
    public void blacklistAccessToken(String accessToken, long remainingTtlMillis) {
        redisTokenStore.blacklistToken(accessToken, remainingTtlMillis);
    }

    @Override
    public boolean isAccessTokenBlacklisted(String accessToken) {
        return redisTokenStore.isBlacklisted(accessToken);
    }

    @Override
    public long getAccessTokenRemainingTtl(String accessToken) {
        try {
            Claims claims = parseClaims(accessToken);
            long expirationMillis = claims.getExpiration().getTime();
            long remainingMillis = expirationMillis - System.currentTimeMillis();
            return Math.max(remainingMillis, 0);
        } catch (Exception e) {
            log.warn("Could not parse token for TTL calculation: {}", e.getMessage());
            return 0;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        System.out.println("JWT Secret from Spring: " + jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.getSecret()));

    }
}