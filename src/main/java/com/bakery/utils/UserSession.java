package com.bakery.utils;

// UserSession giống như một cái "Thẻ ID đeo trên ngực"
//  của nhân viên trong suốt thời gian họ sử dụng phần mềm.
import com.bakery.model.dto.NhanVienDTO;

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
