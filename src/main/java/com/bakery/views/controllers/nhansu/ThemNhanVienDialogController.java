package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.nhansu.NhanVienService;
import com.bakery.utils.DialogHelper;
import com.bakery.utils.PasswordUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller dialog Thêm / Sửa Nhân Viên.
 * - Thêm mới: dùng trực tiếp không cần khoiTaoSua()
 * - Sửa:      gọi khoiTaoSua(NhanVienDTO) trước show()
 */
public class ThemNhanVienDialogController {

    @FXML private Label         lblTieuDe;
    @FXML private TextField     txtHoTen;
    @FXML private TextField     txtSdt;
    @FXML private DatePicker    dpNgaySinh;
    @FXML private TextField     txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private FlowPane      flowVaiTro;
    @FXML private Label         lblError;

    private final NhanVienService nhanVienService = new NhanVienService();

    /** Callback: cha reload sau khi thêm/sửa thành công */
    private Runnable onThemThanhCong;
    /** NV đang sửa — null nếu đang thêm mới */
    private NhanVienDTO nvGoc;
    private boolean duLieuDaThayDoi = false;

    public void setOnThemThanhCong(Runnable callback) {
        this.onThemThanhCong = callback;
    }

    @FXML
    public void initialize() {
        nạpDanhSachVaiTro();
        // Dirty flag
        txtHoTen.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtSdt.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtTenDangNhap.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        txtMatKhau.textProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        dpNgaySinh.valueProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
        // Guard nút X cửa sổ
        Platform.runLater(() -> {
            Stage s = (Stage) txtHoTen.getScene().getWindow();
            s.setOnCloseRequest(ev -> { if (!xacNhanHuyThayDoi()) ev.consume(); });
        });
    }

    /**
     * Chuyển sang mode Sửa: điền sẵn thông tin NV.
     * Gọi TRƯỚC show().
     */
    public void khoiTaoSua(NhanVienDTO nv) {
        this.nvGoc = nv;
        if (lblTieuDe != null) lblTieuDe.setText("Sửa Nhân Viên");
        txtHoTen.setText(nv.getHoTen() != null ? nv.getHoTen() : "");
        txtSdt.setText(nv.getSdt() != null ? nv.getSdt() : "");
        dpNgaySinh.setValue(nv.getNgaySinh());
        txtTenDangNhap.setText(nv.getTenDangNhap() != null ? nv.getTenDangNhap() : "");
        txtMatKhau.clear(); // Không hiển thị mật khẩu hash — để trống = giữ nguyên

        // Chọn đúng checkbox vai trò
        Platform.runLater(() -> {
            for (javafx.scene.Node node : flowVaiTro.getChildren()) {
                if (node instanceof CheckBox chk) {
                    int roleId = (int) chk.getUserData();
                    chk.setSelected(nv.getDanhSachMaVaiTro() != null
                            && nv.getDanhSachMaVaiTro().contains(roleId));
                }
            }
            duLieuDaThayDoi = false; // Pre-fill không tính là "đã thay đổi"
        });
    }

    // ── Nạp vai trò ──────────────────────────────────────────────────────

    private void nạpDanhSachVaiTro() {
        try {
            java.util.Map<Integer, String> roleMap = nhanVienService.layDanhSachVaiTro();
            flowVaiTro.getChildren().clear();
            if (roleMap == null || roleMap.isEmpty()) {
                lblError.setText("Không có vai trò nào trong hệ thống.");
                return;
            }
            for (java.util.Map.Entry<Integer, String> entry : roleMap.entrySet()) {
                CheckBox chk = new CheckBox(entry.getValue());
                chk.setUserData(entry.getKey());
                chk.getStyleClass().add("check-box");
                chk.selectedProperty().addListener((o, ov, nv) -> duLieuDaThayDoi = true);
                flowVaiTro.getChildren().add(chk);
            }
        } catch (Exception e) {
            lblError.setText("Lỗi nạp vai trò: " + e.getMessage());
        }
    }

    // ── Handlers ─────────────────────────────────────────────────────────

    @FXML
    private void onXacNhan() {
        lblError.setText("");
        String hoTen       = trim(txtHoTen.getText());
        String sdt         = trim(txtSdt.getText());
        String tenDangNhap = trim(txtTenDangNhap.getText());
        String matKhau     = trim(txtMatKhau.getText());

        if (hoTen.isBlank())       { lblError.setText("Vui lòng nhập Họ tên."); return; }
        if (sdt.isBlank())         { lblError.setText("Vui lòng nhập Số điện thoại."); return; }
        if (tenDangNhap.isBlank()) { lblError.setText("Vui lòng nhập Tên đăng nhập."); return; }

        List<Integer> dsMaVaiTro  = new ArrayList<>();
        List<String>  dsTenVaiTro = new ArrayList<>();
        for (javafx.scene.Node node : flowVaiTro.getChildren()) {
            if (node instanceof CheckBox chk && chk.isSelected()) {
                dsMaVaiTro.add((int) chk.getUserData());
                dsTenVaiTro.add(chk.getText());
            }
        }

        try {
            if (nvGoc == null) {
                // ── Thêm mới ──
                NhanVienDTO nv = new NhanVienDTO();
                nv.setHoTen(hoTen);
                nv.setSdt(sdt);
                nv.setNgaySinh(dpNgaySinh.getValue());
                nv.setTenDangNhap(tenDangNhap);
                nv.setMatKhau(matKhau.isBlank() ? "1" : PasswordUtils.hash(matKhau));
                nv.setTrangThaiLamViec(1);
                nv.setDanhSachMaVaiTro(dsMaVaiTro);
                nv.setDanhSachTenVaiTro(dsTenVaiTro);

                int newId = nhanVienService.themNhanVien(nv);
                if (newId > 0) {
                    if (onThemThanhCong != null) onThemThanhCong.run();
                    duLieuDaThayDoi = false;
                    dongDialog();
                } else {
                    lblError.setText("Không thể tạo nhân viên. Vui lòng kiểm tra lại.");
                }
            } else {
                // ── Sửa ──
                nvGoc.setHoTen(hoTen);
                nvGoc.setSdt(sdt);
                nvGoc.setNgaySinh(dpNgaySinh.getValue());
                nvGoc.setTenDangNhap(tenDangNhap);
                nvGoc.setMatKhau(matKhau.isBlank() ? nvGoc.getMatKhau() : PasswordUtils.hash(matKhau));
                nvGoc.setDanhSachMaVaiTro(dsMaVaiTro);
                nvGoc.setDanhSachTenVaiTro(dsTenVaiTro);

                nhanVienService.suaNhanVien(nvGoc);
                if (onThemThanhCong != null) onThemThanhCong.run();
                duLieuDaThayDoi = false;
                dongDialog();
            }
        } catch (Exception e) {
            lblError.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void onHuy() {
        if (xacNhanHuyThayDoi()) dongDialog();
    }

    // ── Helpers ───────────────────────────────────────────────────────────

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
        Stage stage = (Stage) txtHoTen.getScene().getWindow();
        stage.close();
    }

    private static String trim(String s) { return s != null ? s.trim() : ""; }
}
