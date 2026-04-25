package com.bakery.presenters;

import com.bakery.models.dto.VaiTroDTO;
import com.bakery.services.AuthService;

import java.util.List;

public class RegisterPresenter {
    private final RegisterView view;
    private final AuthService authService;

    public RegisterPresenter(RegisterView view) {
        this(view, new AuthService());
    }

    public RegisterPresenter(RegisterView view, AuthService authService) {
        this.view = view;
        this.authService = authService;
    }

    public void handleRegister() {
        view.setRegisterEnabled(false);

        try {
            int maNhanVien = authService.register(
                    view.getHoTen(),
                    view.getSoDienThoai(),
                    view.getTenDangNhap(),
                    view.getMatKhau(),
                    view.getMaXacNhanQuanLy(),
                    view.getMaVaiTro()
            );

            view.showSuccess("Dang ky thanh cong. Ma nhan vien moi: " + maNhanVien);
            view.clearForm();
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        } finally {
            view.setRegisterEnabled(true);
        }
    }

    public void loadRoles() {
        try {
            List<VaiTroDTO> roles = authService.getActiveRoles();
            view.showRoles(roles);
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
    }

    public interface RegisterView {
        String getHoTen();
        String getSoDienThoai();
        String getTenDangNhap();
        String getMatKhau();
        String getMaXacNhanQuanLy();
        Integer getMaVaiTro();
        void setRegisterEnabled(boolean enabled);
        void clearForm();
        void showRoles(List<VaiTroDTO> roles);
        void showError(String message);
        void showSuccess(String message);
    }
}
