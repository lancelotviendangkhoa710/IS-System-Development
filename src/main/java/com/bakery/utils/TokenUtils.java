package com.bakery.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * TokenUtils cung cấp các tiện ích để tạo và quản lý token cho phiên đăng nhập.
 */
public class TokenUtils {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_LENGTH = 32; // Độ dài token 32 bytes = 44 ký tự base64

    /**
     * Tạo một token ngẫu nhiên duy nhất.
     * 
     * @return Token dưới dạng chuỗi base64
     */
    public static String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Kiểm tra token có hợp lệ về định dạng không.
     * 
     * @param token Token cần kiểm tra
     * @return true nếu token hợp lệ
     */
    public static boolean isValidTokenFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        // Kiểm tra độ dài và ký tự hợp lệ cho base64 URL-safe
        return token.matches("^[A-Za-z0-9_-]{20,50}$");
    }
}