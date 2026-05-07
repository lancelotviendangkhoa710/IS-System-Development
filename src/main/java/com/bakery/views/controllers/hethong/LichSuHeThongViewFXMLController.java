package com.bakery.views.controllers.hethong;

import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        // Chưa có bảng AUDIT_LOG trong DB. Hiển thị bảng trống.
        tblAuditLog.setItems(masterData);
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
        masterData.clear();
        tblAuditLog.setItems(masterData);
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

}

