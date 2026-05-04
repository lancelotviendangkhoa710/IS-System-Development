package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.nhansu.XacThucService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ThoBepDashboardViewFXMLController extends BaseController {
    private final XacThucService xacThucService = new XacThucService();

    @FXML private Label lblTenThoBep;
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
        lblTenThoBep.setText(nhanVien.getHoTen());
        lblVaiTro.setText(nhanVien.getTenVaiTro());
        lblMoTa.setText("Uu tien don cho xu ly, cap nhat san xuat va phoi hop kho nguyen lieu.");
    }

    @FXML
    private void onMoTheoDoiDon() {
        transitionTo(lblTenThoBep, "/fxml/TheoDoiDonHangView.fxml", "H3K Bakery - Theo doi don hang", 1366, 768);
    }

    @FXML
    private void onMoThanhPhanBanh() {
        transitionTo(lblTenThoBep, "/fxml/ThanhPhanBanhView.fxml", "H3K Bakery - Cong thuc va thanh phan", 1366, 768);
    }

    @FXML
    private void onMoKho() {
        transitionTo(lblTenThoBep, "/fxml/KhoView.fxml", "H3K Bakery - Kho va nguyen lieu", 1280, 720);
    }

    @FXML
    private void onDangXuat() {
        xacThucService.dangXuat();
        UserSession.clear();
        transitionTo(lblTenThoBep, "/fxml/DangNhapView.fxml", "H3K Bakery - Dang nhap", 1280, 720);
    }
}
