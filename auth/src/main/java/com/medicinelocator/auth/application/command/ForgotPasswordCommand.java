package com.medicinelocator.auth.application.command;

public class ForgotPasswordCommand {

    private final String email;

    public ForgotPasswordCommand(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }
}