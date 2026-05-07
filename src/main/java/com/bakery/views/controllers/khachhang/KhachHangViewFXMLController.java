package com.bakery.views.controllers.khachhang;

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
import java.util.List;

public class KhachHangViewFXMLController extends BaseController implements KhachHangView {

    @FXML
    private TableView<KhachHangDTO> customerTable;
    @FXML
    private TableColumn<KhachHangDTO, Integer> colId;
    @FXML
    private TableColumn<KhachHangDTO, String> colName;
    @FXML
    private TableColumn<KhachHangDTO, String> colPhone;
    @FXML
    private TableColumn<KhachHangDTO, String> colAddress;
    @FXML
    private TableColumn<KhachHangDTO, LocalDate> colRegDate;
    @FXML
    private TableColumn<KhachHangDTO, Integer> colPoints;
    @FXML
    private TableColumn<KhachHangDTO, String> colTier;

    @FXML
    private Label lblTotalCustomers;
    @FXML
    private Label lblNewCustomers;
    @FXML
    private TextField searchField;
    @FXML
    private HBox paginationBox;

    @FXML
    private Button btnToggleFilter;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnCheDoThungRac;
    @FXML
    private HBox filterPanel;
    @FXML
    private DatePicker dpFromDate;
    @FXML
    private DatePicker dpToDate;
    @FXML
    private ComboBox<String> cbTierFilter;

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
        colTier.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getTenHang() != null ? cell.getValue().getTenHang() : "Thành viên"));
    }

    @Override
    public void hienThiDanhSachKhachHang(List<KhachHangDTO> danhSach) {
        if (danhSach == null || danhSach.isEmpty()) {
            customerTable.setItems(FXCollections.observableArrayList());
            lblTotalCustomers.setText("0");
            lblNewCustomers.setText("0");
            hienThiLoiLabel("Không có dữ liệu khách hàng.");
            return;
        }
        customerTable.setItems(FXCollections.observableArrayList(danhSach));
        lblTotalCustomers.setText(String.valueOf(danhSach.size()));
        lblNewCustomers.setText("0");
    }

    @Override
    public void capNhatThongTinPhanTrang(String thongTin) {
    }

    @Override
    public void capNhatDieuKhienPhanTrang(int trangHienTai, int tongTrang) {
    }

    @Override
    public void capNhatTongKhachHang(int tongKhachHang) {
    }

    @Override
    public void capNhatKhachHangMoiTrongThang(int soKhachMoi) {
    }

    @Override
    public void batTatTrangThaiBan(boolean ban) {
    }

    @Override
    public void hienThiLoi(String tieuDe, String noiDung) {
        hienThiLoiLabel(noiDung);
    }

    @Override
    public void hienThiThanhCong(String tieuDe, String noiDung) {
        hienThiThanhCongLabel(noiDung);
    }

    @Override
    public void hienThiThongTin(String tieuDe, String noiDung) {
    }

    @Override
    public void capNhatCheDoThungRac(boolean cheDoThungRac) {
    }

    @FXML
    private void onRefreshClicked() {
        presenter.taiDuLieu();
    }

    @FXML
    private void onAddCustomerClicked() {
        hienThiLoiLabel("Chức năng thêm khách hàng đang được phát triển.");
    }

    @FXML
    private void onBack() {
        quayLaiMenuChinh(customerTable);
    }

    @FXML
    private void onToggleFilterClicked() {
        if (filterPanel != null) {
            boolean isVisible = filterPanel.isVisible();
            filterPanel.setVisible(!isVisible);
            filterPanel.setManaged(!isVisible);
        }
    }

    @FXML
    private void onDeletedViewClicked() {
        hienThiLoiLabel("Chức năng thùng rác đang được phát triển.");
    }

    @FXML
    private void onExportExcelClicked() {
        hienThiLoiLabel("Chức năng xuất Excel đang được phát triển.");
    }

    @FXML
    private void onApplyFilterClicked() {
        hienThiLoiLabel("Chức năng bộ lọc đang được phát triển.");
    }

    @FXML
    private void onClearFilterClicked() {
        if (dpFromDate != null)
            dpFromDate.setValue(null);
        if (dpToDate != null)
            dpToDate.setValue(null);
        if (cbTierFilter != null)
            cbTierFilter.getSelectionModel().clearSelection();
        if (searchField != null)
            searchField.clear();
    }
}
