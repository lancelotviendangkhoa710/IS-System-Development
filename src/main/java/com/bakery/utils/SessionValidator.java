package com.bakery.utils;

/**
 * SessionValidator cung cấp các tiện ích để kiểm tra và bảo vệ phiên đăng nhập.
 * Sử dụng SessionContext (in-memory) — nguồn session thực tế của ứng dụng.
 */
public class SessionValidator {

    /**
     * Kiểm tra phiên đăng nhập hiện tại có hợp lệ không.
     *
     * @return true nếu SessionContext đang có session hợp lệ
     */
    public static boolean isSessionValid() {
        return SessionContext.getCurrentSession() != null;
    }

    /**
     * Bảo vệ một hành động cần xác thực. Ném ngoại lệ nếu chưa đăng nhập.
     *
     * @throws Exception nếu phiên không hợp lệ
     */
    public static void requireValidSession() throws Exception {
        if (!isSessionValid()) {
            throw new Exception("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
        }
    }

    /**
     * Lấy thông tin nhân viên từ UserSession (tương thích ngược với code cũ).
     *
     * @return NhanVienDTO nếu phiên hợp lệ, null nếu không
     */
    public static com.bakery.model.dto.nhansu.NhanVienDTO getCurrentUser() {
        if (!isSessionValid()) {
            return null;
        }
        return UserSession.getCurrentUser();
    }

    /**
     * Kiểm tra quyền truy cập dựa trên vai trò (hỗ trợ N-N RBAC).
     *
     * @param requiredRole Mã vai trò yêu cầu
     * @return true nếu có quyền
     */
    public static boolean hasRole(int requiredRole) {
        com.bakery.model.dto.nhansu.NhanVienDTO user = getCurrentUser();
        if (user == null || user.getDanhSachMaVaiTro() == null) return false;
        return user.getDanhSachMaVaiTro().contains(requiredRole);
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
        if (user == null || !user.getDanhSachMaVaiTro().contains(requiredRole)) {
            throw new Exception("Bạn không có quyền thực hiện hành động này.");
        }
    }
}