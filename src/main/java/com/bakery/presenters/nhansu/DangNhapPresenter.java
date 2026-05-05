package com.bakery.presenters.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.AuthService;
import com.bakery.services.nhansu.XacThucService;
import com.bakery.utils.SessionContext;
import com.bakery.utils.UserSession;

public class DangNhapPresenter {
    private final DangNhapView view;
    private final AuthService authService;
    private final XacThucService xacThucService;

    public DangNhapPresenter(DangNhapView view) {
        this(view, new AuthService(), new XacThucService());
    }

    public DangNhapPresenter(DangNhapView view, AuthService authService, XacThucService xacThucService) {
        this.view = view;
        this.authService = authService;
        this.xacThucService = xacThucService;
    }

    public void handleLogin() {
        view.setLoginEnabled(false);

        try {
            // Bước 1: Xác thực và lấy thông tin phân quyền
            NhanVienDTO nhanVien = xacThucService.dangNhap(view.getUsername(), view.getPassword());

            // Bước 2: Đăng nhập và nhận token phiên làm việc
            String token = authService.login(view.getUsername(), view.getPassword());

            // Bước 3: Đồng bộ tenVaiTro từ session phân quyền sang DTO
            SessionContext.AuthSession session = xacThucService.layPhienHienTai();
            if (session != null) {
                nhanVien.setTenVaiTro(session.getTenVaiTro());
            }

            // Bước 4: Lưu vào session dùng chung
            UserSession.setCurrentUser(nhanVien);
            UserSession.setCurrentToken(token);

            view.showSuccess("Đăng nhập thành công. Xin chào, " + nhanVien.getHoTen() + ".");
            view.clearForm();

            // Chuyển sang màn hình chính
            view.navigateToMainScreen();

        } catch (Exception ex) {
            view.showError(ex.getMessage());
        } finally {
            view.setLoginEnabled(true);
        }
    }

    public void handleLogout() {
        try {
            String currentToken = UserSession.getCurrentToken();
            if (currentToken != null) {
                authService.logout(currentToken);
            }
            xacThucService.dangXuat();
            UserSession.clear();
            view.showSuccess("Đã đăng xuất thành công.");
            view.navigateToLoginScreen();
        } catch (Exception ex) {
            view.showError("Lỗi khi đăng xuất: " + ex.getMessage());
        }
    }

    public interface DangNhapView {
        String getUsername();

        String getPassword();

        void setLoginEnabled(boolean enabled);

        void clearForm();

        void showError(String message);

        void showSuccess(String message);

        void navigateToMainScreen();

        void navigateToLoginScreen();
    }
}
