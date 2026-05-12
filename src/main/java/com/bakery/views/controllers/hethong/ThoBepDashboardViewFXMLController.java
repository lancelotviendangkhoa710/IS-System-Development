package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.banhang.DonHangService;
import com.bakery.services.nhansu.XacThucService;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.BaseController;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDateTime;
import java.util.List;

public class ThoBepDashboardViewFXMLController extends BaseController {
    private final XacThucService xacThucService = new XacThucService();
    private final DonHangService donHangService = new DonHangService();

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
        taiDuLieuTuDB();
    }

    // Tải danh sách đơn đang sản xuất từ DB
    private void taiDuLieuTuDB() {
        new Thread(() -> {
            try {
                List<DonDatHangDTO> list = donHangService.layDanhSachDonTheoDoi(
                        null, null, null, null, null, "Đang sản xuất");
                Platform.runLater(() -> {
                    dsDon.setAll(list != null ? list : java.util.Collections.emptyList());
                    if (dsDon.isEmpty()) {
                        lblThongBao.setText("Không có đơn nào đang sản xuất.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblThongBao.setText("Lỗi tải đơn hàng: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void onHoanThanhDon() {
        DonDatHangDTO selected = tblDonBep.getSelectionModel().getSelectedItem();
        if (selected == null) {
            lblThongBao.getStyleClass().setAll("lbl-small-bold", "text-danger");
            lblThongBao.setText("Vui long chon 1 don de hoan thanh!");
            return;
        }
        selected.setTenTrangThai("Cho giao");
        tblDonBep.refresh();
        lblThongBao.getStyleClass().setAll("lbl-small-bold", "text-success");
        lblThongBao.setText("Da cap nhat don #" + selected.getMaDon() + " sang Cho giao.");
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
    private void onMoXuatKho() {
        transitionTo(lblTenThoBep, "/fxml/kho/XuatKhoView.fxml", "H3K Bakery - Lap phieu xuat kho", 1280, 720);
    }

    @FXML
    private void onMoSanXuat() {
        transitionTo(lblTenThoBep, "/fxml/kho/SanXuatView.fxml", "H3K Bakery - Ke hoach san xuat", 1280, 720);
    }

    @FXML
    private void onMoTheoDoiDon() {
        transitionTo(lblTenThoBep, "/fxml/banhang/TheoDoiDonHangView.fxml", "H3K Bakery - Theo doi don hang", 1366, 768);
    }

    @FXML
    private void onMoThanhPhanBanh() {
        transitionTo(lblTenThoBep, "/fxml/kho/ThanhPhanBanhView.fxml", "H3K Bakery - Cong thuc va thanh phan", 1366, 768);
    }

    @FXML
    private void onMoKho() {
        transitionTo(lblTenThoBep, "/fxml/kho/KhoView.fxml", "H3K Bakery - Kho va nguyen lieu", 1280, 720);
    }

    @FXML
    private void onDangXuat() {
        xacThucService.dangXuat();
        UserSession.clear();
        transitionTo(lblTenThoBep, "/fxml/hethong/DangNhapView.fxml", "H3K Bakery - Dang nhap", 1280, 720);
    }
}
