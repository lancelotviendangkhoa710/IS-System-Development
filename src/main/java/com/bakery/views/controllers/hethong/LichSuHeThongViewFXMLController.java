package com.bakery.views.controllers.hethong;

import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LichSuHeThongViewFXMLController extends BaseController {

    @FXML private TableView<AuditLogEntry> tblAuditLog;
    @FXML private TableColumn<AuditLogEntry, String> colThoiGian;
    @FXML private TableColumn<AuditLogEntry, String> colNguoiDung;
    @FXML private TableColumn<AuditLogEntry, String> colHanhDong;
    @FXML private TableColumn<AuditLogEntry, String> colChiTiet;
    @FXML private TableColumn<AuditLogEntry, String> colTrangThai;

    @FXML private TextField txtTimKiem;
    @FXML private ComboBox<String> cbBoLoc;

    private final ObservableList<AuditLogEntry> masterData = FXCollections.observableArrayList();

    public static class AuditLogEntry {
        private String thoiGian; private String nguoiDung; private String hanhDong; private String chiTiet; private String trangThai;
        public AuditLogEntry(String tg, String nd, String hd, String ct, String tt) {
            thoiGian = tg; nguoiDung = nd; hanhDong = hd; chiTiet = ct; trangThai = tt;
        }
        public String getThoiGian() { return thoiGian; }
        public String getNguoiDung() { return nguoiDung; }
        public String getHanhDong() { return hanhDong; }
        public String getChiTiet() { return chiTiet; }
        public String getTrangThai() { return trangThai; }
    }

    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        loadMockData();
    }

    private void setupTable() {
        colThoiGian.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getThoiGian()));
        colNguoiDung.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNguoiDung()));
        colHanhDong.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHanhDong()));
        colChiTiet.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getChiTiet()));
        colTrangThai.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTrangThai()));
    }

    private void setupFilters() {
        cbBoLoc.setItems(FXCollections.observableArrayList("Tất cả", "HỆ THỐNG", "BÁN HÀNG", "KHO HÀNG", "NHÂN SỰ"));
        cbBoLoc.getSelectionModel().selectFirst();
        cbBoLoc.valueProperty().addListener((obs, oldVal, newVal) -> onTimKiem());
    }

    @FXML
    private void onTaiLai() {
        txtTimKiem.clear();
        cbBoLoc.getSelectionModel().selectFirst();
        loadMockData();
    }

    @FXML
    private void onTimKiem() {
        String keyword = txtTimKiem.getText() == null ? "" : txtTimKiem.getText().toLowerCase();
        String filter = cbBoLoc.getValue();
        List<AuditLogEntry> filteredList = new ArrayList<>();

        for (AuditLogEntry entry : masterData) {
            boolean matchKey = entry.getChiTiet().toLowerCase().contains(keyword) || 
                               entry.getNguoiDung().toLowerCase().contains(keyword);
            boolean matchFilter = "Tất cả".equals(filter) || entry.getHanhDong().contains(filter);
            
            if (matchKey && matchFilter) {
                filteredList.add(entry);
            }
        }
        tblAuditLog.setItems(FXCollections.observableArrayList(filteredList));
    }

    private void loadMockData() {
        masterData.clear();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        masterData.add(new AuditLogEntry(now.minusMinutes(5).format(fmt), "Nguyễn Văn Quản Lý (admin)", "[HỆ THỐNG] Đăng nhập", "Đăng nhập hệ thống thành công", "Thành công"));
        masterData.add(new AuditLogEntry(now.minusMinutes(10).format(fmt), "Trần Thị Thu Ngân (cashier1)", "[BÁN HÀNG] Tạo đơn", "Tạo đơn hàng mới #105 trị giá 250,000đ", "Thành công"));
        masterData.add(new AuditLogEntry(now.minusMinutes(15).format(fmt), "Lê Văn Đầu Bếp (chef1)", "[BÁN HÀNG] Cập nhật đơn", "Đổi trạng thái đơn #101 sang 'Đang sản xuất'", "Thành công"));
        masterData.add(new AuditLogEntry(now.minusMinutes(20).format(fmt), "Phạm Minh Kho (warehouse1)", "[KHO HÀNG] Nhập kho", "Nhập 50kg Bột mì đa dụng vào kho chính", "Thành công"));
        masterData.add(new AuditLogEntry(now.minusMinutes(25).format(fmt), "Nguyễn Văn Quản Lý (admin)", "[NHÂN SỰ] Phân quyền", "Thêm quyền 'Thu ngân' cho tài khoản 'cashier2'", "Thành công"));
        masterData.add(new AuditLogEntry(now.minusMinutes(30).format(fmt), "Trần Hữu Kiên (chef2)", "[HỆ THỐNG] Đăng nhập", "Cố gắng đăng nhập sai mật khẩu 3 lần", "Thất bại"));
        masterData.add(new AuditLogEntry(now.minusMinutes(45).format(fmt), "Hệ thống (Tự động)", "[KHO HÀNG] Cảnh báo", "Cảnh báo nguyên liệu 'Đường cát' sắp hết (còn 2kg)", "Cảnh báo"));
        masterData.add(new AuditLogEntry(now.minusHours(2).format(fmt), "Lương Minh Tuấn (cashier3)", "[BÁN HÀNG] Hủy đơn", "Hủy đơn hàng #102 do khách không nhận", "Thành công"));

        tblAuditLog.setItems(masterData);
    }
}
