package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.nhansu.XacThucService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ThuKhoDashboardViewFXMLController extends BaseController {
    private final XacThucService xacThucService = new XacThucService();

    @FXML private Label lblTenThuKho;
    @FXML private Label lblVaiTro;
    @FXML private Label lblMoTa;

    @FXML
    private void initialize() {
        khoiTaoDashboard(UserSession.getCurrentUser());
    }

    public void khoiTaoDashboard(NhanVienDTO nhanVien) {
        if (nhanVien == null) {
            return;
        }
        UserSession.setCurrentUser(nhanVien);
        lblTenThuKho.setText(nhanVien.getHoTen());
        lblVaiTro.setText(nhanVien.getTenVaiTro());
        lblMoTa.setText("Theo doi ton kho, quan ly nguyen lieu va dieu phoi nha cung cap cho san xuat.");
    }

    @FXML
    private void onMoKho() {
        transitionTo(lblTenThuKho, "/fxml/kho/KhoView.fxml", "H3K Bakery - Kho tong", 1280, 720);
    }

    @FXML
    private void onMoNguyenLieu() {
        transitionTo(lblTenThuKho, "/fxml/kho/NguyenLieuView.fxml", "H3K Bakery - Nguyen lieu", 1280, 720);
    }

    @FXML
    private void onMoNhaCungCap() {
        transitionTo(lblTenThuKho, "/fxml/kho/QuanLyNhaCungCapView.fxml", "H3K Bakery - Nha cung cap", 1280, 720);
    }

    @FXML
    private void onMoXuatKho() {
        transitionTo(lblTenThuKho, "/fxml/kho/XuatKhoView.fxml", "H3K Bakery - Xuat phieu nguyen lieu hong", 1280, 720);
    }

    @FXML
    private void onDangXuat() {
        xacThucService.dangXuat();
        UserSession.clear();
        transitionTo(lblTenThuKho, "/fxml/hethong/DangNhapView.fxml", "H3K Bakery - Dang nhap", 1280, 720);
    }
}
