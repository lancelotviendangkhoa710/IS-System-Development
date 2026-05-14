package com.bakery.utils;

import java.security.SecureRandom;

/**
 * Tiện ích tạo mã OTP 6 số dùng cho chức năng đặt lại mật khẩu.
 */
public final class OtpUtils {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    private OtpUtils() {}

    /** Sinh mã OTP 6 chữ số, có thể có số 0 ở đầu. */
    public static String taoOtp() {
        int so = RANDOM.nextInt(900_000) + 100_000; // 100000–999999
        return String.valueOf(so);
    }
}
