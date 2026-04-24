package com.bakery.views.controllers;

import com.bakery.model.dto.KhachHangDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class KhachHangDialogController {

    @FXML private Label lblTitle;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private TextField txtDiaChi;
    @FXML private Label lblError;
    @FXML private Button btnLuu;

    private final com.bakery.services.KhachHangService khachHangService = new com.bakery.services.KhachHangService();
    private KhachHangDTO khachHangHienTai;
    private boolean laChinhSua = false;
    private KhachHangDTO ketQua;

    public void khoiTaoChinhSua(KhachHangDTO kh) {
        if (kh != null) {
            this.khachHangHienTai = kh;
            this.laChinhSua = true;
            lblTitle.setText("Chỉnh sửa Khách Hàng");
            btnLuu.setText("Cập nhật");
            txtHoTen.setText(kh.getHoTen());
            txtSDT.setText(kh.getSdt());
            txtDiaChi.setText(kh.getDiaChi() != null ? kh.getDiaChi() : "");
        }
    }

    public KhachHangDTO getKetQua() {
        return ketQua;
    }

    @FXML
    private void onLuu() {
        lblError.setText("");

        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        // Validation
        if (hoTen.isEmpty()) {
            lblError.setText("Vui lòng nhập tên khách hàng.");
            return;
        }
        if (sdt.isEmpty() || !sdt.matches("\\d{10,15}")) {
            lblError.setText("Số điện thoại không hợp lệ (10-15 chữ số).");
            return;
        }

        if (laChinhSua) {
            // Update mode
            khachHangHienTai.setHoTen(hoTen);
            khachHangHienTai.setSdt(sdt);
            khachHangHienTai.setDiaChi(diaChi.isEmpty() ? null : diaChi);

            boolean ok = khachHangService.capNhatKhachHang(khachHangHienTai);
            if (ok) {
                ketQua = khachHangHienTai;
                hienThongBao("Thành công", "Đã cập nhật thông tin khách hàng.");
                dongDialog();
            } else {
                lblError.setText("Không thể cập nhật. Vui lòng kiểm tra lại.");
            }
        } else {
            // Insert mode
            KhachHangDTO khMoi = new KhachHangDTO();
            khMoi.setHoTen(hoTen);
            khMoi.setSdt(sdt);
            khMoi.setDiaChi(diaChi.isEmpty() ? null : diaChi);

            int maKH = khachHangService.themKhachHangMoi(khMoi);
            if (maKH > 0) {
                khMoi.setMaKH(maKH);
                ketQua = khMoi;
                hienThongBao("Thành công", "Đã thêm khách hàng mới! Mã KH: " + maKH);
                dongDialog();
            } else {
                lblError.setText("Không thể thêm khách hàng. SĐT có thể đã tồn tại.");
            }
        }
    }

    @FXML
    private void onHuy() {
        ketQua = null;
        dongDialog();
    }

    private void dongDialog() {
        Stage stage = (Stage) txtHoTen.getScene().getWindow();
        stage.close();
    }

    private void hienThongBao(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
