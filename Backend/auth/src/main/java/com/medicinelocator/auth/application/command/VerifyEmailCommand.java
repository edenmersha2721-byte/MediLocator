package com.medicinelocator.auth.application.command;

public class VerifyEmailCommand {

    private final String token;

    public VerifyEmailCommand(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
}