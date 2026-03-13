package com.example.cyclexbe.controller;

import com.example.cyclexbe.dto.AddressResponse;
import com.example.cyclexbe.dto.CreateAddressRequest;
import com.example.cyclexbe.dto.UpdateAddressRequest;
import com.example.cyclexbe.service.UserAddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
public class UserAddressController {

    private final UserAddressService addressService;

    public UserAddressController(UserAddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public List<AddressResponse> getAddresses(@PathVariable Integer userId) {
        return addressService.getAddresses(userId);
    }

    @GetMapping("/{addressId}")
    public AddressResponse getAddress(@PathVariable Integer userId, @PathVariable Integer addressId) {
        return addressService.getAddressById(addressId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse createAddress(@PathVariable Integer userId,
            @Valid @RequestBody CreateAddressRequest req) {
        return addressService.createAddress(userId, req);
    }

    @PutMapping("/{addressId}")
    public AddressResponse updateAddress(@PathVariable Integer userId,
            @PathVariable Integer addressId,
            @Valid @RequestBody UpdateAddressRequest req) {
        return addressService.updateAddress(addressId, userId, req);
    }

    @DeleteMapping("/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable Integer userId, @PathVariable Integer addressId) {
        addressService.deleteAddress(addressId, userId);
    }

    @PatchMapping("/{addressId}/set-default")
    public AddressResponse setDefault(@PathVariable Integer userId, @PathVariable Integer addressId) {
        return addressService.setDefault(addressId, userId);
    }
}
