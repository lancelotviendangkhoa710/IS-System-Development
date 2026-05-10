package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NhaCungCapDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

/** Dialog thêm nguyên liệu mới kèm nhập kho lần đầu (atomic). */
public class ThemNguyenLieuDialogController {

    // ── Section: Nguyên liệu ─────────────────────────────────────────────────
    @FXML private TextField txtTenNL;
    @FXML private ComboBox<DonViTinhDTO> cmbDonViTinh;
    @FXML private TextField txtMucTon;
    @FXML private TextField txtXuatXu;

    // ── Section: Nhập kho lần đầu ────────────────────────────────────────────
    @FXML private ComboBox<NhaCungCapDTO> cmbNhaCungCap;
    @FXML private TextField txtSoLuong;
    @FXML private TextField txtDonGia;
    @FXML private DatePicker dpNgaySanXuat;
    @FXML private DatePicker dpHanSuDung;

    @FXML private Label lblLoi;

    // ── Kết quả trả về ────────────────────────────────────────────────────────
    private String tenNL;
    private String xuatXu;
    private double mucTon;
    private DonViTinhDTO donViTinh;
    private NhaCungCapDTO nhaCungCap;
    private double soLuong;
    private double donGia;
    private LocalDate ngaySanXuat;
    private LocalDate hanSuDung;
    private boolean confirmed = false;

    /** Inject dữ liệu từ controller cha trước khi show. */
    public void khoiTao(List<DonViTinhDTO> dsDVT, List<NhaCungCapDTO> dsNCC) {
        cmbDonViTinh.setConverter(new StringConverter<>() {
            @Override public String toString(DonViTinhDTO d) { return d != null ? d.getTenDVT() : ""; }
            @Override public DonViTinhDTO fromString(String s) { return null; }
        });
        if (dsDVT != null) cmbDonViTinh.setItems(FXCollections.observableArrayList(dsDVT));

        cmbNhaCungCap.setConverter(new StringConverter<>() {
            @Override public String toString(NhaCungCapDTO n) { return n != null ? n.getTenNCC() : ""; }
            @Override public NhaCungCapDTO fromString(String s) { return null; }
        });
        if (dsNCC != null) cmbNhaCungCap.setItems(FXCollections.observableArrayList(dsNCC));
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public boolean isConfirmed()           { return confirmed; }
    public String getTenNL()               { return tenNL; }
    public String getXuatXu()              { return xuatXu; }
    public double getMucTon()              { return mucTon; }
    public DonViTinhDTO getDonViTinh()     { return donViTinh; }
    public NhaCungCapDTO getNhaCungCap()   { return nhaCungCap; }
    public double getSoLuong()             { return soLuong; }
    public double getDonGia()              { return donGia; }
    public LocalDate getNgaySanXuat()      { return ngaySanXuat; }
    public LocalDate getHanSuDung()        { return hanSuDung; }

    @FXML
    private void onXacNhan() {
        lblLoi.setText("");

        // Validate nguyên liệu
        String ten = txtTenNL.getText().trim();
        if (ten.isEmpty()) { lblLoi.setText("⚠ Tên nguyên liệu không được để trống."); return; }
        if (cmbDonViTinh.getValue() == null) { lblLoi.setText("⚠ Vui lòng chọn đơn vị tính."); return; }

        // Validate nhập kho
        if (cmbNhaCungCap.getValue() == null) { lblLoi.setText("⚠ Vui lòng chọn nhà cung cấp."); return; }
        double sl, dg;
        try { sl = Double.parseDouble(txtSoLuong.getText().trim()); if (sl <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { lblLoi.setText("⚠ Số lượng phải là số dương."); return; }
        try { dg = Double.parseDouble(txtDonGia.getText().trim()); if (dg <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { lblLoi.setText("⚠ Đơn giá phải là số dương."); return; }

        // Gán kết quả
        tenNL      = ten;
        xuatXu     = txtXuatXu.getText().trim();
        donViTinh  = cmbDonViTinh.getValue();
        try { mucTon = Double.parseDouble(txtMucTon.getText().trim()); } catch (NumberFormatException e) { mucTon = 0; }
        nhaCungCap  = cmbNhaCungCap.getValue();
        soLuong     = sl;
        donGia      = dg;
        ngaySanXuat = dpNgaySanXuat.getValue();
        hanSuDung   = dpHanSuDung.getValue();

        confirmed = true;
        dongDialog();
    }

    @FXML private void onHuy() { confirmed = false; dongDialog(); }

    private void dongDialog() {
        ((Stage) txtTenNL.getScene().getWindow()).close();
    }
}
