package com.bakery.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtils {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_LENGTH = 32;

    public static String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public static boolean isValidTokenFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        return token.matches("^[A-Za-z0-9_-]{20,50}$");
    }
}