package com.bakery.views.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Controller cho PaymentDialog.fxml.
 * Hiển thị thông tin thanh toán còn lại và chờ thu ngân xác nhận.
 * Implements IOrderDialogFactory.showPaymentConfirmation() thông qua cơ chế Stage result.
 */
public class PaymentDialogController {

    @FXML private Label lblMaDon;
    @FXML private Label lblTongTien;
    @FXML private Label lblDaCoc;
    @FXML private Label lblConLai;

    /** Kết quả xác nhận của thu ngân (true = đã thu đủ) */
    private boolean confirmed = false;

    // ─── INJECT DỮ LIỆU ───────────────────────────────────────────────────

    /**
     * Được gọi từ CreateOrderController sau khi load FXML,
     * trước khi showAndWait().
     */
    public void initData(int maDon, double tongTien, double daCoc, double conLai) {
        lblMaDon.setText("#" + maDon);
        lblTongTien.setText(dinhDangTien(tongTien));
        lblDaCoc.setText(dinhDangTien(daCoc));
        lblConLai.setText(dinhDangTien(conLai));
    }

    // ─── XỬ LÝ NÚT ────────────────────────────────────────────────────────

    @FXML
    private void onXacNhan() {
        confirmed = true;
        dongDialog();
    }

    @FXML
    private void onHuy() {
        confirmed = false;
        dongDialog();
    }

    // ─── GETTER KẾT QUẢ ────────────────────────────────────────────────────

    /** @return true nếu thu ngân bấm "Đã thu đủ" */
    public boolean isConfirmed() {
        return confirmed;
    }

    // ─── HELPER ───────────────────────────────────────────────────────────

    private void dongDialog() {
        Stage stage = (Stage) lblMaDon.getScene().getWindow();
        stage.close();
    }

    private String dinhDangTien(double amount) {
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        fmt.setMaximumFractionDigits(0);
        return fmt.format(amount) + " đ";
    }
}
