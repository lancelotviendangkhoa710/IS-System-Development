package com.bakery.utils;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class QRGenerator {

    /**
     * Tạo mã VietQR bằng cách gọi API VietQR.io
     * @param bankId ID ngân hàng (vd: vcb, mbb, acb)
     * @param accountNo Số tài khoản
     * @param accountName Tên chủ tài khoản
     * @param amount Số tiền
     * @param info Nội dung chuyển khoản
     * @return ImageIcon chứa mã QR hoặc null nếu lỗi
     */
    public static ImageIcon generateVietQR(String bankId, String accountNo, String accountName, double amount, String info) {
        try {
            String encodedName = java.net.URLEncoder.encode(accountName, "UTF-8");
            String encodedInfo = java.net.URLEncoder.encode(info, "UTF-8");
            
            String urlString = String.format(
                    "https://img.vietqr.io/image/%s-%s-compact.jpg?amount=%.0f&addInfo=%s&accountName=%s",
                    bankId, accountNo, amount, encodedInfo, encodedName);

            URL url = java.net.URI.create(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Bắt buộc set User-Agent để tránh bị API chặn (Lỗi 403)
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (InputStream in = connection.getInputStream()) {
                Image image = ImageIO.read(in);
                if (image == null) return null;
                return new ImageIcon(image.getScaledInstance(300, 300, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            System.err.println("Lỗi tạo QR: " + e.getMessage());
            return null;
        }
    }
    
    /** Shortcut cho tài khoản mặc định của tiệm */
    public static ImageIcon generateDefaultQR(double amount, String orderId) {
        return generateVietQR("vcb", "1049423992", "VIEN DANG KHOA", amount, orderId);
    }
}
