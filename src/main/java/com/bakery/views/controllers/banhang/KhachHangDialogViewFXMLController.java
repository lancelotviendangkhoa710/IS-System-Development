package com.bakery.views.controllers.banhang;

import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.utils.DialogHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class KhachHangDialogViewFXMLController {

    @FXML private Label lblTitle;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSDT;
    @FXML private TextField txtDiaChi;
    @FXML private Label lblError;
    @FXML private Button btnLuu;

    private final com.bakery.services.khachhang.KhachHangService khachHangService = new com.bakery.services.khachhang.KhachHangService();
    private KhachHangDTO khachHangHienTai;
    private boolean laChinhSua = false;
    private KhachHangDTO ketQua;
    private boolean duLieuDaThayDoi = false;

    @FXML
    public void initialize() {
        txtHoTen.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtSDT.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtDiaChi.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        Platform.runLater(() -> {
            Stage s = (Stage) txtHoTen.getScene().getWindow();
            s.setOnCloseRequest(ev -> { if (!xacNhanHuyThayDoi()) ev.consume(); });
        });
    }

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

        try {
            if (laChinhSua) {
                // Update mode
                khachHangHienTai.setHoTen(hoTen);
                khachHangHienTai.setSdt(sdt);
                khachHangHienTai.setDiaChi(diaChi.isEmpty() ? null : diaChi);

                boolean ok = khachHangService.capNhatKhachHang(khachHangHienTai);
                if (ok) {
                    ketQua = khachHangHienTai;
                    duLieuDaThayDoi = false; // lưu thành công → reset dirty
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
                    duLieuDaThayDoi = false; // lưu thành công → reset dirty
                    hienThongBao("Thành công", "Đã thêm khách hàng mới! Mã KH: " + maKH);
                    dongDialog();
                } else {
                    lblError.setText("Không thể thêm khách hàng. SĐT có thể đã tồn tại.");
                }
            }
        } catch (Exception e) {
            lblError.setText("Lỗi: " + e.getMessage());
            System.err.println("[KhachHangDialog] Lỗi lưu khách hàng: " + e.getMessage());
        }
    }

    @FXML
    private void onHuy() {
        if (!xacNhanHuyThayDoi()) return;
        ketQua = null;
        dongDialog();
    }

    private boolean xacNhanHuyThayDoi() {
        if (!duLieuDaThayDoi) return true;
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có thay đổi chưa lưu. Hủy bỏ?", ButtonType.YES, ButtonType.NO);
        a.setTitle("Dữ liệu chưa lưu"); a.setHeaderText("Cảnh báo — Dữ liệu chưa lưu");
        DialogHelper.applyBakeryTheme(a);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
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
        DialogHelper.applyBakeryTheme(alert);
        alert.showAndWait();
    }
}
