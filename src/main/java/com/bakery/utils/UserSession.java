package com.bakery.utils;

import com.bakery.model.dto.nhansu.NhanVienDTO;

/**
 * UserSession quản lý thông tin nhân viên đang đăng nhập trong phiên làm việc hiện tại.
 * Lưu ý: Đang được duy trì song song với SessionContext để tương thích ngược.
 */
public final class UserSession {
    private static NhanVienDTO currentUser;

    private UserSession() {
    }

    public static void setCurrentUser(NhanVienDTO user) {
        currentUser = user;
    }

    public static NhanVienDTO getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}
