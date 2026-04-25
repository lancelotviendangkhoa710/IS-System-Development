package com.bakery.presenters;

import com.bakery.models.dto.NhanVienDTO;
import com.bakery.services.AuthService;

public class LoginPresenter {
    private final LoginView view;
    private final AuthService authService;

    public LoginPresenter(LoginView view) {
        this(view, new AuthService());
    }

    public LoginPresenter(LoginView view, AuthService authService) {
        this.view = view;
        this.authService = authService;
    }

    public void handleLogin() {
        view.setLoginEnabled(false);

        try {
            NhanVienDTO nhanVien = authService.login(view.getUsername(), view.getPassword());
            view.showSuccess("Dang nhap thanh cong. Xin chao, " + nhanVien.getHoTen() + ".");
            view.clearForm();
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        } finally {
            view.setLoginEnabled(true);
        }
    }

    public interface LoginView {
        String getUsername();
        String getPassword();
        void setLoginEnabled(boolean enabled);
        void clearForm();
        void showError(String message);
        void showSuccess(String message);
    }
}
