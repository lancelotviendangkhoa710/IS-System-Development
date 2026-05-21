package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.utils.DialogHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Controller dialog Sửa Nguyên Liệu.
 * Inject dữ liệu qua khoiTaoSua() trước khi show().
 * Trả kết quả qua getKetQua() sau khi Stage đóng. Null = hủy.
 */
public class SuaNguyenLieuDialogController {

    @FXML private TextField               txtTenNL;
    @FXML private ComboBox<DonViTinhDTO>  cmbDonViTinh;
    @FXML private TextField               txtMucTon;
    @FXML private TextField               txtXuatXu;
    @FXML private Label                   lblLoi;

    /** Bản gốc để giữ maNL khi trả kết quả. */
    private NguyenLieuDTO nlGoc;
    /** Kết quả trả về — null nếu user bấm Hủy. */
    private NguyenLieuDTO ketQua;
    private boolean duLieuDaThayDoi = false;

    @FXML
    public void initialize() {
        txtTenNL.textProperty().addListener((o, ov, nv)      -> duLieuDaThayDoi = true);
        txtXuatXu.textProperty().addListener((o, ov, nv)     -> duLieuDaThayDoi = true);
        txtMucTon.textProperty().addListener((o, ov, nv)     -> duLieuDaThayDoi = true);
        cmbDonViTinh.valueProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);

        Platform.runLater(() -> {
            Stage s = (Stage) txtTenNL.getScene().getWindow();
            s.setOnCloseRequest(ev -> { if (!xacNhanHuyThayDoi()) ev.consume(); });
            txtTenNL.requestFocus();
        });
    }

    /**
     * Inject đơn vị tính + nguyên liệu cần sửa trước khi show().
     * Gọi sau loader.load(), trước stage.showAndWait().
     */
    public void khoiTaoSua(NguyenLieuDTO nl, List<DonViTinhDTO> dsDVT) {
        this.nlGoc = nl;

        // Nạp ComboBox DVT
        cmbDonViTinh.setConverter(new StringConverter<>() {
            @Override public String toString(DonViTinhDTO d)   { return d != null ? d.getTenDVT() : ""; }
            @Override public DonViTinhDTO fromString(String s) { return null; }
        });
        if (dsDVT != null) {
            cmbDonViTinh.setItems(FXCollections.observableArrayList(dsDVT));
        }

        // Pre-fill form
        txtTenNL.setText(nvl(nl.getTenNL()));
        txtXuatXu.setText(nvl(nl.getXuatXu()));
        txtMucTon.setText(String.valueOf(nl.getMucTonAnToan()));

        // Đồng bộ DVT đang chọn
        if (nl.getMaDVT() > 0 && dsDVT != null) {
            dsDVT.stream()
                 .filter(d -> d.getMaDVT() == nl.getMaDVT())
                 .findFirst()
                 .ifPresent(cmbDonViTinh::setValue);
        }

        // Reset dirty sau khi pre-fill
        Platform.runLater(() -> duLieuDaThayDoi = false);
    }

    /** @return NguyenLieuDTO đã cập nhật, hoặc null nếu user hủy. */
    public NguyenLieuDTO getKetQua() { return ketQua; }

    // ── Handlers ─────────────────────────────────────────────────────────────

    @FXML
    private void onLuu() {
        lblLoi.setText("");

        String ten = txtTenNL.getText() == null ? "" : txtTenNL.getText().trim();
        if (ten.isBlank()) {
            lblLoi.setText("⚠ Tên nguyên liệu không được để trống.");
            return;
        }
        if (cmbDonViTinh.getValue() == null) {
            lblLoi.setText("⚠ Vui lòng chọn đơn vị tính.");
            return;
        }

        double mucTon = 0;
        try {
            String mucTonText = txtMucTon.getText() == null ? "" : txtMucTon.getText().trim();
            if (!mucTonText.isEmpty()) {
                mucTon = Double.parseDouble(mucTonText);
                if (mucTon < 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            lblLoi.setText("⚠ Mức tồn an toàn phải là số không âm.");
            return;
        }

        // Tạo DTO kết quả giữ nguyên maNL
        NguyenLieuDTO result = new NguyenLieuDTO();
        result.setMaNL(nlGoc.getMaNL());
        result.setTenNL(ten);
        result.setXuatXu(txtXuatXu.getText() != null ? txtXuatXu.getText().trim() : "");
        result.setMaDVT(cmbDonViTinh.getValue().getMaDVT());
        result.setTenDVT(cmbDonViTinh.getValue().getTenDVT());
        result.setMucTonAnToan(mucTon);

        ketQua = result;
        duLieuDaThayDoi = false;
        dongDialog();
    }

    @FXML
    private void onHuy() {
        if (!xacNhanHuyThayDoi()) return;
        ketQua = null;
        dongDialog();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

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

    private static String nvl(String s) { return s != null ? s : ""; }
}
