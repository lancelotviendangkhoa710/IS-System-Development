package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.nhansu.NhanVienService;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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
    private Map<Integer, String> roleMap;
    private NhanVienDTO selectedNhanVien;

    @FXML
    public void initialize() {
        setupTable();
        loadRoles();
        setupFilters();
        loadData();
        bindPasswordToggle();

        tblNhanVien.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) hienThiChiTiet(newVal);
        });
    }

    private void setupTable() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        colVaiTro.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenVaiTroHienThi()));
        colTenDangNhap.setCellValueFactory(new PropertyValueFactory<>("tenDangNhap"));
        colTrangThai.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrangThaiLamViec() == 1 ? "Hoạt động" : "Ngừng việc"));
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
                tblNhanVien.setItems(masterData);
                lblThongBao.setText("Chưa có nhân viên nào trong hệ thống.");
                return;
            }
            list.sort(Comparator.comparing(NhanVienDTO::getMaNV));
            masterData.setAll(list);
            tblNhanVien.setItems(masterData);
            lblThongBao.setText("Đã tải " + masterData.size() + " nhân viên từ cơ sở dữ liệu.");
        } catch (Exception e) {
            masterData.clear();
            tblNhanVien.setItems(masterData);
            lblThongBao.setText("Lỗi tải dữ liệu: " + e.getMessage());
        }
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
        chkHoatDong.setSelected(nv.getTrangThaiLamViec() == 1);
    }

    @FXML
    private void onThemMoi() {
        selectedNhanVien = null;
        tblNhanVien.getSelectionModel().clearSelection();
        txtHoTen.clear(); txtSdt.clear(); txtTenDangNhap.clear(); txtMatKhau.clear();
        chkHoatDong.setSelected(true);
        flowVaiTro.getChildren().forEach(n -> { if (n instanceof CheckBox c) c.setSelected(false); });
    }

    @FXML
    private void onLuu() {
        try {
            String hoTen = txtHoTen.getText() == null ? "" : txtHoTen.getText().trim();
            String sdt = txtSdt.getText() == null ? "" : txtSdt.getText().trim();
            String tenDangNhap = txtTenDangNhap.getText() == null ? "" : txtTenDangNhap.getText().trim();
            String matKhau = txtMatKhau.getText() == null ? "" : txtMatKhau.getText();

            if (hoTen.isBlank() || tenDangNhap.isBlank()) {
                lblThongBao.setText("Vui lòng nhập đầy đủ Họ tên và Tên đăng nhập.");
                return;
            }

            List<Integer> dsMaVaiTro = new ArrayList<>();
            List<String> dsTenVaiTro = new ArrayList<>();
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
                if (matKhau.isBlank()) { lblThongBao.setText("Mật khẩu không được để trống khi tạo mới."); return; }
                nv.setMatKhau(com.bakery.utils.PasswordUtils.hash(matKhau));
                int newId = nhanVienService.themNhanVien(nv);
                lblThongBao.setText("Tạo nhân viên thành công. Mã NV: " + newId);
            } else {
                if (!matKhau.isBlank()) {
                    nv.setMatKhau(com.bakery.utils.PasswordUtils.hash(matKhau));
                } else {
                    nv.setMatKhau(selectedNhanVien.getMatKhau());
                }
                nhanVienService.suaNhanVien(nv);
                lblThongBao.setText("Cập nhật nhân viên thành công.");
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
            lblThongBao.setText("Vui lòng chọn nhân viên cần vô hiệu hóa.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Bạn có chắc muốn xóa nhân viên \"" + selectedNhanVien.getHoTen() + "\"?",
            ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    nhanVienService.xoaNhanVien(selectedNhanVien.getMaNV());
                    lblThongBao.setText("Đã xóa nhân viên thành công.");
                    loadData();
                    onThemMoi();
                } catch (Exception e) {
                    lblThongBao.setText("Lỗi xóa nhân viên: " + e.getMessage());
                }
            }
        });
    }

    @FXML private void onLamMoi() { loadData(); onThemMoi(); }
    @FXML private void onQuayLai() { quayLaiMenuChinh(tblNhanVien); }
    @FXML private void onToggleMatKhau() { /* Logic toggle handled by bindPasswordToggle */ }

    private void bindPasswordToggle() {
        txtMatKhauVisible.textProperty().bindBidirectional(txtMatKhau.textProperty());
        txtMatKhauVisible.setVisible(false);
        txtMatKhauVisible.setManaged(false);
    }

    private void setupFilters() {
        cmbLocTrangThai.setItems(FXCollections.observableArrayList("Tất cả", "Đang làm việc", "Ngừng việc"));
        cmbLocTrangThai.getSelectionModel().selectFirst();
    }
}
