package com.bakery.views.controllers.taichinh;

import com.bakery.model.dto.hethong.LoaiThuChiDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * Controller dialog Thêm / Sửa Loại Thu Chi (UC53–UC54).
 * Pattern: khoiTao(existing) → showAndWait() → isConfirmed() + getters.
 */
public class ThemSuaLoaiThuChiDialogController {

    @FXML private Label        lblDialogTitle;
    @FXML private TextField    txtTenLoai;
    @FXML private ToggleButton btnThu;
    @FXML private ToggleButton btnChi;
    @FXML private Label        lblLoi;
    @FXML private javafx.scene.control.Button btnXacNhan;

    private final ToggleGroup togglePhanLoai = new ToggleGroup();
    private boolean confirmed = false;

    @FXML
    public void initialize() {
        btnThu.setToggleGroup(togglePhanLoai);
        btnChi.setToggleGroup(togglePhanLoai);
        // Mặc định chọn Thu
        btnThu.setSelected(true);
        apDungStyleToggle();

        // Không cho bỏ chọn cả hai
        togglePhanLoai.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null) togglePhanLoai.selectToggle(oldT);
            else apDungStyleToggle();
        });
    }

    /**
     * Khởi tạo dialog.
     * @param existing null = thêm mới; không null = chế độ sửa
     */
    public void khoiTao(LoaiThuChiDTO existing) {
        if (existing != null) {
            // Chế độ sửa
            lblDialogTitle.setText("Sửa hạng mục");
            btnXacNhan.setText("💾 Lưu thay đổi");
            txtTenLoai.setText(existing.getTenLoaiThuChi());
            if ("Chi".equals(existing.getPhanLoai())) {
                btnChi.setSelected(true);
            } else {
                btnThu.setSelected(true);
            }
            apDungStyleToggle();
        }
    }

    /** Áp dụng style active/inactive cho Toggle buttons. */
    private void apDungStyleToggle() {
        boolean thuSelected = btnThu.isSelected();

        btnThu.getStyleClass().removeAll("toggle-thu-active", "toggle-inactive");
        btnChi.getStyleClass().removeAll("toggle-chi-active", "toggle-inactive");

        btnThu.getStyleClass().add(thuSelected ? "toggle-thu-active" : "toggle-inactive");
        btnChi.getStyleClass().add(thuSelected ? "toggle-inactive" : "toggle-chi-active");
    }

    @FXML
    private void onChonPhanLoai() {
        apDungStyleToggle();
    }

    @FXML
    private void onXacNhan() {
        lblLoi.setText("");

        String ten = txtTenLoai.getText() == null ? "" : txtTenLoai.getText().trim();
        if (ten.isBlank()) {
            lblLoi.setText("❌ Vui lòng nhập tên hạng mục.");
            txtTenLoai.requestFocus();
            return;
        }
        if (ten.length() > 200) {
            lblLoi.setText("❌ Tên hạng mục không được vượt quá 200 ký tự.");
            return;
        }
        if (togglePhanLoai.getSelectedToggle() == null) {
            lblLoi.setText("❌ Vui lòng chọn phân loại Thu hoặc Chi.");
            return;
        }

        confirmed = true;
        dongDialog();
    }

    @FXML
    private void onHuy() {
        confirmed = false;
        dongDialog();
    }

    private void dongDialog() {
        Stage stage = (Stage) txtTenLoai.getScene().getWindow();
        stage.close();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public boolean isConfirmed() { return confirmed; }

    public String getTenLoai() {
        return txtTenLoai.getText() == null ? "" : txtTenLoai.getText().trim();
    }

    public String getPhanLoai() {
        return btnThu.isSelected() ? "Thu" : "Chi";
    }
}
