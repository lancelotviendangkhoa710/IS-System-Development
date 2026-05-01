package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.hethong.CaLamViecService;
import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.services.nhansu.XacThucService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class DangNhapViewFXMLController extends BaseController {
    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    // lblThongBao đã có trong BaseController nên không cần khai báo lại ở đây
    // Trừ khi DangNhapViewFXMLController muốn ánh xạ fx:id="lblThongBao" trực tiếp.
    // BaseController có `protected Label lblThongBao;`. 
    // FXML sẽ tự map vào `lblThongBao` của BaseController nếu có cùng fx:id.

    private final XacThucService xacThucService = new XacThucService();
    private final CaLamViecService caLamViecService = new CaLamViecService();
    private final PhanQuyenService phanQuyenService = new PhanQuyenService();

    public void setLoginInfo(String message) {
        if (message != null) {
            hienThiLoiLabel(message);
        }
    }

    @FXML
    private void onDangNhap() {
        String tenDangNhap = txtTenDangNhap.getText() == null ? "" : txtTenDangNhap.getText().trim();
        String matKhau = txtMatKhau.getText() == null ? "" : txtMatKhau.getText().trim();

        if (tenDangNhap.isBlank() || matKhau.isBlank()) {
            hienThiLoiLabel("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            NhanVienDTO nhanVien = xacThucService.dangNhap(tenDangNhap, matKhau);
            UserSession.setCurrentUser(nhanVien);

            // Kiểm tra và khôi phục ca làm việc nếu có
            com.bakery.model.dto.hethong.CaLamViecDTO caHienTai = caLamViecService.layCaHienTai(nhanVien.getMaNV());
            if (caHienTai != null) {
                com.bakery.utils.SessionContext.getInstance().moCa(caHienTai.getMaCa());
                quayLaiMenuChinh(txtTenDangNhap);
                return;
            }

            // Nếu là Thu ngân (không phải Admin) và chưa có ca -> Bắt buộc mở ca
            if (!phanQuyenService.laAdmin(nhanVien)) {
                System.out.println("[Session] Thu ngân chưa có ca -> Chuyển sang màn hình Mở ca.");
                transitionTo(txtTenDangNhap, "/fxml/MoCaView.fxml", "H3K Bakery - Mở ca làm việc", 1366, 768);
            } else {
                quayLaiMenuChinh(txtTenDangNhap);
            }
        } catch (Exception ex) {
            System.err.println("[DangNhap] Loi dang nhap: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            String msg = ex.getMessage();
            hienThiLoiLabel(msg != null && !msg.isBlank() ? msg : "Lỗi hệ thống: " + ex.getClass().getSimpleName());
        }
    }

}
