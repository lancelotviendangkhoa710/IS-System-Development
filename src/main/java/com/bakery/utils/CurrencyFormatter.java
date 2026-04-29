package com.bakery.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Tiện ích định dạng tiền tệ VNĐ dùng chung toàn ứng dụng.
 * Theo ui_spec.md §3.2: format #,### + ký tự "đ". VD: 65.000đ, 1.250.000đ
 */
public class CurrencyFormatter {

    private static final NumberFormat FMT = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    static {
        FMT.setMaximumFractionDigits(0);
        FMT.setMinimumFractionDigits(0);
    }

    private CurrencyFormatter() {
    }

    /**
     * Định dạng BigDecimal thành chuỗi tiền tệ VNĐ. VD: 1250000 → "1.250.000đ"
     */
    public static String format(BigDecimal amount) {
        if (amount == null)
            return "0đ";
        return FMT.format(amount) + "đ";
    }

    /**
     * Định dạng long thành chuỗi tiền tệ VNĐ. VD: 1250000L → "1.250.000đ"
     */
    public static String format(long amount) {
        return FMT.format(amount) + "đ";
    }

    /**
     * Định dạng double thành chuỗi tiền tệ VNĐ. VD: 1250000.0 → "1.250.000đ"
     */
    public static String format(double amount) {
        return FMT.format(amount) + "đ";
    }

    /**
     * Parse chuỗi tiền người dùng nhập (có thể có dấu chấm phân cách nghìn)
     * thành BigDecimal. VD: "1.250.000" hoặc "1250000" → BigDecimal(1250000)
     *
     * @throws NumberFormatException nếu chuỗi không phải số hợp lệ
     */
    public static BigDecimal parse(String input) {
        if (input == null || input.isBlank())
            return BigDecimal.ZERO;
        String cleaned = input.replace(".", "").replace(",", "").replace("đ", "").trim();
        return new BigDecimal(cleaned);
    }
}
