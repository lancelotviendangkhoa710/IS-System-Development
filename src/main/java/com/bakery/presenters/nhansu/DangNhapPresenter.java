package com.bakery.presenters.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.nhansu.XacThucService;

public class DangNhapPresenter {
    private final DangNhapView view;
    private final XacThucService xacThucService;

    public DangNhapPresenter(DangNhapView view) {
        this(view, new XacThucService());
    }

    public DangNhapPresenter(DangNhapView view, XacThucService xacThucService) {
        this.view = view;
        this.xacThucService = xacThucService;
    }

    public void handleLogin() {
        view.setLoginEnabled(false);

        try {
            NhanVienDTO nhanVien = xacThucService.dangNhap(view.getUsername(), view.getPassword());
            view.showSuccess("Dang nhap thanh cong. Xin chao, " + nhanVien.getHoTen() + ".");
            view.clearForm();
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        } finally {
            view.setLoginEnabled(true);
        }
    }

    public interface DangNhapView {
        String getUsername();
        String getPassword();
        void setLoginEnabled(boolean enabled);
        void clearForm();
        void showError(String message);
        void showSuccess(String message);
    }
}
