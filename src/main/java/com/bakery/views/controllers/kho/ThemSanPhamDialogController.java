package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.utils.DialogHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.util.Map;

/**
 * Controller dialog Thêm Sản Phẩm Mới.
 * Trả kết quả qua getKetQua() sau khi Stage đóng.
 */
public class ThemSanPhamDialogController {

    @FXML private TextField txtTenSP;
    @FXML private ComboBox<Map.Entry<Integer, String>> cmbDanhMuc;
    @FXML private CheckBox chkTuyChinh;
    @FXML private TextField txtTGBaoQuan;
    @FXML private TextField txtTGChuanBi;
    @FXML private ImageView imgPreview;
    @FXML private Label lblLoi;

    private String selectedImagePath = null;
    private SanPhamDTO ketQua = null;
    private boolean duLieuDaThayDoi = false;

    @FXML
    public void initialize() {
        txtTenSP.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        cmbDanhMuc.valueProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtTGBaoQuan.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtTGChuanBi.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        Platform.runLater(() -> {
            Stage s = (Stage) txtTenSP.getScene().getWindow();
            s.setOnCloseRequest(ev -> { if (!xacNhanHuyThayDoi()) ev.consume(); });
        });
    }

    /** Inject danh mục từ controller cha trước khi show dialog. */
    public void khoiTaoDanhMuc(Map<Integer, String> danhMucMap) {
        cmbDanhMuc.setConverter(new StringConverter<>() {
            @Override public String toString(Map.Entry<Integer, String> e) { return e != null ? e.getValue() : ""; }
            @Override public Map.Entry<Integer, String> fromString(String s) { return null; }
        });
        if (danhMucMap != null && !danhMucMap.isEmpty()) {
            cmbDanhMuc.setItems(FXCollections.observableArrayList(danhMucMap.entrySet()));
        }
    }

    /** Kết quả sau khi người dùng nhấn "Thêm & Cấu hình công thức". Null nếu hủy. */
    public SanPhamDTO getKetQua() {
        return ketQua;
    }

    @FXML
    private void onXacNhan() {
        String ten = txtTenSP.getText().trim();
        if (ten.isEmpty()) {
            lblLoi.setText("⚠ Tên sản phẩm không được để trống.");
            return;
        }
        if (cmbDanhMuc.getValue() == null) {
            lblLoi.setText("⚠ Vui lòng chọn danh mục.");
            return;
        }

        // Validate Thời gian bảo quản (CK_SP_BAOQUAN: > 0)
        int tgBaoQuan;
        try {
            tgBaoQuan = Integer.parseInt(txtTGBaoQuan.getText().trim());
            if (tgBaoQuan <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblLoi.setText("⚠ Thời gian bảo quản phải là số nguyên > 0.");
            return;
        }

        // Validate Thời gian chuẩn bị (CK_SP_CHUANBI: >= 0)
        int tgChuanBi;
        try {
            tgChuanBi = Integer.parseInt(txtTGChuanBi.getText().trim());
            if (tgChuanBi < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblLoi.setText("⚠ Thời gian chuẩn bị phải là số nguyên >= 0.");
            return;
        }

        SanPhamDTO sp = new SanPhamDTO();
        sp.setTenSP(ten);
        sp.setMaDM(cmbDanhMuc.getValue().getKey());
        sp.setGiaBan(0);    // tính lại sau khi cấu hình BOM
        sp.setThoiGianBaoQuan(tgBaoQuan);
        sp.setThoiGianChuanBi(tgChuanBi);
        sp.setChoPhepTuyChinh(chkTuyChinh.isSelected() ? 1 : 0);
        sp.setHinhAnh(selectedImagePath);

        ketQua = sp;
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
        a.setTitle("Dữ liệu chưa lưu"); a.setHeaderText("Cảnh báo — Dữ liệu chưa lưu");
        DialogHelper.applyBakeryTheme(a);
        return a.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    @FXML
    private void onChonAnh() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn ảnh sản phẩm (PNG)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ảnh PNG (*.png)", "*.png"));

        File chosen = fc.showOpenDialog(imgPreview.getScene().getWindow());
        if (chosen != null) {
            selectedImagePath = chosen.getAbsolutePath();
            hienThiAnhPreview(selectedImagePath);
        }
    }

    private void hienThiAnhPreview(String path) {
        if (imgPreview == null || path == null) return;
        try {
            File f = new File(path);
            if (f.exists()) {
                imgPreview.setImage(new Image(f.toURI().toString(), true));
            }
        } catch (Exception e) {
            imgPreview.setImage(null);
        }
    }

    private void dongDialog() {
        Stage stage = (Stage) txtTenSP.getScene().getWindow();
        stage.close();
    }
}
