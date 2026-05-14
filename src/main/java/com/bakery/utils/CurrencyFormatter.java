package com.bakery.utils;

import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Tiện ích định dạng tiền tệ (VNĐ).
 * <p>
 * Cung cấp:
 *  - {@link #format} — hiển thị số tiền có dấu phẩy ngăn cách 3 chữ số + ký hiệu ₫
 *  - {@link #apDungDinhDangNhapTien} — gắn listener real-time vào TextField:
 *        nhập "1000000" → hiện "1.000.000" (dấu chấm cách, dễ đọc kiểu VN)
 *  - {@link #docSoTien} — chuyển số sang chữ tiếng Việt (hỗ trợ đến hàng tỷ)
 *  - {@link #parse} — parse chuỗi người dùng nhập về BigDecimal
 */
public class CurrencyFormatter {

    // Format nội bộ hiển thị: dùng NumberFormat "vi_VN" (dấu chấm ngăn 3 số, không có ký hiệu ₫)
    private static final NumberFormat VN_NUMBER = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
    private static final NumberFormat VN_CURRENCY = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    static {
        VN_NUMBER.setMaximumFractionDigits(0);
    }

    // ─── Hiển thị ────────────────────────────────────────────────────────────

    /** Định dạng thành "1.234.567 ₫" */
    public static String format(BigDecimal amount) {
        if (amount == null) return VN_CURRENCY.format(0);
        return VN_CURRENCY.format(amount);
    }

    /** Định dạng thành "1.234.567 ₫" */
    public static String format(double amount) {
        return VN_CURRENCY.format(amount);
    }

    /** Định dạng thành "1.234.567 ₫" */
    public static String format(long amount) {
        return VN_CURRENCY.format(amount);
    }

    /**
     * Định dạng thuần số (không có ký hiệu ₫), dùng cho nội bộ TextField.
     * Ví dụ: 1_234_567 → "1.234.567"
     */
    public static String formatSoThuan(long amount) {
        return VN_NUMBER.format(amount);
    }

    // ─── Real-time input formatter ────────────────────────────────────────────

    /**
     * Gắn listener vào TextField để tự động chèn dấu chấm phân cách mỗi 3 chữ số khi nhập.
     * <p>
     * Ví dụ: gõ "1000000" → hiện thành "1.000.000"
     * Khi lưu, dùng {@link #parse(String)} để lấy giá trị số thực.
     *
     * @param field TextField cần áp dụng
     */
    public static void apDungDinhDangNhapTien(TextField field) {
        field.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null) return;

            // Chỉ giữ lại chữ số
            String digitsOnly = newText.replaceAll("[^\\d]", "");

            if (digitsOnly.isEmpty()) {
                // Tránh vòng lặp vô tận: chỉ set nếu thực sự khác
                if (!newText.isEmpty()) field.setText("");
                return;
            }

            // Giới hạn 15 chữ số (tránh overflow long)
            if (digitsOnly.length() > 15) {
                digitsOnly = digitsOnly.substring(0, 15);
            }

            long value;
            try {
                value = Long.parseLong(digitsOnly);
            } catch (NumberFormatException e) {
                return;
            }

            String formatted = VN_NUMBER.format(value);

            // Chỉ cập nhật nếu text thực sự thay đổi (tránh cursor reset)
            if (!formatted.equals(newText)) {
                field.setText(formatted);
                field.positionCaret(formatted.length());
            }
        });
    }

    // ─── Đọc số thành chữ tiếng Việt ─────────────────────────────────────────

    private static final String[] DON_VI  = {"", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
    private static final String[] CHUC    = {"", "mười", "hai mươi", "ba mươi", "bốn mươi",
                                              "năm mươi", "sáu mươi", "bảy mươi", "tám mươi", "chín mươi"};
    private static final String[] HANG    = {"", "nghìn", "triệu", "tỷ"};

    /**
     * Chuyển đổi số tiền (long, VNĐ) thành chữ tiếng Việt.
     * <p>
     * Ví dụ:
     * <pre>
     *   docSoTien(1_500_000) → "Một triệu năm trăm nghìn đồng"
     *   docSoTien(25_000)    → "Hai mươi lăm nghìn đồng"
     *   docSoTien(0)         → "Không đồng"
     * </pre>
     *
     * @param soTien số tiền ≥ 0, hỗ trợ đến hàng tỷ tỷ (long max)
     * @return chuỗi tiếng Việt có chữ hoa đầu câu + " đồng"
     */
    public static String docSoTien(long soTien) {
        if (soTien < 0) return "Âm " + docSoTien(-soTien);
        if (soTien == 0) return "Không đồng";

        String result = docSo(soTien).trim();
        // Viết hoa chữ cái đầu
        if (!result.isEmpty()) {
            result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
        }
        return result + " đồng";
    }

    /** Overload tiện dụng cho BigDecimal */
    public static String docSoTien(BigDecimal soTien) {
        if (soTien == null) return "Không đồng";
        return docSoTien(soTien.longValue());
    }

    /** Overload cho double */
    public static String docSoTien(double soTien) {
        return docSoTien(Math.round(soTien));
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    /**
     * Đệ quy tách nhóm 3 chữ số và ghép tên đơn vị (nghìn/triệu/tỷ).
     */
    private static String docSo(long n) {
        if (n == 0) return "";

        // Xác định nhóm lớn nhất
        int hangIdx = 0;
        long divisor = 1;
        while (divisor * 1000 <= n) {
            divisor *= 1000;
            hangIdx++;
        }

        long phanLon = n / divisor;
        long phanNho = n % divisor;

        String phanLonChu = docNhomBa((int) phanLon);
        String donViChu   = HANG[Math.min(hangIdx, HANG.length - 1)];

        // Với số tỷ trở lên, phần lớn hơn tỷ cũng đệ quy
        if (hangIdx >= HANG.length) {
            phanLonChu = docSo(phanLon);
            donViChu = HANG[HANG.length - 1];
        }

        String phanNhoChu = phanNho > 0 ? " " + docSo(phanNho) : "";

        // Thêm "linh/lẻ" nếu phần nhỏ < 100 và phần lớn > 0
        if (phanNho > 0 && phanNho < 100 && phanLon > 0) {
            phanNhoChu = " lẻ" + phanNhoChu;
        }

        return phanLonChu + " " + donViChu + phanNhoChu;
    }

    /**
     * Đọc nhóm 3 chữ số (0–999).
     */
    private static String docNhomBa(int n) {
        if (n == 0) return "";
        if (n < 10) return DON_VI[n];

        int tram  = n / 100;
        int chuc  = (n % 100) / 10;
        int donVi = n % 10;

        StringBuilder sb = new StringBuilder();

        if (tram > 0) {
            sb.append(DON_VI[tram]).append(" trăm");
            if (chuc == 0 && donVi > 0) sb.append(" lẻ");
        }

        if (chuc > 0) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(CHUC[chuc]);
            if (donVi == 5 && chuc > 1) {
                // "hai mươi lăm" thay vì "hai mươi năm"
                sb.append(" lăm");
            } else if (donVi == 1 && chuc > 1) {
                // "hai mươi mốt" thay vì "hai mươi một"
                sb.append(" mốt");
            } else if (donVi > 0) {
                sb.append(' ').append(DON_VI[donVi]);
            }
        } else if (donVi > 0) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(DON_VI[donVi]);
        }

        return sb.toString().trim();
    }

    // ─── Parse ───────────────────────────────────────────────────────────────

    /**
     * Chuyển chuỗi người dùng nhập (có thể chứa dấu chấm/phẩy) về BigDecimal.
     * <p>
     * Ví dụ: "1.234.567" → 1234567 | "1,234,567" → 1234567
     */
    public static BigDecimal parse(String input) {
        if (input == null || input.isBlank()) return BigDecimal.ZERO;
        try {
            // Xóa toàn bộ dấu chấm và phẩy phân cách (VN dùng dấu chấm, US dùng phẩy)
            String clean = input.replaceAll("[.,]", "").replaceAll("[^\\d]", "").trim();
            if (clean.isEmpty()) return BigDecimal.ZERO;
            return new BigDecimal(clean);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
