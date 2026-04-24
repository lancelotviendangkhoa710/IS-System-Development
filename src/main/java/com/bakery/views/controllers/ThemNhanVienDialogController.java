package com.bakery.views.controllers;

import com.bakery.model.dto.NhanVienDTO;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Map;

public class ThemNhanVienDialogController {

    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private DatePicker dpNgaySinh;
    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private ComboBox<String> cbVaiTro;
    @FXML private Label lblError;

    private final com.bakery.services.NhanVienService nhanVienService = new com.bakery.services.NhanVienService();
    private Map<Integer, String> vaiTroMap;

    @FXML
    public void initialize() {
        vaiTroMap = nhanVienService.layDanhSachVaiTro();
        cbVaiTro.getItems().addAll(vaiTroMap.values());
    }

    @FXML
    private void onLuu() {
        lblError.setText("");

        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = txtMatKhau.getText().trim();
        String vaiTroChon = cbVaiTro.getValue();

        // Validation
        if (hoTen.isEmpty()) {
            lblError.setText("Vui lòng nhập họ tên.");
            return;
        }
        if (sdt.isEmpty() || !sdt.matches("\\d{10,15}")) {
            lblError.setText("Số điện thoại không hợp lệ (10-15 chữ số).");
            return;
        }
        if (tenDangNhap.isEmpty() || tenDangNhap.length() < 4) {
            lblError.setText("Tên đăng nhập phải có ít nhất 4 ký tự.");
            return;
        }
        if (matKhau.isEmpty() || matKhau.length() < 6) {
            lblError.setText("Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }
        if (vaiTroChon == null || vaiTroChon.isEmpty()) {
            lblError.setText("Vui lòng chọn vai trò.");
            return;
        }

        // Find mã vai trò from selected name
        int maVaiTro = -1;
        for (Map.Entry<Integer, String> entry : vaiTroMap.entrySet()) {
            if (entry.getValue().equals(vaiTroChon)) {
                maVaiTro = entry.getKey();
                break;
            }
        }

        NhanVienDTO nv = new NhanVienDTO();
        nv.setMaVaiTro(maVaiTro);
        nv.setHoTen(hoTen);
        nv.setNgaySinh(dpNgaySinh.getValue());
        nv.setSdt(sdt);
        nv.setTenDangNhap(tenDangNhap);
        nv.setMatKhau(matKhau);

        try {
            int maNV = nhanVienService.themNhanVien(nv);
            if (maNV > 0) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Đã thêm nhân viên thành công! Mã NV: " + maNV);
                alert.showAndWait();
                dongDialog();
            }
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg.contains("ORA-20001")) {
                lblError.setText("Số điện thoại đã tồn tại trong hệ thống.");
            } else if (msg.contains("ORA-20002")) {
                lblError.setText("Tên đăng nhập đã tồn tại trong hệ thống.");
            } else if (msg.contains("ORA-20003")) {
                lblError.setText("Vai trò không hợp lệ.");
            } else {
                lblError.setText("Lỗi hệ thống: " + msg);
            }
        }
    }

    @FXML
    private void onHuy() {
        dongDialog();
    }

    private void dongDialog() {
        Stage stage = (Stage) txtHoTen.getScene().getWindow();
        stage.close();
    }
}
