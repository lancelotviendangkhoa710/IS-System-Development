package com.bakery.views.controllers;

import com.bakery.model.dto.NhanVienDTO;
import com.bakery.services.XacThucService;
import com.bakery.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;

public class DangNhapViewFXMLController {
    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private Label lblThongBao;

    private final XacThucService xacThucService = new XacThucService();
    private final com.bakery.services.CaLamViecService caLamViecService = new com.bakery.services.CaLamViecService();
    private final com.bakery.services.PhanQuyenService phanQuyenService = new com.bakery.services.PhanQuyenService();

    public void setLoginInfo(String message) {
        if (lblThongBao != null && message != null) {
            lblThongBao.setText(message);
        }
    }

    @FXML
    private void onDangNhap() {
        String tenDangNhap = txtTenDangNhap.getText() == null ? "" : txtTenDangNhap.getText().trim();
        String matKhau = txtMatKhau.getText() == null ? "" : txtMatKhau.getText().trim();

        if (tenDangNhap.isBlank() || matKhau.isBlank()) {
            lblThongBao.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            NhanVienDTO nhanVien = xacThucService.dangNhap(tenDangNhap, matKhau);
            UserSession.setCurrentUser(nhanVien);

            // Kiểm tra và khôi phục ca làm việc nếu có
            com.bakery.model.dto.CaLamViecDTO caHienTai = caLamViecService.layCaHienTai(nhanVien.getMaNV());
            if (caHienTai != null) {
                com.bakery.utils.SessionContext.getInstance().moCa(caHienTai.getMaCa());
                moManHinhMenu(nhanVien);
                return;
            }

            // Nếu là Thu ngân (không phải Admin) và chưa có ca -> Bắt buộc mở ca
            if (!phanQuyenService.laAdmin(nhanVien)) {
                System.out.println("[Session] Thu ngân chưa có ca -> Chuyển sang màn hình Mở ca.");
                moManHinhMoCa(nhanVien);
            } else {
                moManHinhMenu(nhanVien);
            }
        } catch (Exception ex) {
            System.err.println("[DangNhap] Loi dang nhap: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
            String msg = ex.getMessage();
            lblThongBao.setText(msg != null && !msg.isBlank() ? msg : "Loi he thong: " + ex.getClass().getSimpleName());
        }
    }

    private void moManHinhMenu(NhanVienDTO nhanVien) throws Exception {
        URL fxmlUrl = getClass().getResource("/fxml/MainMenuView.fxml");
        if (fxmlUrl == null) {
            throw new RuntimeException("Không tìm thấy /fxml/MainMenuView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load(), 1366, 768);
        MainMenuViewFXMLController controller = loader.getController();
        controller.khoiTaoThongTinDangNhap(nhanVien);

        URL cssUrl = getClass().getResource("/css/bakery.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        Stage stage = (Stage) txtTenDangNhap.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private void moManHinhMoCa(NhanVienDTO nhanVien) throws Exception {
        URL fxmlUrl = getClass().getResource("/fxml/MoCaView.fxml");
        if (fxmlUrl == null) {
            throw new RuntimeException("Không tìm thấy /fxml/MoCaView.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(loader.load());

        URL cssUrl = getClass().getResource("/css/bakery.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        Stage stage = (Stage) txtTenDangNhap.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("H3K Bakery - Mở ca làm việc");
        stage.centerOnScreen();
    }
}
