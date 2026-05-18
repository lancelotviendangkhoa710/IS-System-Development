package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.utils.DialogHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Controller dialog Thêm Nguyên Liệu MỚI vào Công Thức.
 * Thu thập: tên NL, DVT, xuất xứ, nhà cung cấp, số lượng nhập, đơn giá, định mức.
 * Caller: CongThucViewFXMLController.onThemCongThuc().
 */
public class ThemCongThucDialogController {

    // ── Section 1: Thông tin nguyên liệu mới ────────────────────────────────
    @FXML private TextField             txtTenNL;
    @FXML private ComboBox<DonViTinhDTO> cmbDonViTinh;
    @FXML private TextField             txtXuatXu;

    // ── Section 2: Nhập kho lần đầu ─────────────────────────────────────────
    @FXML private ComboBox<NhaCungCapDTO> cmbNhaCungCap;
    @FXML private TextField               txtSoLuong;
    @FXML private TextField               txtDonGia;

    // ── Section 3: Định mức trong công thức ─────────────────────────────────
    @FXML private TextField txtDinhMuc;
    @FXML private Label     lblDonVi;   // hiện đơn vị tính động khi chọn DVT

    @FXML private Label lblLoi;

    // ── Kết quả trả về ────────────────────────────────────────────────────────
    private String         tenNL;
    private String         xuatXu;
    private DonViTinhDTO   donViTinh;
    private NhaCungCapDTO  nhaCungCap;
    private double         soLuong;
    private double         donGia;
    private double         dinhMuc;
    private boolean        confirmed       = false;
    private boolean        duLieuDaThayDoi = false;

    @FXML
    public void initialize() {
        // Theo dõi thay đổi để xác nhận hủy
        txtTenNL.textProperty().addListener((o, ov, nv)  -> duLieuDaThayDoi = true);
        txtSoLuong.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtDonGia.textProperty().addListener((o, ov, nv)  -> duLieuDaThayDoi = true);
        txtDinhMuc.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        cmbDonViTinh.valueProperty().addListener((o, ov, nv) -> {
            duLieuDaThayDoi = true;
            // Cập nhật label đơn vị cạnh ô định mức
            if (lblDonVi != null) {
                lblDonVi.setText(nv != null && nv.getTenDVT() != null ? nv.getTenDVT() : "");
            }
        });
        cmbNhaCungCap.valueProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);

        Platform.runLater(() -> {
            Stage s = (Stage) txtTenNL.getScene().getWindow();
            s.setOnCloseRequest(ev -> { if (!xacNhanHuyThayDoi()) ev.consume(); });
        });
    }

    /** Inject danh sách DVT + NCC từ controller cha trước khi show. */
    public void khoiTao(List<DonViTinhDTO> dsDVT, List<NhaCungCapDTO> dsNCC) {
        cmbDonViTinh.setConverter(new StringConverter<>() {
            @Override public String toString(DonViTinhDTO d)  { return d != null ? d.getTenDVT() : ""; }
            @Override public DonViTinhDTO fromString(String s) { return null; }
        });
        if (dsDVT != null) cmbDonViTinh.setItems(FXCollections.observableArrayList(dsDVT));

        cmbNhaCungCap.setConverter(new StringConverter<>() {
            @Override public String toString(NhaCungCapDTO n)  { return n != null ? n.getTenNCC() : ""; }
            @Override public NhaCungCapDTO fromString(String s) { return null; }
        });
        if (dsNCC != null) cmbNhaCungCap.setItems(FXCollections.observableArrayList(dsNCC));
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public boolean       isConfirmed()   { return confirmed; }
    public String        getTenNL()      { return tenNL; }
    public String        getXuatXu()     { return xuatXu; }
    public DonViTinhDTO  getDonViTinh()  { return donViTinh; }
    public NhaCungCapDTO getNhaCungCap() { return nhaCungCap; }
    public double        getSoLuong()    { return soLuong; }
    public double        getDonGia()     { return donGia; }
    public double        getDinhMuc()    { return dinhMuc; }

    @FXML
    private void onXacNhan() {
        lblLoi.setText("");

        // Validate thông tin nguyên liệu
        String ten = txtTenNL.getText().trim();
        if (ten.isEmpty())              { lblLoi.setText("⚠ Tên nguyên liệu không được để trống."); return; }
        if (cmbDonViTinh.getValue() == null) { lblLoi.setText("⚠ Vui lòng chọn đơn vị tính."); return; }

        // Validate nhập kho lần đầu
        if (cmbNhaCungCap.getValue() == null) { lblLoi.setText("⚠ Vui lòng chọn nhà cung cấp."); return; }
        double sl, dg;
        try {
            sl = Double.parseDouble(txtSoLuong.getText().trim());
            if (sl <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) { lblLoi.setText("⚠ Số lượng nhập phải là số dương."); return; }
        try {
            dg = Double.parseDouble(txtDonGia.getText().trim());
            if (dg <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) { lblLoi.setText("⚠ Đơn giá phải là số dương."); return; }

        // Validate định mức
        double dm;
        try {
            dm = Double.parseDouble(txtDinhMuc.getText().trim());
            if (dm <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) { lblLoi.setText("⚠ Định mức tiêu hao phải là số dương hợp lệ."); return; }

        // Gán kết quả
        tenNL     = ten;
        xuatXu    = txtXuatXu.getText().trim();
        donViTinh = cmbDonViTinh.getValue();
        nhaCungCap = cmbNhaCungCap.getValue();
        soLuong   = sl;
        donGia    = dg;
        dinhMuc   = dm;

        confirmed = true;
        dongDialog();
    }

    @FXML private void onHuy() {
        if (!xacNhanHuyThayDoi()) return;
        confirmed = false;
        dongDialog();
    }

    private boolean xacNhanHuyThayDoi() {
        if (!duLieuDaThayDoi) return true;
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có thay đổi chưa lưu. Hủy bỏ?", ButtonType.YES, ButtonType.NO);
        a.setTitle("Dữ liệu chưa lưu");
        a.setHeaderText("Cảnh báo — Dữ liệu chưa lưu");
        DialogHelper.applyBakeryTheme(a);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void dongDialog() {
        ((Stage) txtTenNL.getScene().getWindow()).close();
    }
}
