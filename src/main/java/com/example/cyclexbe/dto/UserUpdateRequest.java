package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @Email(message = "Email is invalid")
    public String email;

    // optional: nếu muốn đổi mật khẩu thì gửi password mới
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    public String password;

    public String fullName;
    public String phone;
    public Role role;
    public String status;
    public String cccd;
    public String avatarUrl;
    public Boolean isVerify;
}
