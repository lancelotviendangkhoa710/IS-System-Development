package com.bakery.services;

import com.bakery.model.dao.NhanVienDAO;
import com.bakery.model.dao.PhanQuyenDAO;
import com.bakery.model.dao.VaiTroDAO;
import com.bakery.model.dto.ChucNangDTO;
import com.bakery.model.dto.NhanVienDTO;
import com.bakery.model.dto.VaiTroDTO;
import com.bakery.utils.PasswordUtils;
import com.bakery.utils.SessionContext;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Dịch vụ xử lý các nghiệp vụ xác thực người dùng (Auth).
 * Quản lý Đăng nhập, Đăng ký, Đổi mật khẩu và Session.
 */
public class AuthService {
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final PhanQuyenDAO phanQuyenDAO = new PhanQuyenDAO();
    private final VaiTroDAO vaiTroDAO = new VaiTroDAO();

    /**
     * Đăng nhập: xác thực thông tin, kiểm tra trạng thái, tạo session.
     */
    public NhanVienDTO login(String username, String password) throws Exception {
        String usernameDaChuanHoa = validateUsername(username);
        String matKhauHopLe = validatePassword(password, "Mật khẩu");

        NhanVienDTO nhanVien = nhanVienDAO.timNhanVienTheoTenDangNhap(usernameDaChuanHoa);
        if (nhanVien == null) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS, "Tên đăng nhập hoặc mật khẩu không chính xác.");
        }

        if (nhanVien.getTrangThaiLamViec() != 1) {
            throw new AuthException(AuthErrorCode.ACCOUNT_DISABLED, "Tài khoản đã bị vô hiệu hóa.");
        }

        if (!PasswordUtils.matches(matKhauHopLe, nhanVien.getMatKhau())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS, "Tên đăng nhập hoặc mật khẩu không chính xác.");
        }

        PhanQuyenDAO.RolePermissionInfo roleInfo = phanQuyenDAO.layThongTinPhanQuyenTheoVaiTro(nhanVien.getMaVaiTro());
        if (roleInfo == null || !roleInfo.isVaiTroHoatDong()) {
            throw new AuthException(AuthErrorCode.ROLE_NOT_ALLOWED, "Vai trò của tài khoản không còn hiệu lực.");
        }

        if (roleInfo.getDanhSachChucNang().isEmpty()) {
            throw new AuthException(AuthErrorCode.ROLE_NOT_ALLOWED, "Vai trò hiện tại không được cấp quyền truy cập.");
        }

        Set<String> permissionKeys = new LinkedHashSet<>(roleInfo.getPermissionKeys());
        SessionContext.AuthSession session = new SessionContext.AuthSession(
                nhanVien.getMaNV(),
                nhanVien.getMaVaiTro(),
                nhanVien.getTenDangNhap(),
                nhanVien.getHoTen(),
                roleInfo.getTenVaiTro(),
                permissionKeys
        );
        SessionContext.createSession(session);
        return nhanVien;
    }

    /**
     * Phương thức đăng nhập cũ để tương thích ngược (dùng cho bypass login).
     */
    public NhanVienDTO dangNhap(String tenDangNhap, String matKhau) throws Exception {
        return login(tenDangNhap, matKhau);
    }

    /**
     * Đăng ký nhân viên mới.
     */
    public int register(
            String hoTen,
            String soDienThoai,
            String tenDangNhap,
            String matKhau,
            String maXacNhanQuanLy,
            Integer maVaiTro
    ) throws Exception {
        if (hoTen == null || hoTen.isBlank()) {
            throw new Exception("Họ tên không được để trống.");
        }

        if (soDienThoai == null || soDienThoai.isBlank()) {
            throw new Exception("Số điện thoại không được để trống.");
        }

        String sdt = soDienThoai.trim();
        if (sdt.length() < 9 || sdt.length() > 15) {
            throw new Exception("Số điện thoại phải từ 9 đến 15 ký tự.");
        }

        String usernameDaChuanHoa = validateUsername(tenDangNhap);
        String matKhauHopLe = validatePassword(matKhau, "Mật khẩu");

        if (maXacNhanQuanLy == null || maXacNhanQuanLy.isBlank()) {
            throw new Exception("Mã xác nhận của quản lý không được để trống.");
        }

        if (maVaiTro == null || maVaiTro <= 0) {
            throw new Exception("Mã vai trò phải lớn hơn 0.");
        }

        NhanVienDTO nhanVien = new NhanVienDTO();
        nhanVien.setHoTen(hoTen.trim());
        nhanVien.setSdt(sdt);
        nhanVien.setTenDangNhap(usernameDaChuanHoa);
        nhanVien.setMatKhau(PasswordUtils.hash(matKhauHopLe));
        nhanVien.setMaVaiTro(maVaiTro);
        nhanVien.setTrangThaiLamViec(1);

        return nhanVienDAO.themNhanVien(nhanVien);
    }

    /**
     * Đổi mật khẩu cho phiên đăng nhập hiện tại.
     */
    public void changePassword(String matKhauHienTai, String matKhauMoi, String xacNhanMatKhauMoi) throws Exception {
        SessionContext.AuthSession session = requireActiveSession();
        String currentPassword = validatePassword(matKhauHienTai, "Mật khẩu hiện tại");
        String newPassword = validatePassword(matKhauMoi, "Mật khẩu mới");
        String confirmPassword = validatePassword(xacNhanMatKhauMoi, "Xác nhận mật khẩu mới");

        if (!newPassword.equals(confirmPassword)) {
            throw new AuthException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH, "Mật khẩu mới và xác nhận mật khẩu mới không khớp.");
        }

        NhanVienDTO nhanVien = nhanVienDAO.timNhanVienTheoMa(session.getMaNhanVien());
        if (nhanVien == null) {
            throw new AuthException(AuthErrorCode.SYSTEM_ERROR, "Không tìm thấy thông tin tài khoản hiện tại.");
        }

        if (nhanVien.getTrangThaiLamViec() != 1) {
            logout();
            throw new AuthException(AuthErrorCode.ACCOUNT_DISABLED, "Tài khoản đã bị vô hiệu hóa.");
        }

        if (!PasswordUtils.matches(currentPassword, nhanVien.getMatKhau())) {
            throw new AuthException(AuthErrorCode.INVALID_CURRENT_PASSWORD, "Mật khẩu hiện tại không chính xác.");
        }

        if (PasswordUtils.matches(newPassword, nhanVien.getMatKhau())) {
            throw new AuthException(AuthErrorCode.PASSWORD_INVALID, "Mật khẩu mới phải khác mật khẩu hiện tại.");
        }

        boolean updated = nhanVienDAO.doiMatKhau(session.getMaNhanVien(), PasswordUtils.hash(newPassword));
        if (!updated) {
            throw new AuthException(AuthErrorCode.SYSTEM_ERROR, "Không thể cập nhật mật khẩu trong CSDL.");
        }

        logout();
    }

    public List<VaiTroDTO> getActiveRoles() throws Exception {
        return vaiTroDAO.layDanhSachVaiTroDangHoatDong();
    }

    public List<ChucNangDTO> getPermissionsForCurrentSession() throws Exception {
        SessionContext.AuthSession session = requireActiveSession();
        return phanQuyenDAO.layDanhSachChucNangTheoVaiTro(session.getMaVaiTro());
    }

    public SessionContext.AuthSession getCurrentSession() {
        return SessionContext.getCurrentSession();
    }

    public void logout() {
        SessionContext.clear();
    }

    private SessionContext.AuthSession requireActiveSession() throws Exception {
        SessionContext.AuthSession session = SessionContext.getCurrentSession();
        if (session == null) {
            throw new AuthException(AuthErrorCode.SYSTEM_ERROR, "Không có phiên đăng nhập hợp lệ.");
        }
        return session;
    }

    private String validateUsername(String username) throws Exception {
        if (username == null) {
            throw new AuthException(AuthErrorCode.VALIDATION_ERROR, "Tên đăng nhập không được để trống.");
        }

        String normalized = username.trim();
        if (normalized.isEmpty()) {
            throw new AuthException(AuthErrorCode.VALIDATION_ERROR, "Tên đăng nhập không được để trống.");
        }

        if (normalized.length() < 3 || normalized.length() > 50) {
            throw new AuthException(AuthErrorCode.VALIDATION_ERROR, "Tên đăng nhập phải từ 3 đến 50 ký tự.");
        }
        return normalized;
    }

    private String validatePassword(String password, String fieldName) throws Exception {
        if (password == null || password.isEmpty()) {
            throw new AuthException(AuthErrorCode.PASSWORD_INVALID, fieldName + " không được để trống.");
        }

        if (password.length() < 6 || password.length() > 100) {
            throw new AuthException(AuthErrorCode.PASSWORD_INVALID, fieldName + " phải từ 6 đến 100 ký tự.");
        }

        for (int i = 0; i < password.length(); i++) {
            if (Character.isISOControl(password.charAt(i))) {
                throw new AuthException(AuthErrorCode.PASSWORD_INVALID, fieldName + " không được chứa ký tự điều khiển.");
            }
        }
        return password;
    }

    public enum AuthErrorCode {
        VALIDATION_ERROR,
        INVALID_CREDENTIALS,
        ACCOUNT_DISABLED,
        ROLE_NOT_ALLOWED,
        PASSWORD_INVALID,
        INVALID_CURRENT_PASSWORD,
        PASSWORD_CONFIRM_MISMATCH,
        SYSTEM_ERROR
    }

    public static class AuthException extends Exception {
        private final AuthErrorCode errorCode;

        public AuthException(AuthErrorCode errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public AuthErrorCode getErrorCode() {
            return errorCode;
        }
    }
}
