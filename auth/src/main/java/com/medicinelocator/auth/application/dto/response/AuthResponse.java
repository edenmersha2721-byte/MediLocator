package com.medicinelocator.auth.application.dto.response;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long accessTokenExpiresIn;
    private String role;

    public AuthResponse(String accessToken, String refreshToken, String tokenType,
                        long accessTokenExpiresIn, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.role = role;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public long getAccessTokenExpiresIn() { return accessTokenExpiresIn; }
    public String getRole() { return role; }
}