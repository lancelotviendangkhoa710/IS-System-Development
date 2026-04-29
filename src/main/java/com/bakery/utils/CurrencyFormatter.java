package com.bakery.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Tiện ích định dạng tiền tệ (VNĐ).
 */
public class CurrencyFormatter {
    private static final NumberFormat VN_FORMAT = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    static {
        // Đôi khi cần tinh chỉnh format tiền VNĐ (ví dụ bỏ ký tự ₫ hoặc thay đổi vị trí)
    }

    public static String format(BigDecimal amount) {
        if (amount == null) return format(0);
        return VN_FORMAT.format(amount);
    }

    public static String format(double amount) {
        return VN_FORMAT.format(amount);
    }

    public static String format(long amount) {
        return VN_FORMAT.format(amount);
    }

    /**
     * Chuyển đổi chuỗi số thành BigDecimal.
     * Hỗ trợ cả trường hợp người dùng nhập số có dấu phẩy hoặc chấm.
     */
    public static BigDecimal parse(String input) {
        if (input == null || input.isBlank()) return BigDecimal.ZERO;
        try {
            // Loại bỏ các ký tự không phải số (giữ lại dấu chấm/phẩy thập phân nếu cần)
            String clean = input.replaceAll("[^\\d.,]", "").replace(",", ".");
            return new BigDecimal(clean);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
