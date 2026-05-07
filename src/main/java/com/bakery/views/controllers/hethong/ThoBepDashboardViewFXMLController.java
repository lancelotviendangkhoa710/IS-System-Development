package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.nhansu.XacThucService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDateTime;

public class ThoBepDashboardViewFXMLController extends BaseController {
    private final XacThucService xacThucService = new XacThucService();

    @FXML private Label lblTenThoBep;
    @FXML private Label lblVaiTro;
    @FXML private Label lblMoTa;
    @FXML private Label lblThongBao;

    @FXML private TableView<DonDatHangDTO> tblDonBep;
    @FXML private TableColumn<DonDatHangDTO, Integer> colMaDon;
    @FXML private TableColumn<DonDatHangDTO, String> colNgayNhan;
    @FXML private TableColumn<DonDatHangDTO, String> colTenKhach;
    @FXML private TableColumn<DonDatHangDTO, String> colTrangThai;

    private final ObservableList<DonDatHangDTO> dsDon = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        khoiTaoDashboard(UserSession.getCurrentUser());
        
        colMaDon.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getMaDon()));
        colNgayNhan.setCellValueFactory(data -> {
            LocalDateTime dt = data.getValue().getNgayGioNhanBanh();
            return new ReadOnlyStringWrapper(dt != null ? dt.toString().replace("T", " ") : "N/A");
        });
        colTenKhach.setCellValueFactory(data -> {
            Integer maKH = data.getValue().getMaKH();
            return new ReadOnlyStringWrapper(maKH == null ? "Khách Vãng Lai" : "KH #" + maKH);
        });
        colTrangThai.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenTrangThai()));
        
        tblDonBep.setItems(dsDon);
        loadMockData();
    }

    private void loadMockData() {
        dsDon.clear();
        dsDon.add(createMockDon(101, LocalDateTime.now().plusHours(2), 5, "Đang sản xuất"));
        dsDon.add(createMockDon(102, LocalDateTime.now().plusHours(4), null, "Mới đặt"));
        dsDon.add(createMockDon(103, LocalDateTime.now().plusDays(1), 12, "Đã cọc"));
        dsDon.add(createMockDon(104, LocalDateTime.now().plusHours(1), 8, "Đang sản xuất"));
    }

    private DonDatHangDTO createMockDon(int maDon, LocalDateTime ngayNhan, Integer maKH, String trangThai) {
        DonDatHangDTO don = new DonDatHangDTO();
        don.setMaDon(maDon);
        don.setNgayGioNhanBanh(ngayNhan);
        don.setMaKH(maKH);
        don.setTenTrangThai(trangThai);
        return don;
    }

    @FXML
    private void onHoanThanhDon() {
        DonDatHangDTO selected = tblDonBep.getSelectionModel().getSelectedItem();
        if (selected == null) {
            lblThongBao.setStyle("-fx-text-fill: red;");
            lblThongBao.setText("Vui lòng chọn 1 đơn để hoàn thành!");
            return;
        }
        selected.setTenTrangThai("Chờ giao");
        tblDonBep.refresh();
        lblThongBao.setStyle("-fx-text-fill: green;");
        lblThongBao.setText("Đã cập nhật đơn #" + selected.getMaDon() + " sang Chờ giao.");
    }

    public void khoiTaoDashboard(NhanVienDTO nhanVien) {
        if (nhanVien == null) {
            return;
        }
        UserSession.setCurrentUser(nhanVien);
        lblTenThoBep.setText(nhanVien.getHoTen());
        lblVaiTro.setText(nhanVien.getTenVaiTro());
        lblMoTa.setText("Uu tien don cho xu ly, cap nhat san xuat va phoi hop kho nguyen lieu.");
    }

    @FXML
    private void onMoTheoDoiDon() {
        transitionTo(lblTenThoBep, "/fxml/TheoDoiDonHangView.fxml", "H3K Bakery - Theo doi don hang", 1366, 768);
    }

    @FXML
    private void onMoThanhPhanBanh() {
        transitionTo(lblTenThoBep, "/fxml/ThanhPhanBanhView.fxml", "H3K Bakery - Cong thuc va thanh phan", 1366, 768);
    }

    @FXML
    private void onMoKho() {
        transitionTo(lblTenThoBep, "/fxml/KhoView.fxml", "H3K Bakery - Kho va nguyen lieu", 1280, 720);
    }

    @FXML
    private void onDangXuat() {
        xacThucService.dangXuat();
        UserSession.clear();
        transitionTo(lblTenThoBep, "/fxml/DangNhapView.fxml", "H3K Bakery - Dang nhap", 1280, 720);
    }
}
