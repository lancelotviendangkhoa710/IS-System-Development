package com.bakery.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho {@link OtpUtils} — kiểm tra sinh OTP 6 số.
 */
@DisplayName("OtpUtils Tests")
class OtpUtilsTest {

    @Test
    @DisplayName("taoOtp() không trả về null")
    void taoOtp_khongNull() {
        assertNotNull(OtpUtils.taoOtp());
    }

    @Test
    @DisplayName("taoOtp() trả về chuỗi 6 ký tự")
    void taoOtp_dauDai6KyTu() {
        String otp = OtpUtils.taoOtp();
        assertEquals(6, otp.length(), "OTP phải có đúng 6 ký tự: " + otp);
    }

    @Test
    @DisplayName("taoOtp() chỉ chứa chữ số")
    void taoOtp_chiChuaSo() {
        String otp = OtpUtils.taoOtp();
        assertTrue(otp.matches("\\d{6}"), "OTP phải là 6 chữ số: " + otp);
    }

    @Test
    @DisplayName("taoOtp() trong khoảng 100000–999999")
    void taoOtp_trongKhoang() {
        int otp = Integer.parseInt(OtpUtils.taoOtp());
        assertTrue(otp >= 100_000 && otp <= 999_999,
                "OTP phải trong khoảng 100000-999999, nhận được: " + otp);
    }

    @RepeatedTest(10)
    @DisplayName("taoOtp() mỗi lần gọi cho kết quả hợp lệ (lặp 10 lần)")
    void taoOtp_luonHopLe() {
        String otp = OtpUtils.taoOtp();
        assertTrue(otp.matches("\\d{6}"), "OTP không hợp lệ: " + otp);
    }
}
