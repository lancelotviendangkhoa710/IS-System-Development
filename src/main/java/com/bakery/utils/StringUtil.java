package com.bakery.utils;

import java.text.Normalizer;

/**
 * Tiện ích xử lý chuỗi dùng chung toàn hệ thống.
 */
public class StringUtil {

    /**
     * Chuẩn hóa tên trạng thái hoặc chuỗi tiếng Việt: 
     * Bỏ dấu, chuyển thành Uppercase, thay khoảng trắng bằng '_'.
     * Ví dụ: "Mới đặt" -> "MOI_DAT"
     */
    public static String chuanHoa(String raw) {
        if (raw == null) return "";
        String normalized = Normalizer.normalize(raw.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d").replace("Đ", "D")
                .toUpperCase().replace(' ', '_');
        
        // Fix đặc biệt cho trạng thái chờ khách lấy
        if (normalized.contains("KHACH") && normalized.contains("LAY"))
            return "CHO_KHACH_LAY";
            
        return normalized;
    }
}
