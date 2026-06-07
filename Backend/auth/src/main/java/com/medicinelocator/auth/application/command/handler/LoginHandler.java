package com.medicinelocator.auth.application.command.handler;

import com.medicinelocator.auth.application.command.LoginCommand;
import com.medicinelocator.auth.application.dto.response.AuthResponse;
import com.medicinelocator.auth.application.service.*;
import com.medicinelocator.auth.domain.enums.AccountStatus;
import com.medicinelocator.auth.domain.enums.Role;
import com.medicinelocator.auth.domain.exception.AccountLockedException;
import com.medicinelocator.auth.domain.exception.EmailNotVerifiedException;
import com.medicinelocator.auth.domain.exception.PharmacyNotApprovedException;
import com.medicinelocator.auth.domain.model.Admin;
import com.medicinelocator.auth.domain.model.Customer;
import com.medicinelocator.auth.domain.model.Pharmacy;
import com.medicinelocator.auth.domain.model.RefreshToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final CustomerService customerService;
    private final PharmacyService pharmacyService;
    private final AdminService adminService;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    @Value("${jwt.access-token-expiration:900000}")
    private long accessTokenExpiration;

    public LoginHandler(CustomerService customerService,
                        PharmacyService pharmacyService,
                        AdminService adminService,
                        PasswordHasher passwordHasher,
                        TokenService tokenService) {
        this.customerService = customerService;
        this.pharmacyService = pharmacyService;
        this.adminService = adminService;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
    }

    @Transactional
    public AuthResponse handle(LoginCommand command) {
        String email = command.getEmail();

        Optional<Customer> customerOpt = customerService.findByEmail(email);
        if (customerOpt.isPresent()) {
            return loginAsCustomer(customerOpt.get(), command.getPassword());
        }

        Optional<Pharmacy> pharmacyOpt = pharmacyService.findByEmail(email);
        if (pharmacyOpt.isPresent()) {
            return loginAsPharmacy(pharmacyOpt.get(), command.getPassword());
        }

        Optional<Admin> adminOpt = adminService.findByEmail(email);
        if (adminOpt.isPresent()) {
            return loginAsAdmin(adminOpt.get(), command.getPassword());
        }

        throw new IllegalArgumentException("Invalid email or password");
    }

    private AuthResponse loginAsCustomer(Customer customer, String rawPassword) {
        if (customer.isAccountLocked()) {
            throw new AccountLockedException("Account is locked. Please try again later.");
        }

        if (!passwordHasher.matches(rawPassword, customer.getPasswordHash())) {
            customer.incrementFailedLoginAttempts();
            if (customer.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                customer.lockAccount();
                log.warn("Customer account locked after {} failed attempts: email={}",
                        MAX_FAILED_ATTEMPTS, customer.getEmail());
            }
            customerService.update(customer);
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!customer.isEmailVerified()) {
            throw new EmailNotVerifiedException("Email address is not verified. Please check your inbox.");
        }

        if (customer.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountLockedException("Account is not active.");
        }

        customer.resetFailedLoginAttempts();
        customerService.update(customer);

        String accessToken = tokenService.generateAccessToken(customer.getId(), customer.getEmail(), Role.CUSTOMER);
        RefreshToken refreshToken = tokenService.generateRefreshToken(customer.getId(), Role.CUSTOMER);

        log.info("Customer logged in: email={}", customer.getEmail());
        return new AuthResponse(accessToken, refreshToken.getTokenHash(), "Bearer", accessTokenExpiration, "CUSTOMER");
    }

    private AuthResponse loginAsPharmacy(Pharmacy pharmacy, String rawPassword) {
        if (pharmacy.isAccountLocked()) {
            throw new AccountLockedException("Account is locked. Please try again later.");
        }

        if (!passwordHasher.matches(rawPassword, pharmacy.getPasswordHash())) {
            pharmacy.incrementFailedLoginAttempts();
            if (pharmacy.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                pharmacy.lockAccount();
                log.warn("Pharmacy account locked after {} failed attempts: email={}",
                        MAX_FAILED_ATTEMPTS, pharmacy.getEmail());
            }
            pharmacyService.update(pharmacy);
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!pharmacy.isEmailVerified()) {
            throw new EmailNotVerifiedException("Email address is not verified. Please check your inbox.");
        }

        if (!pharmacy.isApproved()) {
            throw new PharmacyNotApprovedException("Pharmacy account is pending approval by admin.");
        }

        if (pharmacy.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountLockedException("Account is not active.");
        }

        pharmacy.resetFailedLoginAttempts();
        pharmacyService.update(pharmacy);

        String accessToken = tokenService.generateAccessToken(pharmacy.getId(), pharmacy.getEmail(), Role.PHARMACY);
        RefreshToken refreshToken = tokenService.generateRefreshToken(pharmacy.getId(), Role.PHARMACY);

        log.info("Pharmacy logged in: email={}", pharmacy.getEmail());
        return new AuthResponse(accessToken, refreshToken.getTokenHash(), "Bearer", accessTokenExpiration, "PHARMACY");
    }

    private AuthResponse loginAsAdmin(Admin admin, String rawPassword) {
        log.info("=== ADMIN LOGIN ATTEMPT ===");
        log.info("Email: {}", admin.getEmail());
        log.info("Raw password received: '{}'", rawPassword);
        log.info("Raw password length: {}", rawPassword != null ? rawPassword.length() : "null");

        boolean passwordMatches = passwordHasher.matches(rawPassword, admin.getPasswordHash());
        log.info("Password matches? {}", passwordMatches);

        if (admin.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountLockedException("Admin account is not active.");
        }

        if (!passwordMatches) {
            log.warn("❌ Password match FAILED for admin: {}", admin.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        log.info("✅ Admin login successful: {}", admin.getEmail());

        String accessToken = tokenService.generateAccessToken(admin.getId(), admin.getEmail(), Role.ADMIN);
        RefreshToken refreshToken = tokenService.generateRefreshToken(admin.getId(), Role.ADMIN);

        return new AuthResponse(accessToken, refreshToken.getTokenHash(), "Bearer", accessTokenExpiration, "ADMIN");
    }
}