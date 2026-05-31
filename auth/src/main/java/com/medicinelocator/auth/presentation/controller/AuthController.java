package com.medicinelocator.auth.presentation.controller;

import com.medicinelocator.auth.application.command.*;
import com.medicinelocator.auth.application.command.handler.*;
import com.medicinelocator.auth.application.dto.request.*;
import com.medicinelocator.auth.application.dto.response.*;
import com.medicinelocator.auth.application.mapper.AuthMapper;
import com.medicinelocator.auth.application.query.GetCurrentUserQuery;
import com.medicinelocator.auth.application.query.handler.GetCurrentUserHandler;
import com.medicinelocator.auth.domain.enums.Role;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterCustomerHandler registerCustomerHandler;
    private final RegisterPharmacyHandler registerPharmacyHandler;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final RefreshTokenHandler refreshTokenHandler;
    private final PasswordResetHandler passwordResetHandler;
    private final EmailVerificationHandler emailVerificationHandler;
    private final GetCurrentUserHandler getCurrentUserHandler;
    private final AuthMapper authMapper;

    public AuthController(RegisterCustomerHandler registerCustomerHandler,
                          RegisterPharmacyHandler registerPharmacyHandler,
                          LoginHandler loginHandler,
                          LogoutHandler logoutHandler,
                          RefreshTokenHandler refreshTokenHandler,
                          PasswordResetHandler passwordResetHandler,
                          EmailVerificationHandler emailVerificationHandler,
                          GetCurrentUserHandler getCurrentUserHandler,
                          AuthMapper authMapper) {
        this.registerCustomerHandler = registerCustomerHandler;
        this.registerPharmacyHandler = registerPharmacyHandler;
        this.loginHandler = loginHandler;
        this.logoutHandler = logoutHandler;
        this.refreshTokenHandler = refreshTokenHandler;
        this.passwordResetHandler = passwordResetHandler;
        this.emailVerificationHandler = emailVerificationHandler;
        this.getCurrentUserHandler = getCurrentUserHandler;
        this.authMapper = authMapper;
    }

    @PostMapping("/register/customer")
    public ResponseEntity<MessageResponse> registerCustomer(
            @Valid @RequestBody CustomerRegisterRequest request) {
        RegisterCustomerCommand command = authMapper.toRegisterCustomerCommand(request);
        registerCustomerHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Registration successful. Please check your email to verify your account."));
    }

    @PostMapping("/register/pharmacy")
    public ResponseEntity<MessageResponse> registerPharmacy(
            @Valid @RequestBody PharmacyRegisterRequest request) {
        RegisterPharmacyCommand command = authMapper.toRegisterPharmacyCommand(request);
        registerPharmacyHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Pharmacy registration successful. Please verify your email. Approval from admin is required before login."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = authMapper.toLoginCommand(request);
        AuthResponse response = loginHandler.handle(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false) String refreshToken) {
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }
        LogoutCommand command = new LogoutCommand(accessToken, refreshToken);
        logoutHandler.handle(command);
        return ResponseEntity.ok(new MessageResponse("Logged out successfully."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
        RefreshTokenCommand command = new RefreshTokenCommand(refreshToken);
        AuthResponse response = refreshTokenHandler.handle(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestParam String token) {
        emailVerificationHandler.handle(new VerifyEmailCommand(token));
        System.out.println("TOKEN = " + token);
        return ResponseEntity.ok(new MessageResponse("Email verified successfully. You can now log in."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetHandler.handleForgotPassword(authMapper.toForgotPasswordCommand(request));
        return ResponseEntity.ok(new MessageResponse("If an account exists with that email, a password reset link has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordResetHandler.handleResetPassword(authMapper.toResetPasswordCommand(request));
        return ResponseEntity.ok(new MessageResponse("Password reset successfully. You can now log in with your new password."));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        GetCurrentUserQuery query = new GetCurrentUserQuery(
                UUID.fromString(userId),
                Role.valueOf(role)
        );
        UserProfileResponse response = getCurrentUserHandler.handle(query);
        return ResponseEntity.ok(response);
    }
}