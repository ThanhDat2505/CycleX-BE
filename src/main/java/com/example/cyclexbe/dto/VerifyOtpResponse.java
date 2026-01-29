package com.example.cyclexbe.dto;

public class VerifyOtpResponse {
    public String message;
    public UserResponse user;

    public VerifyOtpResponse() {}

    public void setMessage(String message) {
        this.message = message;
    }
    public void setUser(UserResponse user) {
        this.user = user;
    }
}
