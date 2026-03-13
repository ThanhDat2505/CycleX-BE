package com.example.cyclexbe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateAddressRequest {

    @Size(max = 50, message = "Label tối đa 50 ký tự")
    public String label;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Size(max = 100)
    public String province;

    @NotBlank(message = "Quận/Huyện không được để trống")
    @Size(max = 100)
    public String district;

    @NotBlank(message = "Phường/Xã không được để trống")
    @Size(max = 100)
    public String ward;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Size(max = 300)
    public String streetAddress;

    @Size(max = 150)
    public String receiverName;

    @Size(max = 30)
    public String receiverPhone;

    public Boolean isDefault;
}
