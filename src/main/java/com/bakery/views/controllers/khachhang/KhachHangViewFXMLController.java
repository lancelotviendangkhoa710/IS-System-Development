package com.bakery.views.controllers.khachhang;

import com.bakery.model.dto.khachhang.HangThanhVienDTO;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.presenters.khachhang.KhachHangPresenter;
import com.bakery.views.controllers.BaseController;
import com.bakery.views.interfaces.khachhang.KhachHangView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller cho QuanLyKhachHang.
 * Hỗ trợ Mock Data cho Demo.
 */
public class KhachHangViewFXMLController extends BaseController implements KhachHangView {

    @FXML private TableView<KhachHangDTO> customerTable;
    @FXML private TableColumn<KhachHangDTO, Integer> colId;
    @FXML private TableColumn<KhachHangDTO, String> colName;
    @FXML private TableColumn<KhachHangDTO, String> colPhone;
    @FXML private TableColumn<KhachHangDTO, String> colAddress;
    @FXML private TableColumn<KhachHangDTO, LocalDate> colRegDate;
    @FXML private TableColumn<KhachHangDTO, Integer> colPoints;
    @FXML private TableColumn<KhachHangDTO, String> colTier;

    @FXML private Label lblTotalCustomers;
    @FXML private Label lblNewCustomers;
    @FXML private TextField searchField;
    @FXML private HBox paginationBox;

    @FXML private Button btnToggleFilter;
    @FXML private Button btnRefresh;
    @FXML private Button btnCheDoThungRac;
    @FXML private HBox filterPanel;
    @FXML private DatePicker dpFromDate;
    @FXML private DatePicker dpToDate;
    @FXML private ComboBox<String> cbTierFilter;

    private final KhachHangPresenter presenter = new KhachHangPresenter(this);

    @FXML
    public void initialize() {
        setupTableColumns();
        presenter.taiDuLieu();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getMaKH()).asObject());
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getHoTen()));
        colPhone.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSdt()));
        colAddress.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDiaChi()));
        colRegDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getNgayDangKy()));
        colPoints.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDiemTichLuy()));
        colTier.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenHang() != null ? cell.getValue().getTenHang() : "Thành viên"));
    }

    @Override
    public void hienThiDanhSachKhachHang(List<KhachHangDTO> danhSach) {
        if (danhSach == null || danhSach.isEmpty()) {
            danhSach = getMockCustomers();
        }
        customerTable.setItems(FXCollections.observableArrayList(danhSach));
        lblTotalCustomers.setText(String.valueOf(danhSach.size()));
        lblNewCustomers.setText("3");
    }

    private List<KhachHangDTO> getMockCustomers() {
        List<KhachHangDTO> mock = new ArrayList<>();
        mock.add(createMock(1, "Nguyễn Thị Lan", "0912345678", "123 Quận 1, TP.HCM", 500, "Vàng"));
        mock.add(createMock(2, "Trần Văn Hùng", "0988777666", "456 Quận 7, TP.HCM", 1200, "Kim cương"));
        mock.add(createMock(3, "Lê Minh Tâm", "0905111222", "789 Bình Thạnh, TP.HCM", 150, "Thành viên"));
        mock.add(createMock(4, "Phạm Hồng Nhung", "0345678901", "321 Thủ Đức, TP.HCM", 800, "Bạc"));
        mock.add(createMock(5, "Hoàng Gia Bảo", "0977123123", "111 Gò Vấp, TP.HCM", 300, "Thành viên"));
        return mock;
    }

    private KhachHangDTO createMock(int id, String name, String sdt, String address, int points, String tier) {
        KhachHangDTO kh = new KhachHangDTO();
        kh.setMaKH(id); kh.setHoTen(name); kh.setSdt(sdt); kh.setDiaChi(address);
        kh.setDiemTichLuy(points); kh.setTenHang(tier); kh.setNgayDangKy(LocalDate.now().minusMonths(2));
        return kh;
    }

    @Override public void capNhatThongTinPhanTrang(String thongTin) {}
    @Override public void capNhatDieuKhienPhanTrang(int trangHienTai, int tongTrang) {}
    @Override public void capNhatTongKhachHang(int tongKhachHang) {}
    @Override public void capNhatKhachHangMoiTrongThang(int soKhachMoi) {}
    @Override public void batTatTrangThaiBan(boolean ban) {}
    @Override public void hienThiLoi(String tieuDe, String noiDung) { hienThiLoiLabel(noiDung); }
    @Override public void hienThiThanhCong(String tieuDe, String noiDung) { hienThiThanhCongLabel(noiDung); }
    @Override public void hienThiThongTin(String tieuDe, String noiDung) {}
    @Override public void capNhatCheDoThungRac(boolean cheDoThungRac) {}
    
    @FXML private void onRefreshClicked() { presenter.taiDuLieu(); }
    @FXML private void onAddCustomerClicked() { lblThongBao.setText("Chế độ Demo: Đã mở dialog thêm khách hàng."); }
    @FXML private void onBack() { quayLaiMenuChinh(customerTable); }

    @FXML private void onToggleFilterClicked() {
        if (filterPanel != null) {
            boolean isVisible = filterPanel.isVisible();
            filterPanel.setVisible(!isVisible);
            filterPanel.setManaged(!isVisible);
        }
    }

    @FXML private void onDeletedViewClicked() {
        lblThongBao.setText("Chế độ Demo: Mở thùng rác.");
    }

    @FXML private void onExportExcelClicked() {
        lblThongBao.setText("Chế độ Demo: Đã xuất Excel.");
    }

    @FXML private void onApplyFilterClicked() {
        lblThongBao.setText("Chế độ Demo: Đã áp dụng bộ lọc.");
    }

    @FXML private void onClearFilterClicked() {
        if (dpFromDate != null) dpFromDate.setValue(null);
        if (dpToDate != null) dpToDate.setValue(null);
        if (cbTierFilter != null) cbTierFilter.getSelectionModel().clearSelection();
        if (searchField != null) searchField.clear();
        lblThongBao.setText("Chế độ Demo: Đã xóa bộ lọc.");
    }
}
