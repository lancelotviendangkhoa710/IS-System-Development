package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.nhansu.NhanVienService;
import com.bakery.views.controllers.BaseController;
import com.bakery.utils.PasswordUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Controller cho QuanLyNhanVienView.
 * Mọi dữ liệu được lấy từ DB. Không có Mock Data.
 */
public class QuanLyNhanVienViewFXMLController extends BaseController {

    @FXML private TableView<NhanVienDTO> tblNhanVien;
    @FXML private TableColumn<NhanVienDTO, Integer> colMaNV;
    @FXML private TableColumn<NhanVienDTO, String> colHoTen;
    @FXML private TableColumn<NhanVienDTO, String> colSdt;
    @FXML private TableColumn<NhanVienDTO, String> colVaiTro;
    @FXML private TableColumn<NhanVienDTO, String> colTenDangNhap;
    @FXML private TableColumn<NhanVienDTO, String> colTrangThai;

    @FXML private TextField txtHoTen;
    @FXML private TextField txtSdt;
    @FXML private javafx.scene.layout.FlowPane flowVaiTro;
    @FXML private TextField txtTenDangNhap;
    @FXML private PasswordField txtMatKhau;
    @FXML private TextField txtMatKhauVisible;
    @FXML private Button btnToggleMatKhau;
    @FXML private CheckBox chkHoatDong;
    @FXML private TextField txtTimKiem;
    @FXML private ComboBox<String> cmbLocTrangThai;

    private final NhanVienService nhanVienService = new NhanVienService();
    private final ObservableList<NhanVienDTO> masterData = FXCollections.observableArrayList();
    private FilteredList<NhanVienDTO> filteredData;
    private Map<Integer, String> roleMap;
    private NhanVienDTO selectedNhanVien;

