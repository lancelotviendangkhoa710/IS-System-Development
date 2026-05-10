package com.bakery.views.controllers.khachhang;

import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.presenters.khachhang.KhachHangPresenter;
import com.bakery.views.controllers.BaseController;
import com.bakery.views.controllers.banhang.KhachHangDialogViewFXMLController;
import com.bakery.views.interfaces.khachhang.KhachHangView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class KhachHangViewFXMLController extends BaseController implements KhachHangView {


    @FXML private TableView<KhachHangDTO>          customerTable;
    @FXML private TableColumn<KhachHangDTO, Integer>   colId;
    @FXML private TableColumn<KhachHangDTO, String>    colName;
    @FXML private TableColumn<KhachHangDTO, String>    colPhone;
    @FXML private TableColumn<KhachHangDTO, String>    colAddress;
    @FXML private TableColumn<KhachHangDTO, LocalDate> colRegDate;
    @FXML private TableColumn<KhachHangDTO, Integer>   colPoints;
    @FXML private TableColumn<KhachHangDTO, String>    colTier;
    @FXML private TableColumn<KhachHangDTO, Void>      colActions;

    @FXML private Label       lblTotalCustomers;
    @FXML private Label       lblNewCustomers;
    @FXML private TextField   searchField;
    @FXML private HBox        paginationBox;
    @FXML private Button      btnToggleFilter;
    @FXML private Button      btnRefresh;
    @FXML private Button      btnCheDoThungRac;
    @FXML private HBox        filterPanel;
    @FXML private DatePicker  dpFromDate;
    @FXML private DatePicker  dpToDate;
    @FXML private ComboBox<String> cbTierFilter;

    private final KhachHangPresenter presenter = new KhachHangPresenter(this);

    @FXML
    public void initialize() {
        setupTableColumns();
        setupActionsColumn();
        setupDoubleClickEdit();
        presenter.taiDuLieu();
    }

    // ── KhachHangView interface ──────────────────────────────────────────

    @Override
    public void hienThiDanhSachKhachHang(List<KhachHangDTO> danhSach) {
        if (danhSach == null || danhSach.isEmpty()) {
            customerTable.setItems(FXCollections.observableArrayList());
            hienThiLoiLabel("Không có dữ liệu khách hàng.");
            return;
        }
        customerTable.setItems(FXCollections.observableArrayList(danhSach));
    }

    @Override
    public void capNhatThongTinPhanTrang(String thongTin) {}

    @Override
    public void capNhatDieuKhienPhanTrang(int trangHienTai, int tongTrang) {}

    @Override
    public void capNhatTongKhachHang(int tongKhachHang) {
        if (lblTotalCustomers != null) lblTotalCustomers.setText(String.valueOf(tongKhachHang));
    }

    @Override
    public void capNhatKhachHangMoiTrongThang(int soKhachMoi) {
        if (lblNewCustomers != null) lblNewCustomers.setText(String.valueOf(soKhachMoi));
    }

    @Override
    public void batTatTrangThaiBan(boolean ban) {}

    @Override
    public void hienThiLoi(String tieuDe, String noiDung) {
        hienThiLoiLabel(noiDung);
    }

    @Override
    public void hienThiThanhCong(String tieuDe, String noiDung) {
        hienThiThanhCongLabel(noiDung);
    }

    @Override
    public void hienThiThongTin(String tieuDe, String noiDung) {}

    @Override
    public void capNhatCheDoThungRac(boolean cheDoThungRac) {}

    /**
     * Mở dialog lịch sử mua hàng dựa trên FXML — kế thừa stylesheet từ Scene gốc.
     */
    @Override
    public void hienThiLichSuMuaHang(KhachHangDTO kh, List<DonDatHangDTO> dsDon) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/LichSuMuaHangDialog.fxml"));
            Parent root = loader.load();
            LichSuMuaHangDialogViewFXMLController ctrl = loader.getController();
            ctrl.khoiTao(kh, dsDon);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Lịch sử mua hàng — " + kh.getHoTen());
            stage.setResizable(true);

            Scene scene = new Scene(root, 900, 560);
            // Kế thừa stylesheet từ Scene chính
            if (customerTable.getScene() != null
                    && !customerTable.getScene().getStylesheets().isEmpty()) {
                scene.getStylesheets().addAll(customerTable.getScene().getStylesheets());
            }
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở lịch sử: " + e.getMessage());
        }
    }

    // ── FXML handlers ────────────────────────────────────────────────────

    @FXML private void onRefreshClicked()     { presenter.taiDuLieu(); }
    @FXML private void onAddCustomerClicked() { moDialogKhachHang(null); }
    @FXML private void onBack()               { quayLaiMenuChinh(customerTable); }

    @FXML
    private void onToggleFilterClicked() {
        if (filterPanel != null) {
            boolean visible = filterPanel.isVisible();
            filterPanel.setVisible(!visible);
            filterPanel.setManaged(!visible);
        }
    }

    @FXML private void onDeletedViewClicked()  { hienThiLoiLabel("Chức năng thùng rác đang được phát triển."); }
    @FXML private void onExportExcelClicked()  { hienThiLoiLabel("Chức năng xuất Excel đang được phát triển."); }
    @FXML private void onApplyFilterClicked()  { hienThiLoiLabel("Chức năng bộ lọc đang được phát triển."); }

    @FXML
    private void onClearFilterClicked() {
        if (dpFromDate != null)  dpFromDate.setValue(null);
        if (dpToDate != null)    dpToDate.setValue(null);
        if (cbTierFilter != null) cbTierFilter.getSelectionModel().clearSelection();
        if (searchField != null)  searchField.clear();
        presenter.taiDuLieu();
    }

    // ── private helpers ──────────────────────────────────────────────────

    private void setupTableColumns() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMaKH()).asObject());
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoTen()));
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSdt()));
        colAddress.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDiaChi()));
        colRegDate.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getNgayDangKy()));
        colPoints.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDiemTichLuy()));
        colTier.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTenHang() != null ? c.getValue().getTenHang() : "Thành viên"));
    }

    /** Thêm nút "Lịch sử" vào cột Actions. */
    private void setupActionsColumn() {
        if (colActions == null) return;
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnLichSu = new Button("📋 Lịch sử");

            {
                btnLichSu.getStyleClass().add("btn-secondary");
                btnLichSu.setOnAction(e -> {
                    KhachHangDTO kh = getTableView().getItems().get(getIndex());
                    presenter.xemLichSuMuaHang(kh);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnLichSu);
            }
        });
    }

    private void setupDoubleClickEdit() {
        customerTable.setRowFactory(tv -> {
            TableRow<KhachHangDTO> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    moDialogKhachHang(row.getItem());
                }
            });
            return row;
        });
    }

    private void moDialogKhachHang(KhachHangDTO kh) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/KhachHangDialog.fxml"));
            Parent root = loader.load();
            KhachHangDialogViewFXMLController controller = loader.getController();
            if (kh != null) controller.khoiTaoChinhSua(kh);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(kh == null ? "Thêm Khách Hàng" : "Chỉnh sửa Khách Hàng");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.getKetQua() != null) presenter.taiDuLieu();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog: " + e.getMessage());
        }
    }
}
