package com.bakery.utils;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class QRGenerator {

    /**
     * Tạo URL mã VietQR để hiển thị trực tiếp trên JavaFX ImageView.
     */
    public static String generateVietQRUrl(String bankId, String accountNo, String accountName, double amount, String info) {
        try {
            String encodedName = URLEncoder.encode(accountName, StandardCharsets.UTF_8);
            String encodedInfo = URLEncoder.encode(info, StandardCharsets.UTF_8);

            String url = String.format(
                    "https://img.vietqr.io/image/%s-%s-compact.jpg?amount=%.0f&addInfo=%s&accountName=%s",
                    bankId, accountNo, amount, encodedInfo, encodedName);

            // Validate URL theo quy tắc Java 20+ (tránh constructor URL deprecated)
            URI.create(url).toURL();
            return url;
        } catch (Exception e) {
            System.err.println("Loi tao URL QR: " + e.getMessage());
            return null;
        }
    }

    /** Shortcut cho tài khoản mặc định của tiệm */
    public static String generateDefaultQRUrl(double amount, String orderId) {
        return generateVietQRUrl("vcb", "1049423992", "VIEN DANG KHOA", amount, orderId);
    }
}
