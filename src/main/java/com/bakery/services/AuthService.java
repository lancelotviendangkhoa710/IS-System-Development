package com.bakery.services;

import com.bakery.models.dao.NhanVienDAO;
import com.bakery.models.dao.PhanQuyenDAO;
import com.bakery.models.dao.VaiTroDAO;
import com.bakery.models.dto.ChucNangDTO;
import com.bakery.models.dto.NhanVienDTO;
import com.bakery.models.dto.VaiTroDTO;
import com.bakery.utils.PasswordUtils;
import com.bakery.utils.SessionContext;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AuthService {
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final PhanQuyenDAO phanQuyenDAO = new PhanQuyenDAO();
    private final VaiTroDAO vaiTroDAO = new VaiTroDAO();

    public NhanVienDTO login(String username, String password) throws Exception {
        String usernameDaChuanHoa = validateUsername(username);
        String matKhauHopLe = validatePassword(password, "Mat khau");

        NhanVienDTO nhanVien = nhanVienDAO.timNhanVienTheoTenDangNhap(usernameDaChuanHoa);
        if (nhanVien == null) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS, "Ten dang nhap hoac mat khau khong chinh xac.");
        }

        if (nhanVien.getTrangThaiLamViec() != 1) {
            throw new AuthException(AuthErrorCode.ACCOUNT_DISABLED, "Tai khoan da bi vo hieu hoa.");
        }

        if (!PasswordUtils.matches(matKhauHopLe, nhanVien.getMatKhau())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS, "Ten dang nhap hoac mat khau khong chinh xac.");
        }

        PhanQuyenDAO.RolePermissionInfo roleInfo = phanQuyenDAO.layThongTinPhanQuyenTheoVaiTro(nhanVien.getMaVaiTro());
        if (roleInfo == null || !roleInfo.isVaiTroHoatDong()) {
            throw new AuthException(AuthErrorCode.ROLE_NOT_ALLOWED, "Vai tro cua tai khoan khong con hieu luc.");
        }

        if (roleInfo.getDanhSachChucNang().isEmpty()) {
            throw new AuthException(AuthErrorCode.ROLE_NOT_ALLOWED, "Vai tro hien tai khong duoc cap quyen truy cap.");
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

    public int register(
            String hoTen,
            String soDienThoai,
            String tenDangNhap,
            String matKhau,
            String maXacNhanQuanLy,
            Integer maVaiTro
    ) throws Exception {
        if (hoTen == null || hoTen.isBlank()) {
            throw new Exception("Ho ten khong duoc de trong.");
        }

        if (soDienThoai == null || soDienThoai.isBlank()) {
            throw new Exception("So dien thoai khong duoc de trong.");
        }

        String sdt = soDienThoai.trim();
        if (sdt.length() < 9 || sdt.length() > 15) {
            throw new Exception("So dien thoai phai tu 9 den 15 ky tu.");
        }

        String usernameDaChuanHoa = validateUsername(tenDangNhap);
        String matKhauHopLe = validatePassword(matKhau, "Mat khau");

        if (maXacNhanQuanLy == null || maXacNhanQuanLy.isBlank()) {
            throw new Exception("Ma xac nhan cua quan ly khong duoc de trong.");
        }

        if (maVaiTro == null || maVaiTro <= 0) {
            throw new Exception("Ma vai tro phai lon hon 0.");
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

    public void changePassword(String matKhauHienTai, String matKhauMoi, String xacNhanMatKhauMoi) throws Exception {
        SessionContext.AuthSession session = requireActiveSession();
        String currentPassword = validatePassword(matKhauHienTai, "Mat khau hien tai");
        String newPassword = validatePassword(matKhauMoi, "Mat khau moi");
        String confirmPassword = validatePassword(xacNhanMatKhauMoi, "Xac nhan mat khau moi");

        if (!newPassword.equals(confirmPassword)) {
            throw new AuthException(AuthErrorCode.PASSWORD_CONFIRM_MISMATCH, "Mat khau moi va xac nhan mat khau moi khong khop.");
        }

        NhanVienDTO nhanVien = nhanVienDAO.timNhanVienTheoMa(session.getMaNhanVien());
        if (nhanVien == null) {
            throw new AuthException(AuthErrorCode.SYSTEM_ERROR, "Khong tim thay thong tin tai khoan hien tai.");
        }

        if (nhanVien.getTrangThaiLamViec() != 1) {
            logout();
            throw new AuthException(AuthErrorCode.ACCOUNT_DISABLED, "Tai khoan da bi vo hieu hoa.");
        }

        if (!PasswordUtils.matches(currentPassword, nhanVien.getMatKhau())) {
            throw new AuthException(AuthErrorCode.INVALID_CURRENT_PASSWORD, "Mat khau hien tai khong chinh xac.");
        }

        if (PasswordUtils.matches(newPassword, nhanVien.getMatKhau())) {
            throw new AuthException(AuthErrorCode.PASSWORD_INVALID, "Mat khau moi phai khac mat khau hien tai.");
        }

        boolean updated = nhanVienDAO.doiMatKhau(session.getMaNhanVien(), PasswordUtils.hash(newPassword));
        if (!updated) {
            throw new AuthException(AuthErrorCode.SYSTEM_ERROR, "Khong the cap nhat mat khau trong CSDL.");
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
            throw new AuthException(AuthErrorCode.SYSTEM_ERROR, "Khong co phien dang nhap hop le.");
        }
        return session;
    }

    private String validateUsername(String username) throws Exception {
        if (username == null) {
            throw new AuthException(AuthErrorCode.VALIDATION_ERROR, "Ten dang nhap khong duoc de trong.");
        }

        String normalized = username.trim();
        if (normalized.isEmpty()) {
            throw new AuthException(AuthErrorCode.VALIDATION_ERROR, "Ten dang nhap khong duoc de trong.");
        }

        if (normalized.length() < 3 || normalized.length() > 50) {
            throw new AuthException(AuthErrorCode.VALIDATION_ERROR, "Ten dang nhap phai tu 3 den 50 ky tu.");
        }
        return normalized;
    }

    private String validatePassword(String password, String fieldName) throws Exception {
        if (password == null || password.isEmpty()) {
            throw new AuthException(AuthErrorCode.PASSWORD_INVALID, fieldName + " khong duoc de trong.");
        }

        if (password.length() < 6 || password.length() > 100) {
            throw new AuthException(AuthErrorCode.PASSWORD_INVALID, fieldName + " phai tu 6 den 100 ky tu.");
        }

        for (int i = 0; i < password.length(); i++) {
            if (Character.isISOControl(password.charAt(i))) {
                throw new AuthException(AuthErrorCode.PASSWORD_INVALID, fieldName + " khong duoc chua ky tu dieu khien.");
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
