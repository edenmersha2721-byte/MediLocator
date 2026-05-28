package com.medicinelocator.auth.application.service;

public interface EmailService {

    void sendEmailVerification(String to, String token);

    void sendPasswordReset(String to, String token);

    void sendWelcomeEmail(String to, String name);
}