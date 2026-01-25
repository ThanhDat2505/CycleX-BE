package com.example.cyclexbe.service;

import com.example.cyclexbe.dto.UserCreateRequest;
import com.example.cyclexbe.dto.UserResponse;
import com.example.cyclexbe.dto.UserUpdateRequest;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.UserRepository;
import com.example.cyclexbe.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse create(UserCreateRequest req) {
        if (userRepository.existsByEmail(req.email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User u = new User();
        u.setEmail(req.email);
        u.setPasswordHash(passwordEncoder.encode(req.password));

        u.setFullName(req.fullName);
        u.setPhone(req.phone);
        u.setRole(req.role);
        u.setStatus(req.status);
        u.setCccd(req.cccd);
        u.setAvatarUrl(req.avatarUrl);

        if (req.isVerify != null) u.setVerify(req.isVerify);

        User saved = userRepository.save(u);
        return toResponse(saved);
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse getById(Integer id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(u);
    }

    public UserResponse update(Integer id, UserUpdateRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (req.email != null && !req.email.equalsIgnoreCase(u.getEmail())) {
            if (userRepository.existsByEmail(req.email)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
            }
            u.setEmail(req.email);
        }

        if (req.password != null && !req.password.isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(req.password));
        }

        if (req.fullName != null) u.setFullName(req.fullName);
        if (req.phone != null) u.setPhone(req.phone);
        if (req.role != null) u.setRole(req.role);
        if (req.status != null) u.setStatus(req.status);
        if (req.cccd != null) u.setCccd(req.cccd);
        if (req.avatarUrl != null) u.setAvatarUrl(req.avatarUrl);
        if (req.isVerify != null) u.setVerify(req.isVerify);

        User saved = userRepository.save(u);
        return toResponse(saved);
    }

    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    private UserResponse toResponse(User u) {
        UserResponse r = new UserResponse();
        r.userId = u.getUserId();
        r.email = u.getEmail();
        r.fullName = u.getFullName();
        r.phone = u.getPhone();
        r.role = u.getRole();
        r.isVerify = u.isVerify();
        r.status = u.getStatus();
        r.cccd = u.getCccd();
        r.avatarUrl = u.getAvatarUrl();
        r.createdAt = u.getCreatedAt();
        r.updatedAt = u.getUpdatedAt();
        r.lastLogin = u.getLastLogin();
        return r;
    }
}
