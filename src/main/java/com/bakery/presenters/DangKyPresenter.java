package com.bakery.presenters;

import com.bakery.model.dto.VaiTroDTO;
import com.bakery.services.XacThucService;

import java.util.List;

public class DangKyPresenter {
    private final RegisterView view;
    private final XacThucService xacThucService;

    public DangKyPresenter(RegisterView view) {
        this(view, new XacThucService());
    }

    public DangKyPresenter(RegisterView view, XacThucService xacThucService) {
        this.view = view;
        this.xacThucService = xacThucService;
    }

    public void handleRegister() {
        view.setRegisterEnabled(false);

        try {
            int maNhanVien = xacThucService.dangKy(
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
            List<VaiTroDTO> roles = xacThucService.layDanhSachVaiTroDangHoatDong();
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
