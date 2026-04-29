package com.bakery.views.controllers;

import com.bakery.main.App;
import com.bakery.model.dto.NhanVienDTO;
import com.bakery.services.NhanVienService;
import com.bakery.utils.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;

public class NhanVienManagementController {

    @FXML
    private TableView<NhanVienDTO> tblNhanVien;
    @FXML
    private TableColumn<NhanVienDTO, Integer> colMaNV;
    @FXML
    private TableColumn<NhanVienDTO, String> colHoTen;
    @FXML
    private TableColumn<NhanVienDTO, String> colSdt;
    @FXML
    private TableColumn<NhanVienDTO, String> colVaiTro;
    @FXML
    private TableColumn<NhanVienDTO, String> colTenDangNhap;
    @FXML
    private TableColumn<NhanVienDTO, String> colTrangThai;

    @FXML
    private TextField txtHoTen;
    @FXML
    private TextField txtSdt;
    @FXML
    private ComboBox<String> cmbVaiTro;
    @FXML
    private TextField txtTenDangNhap;
    @FXML
    private PasswordField txtMatKhau;
    @FXML
    private CheckBox chkHoatDong;
    @FXML
    private TextField txtTimKiem;
    @FXML
    private Label lblThongBao;

    private final NhanVienService nhanVienService = new NhanVienService();
    private ObservableList<NhanVienDTO> masterData = FXCollections.observableArrayList();
    private Map<Integer, String> roleMap;
    private NhanVienDTO selectedNhanVien;