    @FXML
    public void initialize() {
        setupTable();
        loadRoles();
        setupFilters();
        loadData();
        setupPasswordToggle();
        setupAutoComplete();

        tblNhanVien.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) hienThiChiTiet(newVal);
        });
    }

    private void setupTable() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        colVaiTro.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenVaiTroHienThi()));
        colTenDangNhap.setCellValueFactory(new PropertyValueFactory<>("tenDangNhap"));
        colTrangThai.setCellValueFactory(c -> {
            NhanVienDTO nv = c.getValue();
            String trangThai = nv.getTrangThaiLamViec() == 1 ? "Đang làm việc" : "Đã thôi việc";
            return new SimpleStringProperty(trangThai);
        });
    }

    private void loadRoles() {
        try {
            roleMap = nhanVienService.layDanhSachVaiTro();
            flowVaiTro.getChildren().clear();
            if (roleMap == null || roleMap.isEmpty()) {
                lblThongBao.setText("Không có vai trò nào trong hệ thống.");
                return;
            }
            for (Map.Entry<Integer, String> entry : roleMap.entrySet()) {
                CheckBox chk = new CheckBox(entry.getValue());
                chk.setUserData(entry.getKey());
                chk.getStyleClass().add("check-box-role");
                flowVaiTro.getChildren().add(chk);
            }
        } catch (Exception e) {
            lblThongBao.setText("Lỗi nạp vai trò: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            List<NhanVienDTO> list = nhanVienService.layTatCaNhanVien();
            if (list == null || list.isEmpty()) {
                masterData.clear();
                lblThongBao.setText("Chưa có nhân viên nào trong hệ thống.");
            } else {
                list.sort(Comparator.comparing(NhanVienDTO::getMaNV));
                masterData.setAll(list);
                lblThongBao.setText("Đã tải " + masterData.size() + " nhân viên từ cơ sở dữ liệu.");
            }
            applyFilter(); // Áp bộ lọc sau khi tải
        } catch (Exception e) {
            masterData.clear();
            lblThongBao.setText("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void setupFilters() {
        cmbLocTrangThai.setItems(FXCollections.observableArrayList("Tất cả", "Đang làm việc", "Đã thôi việc"));
        cmbLocTrangThai.getSelectionModel().selectFirst();

        filteredData = new FilteredList<>(masterData, p -> true);
        tblNhanVien.setItems(filteredData);

        // Khi filter thay đổi → cập nhật predicate
        txtTimKiem.textProperty().addListener((obs, old, val) -> applyFilter());
        cmbLocTrangThai.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> applyFilter());
    }

    private void applyFilter() {
        String keyword = txtTimKiem.getText() == null ? "" : txtTimKiem.getText().trim().toLowerCase();
        String trangThai = cmbLocTrangThai.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(nv -> {
            boolean matchKeyword = keyword.isBlank()
                    || nv.getHoTen().toLowerCase().contains(keyword)
                    || nv.getSdt().contains(keyword)
                    || nv.getTenDangNhap().toLowerCase().contains(keyword);

            boolean matchTrangThai = switch (trangThai == null ? "Tất cả" : trangThai) {
                case "Đang làm việc" -> nv.getTrangThaiLamViec() == 1;
                case "Đã thôi việc"  -> nv.getTrangThaiLamViec() == 0;
                default              -> true;
            };

            return matchKeyword && matchTrangThai;
        });
    }

    /** Auto-complete: gợi ý tần, mã khi user gõ vào txtTimKiem. */
    private void setupAutoComplete() {
        ContextMenu popup = new ContextMenu();
        popup.setMaxHeight(200);

        txtTimKiem.textProperty().addListener((obs, old, val) -> {
            String keyword = val == null ? "" : val.trim().toLowerCase();
            if (keyword.isBlank() || masterData.isEmpty()) {
                popup.hide();
                return;
            }

            // Lọc tối đa 8 gợi ý khớp theo tần hoặc mã
            popup.getItems().clear();
            masterData.stream()
                    .filter(nv -> nv.getHoTen().toLowerCase().contains(keyword)
                            || nv.getTenDangNhap().toLowerCase().contains(keyword)
                            || nv.getSdt().contains(keyword))
                    .limit(8)
                    .forEach(nv -> {
                        MenuItem item = new MenuItem(nv.getHoTen() + " — " + nv.getTenDangNhap());
                        item.setOnAction(e -> {
                            txtTimKiem.setText(nv.getHoTen());
                            popup.hide();
                            hienThiChiTiet(nv);
                            tblNhanVien.getSelectionModel().select(nv);
                        });
                        popup.getItems().add(item);
                    });

            if (!popup.getItems().isEmpty()) {
                popup.show(txtTimKiem, javafx.geometry.Side.BOTTOM, 0, 0);
            } else {
                popup.hide();
            }
        });

        // Ẩn popup khi mất focus
        txtTimKiem.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused) popup.hide();
        });
    }

    private void hienThiChiTiet(NhanVienDTO nv) {
        selectedNhanVien = nv;
        txtHoTen.setText(nv.getHoTen());
        txtSdt.setText(nv.getSdt());
        for (javafx.scene.Node node : flowVaiTro.getChildren()) {
            if (node instanceof CheckBox chk) {
                int roleId = (int) chk.getUserData();
                chk.setSelected(nv.getDanhSachMaVaiTro() != null && nv.getDanhSachMaVaiTro().contains(roleId));
            }
        }
        txtTenDangNhap.setText(nv.getTenDangNhap());
        txtMatKhau.clear();
        if (txtMatKhauVisible != null) txtMatKhauVisible.clear();
        chkHoatDong.setSelected(nv.getTrangThaiLamViec() == 1);
    }

    @FXML
    private void onThemMoi() {
        selectedNhanVien = null;
        tblNhanVien.getSelectionModel().clearSelection();
        txtHoTen.clear(); txtSdt.clear(); txtTenDangNhap.clear();
        txtMatKhau.clear();
        if (txtMatKhauVisible != null) txtMatKhauVisible.clear();
        chkHoatDong.setSelected(true);
        flowVaiTro.getChildren().forEach(n -> { if (n instanceof CheckBox c) c.setSelected(false); });
        lblThongBao.setText("Điền thông tin nhân viên mới và nhấn Lưu.");
    }

    @FXML
    private void onLuu() {
        try {
            String hoTen = txtHoTen.getText() == null ? "" : txtHoTen.getText().trim();
            String sdt   = txtSdt.getText() == null ? "" : txtSdt.getText().trim();
            String tenDangNhap = txtTenDangNhap.getText() == null ? "" : txtTenDangNhap.getText().trim();
            String matKhau = getMatKhau();

            if (hoTen.isBlank()) { lblThongBao.setText("Vui lòng nhập Họ tên."); return; }
            if (tenDangNhap.isBlank()) { lblThongBao.setText("Vui lòng nhập Tên đăng nhập."); return; }

            List<Integer> dsMaVaiTro = new ArrayList<>();
            List<String>  dsTenVaiTro = new ArrayList<>();
            for (javafx.scene.Node node : flowVaiTro.getChildren()) {
                if (node instanceof CheckBox chk && chk.isSelected()) {
                    dsMaVaiTro.add((int) chk.getUserData());
                    dsTenVaiTro.add(chk.getText());
                }
            }

            NhanVienDTO nv = selectedNhanVien != null ? selectedNhanVien : new NhanVienDTO();
            nv.setHoTen(hoTen);
            nv.setSdt(sdt);
            nv.setTenDangNhap(tenDangNhap);
            nv.setDanhSachMaVaiTro(dsMaVaiTro);
            nv.setDanhSachTenVaiTro(dsTenVaiTro);
            nv.setTrangThaiLamViec(chkHoatDong.isSelected() ? 1 : 0);

            if (selectedNhanVien == null) {
                // Tạo mới — mật khẩu mặc định "1" (plaintext), hệ thống sẽ kêu đổi khi đăng nhập lần đầu
                String matKhauLuu = matKhau.isBlank() ? "1" : PasswordUtils.hash(matKhau);
                nv.setMatKhau(matKhauLuu);
                int newId = nhanVienService.themNhanVien(nv);
                lblThongBao.setText("✅ Tạo nhân viên thành công. Mã NV: " + newId + ". Mật khẩu mặc định: 1");
            } else {
                // Cập nhật — mật khẩu để trống = giữ nguyên
                nv.setMatKhau(matKhau.isBlank() ? selectedNhanVien.getMatKhau() : PasswordUtils.hash(matKhau));
                nhanVienService.suaNhanVien(nv);
                lblThongBao.setText("✅ Cập nhật nhân viên thành công.");
            }
            loadData();
            onThemMoi();
        } catch (Exception e) {
            lblThongBao.setText("❌ Lỗi khi lưu: " + e.getMessage());
        }
    }

    @FXML
    private void onChoThoiViec() {
        if (selectedNhanVien == null) {
            lblThongBao.setText("Vui lòng chọn nhân viên cần cho thôi việc.");
            return;
        }
        if (selectedNhanVien.getTrangThaiLamViec() == 0) {
            lblThongBao.setText("Nhân viên này đã thôi việc rồi.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Cho nhân viên \"" + selectedNhanVien.getHoTen() + "\" thôi việc?\n" +
            "Tài khoản đăng nhập sẽ bị khóa nhưng lịch sử được giữ lại.",
            ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận cho thôi việc");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    nhanVienService.thoiViec(selectedNhanVien.getMaNV());
                    lblThongBao.setText("✅ Đã cho nhân viên thôi việc thành công.");
                    loadData();
                    onThemMoi();
                } catch (Exception e) {
                    lblThongBao.setText("❌ Lỗi cho thôi việc: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onLamMoi() {
        loadData();
        onThemMoi();
    }

    @FXML
    private void onQuayLai() {
        quayLaiMenuChinh(tblNhanVien);
    }

    @FXML
    private void onToggleMatKhau() {
        if (txtMatKhauVisible == null) return;
        boolean showing = txtMatKhauVisible.isVisible();
        txtMatKhauVisible.setVisible(!showing);
        txtMatKhauVisible.setManaged(!showing);
        txtMatKhau.setVisible(showing);
        txtMatKhau.setManaged(showing);
        if (btnToggleMatKhau != null) btnToggleMatKhau.setText(showing ? "Hiện" : "Ẩn");
    }

    private void setupPasswordToggle() {
        if (txtMatKhauVisible == null) return;
        txtMatKhauVisible.textProperty().bindBidirectional(txtMatKhau.textProperty());
        txtMatKhauVisible.setVisible(false);
        txtMatKhauVisible.setManaged(false);
    }

    private String getMatKhau() {
        if (txtMatKhauVisible != null && txtMatKhauVisible.isVisible()) {
            return txtMatKhauVisible.getText() == null ? "" : txtMatKhauVisible.getText();
        }
        return txtMatKhau.getText() == null ? "" : txtMatKhau.getText();
    }
}
