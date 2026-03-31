package com.example.cyclexbe.config;

import com.example.cyclexbe.domain.enums.Role;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        userRepository.findByEmail("admin@cyclex.com").ifPresentOrElse(
                existing -> {
                    // Ensure the admin account is always active and has the correct role
                    boolean dirty = false;
                    if (!"ACTIVE".equals(existing.getStatus())) {
                        existing.setStatus("ACTIVE");
                        dirty = true;
                    }
                    if (existing.getRole() != Role.ADMIN) {
                        existing.setRole(Role.ADMIN);
                        dirty = true;
                    }
                    if (!existing.isVerify()) {
                        existing.setVerify(true);
                        dirty = true;
                    }
                    if (existing.getPhone() == null || existing.getPhone().isBlank()) {
                        existing.setPhone("09847588237");
                        dirty = true;
                    }
                    if (existing.getCccd() == null || existing.getCccd().isBlank()) {
                        existing.setCccd("072998003394");
                        dirty = true;
                    }
                    if (dirty) {
                        userRepository.save(existing);
                    }
                },
                () -> {
                    User admin = new User();
                    admin.setEmail("admin@cyclex.com");
                    admin.setPasswordHash(passwordEncoder.encode("admin123"));
                    admin.setRole(Role.ADMIN);
                    admin.setFullName("Admin");
                    admin.setVerify(true);
                    admin.setStatus("ACTIVE");
                    admin.setPhone("09847588237");
                    admin.setCccd("072998003394");
                    userRepository.save(admin);
                });

        // Seed BUYER account
        userRepository.findByEmail("buyer@cyclex.com").ifPresentOrElse(
                existing -> {
                    boolean dirty = false;
                    if (!"ACTIVE".equals(existing.getStatus())) {
                        existing.setStatus("ACTIVE");
                        dirty = true;
                    }
                    if (existing.getRole() != Role.BUYER) {
                        existing.setRole(Role.BUYER);
                        dirty = true;
                    }
                    if (!existing.isVerify()) {
                        existing.setVerify(true);
                        dirty = true;
                    }
                    if (existing.getPhone() == null || existing.getPhone().isBlank()) {
                        existing.setPhone("09847588237");
                        dirty = true;
                    }
                    if (existing.getCccd() == null || existing.getCccd().isBlank()) {
                        existing.setCccd("072998003394");
                        dirty = true;
                    }
                    if (dirty) {
                        userRepository.save(existing);
                    }
                },
                () -> {
                    User buyer = new User();
                    buyer.setEmail("buyer@cyclex.com");
                    buyer.setPasswordHash(passwordEncoder.encode("123456"));
                    buyer.setRole(Role.BUYER);
                    buyer.setFullName("Buyer");
                    buyer.setVerify(true);
                    buyer.setStatus("ACTIVE");
                    buyer.setPhone("09847588237");
                    buyer.setCccd("072998003394");
                    userRepository.save(buyer);
                });

        // Seed INSPECTOR account
        userRepository.findByEmail("inspector@cyclex.com").ifPresentOrElse(
                existing -> {
                    boolean dirty = false;
                    if (!"ACTIVE".equals(existing.getStatus())) {
                        existing.setStatus("ACTIVE");
                        dirty = true;
                    }
                    if (existing.getRole() != Role.INSPECTOR) {
                        existing.setRole(Role.INSPECTOR);
                        dirty = true;
                    }
                    if (!existing.isVerify()) {
                        existing.setVerify(true);
                        dirty = true;
                    }
                    if (existing.getPhone() == null || existing.getPhone().isBlank()) {
                        existing.setPhone("09847588237");
                        dirty = true;
                    }
                    if (existing.getCccd() == null || existing.getCccd().isBlank()) {
                        existing.setCccd("072998003394");
                        dirty = true;
                    }
                    if (dirty) {
                        userRepository.save(existing);
                    }
                },
                () -> {
                    User inspector = new User();
                    inspector.setEmail("inspector@cyclex.com");
                    inspector.setPasswordHash(passwordEncoder.encode("123456"));
                    inspector.setRole(Role.INSPECTOR);
                    inspector.setFullName("Inspector");
                    inspector.setVerify(true);
                    inspector.setStatus("ACTIVE");
                    inspector.setPhone("09847588237");
                    inspector.setCccd("072998003394");
                    userRepository.save(inspector);
                });
    }
}
