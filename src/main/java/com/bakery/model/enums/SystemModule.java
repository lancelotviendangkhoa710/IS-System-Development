package com.bakery.model.enums;

/**
 * Danh sách các Module chính của hệ thống để phân quyền và điều hướng.
 * Enum này được sử dụng xuyên suốt các lớp DAO, DTO, Service và UI.
 */
public enum SystemModule {
    POS,            // Bán hàng & Quản lý đơn
    INVENTORY,      // Kho & Nguyên liệu
    STAFF,          // Nhân sự & Phân quyền
    REPORTS,        // Báo cáo & Thống kê
    CRM,            // Khách hàng & Hội viên
    KDS,            // Nhà bếp (Kitchen Display System)
    AUDIT_LOGS,     // Nhật ký hệ thống
    SYSTEM          // Cấu hình hệ thống & Tham số
}
