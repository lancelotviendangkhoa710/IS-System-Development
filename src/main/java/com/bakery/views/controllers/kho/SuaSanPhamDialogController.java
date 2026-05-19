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
 * Controller dialog Sửa Sản Phẩm.
 * Nhận SP hiện tại qua khoiTao(), trả kết quả qua getKetQua() sau khi Stage đóng.
 */
public class SuaSanPhamDialogController {

    @FXML private TextField txtTenSP;
    @FXML private ComboBox<Map.Entry<Integer, String>> cmbDanhMuc;
    @FXML private TextField txtGiaBan;

    @FXML private CheckBox chkTuyChinh;
    @FXML private TextField txtTGBaoQuan;
    @FXML private TextField txtTGChuanBi;
    @FXML private ImageView imgPreview;
    @FXML private Label lblLoi;

    private String selectedImagePath = null;
    private SanPhamDTO spGoc = null;   // SP gốc để giữ lại maSP, giaVon, soLuongTon
    private SanPhamDTO ketQua = null;
    private boolean duLieuDaThayDoi = false;

    @FXML
    public void initialize() {
        txtTenSP.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtGiaBan.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtTGBaoQuan.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtTGChuanBi.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        cmbDanhMuc.valueProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        Platform.runLater(() -> {
            Stage s = (Stage) txtTenSP.getScene().getWindow();
            s.setOnCloseRequest(ev -> { if (!xacNhanHuyThayDoi()) ev.consume(); });
        });
    }

    /** Điền sẵn dữ liệu SP và danh mục trước khi show dialog. */
    public void khoiTao(SanPhamDTO sp, Map<Integer, String> danhMucMap) {
        this.spGoc = sp;

        // Setup ComboBox danh mục
        cmbDanhMuc.setConverter(new StringConverter<>() {
            @Override public String toString(Map.Entry<Integer, String> e) { return e != null ? e.getValue() : ""; }
            @Override public Map.Entry<Integer, String> fromString(String s) { return null; }
        });
        if (danhMucMap != null && !danhMucMap.isEmpty()) {
            cmbDanhMuc.setItems(FXCollections.observableArrayList(danhMucMap.entrySet()));
            // Chọn đúng danh mục của SP
            cmbDanhMuc.getItems().stream()
                    .filter(e -> e.getKey() == sp.getMaDM())
                    .findFirst()
                    .ifPresent(cmbDanhMuc::setValue);
        }

        // Điền form
        txtTenSP.setText(sp.getTenSP());
        txtGiaBan.setText(String.valueOf(sp.getGiaBan()));

        chkTuyChinh.setSelected(sp.getChoPhepTuyChinh() == 1);
        txtTGBaoQuan.setText(String.valueOf(sp.getThoiGianBaoQuan()));
        txtTGChuanBi.setText(String.valueOf(sp.getThoiGianChuanBi()));

        // Hiển thị ảnh
        selectedImagePath = sp.getHinhAnh();
        hienThiAnhPreview(selectedImagePath);
    }

    /** Kết quả sau khi người dùng nhấn Lưu. Null nếu hủy. */
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

        int tgBaoQuan;
        try {
            tgBaoQuan = Integer.parseInt(txtTGBaoQuan.getText().trim());
            if (tgBaoQuan <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblLoi.setText("⚠ Thời gian bảo quản phải là số nguyên > 0.");
            return;
        }

        int tgChuanBi;
        try {
            tgChuanBi = Integer.parseInt(txtTGChuanBi.getText().trim());
            if (tgChuanBi < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            lblLoi.setText("⚠ Thời gian chuẩn bị phải là số nguyên >= 0.");
            return;
        }

        double giaBan;
        try {
            giaBan = Double.parseDouble(txtGiaBan.getText().trim());
        } catch (NumberFormatException e) {
            lblLoi.setText("⚠ Giá bán không hợp lệ.");
            return;
        }

        SanPhamDTO sp = new SanPhamDTO();
        sp.setMaSP(spGoc.getMaSP());
        sp.setGiaVon(spGoc.getGiaVon());
        sp.setSoLuongTon(spGoc.getSoLuongTon());
        sp.setTenSP(ten);
        sp.setMaDM(cmbDanhMuc.getValue().getKey());
        sp.setGiaBan(giaBan);
        sp.setChoPhepTuyChinh(chkTuyChinh.isSelected() ? 1 : 0);
        sp.setThoiGianBaoQuan(tgBaoQuan);
        sp.setThoiGianChuanBi(tgChuanBi);
        sp.setHinhAnh(selectedImagePath != null ? selectedImagePath : spGoc.getHinhAnh());

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
        if (selectedImagePath != null) {
            File lastDir = new File(selectedImagePath).getParentFile();
            if (lastDir != null && lastDir.exists()) fc.setInitialDirectory(lastDir);
        }
        File chosen = fc.showOpenDialog(imgPreview.getScene().getWindow());
        if (chosen != null) {
            selectedImagePath = chosen.getAbsolutePath();
            hienThiAnhPreview(selectedImagePath);
        }
    }

    private void hienThiAnhPreview(String path) {
        if (imgPreview == null || path == null || path.isBlank()) {
            if (imgPreview != null) imgPreview.setImage(null);
            return;
        }
        try {
            File f = new File(path);
            if (f.exists()) {
                imgPreview.setImage(new Image(f.toURI().toString(), true));
            } else {
                var res = getClass().getResourceAsStream(path);
                if (res != null) imgPreview.setImage(new Image(res));
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
