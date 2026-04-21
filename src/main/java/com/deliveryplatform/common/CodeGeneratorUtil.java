package com.deliveryplatform.common;

import java.security.SecureRandom;


public class CodeGeneratorUtil {

    private static final SecureRandom random = new SecureRandom();

    // ex: DEL-5436
    public static String generateParcelCode() {
        int number = random.nextInt(9000) + 1000;
        return "PAR-" + number;
    }

    public static String generateVerificationCode() {
        int number = random.nextInt(9000) + 1000;
        return "VERF-" + number;
    }

}