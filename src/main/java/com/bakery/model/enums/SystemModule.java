package com.bakery.model.enums;

/**
 * Danh sách các Module chính của hệ thống để phân quyền và điều hướng.
 * Enum này được sử dụng xuyên suốt các lớp DAO, DTO, Service và UI.
 */
public enum SystemModule {
    BAN_HANG,       // Bán hàng & Quản lý đơn
    KHO,            // Kho & Nguyên liệu
    NHAN_SU,        // Nhân sự & Phân quyền
    BAO_CAO,        // Báo cáo & Thống kê
    KHACH_HANG,      // Khách hàng & Hội viên
    NHA_BEP,        // Nhà bếp (Kitchen Display System)
    NHAT_KY,        // Nhật ký hệ thống
    HE_THONG;       // Cấu hình hệ thống & Tham số

    /**
     * Chuyển đổi từ chuỗi (hỗ trợ cả tên cũ tiếng Anh và tên mới tiếng Việt).
     */
    public static SystemModule fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        String upper = value.toUpperCase().trim();
        return switch (upper) {
            case "POS", "BAN_HANG", "BAN HANG" -> BAN_HANG;
            case "INVENTORY", "KHO" -> KHO;
            case "STAFF", "NHAN_SU", "NHAN SU" -> NHAN_SU;
            case "REPORTS", "BAO_CAO", "BAO CAO" -> BAO_CAO;
            case "CRM", "KHACH_HANG", "KHACH HANG" -> KHACH_HANG;
            case "KDS", "NHA_BEP", "NHA BEP" -> NHA_BEP;
            case "AUDIT_LOGS", "NHAT_KY", "NHAT KY" -> NHAT_KY;
            case "SYSTEM", "HE_THONG", "HE THONG" -> HE_THONG;
            default -> {
                try {
                    yield SystemModule.valueOf(upper);
                } catch (IllegalArgumentException e) {
                    yield null;
                }
            }
        };
    }
}
