package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.NguyenLieuDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Controller dialog Thêm Nguyên Liệu vào Công Thức.
 * Thu thập: chọn nguyên liệu + nhập định mức. Trả kết quả sau khi Stage đóng.
 */
public class ThemCongThucDialogController {

    @FXML private ComboBox<NguyenLieuDTO> cmbNguyenLieu;
    @FXML private TextField txtDinhMuc;
    @FXML private Label     lblDonVi;  // hiện đơn vị tính động khi chọn NL
    @FXML private Label     lblLoi;

    private NguyenLieuDTO nguyenLieuChon;
    private double dinhMuc;
    private boolean confirmed = false;

    /** Inject danh sách nguyên liệu từ controller cha trước khi show. */
    public void khoiTaoDanhSachNguyenLieu(List<NguyenLieuDTO> dsNL) {
        // Converter hiện "Tên NL (DVT)" để user biết đơn vị trước khi nhập
        cmbNguyenLieu.setConverter(new StringConverter<>() {
            @Override public String toString(NguyenLieuDTO nl) {
                if (nl == null) return "";
                String dvt = nl.getTenDVT();
                return dvt.isEmpty() ? nl.getTenNL() : nl.getTenNL() + " (" + dvt + ")";
            }
            @Override public NguyenLieuDTO fromString(String s) { return null; }
        });

        if (dsNL != null) cmbNguyenLieu.setItems(FXCollections.observableArrayList(dsNL));

        // Cập nhật label đơn vị ngay khi chọn nguyên liệu
        cmbNguyenLieu.valueProperty().addListener((obs, old, nl) -> {
            if (lblDonVi != null) {
                lblDonVi.setText(nl != null && !nl.getTenDVT().isEmpty() ? nl.getTenDVT() : "");
            }
        });
    }

    public boolean isConfirmed()           { return confirmed; }
    public NguyenLieuDTO getNguyenLieu()   { return nguyenLieuChon; }
    public double getDinhMuc()             { return dinhMuc; }

    @FXML
    private void onXacNhan() {
        nguyenLieuChon = cmbNguyenLieu.getValue();
        if (nguyenLieuChon == null) { lblLoi.setText("⚠ Vui lòng chọn nguyên liệu."); return; }
        try {
            dinhMuc = Double.parseDouble(txtDinhMuc.getText().trim());
            if (dinhMuc <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblLoi.setText("⚠ Định mức phải là số dương hợp lệ.");
            return;
        }
        confirmed = true;
        dongDialog();
    }

    @FXML private void onHuy() { confirmed = false; dongDialog(); }

    private void dongDialog() {
        ((Stage) cmbNguyenLieu.getScene().getWindow()).close();
    }
}
