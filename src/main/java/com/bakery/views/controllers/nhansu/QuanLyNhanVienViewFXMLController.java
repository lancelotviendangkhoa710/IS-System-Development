package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.nhansu.NhanVienService;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class QuanLyNhanVienViewFXMLController extends BaseController {

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
    private ComboBox<String> cmbLocTrangThai;
    @FXML
    private Button btnVoHieuHoa;

    private final NhanVienService nhanVienService = new NhanVienService();
    private ObservableList<NhanVienDTO> masterData = FXCollections.observableArrayList();
    private Map<Integer, String> roleMap;
    private NhanVienDTO selectedNhanVien;

    @FXML
    public void initialize() {
        setupTable();
        loadRoles();
        setupFilters();
        loadData();

        tblNhanVien.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                hienThiChiTiet(newVal);
            }
        });
    }

    private void setupFilters() {
        cmbLocTrangThai.setItems(FXCollections.observableArrayList("Tất cả trạng thái", "Đang làm việc", "Ngừng việc"));
        cmbLocTrangThai.getSelectionModel().selectFirst();

        txtTimKiem.textProperty().addListener((obs, oldVal, newVal) -> filterData(newVal));
        cmbLocTrangThai.valueProperty().addListener((obs, oldVal, newVal) -> filterData(txtTimKiem.getText()));
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
            List<NhanVienDTO> list = nhanVienService.layTatCaNhanVien();
            // Group by vai trò, sau đó sort theo mã nhân viên
            list.sort(Comparator.comparing(NhanVienDTO::getTenVaiTro, Comparator.nullsLast(String::compareTo))
                    .thenComparing(NhanVienDTO::getMaNV));

            masterData.setAll(list);
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
        String selectedStatus = cmbLocTrangThai.getValue();

        FilteredList<NhanVienDTO> filtered = new FilteredList<>(masterData, nv -> {
            // Lọc theo từ khóa (Tên, SĐT, Tên đăng nhập)
            boolean matchesKeyword = true;
            if (keyword != null && !keyword.isBlank()) {
                String lowerKey = keyword.toLowerCase().trim();
                matchesKeyword = nv.getHoTen().toLowerCase().contains(lowerKey) ||
                        nv.getSdt().contains(lowerKey) ||
                        nv.getTenDangNhap().toLowerCase().contains(lowerKey);
            }

            // Lọc theo trạng thái
            boolean matchesStatus = true;
            if (selectedStatus != null && !selectedStatus.equals("Tất cả trạng thái")) {
                int status = nv.getTrangThaiLamViec();
                if (selectedStatus.equals("Đang làm việc")) {
                    matchesStatus = (status == 1);
                } else if (selectedStatus.equals("Ngừng việc")) {
                    matchesStatus = (status == 0);
                }
            }

            return matchesKeyword && matchesStatus;
        });

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
            String msg = e.getMessage();
            // Xử lý thông báo lỗi từ Oracle để thân thiện hơn (bỏ ORA-xxxxx)
            if (msg.contains("ORA-")) {
                msg = msg.substring(msg.indexOf(":") + 1).trim();
                if (msg.contains("ORA-")) { // Nếu vẫn còn (do lồng nhau)
                    msg = msg.substring(msg.indexOf(":") + 1).trim();
                }
            }

            hienThiThongBaoLoi("Lỗi nghiệp vụ", msg);
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
        quayLaiMenuChinh(tblNhanVien);
    }

    private boolean validateInput() {
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String tenDN = txtTenDangNhap.getText().trim();

        if (hoTen.isEmpty() || sdt.isEmpty() || tenDN.isEmpty() || cmbVaiTro.getValue() == null) {
            hienThiThongBaoLoi("Lỗi nhập liệu", "Vui lòng điền đầy đủ các trường bắt buộc (*).");
            return false;
        }

        // Kiểm tra định dạng số điện thoại
        if (!sdt.matches("\\d{10,11}")) {
            hienThiThongBaoLoi("Lỗi kiểu dữ liệu", "Số điện thoại phải là chữ số và có độ dài từ 10-11 ký tự.");
            return false;
        }

        // Kiểm tra tên đăng nhập (không chứa khoảng trắng)
        if (tenDN.contains(" ")) {
            hienThiThongBaoLoi("Lỗi kiểu dữ liệu", "Tên đăng nhập không được chứa khoảng trắng.");
            return false;
        }

        if (selectedNhanVien == null && txtMatKhau.getText().isEmpty()) {
            hienThiThongBaoLoi("Lỗi nhập liệu", "Nhân viên mới bắt buộc phải có mật khẩu.");
            return false;
        }
        return true;
    }

}
