package com.example.cyclexbe.dto;

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
}
