package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.dto.nhansu.VaiTroDTO;
import com.bakery.services.nhansu.NhanVienService;
import com.bakery.utils.PasswordUtils;
import javafx.fxml.FXML;
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
 * Controller cho dialog Thêm Nhân Viên Mới.
 * Thu thập dữ liệu → gọi NhanVienService → đóng dialog.
 */
public class ThemNhanVienDialogController {

    @FXML private TextField    txtHoTen;
    @FXML private TextField    txtSdt;
    @FXML private DatePicker   dpNgaySinh;
    @FXML private TextField    txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private FlowPane     flowVaiTro;
    @FXML private Label        lblError;

    private final NhanVienService nhanVienService = new NhanVienService();
    /** Callback để thông báo cho màn hình cha reload dữ liệu sau khi thêm thành công */
    private Runnable onThemThanhCong;

    public void setOnThemThanhCong(Runnable callback) {
        this.onThemThanhCong = callback;
    }

    @FXML
    public void initialize() {
        nạpDanhSachVaiTro();
    }

    /** Nạp danh sách vai trò đang hoạt động vào FlowPane dưới dạng CheckBox */
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
                flowVaiTro.getChildren().add(chk);
            }
        } catch (Exception e) {
            lblError.setText("Lỗi nạp vai trò: " + e.getMessage());
        }
    }

    @FXML
    private void onXacNhan() {
        lblError.setText("");

        // Validation
        String hoTen = txtHoTen.getText() == null ? "" : txtHoTen.getText().trim();
        String sdt   = txtSdt.getText() == null ? "" : txtSdt.getText().trim();
        String tenDangNhap = txtTenDangNhap.getText() == null ? "" : txtTenDangNhap.getText().trim();
        String matKhau     = txtMatKhau.getText() == null ? "" : txtMatKhau.getText().trim();

        if (hoTen.isBlank()) { lblError.setText("Vui lòng nhập Họ tên."); return; }
        if (sdt.isBlank())   { lblError.setText("Vui lòng nhập Số điện thoại."); return; }
        if (tenDangNhap.isBlank()) { lblError.setText("Vui lòng nhập Tên đăng nhập."); return; }

        // Thu thập vai trò được chọn
        List<Integer> dsMaVaiTro  = new ArrayList<>();
        List<String>  dsTenVaiTro = new ArrayList<>();
        for (javafx.scene.Node node : flowVaiTro.getChildren()) {
            if (node instanceof CheckBox chk && chk.isSelected()) {
                dsMaVaiTro.add((int) chk.getUserData());
                dsTenVaiTro.add(chk.getText());
            }
        }

        // Xây dựng DTO
        NhanVienDTO nv = new NhanVienDTO();
        nv.setHoTen(hoTen);
        nv.setSdt(sdt);
        nv.setNgaySinh(dpNgaySinh.getValue());
        nv.setTenDangNhap(tenDangNhap);
        // Mật khẩu mặc định "1" nếu để trống
        nv.setMatKhau(matKhau.isBlank() ? "1" : PasswordUtils.hash(matKhau));
        nv.setTrangThaiLamViec(1);
        nv.setDanhSachMaVaiTro(dsMaVaiTro);
        nv.setDanhSachTenVaiTro(dsTenVaiTro);

        try {
            int newId = nhanVienService.themNhanVien(nv);
            if (newId > 0) {
                if (onThemThanhCong != null) onThemThanhCong.run();
                dongDialog();
            } else {
                lblError.setText("Không thể tạo nhân viên. Vui lòng kiểm tra lại thông tin.");
            }
        } catch (Exception e) {
            lblError.setText("Lỗi: " + e.getMessage());
        }
    }

    @FXML
    private void onHuy() {
        dongDialog();
    }

    private void dongDialog() {
        Stage stage = (Stage) txtHoTen.getScene().getWindow();
        stage.close();
    }
}
