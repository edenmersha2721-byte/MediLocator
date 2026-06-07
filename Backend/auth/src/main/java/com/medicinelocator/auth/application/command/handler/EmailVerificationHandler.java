package com.medicinelocator.auth.application.command.handler;

import com.medicinelocator.auth.application.command.VerifyEmailCommand;
import com.medicinelocator.auth.application.service.CustomerService;
import com.medicinelocator.auth.application.service.PharmacyService;
import com.medicinelocator.auth.domain.enums.AccountStatus;
import com.medicinelocator.auth.domain.exception.InvalidTokenException;
import com.medicinelocator.auth.domain.model.Customer;
import com.medicinelocator.auth.domain.model.Pharmacy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class EmailVerificationHandler {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationHandler.class);

    private final CustomerService customerService;
    private final PharmacyService pharmacyService;
    private final StringRedisTemplate redisTemplate;

    public EmailVerificationHandler(CustomerService customerService,
                                    PharmacyService pharmacyService,
                                    StringRedisTemplate redisTemplate) {
        this.customerService = customerService;
        this.pharmacyService = pharmacyService;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void handle(VerifyEmailCommand command) {
        String redisKey = "email_verify:" + command.getToken();
        String value = redisTemplate.opsForValue().get(redisKey);

        if (value == null) {
            throw new InvalidTokenException("Email verification token is invalid or has expired");
        }

        String[] parts = value.split(":");
        String userType = parts[0];
        UUID userId = UUID.fromString(parts[1]);

        switch (userType) {
            case "CUSTOMER" -> {
                Customer customer = customerService.findById(userId)
                        .orElseThrow(() -> new InvalidTokenException("Customer not found"));
                customer.verifyEmail();
                customer.setAccountStatus(AccountStatus.ACTIVE);
                customerService.update(customer);
                log.info("Email verified for customer: id={}", userId);
            }
            case "PHARMACY" -> {
                Pharmacy pharmacy = pharmacyService.findById(userId)
                        .orElseThrow(() -> new InvalidTokenException("Pharmacy not found"));
                pharmacy.verifyEmail();
                // Pharmacy status remains PENDING until admin approval
                pharmacyService.update(pharmacy);
                log.info("Email verified for pharmacy: id={}", userId);
            }
            default -> throw new InvalidTokenException("Invalid token data");
        }

        redisTemplate.delete(redisKey);
    }
}