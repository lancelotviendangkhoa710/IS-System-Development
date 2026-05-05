package com.bakery.utils;

import com.bakery.services.AuthService;

/**
 * SessionValidator cung cấp các tiện ích để kiểm tra và bảo vệ phiên đăng nhập.
 */
public class SessionValidator {
    private static final AuthService authService = new AuthService();

    /**
     * Kiểm tra phiên đăng nhập hiện tại có hợp lệ không.
     * 
     * @return true nếu đã đăng nhập và token hợp lệ
     */
    public static boolean isSessionValid() {
        String token = UserSession.getCurrentToken();
        if (token == null) {
            return false;
        }

        try {
            return authService.validateToken(token);
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra token: " + e.getMessage());
            return false;
        }
    }

    /**
     * Bảo vệ một hành động cần xác thực.
     * Nếu phiên không hợp lệ, ném ngoại lệ.
     * 
     * @throws Exception nếu phiên không hợp lệ
     */
    public static void requireValidSession() throws Exception {
        if (!isSessionValid()) {
            throw new Exception("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
        }
    }

    /**
     * Lấy thông tin nhân viên hiện tại.
     * 
     * @return NhanVienDTO nếu phiên hợp lệ, null nếu không
     */
    public static com.bakery.model.dto.nhansu.NhanVienDTO getCurrentUser() {
        if (!isSessionValid()) {
            return null;
        }

        try {
            String token = UserSession.getCurrentToken();
            return authService.getUserFromToken(token);
        } catch (Exception e) {
            System.err.println("Lỗi lấy thông tin người dùng: " + e.getMessage());
            return null;
        }
    }

    /**
     * Kiểm tra quyền truy cập dựa trên vai trò.
     * 
     * @param requiredRole Mã vai trò yêu cầu
     * @return true nếu có quyền
     */
    public static boolean hasRole(int requiredRole) {
        com.bakery.model.dto.nhansu.NhanVienDTO user = getCurrentUser();
        return user != null && user.getMaVaiTro() == requiredRole;
    }

    /**
     * Bảo vệ hành động với quyền cụ thể.
     * 
     * @param requiredRole Mã vai trò yêu cầu
     * @throws Exception nếu không có quyền
     */
    public static void requireRole(int requiredRole) throws Exception {
        requireValidSession();

        com.bakery.model.dto.nhansu.NhanVienDTO user = getCurrentUser();
        if (user.getMaVaiTro() != requiredRole) {
            throw new Exception("Bạn không có quyền thực hiện hành động này.");
        }
    }
}