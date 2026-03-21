package com.example.cyclexbe.dto;

import com.example.cyclexbe.domain.enums.Role;

import java.util.List;

public class AdminUserListResponse {

    public List<UserResponse> items;
    public long total;
    public int page;
    public int pageSize;
    public int totalPages;

    public AdminUserListResponse() {}

    public AdminUserListResponse(List<UserResponse> items, long total, int page, int pageSize) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }
}