    @FXML
    public void initialize() {
        setupTable();
        loadRoles();
        loadData();

        tblNhanVien.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                hienThiChiTiet(newVal);
            }
        });

        txtTimKiem.textProperty().addListener((obs, oldVal, newVal) -> filterData(newVal));
    }

    private void setupTable() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        colVaiTro.setCellValueFactory(new PropertyValueFactory<>("tenVaiTro"));
        colTenDangNhap.setCellValueFactory(new PropertyValueFactory<>("tenDangNhap"));
        colTrangThai.setCellValueFactory(cellData -> {
            int status = cellData.getValue().getTrangThaiLamViec();
            return new SimpleStringProperty(status == 1 ? "Hoạt động" : "Ngừng việc");
        });
    }

    private void loadRoles() {
        try {
            roleMap = nhanVienService.layDanhSachVaiTro();
            cmbVaiTro.getItems().setAll(roleMap.values());
        } catch (Exception e) {
            lblThongBao.setText("Lỗi tải vai trò: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            masterData.setAll(nhanVienService.layTatCaNhanVien());
            tblNhanVien.setItems(masterData);
            lblThongBao.setText("Đã tải " + masterData.size() + " nhân viên.");
        } catch (Exception e) {
            lblThongBao.setText("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void hienThiChiTiet(NhanVienDTO nv) {
        selectedNhanVien = nv;
        txtHoTen.setText(nv.getHoTen());
        txtSdt.setText(nv.getSdt());
        cmbVaiTro.getSelectionModel().select(nv.getTenVaiTro());
        txtTenDangNhap.setText(nv.getTenDangNhap());
        txtMatKhau.clear();
        chkHoatDong.setSelected(nv.getTrangThaiLamViec() == 1);
        lblThongBao.setText("Đang xem: " + nv.getHoTen());
    }

    private void filterData(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            tblNhanVien.setItems(masterData);
            return;
        }
        String lowerKey = keyword.toLowerCase();
        FilteredList<NhanVienDTO> filtered = new FilteredList<>(masterData,
                nv -> nv.getHoTen().toLowerCase().contains(lowerKey) ||
                        nv.getSdt().contains(lowerKey) ||
                        nv.getTenDangNhap().toLowerCase().contains(lowerKey));
        tblNhanVien.setItems(filtered);
    }

    @FXML
    private void onThemMoi() {
        selectedNhanVien = null;
        tblNhanVien.getSelectionModel().clearSelection();
        txtHoTen.clear();
        txtSdt.clear();
        cmbVaiTro.getSelectionModel().clearSelection();
        txtTenDangNhap.clear();
        txtMatKhau.clear();
        chkHoatDong.setSelected(true);
        txtHoTen.requestFocus();
        lblThongBao.setText("Mời nhập thông tin nhân viên mới.");
    }

    @FXML
    private void onLuu() {
        try {
            if (!validateInput())
                return;

            NhanVienDTO nv = selectedNhanVien != null ? selectedNhanVien : new NhanVienDTO();
            nv.setHoTen(txtHoTen.getText().trim());
            nv.setSdt(txtSdt.getText().trim());
            nv.setTenDangNhap(txtTenDangNhap.getText().trim());
            nv.setTrangThaiLamViec(chkHoatDong.isSelected() ? 1 : 0);

            // Map vai tro name back to ID
            String roleName = cmbVaiTro.getValue();
            int roleId = roleMap.entrySet().stream()
                    .filter(e -> e.getValue().equals(roleName))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(0);
            nv.setMaVaiTro(roleId);

            if (!txtMatKhau.getText().isEmpty()) {
                // In a real app, hash password here or in service.
                // For simplicity assuming service or DAO handles hashing if password is set.
                // However NhanVienDAO.themNhanVien expects hashed password.
                nv.setMatKhau(com.bakery.utils.PasswordUtils.hash(txtMatKhau.getText()));
            }

            if (selectedNhanVien == null) {
                nhanVienService.themNhanVien(nv);
                lblThongBao.setText("Thêm nhân viên thành công!");
            } else {
                nhanVienService.suaNhanVien(nv);
                lblThongBao.setText("Cập nhật thành công!");
            }

            loadData();
            onThemMoi();
        } catch (Exception e) {
            lblThongBao.setText("Lỗi khi lưu: " + e.getMessage());
        }
    }

    @FXML
    private void onVoHieuHoa() {
        if (selectedNhanVien == null) {
            lblThongBao.setText("Vui lòng chọn nhân viên để vô hiệu hóa.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn vô hiệu hóa tài khoản " + selectedNhanVien.getHoTen() + "?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Xác nhận");
        alert.showAndWait().ifPresent(type -> {
            if (type == ButtonType.YES) {
                try {
                    selectedNhanVien.setTrangThaiLamViec(0);
                    nhanVienService.suaNhanVien(selectedNhanVien);
                    loadData();
                    onThemMoi();
                    lblThongBao.setText("Đã vô hiệu hóa tài khoản.");
                } catch (Exception e) {
                    lblThongBao.setText("Lỗi: " + e.getMessage());
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
        try {
            // Load lại MainMenuView đúng cách
            FXMLLoader loader = new FXMLLoader(App.class.getResource(App.MAIN_MENU_VIEW));
            Parent root = loader.load();

            // Lấy controller và khởi tạo dữ liệu TRƯỚC KHI set scene
            MainMenuViewFXMLController controller = loader.getController();
            NhanVienDTO user = UserSession.getCurrentUser();
            if (user != null) {
                controller.khoiTaoThongTinDangNhap(user);
            }

            Scene scene = new Scene(root, 1366, 768);

            // Áp dụng CSS đồng nhất với MainController
            URL cssUrl = App.class.getResource("/css/bakery.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) tblNhanVien.getScene().getWindow();
            stage.setTitle("H3K Bakery - Hệ thống Quản trị");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(1280);
            stage.setMinHeight(720);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            lblThongBao.setText("Lỗi quay lại menu: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        if (txtHoTen.getText().isBlank() || txtSdt.getText().isBlank() ||
                txtTenDangNhap.getText().isBlank() || cmbVaiTro.getValue() == null) {
            lblThongBao.setText("Vui lòng điền đầy đủ các trường bắt buộc.");
            return false;
        }
        if (selectedNhanVien == null && txtMatKhau.getText().isEmpty()) {
            lblThongBao.setText("Nhân viên mới bắt buộc phải có mật khẩu.");
            return false;
        }
        return true;
    }
}
