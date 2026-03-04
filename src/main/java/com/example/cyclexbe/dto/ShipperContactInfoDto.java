package com.example.cyclexbe.dto;

/**
 * DTO for contact information (reusable for seller/buyer in delivery details)
 * Used in ShipperDeliveryDetailResponse
 */
public class ShipperContactInfoDto {

    private Integer userId;
    private String fullName;
    private String phone;
    private String address;
    private String city;

    public ShipperContactInfoDto() {}

    public ShipperContactInfoDto(
            Integer userId,
            String fullName,
            String phone,
            String address,
            String city) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.city = city;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}

