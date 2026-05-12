package com.deliveryplatform.common;

import java.security.SecureRandom;


public final class CodeGeneratorUtil {

    private static final SecureRandom random = new SecureRandom();

    private CodeGeneratorUtil() {}

    // ex: BOK-5436
    public static String generateBookingCode() {
        int number = random.nextInt(9000) + 1000;
        return "BOK-" + number;
    }

    public static String generateVerificationCode() {
        int number = random.nextInt(9000) + 1000;
        return "VERF-" + number;
    }

}