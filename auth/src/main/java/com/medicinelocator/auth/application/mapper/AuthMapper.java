package com.medicinelocator.auth.application.mapper;

import com.medicinelocator.auth.application.command.*;
import com.medicinelocator.auth.application.dto.request.*;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public RegisterCustomerCommand toRegisterCustomerCommand(CustomerRegisterRequest request) {
        return new RegisterCustomerCommand(
                request.getEmail().trim().toLowerCase(),
                request.getPassword(),
                request.getFirstName().trim(),
                request.getLastName().trim(),
                request.getPhoneNumber()
        );
    }

    public RegisterPharmacyCommand toRegisterPharmacyCommand(PharmacyRegisterRequest request) {
        return new RegisterPharmacyCommand(
                request.getEmail().trim().toLowerCase(),
                request.getPassword(),
                request.getPharmacyName().trim(),
                request.getLicenseNumber().trim(),
                request.getPhoneNumber(),
                request.getAddress().trim(),
                request.getCity().trim(),
                request.getLatitude(),
                request.getLongitude()
        );
    }

    public LoginCommand toLoginCommand(LoginRequest request) {
        return new LoginCommand(
                request.getEmail().trim().toLowerCase(),
                request.getPassword()
        );
    }

    public ForgotPasswordCommand toForgotPasswordCommand(ForgotPasswordRequest request) {
        return new ForgotPasswordCommand(request.getEmail().trim().toLowerCase());
    }

    public ResetPasswordCommand toResetPasswordCommand(ResetPasswordRequest request) {
        return new ResetPasswordCommand(request.getToken().trim(), request.getNewPassword());
    }
}