package com.example.cyclexbe.dto;

import jakarta.validation.constraints.Size;

public class UpdateAddressRequest {

    @Size(max = 50, message = "Label tối đa 50 ký tự")
    public String label;

    @Size(max = 100)
    public String province;

    @Size(max = 100)
    public String district;

    @Size(max = 100)
    public String ward;

    @Size(max = 300)
    public String streetAddress;

    @Size(max = 150)
    public String receiverName;

    @Size(max = 30)
    public String receiverPhone;

    public Boolean isDefault;
}
