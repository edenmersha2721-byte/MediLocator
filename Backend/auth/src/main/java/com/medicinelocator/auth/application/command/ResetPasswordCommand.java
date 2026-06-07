package com.medicinelocator.auth.application.command;

public class ResetPasswordCommand {

    private final String token;
    private final String newPassword;

    public ResetPasswordCommand(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public String getToken() { return token; }
    public String getNewPassword() { return newPassword; }
}