package com.medicinelocator.auth.application.command.handler;

import com.medicinelocator.auth.application.command.ForgotPasswordCommand;
import com.medicinelocator.auth.application.command.ResetPasswordCommand;
import com.medicinelocator.auth.application.service.*;
import com.medicinelocator.auth.domain.exception.InvalidTokenException;
import com.medicinelocator.auth.domain.model.Admin;
import com.medicinelocator.auth.domain.model.Customer;
import com.medicinelocator.auth.domain.model.Pharmacy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class PasswordResetHandler {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetHandler.class);

    private final CustomerService customerService;
    private final PharmacyService pharmacyService;
    private final AdminService adminService;
    private final PasswordHasher passwordHasher;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    public PasswordResetHandler(CustomerService customerService,
                                PharmacyService pharmacyService,
                                AdminService adminService,
                                PasswordHasher passwordHasher,
                                EmailService emailService,
                                StringRedisTemplate redisTemplate) {
        this.customerService = customerService;
        this.pharmacyService = pharmacyService;
        this.adminService = adminService;
        this.passwordHasher = passwordHasher;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void handleForgotPassword(ForgotPasswordCommand command) {
        String email = command.getEmail();
        String resetToken = UUID.randomUUID().toString();
        boolean found = false;

        Optional<Customer> customer = customerService.findByEmail(email);
        if (customer.isPresent()) {
            redisTemplate.opsForValue().set(
                    "pwd_reset:" + resetToken,
                    "CUSTOMER:" + customer.get().getId().toString(),
                    1, TimeUnit.HOURS
            );
            found = true;
        }

        if (!found) {
            Optional<Pharmacy> pharmacy = pharmacyService.findByEmail(email);
            if (pharmacy.isPresent()) {
                redisTemplate.opsForValue().set(
                        "pwd_reset:" + resetToken,
                        "PHARMACY:" + pharmacy.get().getId().toString(),
                        1, TimeUnit.HOURS
                );
                found = true;
            }
        }

        if (!found) {
            Optional<Admin> admin = adminService.findByEmail(email);
            if (admin.isPresent()) {
                redisTemplate.opsForValue().set(
                        "pwd_reset:" + resetToken,
                        "ADMIN:" + admin.get().getId().toString(),
                        1, TimeUnit.HOURS
                );
                found = true;
            }
        }

        // Always send response to prevent email enumeration
        if (found) {
            emailService.sendPasswordReset(email, resetToken);
            log.info("Password reset email sent to: {}", email);
        } else {
            log.warn("Password reset requested for non-existing email: {}", email);
        }
    }

    @Transactional
    public void handleResetPassword(ResetPasswordCommand command) {
        String redisKey = "pwd_reset:" + command.getToken();
        String value = redisTemplate.opsForValue().get(redisKey);

        if (value == null) {
            throw new InvalidTokenException("Password reset token is invalid or has expired");
        }

        String[] parts = value.split(":");
        String userType = parts[0];
        UUID userId = UUID.fromString(parts[1]);
        String newPasswordHash = passwordHasher.hash(command.getNewPassword());

        switch (userType) {
            case "CUSTOMER" -> {
                Customer customer = customerService.findById(userId)
                        .orElseThrow(() -> new InvalidTokenException("User not found"));
                customer.setPasswordHash(newPasswordHash);
                customerService.update(customer);
            }
            case "PHARMACY" -> {
                Pharmacy pharmacy = pharmacyService.findById(userId)
                        .orElseThrow(() -> new InvalidTokenException("User not found"));
                pharmacy.setPasswordHash(newPasswordHash);
                pharmacyService.update(pharmacy);
            }
            case "ADMIN" -> {
                log.warn("Admin password reset attempted via token for userId={}", userId);
                throw new InvalidTokenException("Admin password reset is not supported this way");
            }
            default -> throw new InvalidTokenException("Invalid token data");
        }

        redisTemplate.delete(redisKey);
        log.info("Password reset successful for userId={}", userId);
    }
}