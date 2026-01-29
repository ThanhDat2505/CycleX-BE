package com.example.cyclexbe.util;

import java.util.Random;

public class OtpUtil {
    public OtpUtil() {}

    public static String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}