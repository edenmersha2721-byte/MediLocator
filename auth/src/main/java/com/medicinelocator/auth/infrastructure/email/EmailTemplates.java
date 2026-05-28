package com.medicinelocator.auth.infrastructure.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplates {

    private final String baseUrl;

    public EmailTemplates(@Value("${app.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String verificationSubject() {
        return "Verify your Medicine Locator email address";
    }

    public String verificationBody(String token) {
        String verificationUrl = baseUrl + "/api/auth/verify-email?token=" + token;
        return """
                <html>
                <body>
                    <h2>Email Verification</h2>
                    <p>Thank you for registering with Medicine Locator.</p>
                    <p>Please verify your email address by clicking the link below:</p>
                    <a href="%s">Verify Email Address</a>
                    <p>This link will expire in 24 hours.</p>
                    <p>If you did not create an account, please ignore this email.</p>
                </body>
                </html>
                """.formatted(verificationUrl);
    }

    public String passwordResetSubject() {
        return "Reset your Medicine Locator password";
    }

    public String passwordResetBody(String token) {
        String resetUrl = baseUrl + "/api/auth/reset-password?token=" + token;
        return """
                <html>
                <body>
                    <h2>Password Reset Request</h2>
                    <p>We received a request to reset your password.</p>
                    <p>Click the link below to reset your password:</p>
                    <a href="%s">Reset Password</a>
                    <p>This link will expire in 1 hour.</p>
                    <p>If you did not request a password reset, please ignore this email.</p>
                </body>
                </html>
                """.formatted(resetUrl);
    }

    public String welcomeSubject() {
        return "Welcome to Medicine Locator!";
    }

    public String welcomeBody(String name) {
        return """
                <html>
                <body>
                    <h2>Welcome, %s!</h2>
                    <p>Your account has been successfully verified.</p>
                    <p>You can now start using Medicine Locator to find medicines and pharmacies near you.</p>
                </body>
                </html>
                """.formatted(name);
    }
}