package com.bakery.utils;

import com.bakery.model.dto.nhansu.NhanVienDTO;

public class SessionManager {
    private static NhanVienDTO currentUser;

    public static void setCurrentUser(NhanVienDTO user) {
        currentUser = user;
    }

    public static NhanVienDTO getCurrentUser() {
        return currentUser;
    }

    public static int getCurrentEmployeeId() {
        return currentUser != null ? currentUser.getMaNV() : -1;
    }

    public static String getCurrentEmployeeName() {
        return currentUser != null ? currentUser.getHoTen() : "Unknown";
    }
}
