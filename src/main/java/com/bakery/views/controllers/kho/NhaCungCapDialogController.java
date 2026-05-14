package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.utils.DialogHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller dialog Thêm / Sửa Nhà Cung Cấp.
 * Trả kết quả qua getKetQua() sau khi Stage đóng. Null = hủy.
 */
public class NhaCungCapDialogController {

    @FXML private Label    lblTieuDe;
    @FXML private TextField txtTenNCC;
    @FXML private TextField txtSdt;
    @FXML private TextArea  txtDiaChi;
    @FXML private Label    lblLoi;

    private NhaCungCapDTO nccGoc;   // null = Thêm mới, non-null = Sửa
    private NhaCungCapDTO ketQua;
    private boolean duLieuDaThayDoi = false;

    @FXML
    public void initialize() {
        txtTenNCC.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtSdt.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtDiaChi.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        Platform.runLater(() -> {
            Stage s = (Stage) txtTenNCC.getScene().getWindow();
            s.setOnCloseRequest(ev -> { if (!xacNhanHuyThayDoi()) ev.consume(); });
            txtTenNCC.requestFocus();
        });
    }

    /** Inject NCC hiện tại để vào chế độ Sửa. Gọi trước show(). */
    public void khoiTaoSua(NhaCungCapDTO ncc) {
        this.nccGoc = ncc;
        lblTieuDe.setText("Sửa Nhà Cung Cấp");
        txtTenNCC.setText(nvl(ncc.getTenNCC()));
        txtSdt.setText(nvl(ncc.getSdt()));
        txtDiaChi.setText(nvl(ncc.getDiaChi()));
        // Reset dirty vì đây là pre-fill, chưa phải user nhập
        Platform.runLater(() -> duLieuDaThayDoi = false);
    }

    public NhaCungCapDTO getKetQua() { return ketQua; }

    @FXML
    private void onLuu() {
        lblLoi.setText("");
        String ten = txtTenNCC.getText() == null ? "" : txtTenNCC.getText().trim();
        if (ten.isBlank()) {
            lblLoi.setText("⚠ Tên nhà cung cấp không được để trống.");
            return;
        }

        NhaCungCapDTO ncc = nccGoc != null ? nccGoc : new NhaCungCapDTO();
        ncc.setTenNCC(ten);
        ncc.setSdt(txtSdt.getText() != null ? txtSdt.getText().trim() : "");
        ncc.setDiaChi(txtDiaChi.getText() != null ? txtDiaChi.getText().trim() : "");

        ketQua = ncc;
        duLieuDaThayDoi = false;
        dongDialog();
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
        a.setTitle("Dữ liệu chưa lưu");
        a.setHeaderText("Cảnh báo — Dữ liệu chưa lưu");
        DialogHelper.applyBakeryTheme(a);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void dongDialog() {
        ((Stage) txtTenNCC.getScene().getWindow()).close();
    }

    private static String nvl(String s) { return s != null ? s : ""; }
}
