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
        if (!userRepository.existsByEmail("admin@cyclex.com")) {
            User admin = new User();
            admin.setEmail("admin@cyclex.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setFullName("Admin");
            admin.setVerify(true);
            admin.setStatus("ACTIVE");
            userRepository.save(admin);
        }
    }
}
