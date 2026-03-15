package com.example.cyclexbe.dto;

import com.example.cyclexbe.entity.UserAddress;

import java.time.LocalDateTime;

public class AddressResponse {

    public Integer addressId;
    public String label;
    public String province;
    public String district;
    public String ward;
    public String streetAddress;
    public String fullAddress;
    public String receiverName;
    public String receiverPhone;
    public boolean isDefault;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public AddressResponse() {
    }

    public static AddressResponse from(UserAddress addr) {
        AddressResponse r = new AddressResponse();
        r.addressId = addr.getAddressId();
        r.label = addr.getLabel();
        r.province = addr.getProvince();
        r.district = addr.getDistrict();
        r.ward = addr.getWard();
        r.streetAddress = addr.getStreetAddress();
        r.fullAddress = addr.getFullAddress();
        r.receiverName = addr.getReceiverName();
        r.receiverPhone = addr.getReceiverPhone();
        r.isDefault = addr.isDefault();
        r.createdAt = addr.getCreatedAt();
        r.updatedAt = addr.getUpdatedAt();
        return r;
    }
}
