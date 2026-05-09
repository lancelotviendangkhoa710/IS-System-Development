package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.DonViTinhDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Controller dialog Thêm Nguyên Liệu Mới.
 * Thu thập dữ liệu từ form, trả kết quả qua getter sau khi Stage đóng.
 */
public class ThemNguyenLieuDialogController {

    @FXML private TextField txtTenNL;
    @FXML private ComboBox<DonViTinhDTO> cmbDonViTinh;
    @FXML private TextField txtMucTon;
    @FXML private TextField txtXuatXu;
    @FXML private Label lblLoi;

    // Kết quả trả về cho controller cha
    private String tenNL;
    private String xuatXu;
    private double mucTon;
    private DonViTinhDTO donViTinh;
    private boolean confirmed = false;

    /** Inject danh sách đơn vị tính từ controller cha trước khi show. */
    public void khoiTaoDanhSachDVT(List<DonViTinhDTO> dsDVT) {
        cmbDonViTinh.setConverter(new StringConverter<>() {
            @Override public String toString(DonViTinhDTO d) { return d != null ? d.getTenDVT() : ""; }
            @Override public DonViTinhDTO fromString(String s) { return null; }
        });
        if (dsDVT != null) cmbDonViTinh.setItems(FXCollections.observableArrayList(dsDVT));
    }

    public boolean isConfirmed()      { return confirmed; }
    public String getTenNL()          { return tenNL; }
    public String getXuatXu()         { return xuatXu; }
    public double getMucTon()         { return mucTon; }
    public DonViTinhDTO getDonViTinh() { return donViTinh; }

    @FXML
    private void onXacNhan() {
        String ten = txtTenNL.getText().trim();
        if (ten.isEmpty()) { lblLoi.setText("⚠ Tên nguyên liệu không được để trống."); return; }
        if (cmbDonViTinh.getValue() == null) { lblLoi.setText("⚠ Vui lòng chọn đơn vị tính."); return; }

        tenNL = ten;
        xuatXu = txtXuatXu.getText().trim();
        donViTinh = cmbDonViTinh.getValue();
        try { mucTon = Double.parseDouble(txtMucTon.getText().trim()); }
        catch (NumberFormatException ignored) { mucTon = 0; }

        confirmed = true;
        dongDialog();
    }

    @FXML private void onHuy() { confirmed = false; dongDialog(); }

    private void dongDialog() {
        ((Stage) txtTenNL.getScene().getWindow()).close();
    }
}
