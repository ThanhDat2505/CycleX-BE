package com.example.cyclexbe.service;

import com.example.cyclexbe.entity.EmailOtp;
import com.example.cyclexbe.entity.User;
import com.example.cyclexbe.repository.EmailOtpRepository;
import com.example.cyclexbe.util.OtpUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class OtpService {
    private final EmailOtpRepository emailOtpRepository;
    private final OtpUtil otpUtil = new OtpUtil();
    private final UserService userService;
    private final EmailService emailService;
    public OtpService(EmailOtpRepository emailOtpRepository, UserService userService, EmailService emailService, EmailService emailService1) {
        this.emailOtpRepository = emailOtpRepository;
        this.userService = userService;
        this.emailService = emailService1;
    }

    public String sendOtp(String email) {
        User user = userService.findByEmail(email);
        if (user.isVerify()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already verified");
        }
        String otp = OtpUtil.generateOtp();
        emailOtpRepository.findByUser(user).ifPresent(emailOtpRepository::delete);
        EmailOtp emailOtp = new EmailOtp(user, otp, LocalDateTime.now().plusMinutes(5));
        emailOtpRepository.save(emailOtp);
        boolean statusSendEmail = emailService.sendOtpEmail(email, otp);
        if (!statusSendEmail) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending OTP");
        }
        System.out.println("OTP sent to " + email);
        return otp;
    }
    public void verifyOtp(String email, String otp) {
        User user = userService.findByEmail(email);
        EmailOtp emailOtp = emailOtpRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP not found"));

        if (emailOtp.isLocked()) {
            throw new ResponseStatusException(HttpStatus.LOCKED, "OTP is locked. Please request a new OTP.");
        }

        if (emailOtp.getExpiryTime() != null && emailOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired. Please request a new OTP.");
        }
        if (emailOtp.getAttempts() >= 5) {
            emailOtp.setLocked(true);
            emailOtpRepository.save(emailOtp);
            throw new ResponseStatusException(HttpStatus.LOCKED, "OTP is locked. Please request a new OTP.");
        }
        System.out.println(emailOtp.getOtp());
        System.out.println(otp);
        System.out.println(emailOtp.getOtp().equals(otp));
        if (emailOtp.getOtp() == null || !emailOtp.getOtp().equals(otp)) {
            emailOtp.setAttempts(emailOtp.getAttempts() + 1);
            emailOtpRepository.save(emailOtp);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }


        user.setVerify(true);
        userService.save(user);
        emailOtpRepository.delete(emailOtp);
    }
}
