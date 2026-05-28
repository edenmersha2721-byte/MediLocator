package com.medicinelocator.auth.application.command.handler;

import com.medicinelocator.auth.application.command.RegisterPharmacyCommand;
import com.medicinelocator.auth.application.service.EmailService;
import com.medicinelocator.auth.application.service.PasswordHasher;
import com.medicinelocator.auth.application.service.PharmacyService;
import com.medicinelocator.auth.domain.enums.AccountStatus;
import com.medicinelocator.auth.domain.enums.PharmacyStatus;
import com.medicinelocator.auth.domain.model.Pharmacy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RegisterPharmacyHandler {

    private static final Logger log = LoggerFactory.getLogger(RegisterPharmacyHandler.class);

    private final PharmacyService pharmacyService;
    private final PasswordHasher passwordHasher;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    public RegisterPharmacyHandler(PharmacyService pharmacyService,
                                   PasswordHasher passwordHasher,
                                   EmailService emailService,
                                   StringRedisTemplate redisTemplate) {
        this.pharmacyService = pharmacyService;
        this.passwordHasher = passwordHasher;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void handle(RegisterPharmacyCommand command) {
        if (pharmacyService.existsByEmail(command.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + command.getEmail());
        }

        String hashedPassword = passwordHasher.hash(command.getPassword());
        String verificationToken = UUID.randomUUID().toString();

        Pharmacy pharmacy = new Pharmacy(
                UUID.randomUUID(),
                command.getEmail(),
                hashedPassword,
                command.getPharmacyName(),
                command.getLicenseNumber(),
                command.getPhoneNumber(),
                command.getAddress(),
                command.getCity(),
                command.getLatitude(),
                command.getLongitude(),
                AccountStatus.PENDING,
                PharmacyStatus.PENDING,
                false,
                0,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        pharmacyService.save(pharmacy);

        redisTemplate.opsForValue().set(
                "email_verify:" + verificationToken,
                "PHARMACY:" + pharmacy.getId().toString(),
                24,
                TimeUnit.HOURS
        );

        emailService.sendEmailVerification(pharmacy.getEmail(), verificationToken);

        log.info("Pharmacy registered successfully: email={}", pharmacy.getEmail());
    }
}