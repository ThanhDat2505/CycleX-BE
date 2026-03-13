package com.example.cyclexbe.service;

import com.example.cyclexbe.dto.AddressResponse;
import com.example.cyclexbe.dto.CreateAddressRequest;
import com.example.cyclexbe.dto.UpdateAddressRequest;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.entity.UserAddress;
import com.example.cyclexbe.repository.UserAddressRepository;
import com.example.cyclexbe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserAddressService {

    private static final int MAX_ADDRESSES_PER_USER = 10;

    private final UserAddressRepository addressRepository;
    private final UserRepository userRepository;

    public UserAddressService(UserAddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<AddressResponse> getAddresses(Integer userId) {
        return addressRepository.findByUserUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                .stream()
                .map(AddressResponse::from)
                .toList();
    }

    public AddressResponse getAddressById(Integer addressId, Integer userId) {
        UserAddress addr = addressRepository.findByAddressIdAndUserUserId(addressId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ"));
        return AddressResponse.from(addr);
    }

    @Transactional
    public AddressResponse createAddress(Integer userId, CreateAddressRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        long count = addressRepository.countByUserUserId(userId);
        if (count >= MAX_ADDRESSES_PER_USER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tối đa " + MAX_ADDRESSES_PER_USER + " địa chỉ");
        }

        UserAddress addr = new UserAddress();
        addr.setUser(user);
        addr.setLabel(req.label != null ? req.label : "Nhà riêng");
        addr.setProvince(req.province);
        addr.setDistrict(req.district);
        addr.setWard(req.ward);
        addr.setStreetAddress(req.streetAddress);
        addr.setReceiverName(req.receiverName);
        addr.setReceiverPhone(req.receiverPhone);

        boolean shouldBeDefault = (req.isDefault != null && req.isDefault) || count == 0;
        if (shouldBeDefault) {
            clearDefaultAddress(userId);
        }
        addr.setDefault(shouldBeDefault);

        UserAddress saved = addressRepository.save(addr);

        // Also update user's main address field if this is default
        if (saved.isDefault()) {
            user.setAddress(saved.getFullAddress());
            userRepository.save(user);
        }

        return AddressResponse.from(saved);
    }

    @Transactional
    public AddressResponse updateAddress(Integer addressId, Integer userId, UpdateAddressRequest req) {
        UserAddress addr = addressRepository.findByAddressIdAndUserUserId(addressId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ"));

        if (req.label != null)
            addr.setLabel(req.label);
        if (req.province != null)
            addr.setProvince(req.province);
        if (req.district != null)
            addr.setDistrict(req.district);
        if (req.ward != null)
            addr.setWard(req.ward);
        if (req.streetAddress != null)
            addr.setStreetAddress(req.streetAddress);
        if (req.receiverName != null)
            addr.setReceiverName(req.receiverName);
        if (req.receiverPhone != null)
            addr.setReceiverPhone(req.receiverPhone);

        if (req.isDefault != null && req.isDefault && !addr.isDefault()) {
            clearDefaultAddress(userId);
            addr.setDefault(true);
        }

        UserAddress saved = addressRepository.save(addr);

        // Sync user.address if default
        if (saved.isDefault()) {
            User user = saved.getUser();
            user.setAddress(saved.getFullAddress());
            userRepository.save(user);
        }

        return AddressResponse.from(saved);
    }

    @Transactional
    public void deleteAddress(Integer addressId, Integer userId) {
        UserAddress addr = addressRepository.findByAddressIdAndUserUserId(addressId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ"));

        boolean wasDefault = addr.isDefault();
        addressRepository.delete(addr);

        // If deleted the default, set next one as default
        if (wasDefault) {
            List<UserAddress> remaining = addressRepository.findByUserUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
            if (!remaining.isEmpty()) {
                UserAddress newDefault = remaining.get(0);
                newDefault.setDefault(true);
                addressRepository.save(newDefault);

                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    user.setAddress(newDefault.getFullAddress());
                    userRepository.save(user);
                }
            } else {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    user.setAddress(null);
                    userRepository.save(user);
                }
            }
        }
    }

    @Transactional
    public AddressResponse setDefault(Integer addressId, Integer userId) {
        UserAddress addr = addressRepository.findByAddressIdAndUserUserId(addressId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ"));

        clearDefaultAddress(userId);
        addr.setDefault(true);
        UserAddress saved = addressRepository.save(addr);

        User user = saved.getUser();
        user.setAddress(saved.getFullAddress());
        userRepository.save(user);

        return AddressResponse.from(saved);
    }

    private void clearDefaultAddress(Integer userId) {
        addressRepository.findByUserUserIdAndIsDefaultTrue(userId)
                .ifPresent(existing -> {
                    existing.setDefault(false);
                    addressRepository.save(existing);
                });
    }
}
