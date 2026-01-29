package com.example.cyclexbe.repository;

import com.example.cyclexbe.entity.EmailOtp;
import com.example.cyclexbe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository
        extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findByUser(User user);
}
