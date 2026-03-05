package com.example.cyclexbe.dto;

/**
 * DTO for pickup location details (S-61 F3/F4)
 * Contains address and contact information for pickup point
 */
public class ShipperPickupLocationDto {

    private String address;
    private String city;
    private String contactName;
    private String contactPhone;

    public ShipperPickupLocationDto() {}

    public ShipperPickupLocationDto(
            String address,
            String city,
            String contactName,
            String contactPhone) {
        this.address = address;
        this.city = city;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
}

