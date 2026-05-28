package com.medicinelocator.auth.application.command;

public class LogoutCommand {

    private final String accessToken;
    private final String refreshToken;

    public LogoutCommand(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
}