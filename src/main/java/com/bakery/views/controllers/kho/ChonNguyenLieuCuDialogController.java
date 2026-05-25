package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.utils.DialogHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Controller dialog Chọn Nguyên Liệu Có Sẵn vào Công Thức.
 * Thu thập: maNL (chọn từ danh sách) + dinhMuc.
 * Caller: CongThucViewFXMLController.onThemCongThuc().
 */
public class ChonNguyenLieuCuDialogController {

    @FXML private ComboBox<NguyenLieuDTO> cmbNguyenLieu;
    @FXML private Label                   lblDonViTinh;
    @FXML private Label                   lblTonKho;
    @FXML private TextField               txtDinhMuc;
    @FXML private Label                   lblDonViDinhMuc;
    @FXML private Label                   lblLoi;

    private static final NumberFormat NF = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

    // ── Kết quả trả về ────────────────────────────────────────────────────────
    private int     maNL      = -1;
    private double  dinhMuc   = 0;
    private boolean confirmed = false;

    // Danh sách gốc để reset filter
    private List<NguyenLieuDTO> dsNguyenLieuGoc;

    @FXML
    public void initialize() {
        cmbNguyenLieu.setConverter(new StringConverter<>() {
            @Override public String toString(NguyenLieuDTO nl) {
                return nl != null ? nl.getTenNL() : "";
            }
            @Override public NguyenLieuDTO fromString(String s) { return null; }
        });

        // Autocomplete: lọc danh sách khi gõ
        cmbNguyenLieu.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            NguyenLieuDTO selected = cmbNguyenLieu.getValue();
            if (selected != null && cmbNguyenLieu.getConverter().toString(selected).equals(newVal)) return;

            String filter = newVal == null ? "" : newVal.trim().toLowerCase();
            List<NguyenLieuDTO> filtered = dsNguyenLieuGoc == null ? List.of()
                    : dsNguyenLieuGoc.stream()
                        .filter(nl -> nl.getTenNL().toLowerCase().contains(filter))
                        .toList();
            cmbNguyenLieu.setItems(FXCollections.observableArrayList(filtered));
            if (!filtered.isEmpty() && !filter.isEmpty()) cmbNguyenLieu.show();
        });

        // Khi chọn NL → cập nhật label DVT + tồn kho
        cmbNguyenLieu.valueProperty().addListener((obs, oldNL, newNL) -> {
            if (newNL != null) {
                lblDonViTinh.setText(newNL.getTenDVT());
                lblDonViDinhMuc.setText(newNL.getTenDVT());
                String tonKho = newNL.getSoLuongTonTong() != null
                        ? NF.format(newNL.getSoLuongTonTong()) + " " + newNL.getTenDVT()
                        : "—";
                lblTonKho.setText(tonKho);
            } else {
                lblDonViTinh.setText("—");
                lblDonViDinhMuc.setText("");
                lblTonKho.setText("—");
            }
        });

        Platform.runLater(() -> {
            Stage s = (Stage) cmbNguyenLieu.getScene().getWindow();
            s.setOnCloseRequest(ev -> confirmed = false);
        });
    }

    /** Inject danh sách nguyên liệu từ cachedDsNguyenLieu của View cha. */
    public void khoiTao(List<NguyenLieuDTO> dsNL) {
        this.dsNguyenLieuGoc = dsNL != null ? dsNL : List.of();
        cmbNguyenLieu.setItems(FXCollections.observableArrayList(dsNguyenLieuGoc));
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public boolean isConfirmed() { return confirmed; }
    public int     getMaNL()    { return maNL; }
    public double  getDinhMuc() { return dinhMuc; }

    @FXML
    private void onXacNhan() {
        lblLoi.setText("");

        NguyenLieuDTO chon = cmbNguyenLieu.getValue();
        if (chon == null) {
            lblLoi.setText("⚠ Vui lòng chọn nguyên liệu.");
            return;
        }

        double dm;
        try {
            dm = Double.parseDouble(txtDinhMuc.getText().trim());
            if (dm <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblLoi.setText("⚠ Định mức tiêu hao phải là số dương hợp lệ.");
            return;
        }

        maNL    = chon.getMaNL();
        dinhMuc = dm;
        confirmed = true;
        dongDialog();
    }

    @FXML
    private void onHuy() {
        confirmed = false;
        dongDialog();
    }

    private void dongDialog() {
        ((Stage) cmbNguyenLieu.getScene().getWindow()).close();
    }
}
