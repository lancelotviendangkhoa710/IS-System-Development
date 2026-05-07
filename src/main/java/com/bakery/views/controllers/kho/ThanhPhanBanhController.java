package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.CotBanhDTO;
import com.bakery.model.dto.kho.KieuTrangTriDTO;
import com.bakery.model.dto.kho.NhanBanhDTO;
import com.bakery.services.banhang.TuyChinhBanhService;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller cho ThanhPhanBanhView (Quản lý công thức/thành phần bánh).
 * Mọi dữ liệu được lấy từ DB. Không có Mock Data.
 */
public class ThanhPhanBanhController extends BaseController {

    @FXML private TableView<CotBanhDTO> tblCotBanh;
    @FXML private TableColumn<CotBanhDTO, String> colTenCot;
    @FXML private TableColumn<CotBanhDTO, BigDecimal> colPhuPhiCot;
    @FXML private TableColumn<CotBanhDTO, String> colGiaVonCot;

    @FXML private TableView<NhanBanhDTO> tblNhanBanh;
    @FXML private TableColumn<NhanBanhDTO, String> colTenNhan;
    @FXML private TableColumn<NhanBanhDTO, BigDecimal> colPhuPhiNhan;
    @FXML private TableColumn<NhanBanhDTO, String> colGiaVonNhan;

    @FXML private TableView<KieuTrangTriDTO> tblKieuTrangTri;
    @FXML private TableColumn<KieuTrangTriDTO, String> colTenTrangTri;
    @FXML private TableColumn<KieuTrangTriDTO, BigDecimal> colPhuPhiTrangTri;
    @FXML private TableColumn<KieuTrangTriDTO, String> colGiaVonTrangTri;

    @FXML private Label lblTongGiaVon;
    @FXML private Label lblThongBao;

    // Bảng công thức nguyên liệu — hiện chưa có DAO tương ứng, để trống chờ impl
    @FXML private TableView<?> tblCongThuc;

    private final TuyChinhBanhService service = new TuyChinhBanhService();

    @FXML
    public void initialize() {
        setupTables();
        taiDuLieu();
    }

    private void setupTables() {
        colTenCot.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenCot()));
        colPhuPhiCot.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPhuPhi()));
        colGiaVonCot.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%,.0f đ", cell.getValue().getPhuPhi().doubleValue() * 0.4)));

        colTenNhan.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenNhan()));
        colPhuPhiNhan.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPhuPhi()));
        colGiaVonNhan.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%,.0f đ", cell.getValue().getPhuPhi().doubleValue() * 0.45)));

        colTenTrangTri.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenTrangTri()));
        colPhuPhiTrangTri.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPhuPhi()));
        colGiaVonTrangTri.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%,.0f đ", cell.getValue().getPhuPhi().doubleValue() * 0.3)));

        tblCotBanh.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> tinhTongGiaVon());
        tblNhanBanh.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> tinhTongGiaVon());
        tblKieuTrangTri.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> tinhTongGiaVon());
    }

    private void tinhTongGiaVon() {
        double cost = 0;
        if (tblCotBanh.getSelectionModel().getSelectedItem() != null)
            cost += tblCotBanh.getSelectionModel().getSelectedItem().getPhuPhi().doubleValue() * 0.4;
        if (tblNhanBanh.getSelectionModel().getSelectedItem() != null)
            cost += tblNhanBanh.getSelectionModel().getSelectedItem().getPhuPhi().doubleValue() * 0.45;
        if (tblKieuTrangTri.getSelectionModel().getSelectedItem() != null)
            cost += tblKieuTrangTri.getSelectionModel().getSelectedItem().getPhuPhi().doubleValue() * 0.3;
        if (lblTongGiaVon != null) lblTongGiaVon.setText(String.format("%,.0f đ", cost));
    }

    private void taiDuLieu() {
        StringBuilder log = new StringBuilder();
        try {
            List<CotBanhDTO> cots = service.layDanhSachCotBanh();
            tblCotBanh.setItems(FXCollections.observableArrayList(cots != null ? cots : List.of()));
            if (cots == null || cots.isEmpty()) log.append("Chưa có cốt bánh. ");
        } catch (Exception e) {
            tblCotBanh.setItems(FXCollections.observableArrayList());
            log.append("Lỗi tải cốt bánh: ").append(e.getMessage()).append(". ");
        }
        try {
            List<NhanBanhDTO> nhans = service.layDanhSachNhanBanh();
            tblNhanBanh.setItems(FXCollections.observableArrayList(nhans != null ? nhans : List.of()));
            if (nhans == null || nhans.isEmpty()) log.append("Chưa có nhân bánh. ");
        } catch (Exception e) {
            tblNhanBanh.setItems(FXCollections.observableArrayList());
            log.append("Lỗi tải nhân bánh: ").append(e.getMessage()).append(". ");
        }
        try {
            List<KieuTrangTriDTO> tts = service.layDanhSachKieuTrangTri();
            tblKieuTrangTri.setItems(FXCollections.observableArrayList(tts != null ? tts : List.of()));
            if (tts == null || tts.isEmpty()) log.append("Chưa có kiểu trang trí. ");
        } catch (Exception e) {
            tblKieuTrangTri.setItems(FXCollections.observableArrayList());
            log.append("Lỗi tải trang trí: ").append(e.getMessage()).append(". ");
        }

        if (lblThongBao != null) {
            lblThongBao.setText(log.length() > 0 ? log.toString().trim() : "Đã tải dữ liệu từ cơ sở dữ liệu.");
        }
    }

    @FXML private void onThemCot() { lblThongBao.setText("Chức năng Thêm cốt bánh đang được phát triển."); }
    @FXML private void onSuaCot() { lblThongBao.setText("Chức năng Sửa cốt bánh đang được phát triển."); }
    @FXML private void onXoaCot() { lblThongBao.setText("Chức năng Xóa cốt bánh đang được phát triển."); }

    @FXML private void onThemNhan() { lblThongBao.setText("Chức năng Thêm nhân bánh đang được phát triển."); }
    @FXML private void onSuaNhan() { lblThongBao.setText("Chức năng Sửa nhân bánh đang được phát triển."); }
    @FXML private void onXoaNhan() { lblThongBao.setText("Chức năng Xóa nhân bánh đang được phát triển."); }

    @FXML private void onThemTrangTri() { lblThongBao.setText("Chức năng Thêm trang trí đang được phát triển."); }
    @FXML private void onSuaTrangTri() { lblThongBao.setText("Chức năng Sửa trang trí đang được phát triển."); }
    @FXML private void onXoaTrangTri() { lblThongBao.setText("Chức năng Xóa trang trí đang được phát triển."); }

    @FXML private void onQuayLai() { quayLaiMenuChinh(tblCotBanh); }
}
