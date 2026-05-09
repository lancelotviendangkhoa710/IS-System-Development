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
    @FXML private Label lblLoi;

    private NguyenLieuDTO nguyenLieuChon;
    private double dinhMuc;
    private boolean confirmed = false;

    /** Inject danh sách nguyên liệu từ controller cha trước khi show. */
    public void khoiTaoDanhSachNguyenLieu(List<NguyenLieuDTO> dsNL) {
        cmbNguyenLieu.setConverter(new StringConverter<>() {
            @Override public String toString(NguyenLieuDTO nl) { return nl != null ? nl.getTenNL() : ""; }
            @Override public NguyenLieuDTO fromString(String s) { return null; }
        });
        if (dsNL != null) cmbNguyenLieu.setItems(FXCollections.observableArrayList(dsNL));
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
