package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.InspectionResponse;

/**
 * Locks DTO - Trạng thái lock của response
 */
public class LocksResponse {

    /**
     * true nếu response đã submit (bị lock, không cho sửa/xóa/upload thêm)
     */
    private boolean responseLocked;

    public LocksResponse() {
    }

    public LocksResponse(boolean responseLocked) {
        this.responseLocked = responseLocked;
    }

    public static LocksResponse from(InspectionResponse response) {
        if (response == null) {
            return new LocksResponse(false);
        }

        String status = response.getStatus();
        boolean locked = status != null && "SUBMITTED".equalsIgnoreCase(status);

        return new LocksResponse(locked);
    }

    public boolean isResponseLocked() {
        return responseLocked;
    }

    public void setResponseLocked(boolean responseLocked) {
        this.responseLocked = responseLocked;
    }

    @Override
    public String toString() {
        return "LocksResponse{" +
                "responseLocked=" + responseLocked +
                '}';
    }
}