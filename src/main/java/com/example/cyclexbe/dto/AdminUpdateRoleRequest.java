package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.Role;
import jakarta.validation.constraints.NotNull;

public class AdminUpdateRoleRequest {
    @NotNull(message = "Role is required")
    public Role role;
}
