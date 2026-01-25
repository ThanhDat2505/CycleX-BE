package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.User;

import java.time.LocalDateTime;

public class UserResponse {

    public Integer userId;
    public String email;
    public String fullName;
    public String phone;
    public String role;
    public boolean isVerify;
    public String status;
    public String cccd;
    public String avatarUrl;

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public LocalDateTime lastLogin;

    public UserResponse() {}
    public UserResponse(Integer userId, String email, String fullName, String phone, String role, boolean isVerify, String status, String cccd, String avatarUrl, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLogin) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.isVerify = isVerify;
        this.status = status;
        this.cccd = cccd;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLogin = lastLogin;
    }


    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.isVerify(),
                user.getStatus(),
                user.getCccd(),
                user.getAvatarUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLogin()
        );
    }

}
