package com.bakery.views.controllers.khachhang;

import com.bakery.model.dto.khachhang.HangThanhVienDTO;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.presenters.khachhang.KhachHangPresenter;
import com.bakery.services.khachhang.CustomerTierService;
import com.bakery.utils.SessionManager;
import com.bakery.views.controllers.BaseController;
import com.bakery.views.controllers.DefaultViewFactory;
import com.bakery.views.interfaces.khachhang.KhachHangView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KhachHangViewFXMLController extends BaseController implements KhachHangView {

    private static final Logger LOGGER = Logger.getLogger(KhachHangViewFXMLController.class.getName());

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
    private TableColumn<KhachHangDTO, java.time.LocalDate> colRegDate;
    @FXML
    private TableColumn<KhachHangDTO, Integer> colPoints;
    @FXML
    private TableColumn<KhachHangDTO, String> colTier;
    @FXML
    private TableColumn<KhachHangDTO, Void> colActions;
    @FXML
    private Label lblTotalCustomers;
    @FXML
    private Label lblNewCustomers;
    @FXML
    private Label lblPageInfo;
    @FXML
    private TextField searchField;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnCheDoThungRac;
    @FXML
    private HBox paginationBox;
    @FXML
    private Button btnToggleFilter;
    @FXML
    private HBox filterPanel;
    @FXML
    private DatePicker dpFromDate;
    @FXML
    private DatePicker dpToDate;
    @FXML
    private ComboBox<String> cbTierFilter;

    private final KhachHangPresenter presenter = new KhachHangPresenter(this);
    private final DefaultViewFactory viewFactory = new DefaultViewFactory(null);
    private final List<HangThanhVienDTO> danhSachHang = new ArrayList<>();

    private boolean cheDoThungRac;

    @FXML
    public void initialize() {
        cheDoThungRac = false;
        setupTableColumns();
        customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        customerTable.setFixedCellSize(36);
        bindEvents();
        taiBoLocHang();
        presenter.chuyenCheDoThungRac(false);
    }

    @FXML
    private void onRefreshClicked() {
        taiBoLocHang();
        presenter.taiDuLieu();
    }

    @FXML
    private void onAddCustomerClicked() {
        Optional<ThongTinNhapKhachHang> ketQua = hienThiDialogNhapKhachHang("Thêm khách hàng", null);
        ketQua.ifPresent(thongTin -> presenter.themKhachHang(thongTin.hoTen(), thongTin.sdt(), thongTin.diaChi()));
    }

    @FXML
    private void onDeletedViewClicked() {
        cheDoThungRac = !cheDoThungRac;
        presenter.chuyenCheDoThungRac(cheDoThungRac);
    }

    @FXML
    private void onExportExcelClicked() {
        File tep = chonTepExcel();
        if (tep != null) {
            presenter.xuatExcel(tep);
        }
    }

    @FXML
    private void onTierManagementClicked() {
        viewFactory.openMembershipTierDialog(this::taiBoLocHang);
    }

    @FXML
    private void onToggleFilterClicked() {
        boolean visible = !filterPanel.isVisible();
        filterPanel.setVisible(visible);
        filterPanel.setManaged(visible);
    }

    @FXML
    private void onApplyFilterClicked() {
        String tenHang = cbTierFilter.getValue();
        if ("Tất cả".equals(tenHang)) {
            tenHang = null;
        }
        presenter.loc(dpFromDate.getValue(), dpToDate.getValue(), tenHang);
    }

    @FXML
    private void onClearFilterClicked() {
        dpFromDate.setValue(null);
        dpToDate.setValue(null);
        cbTierFilter.setValue("Tất cả");
        presenter.loc(null, null, null);
    }

    @Override
    public void hienThiDanhSachKhachHang(List<KhachHangDTO> danhSach) {
        customerTable.setItems(FXCollections.observableArrayList(danhSach));
        customerTable.refresh();
    }

    @Override
    public void capNhatThongTinPhanTrang(String thongTin) {
        lblPageInfo.setText(thongTin);
    }

    @Override
    public void capNhatDieuKhienPhanTrang(int trangHienTai, int tongTrang) {
        paginationBox.getChildren().clear();

        Button btnPrev = new Button("◀");
        btnPrev.getStyleClass().add("pagination-button");
        btnPrev.setDisable(trangHienTai <= 1);
        btnPrev.setOnAction(event -> presenter.chuyenTrang(trangHienTai - 1));
        paginationBox.getChildren().add(btnPrev);

        int batDau = Math.max(1, trangHienTai - 2);
        int ketThuc = Math.min(tongTrang, trangHienTai + 2);
        for (int page = batDau; page <= ketThuc; page++) {
            Button btnPage = new Button(String.valueOf(page));
            btnPage.getStyleClass().add(page == trangHienTai ? "pagination-button-active" : "pagination-button");
            int dich = page;
            btnPage.setOnAction(event -> presenter.chuyenTrang(dich));
            paginationBox.getChildren().add(btnPage);
        }

        Button btnNext = new Button("▶");
        btnNext.getStyleClass().add("pagination-button");
        btnNext.setDisable(trangHienTai >= tongTrang);
        btnNext.setOnAction(event -> presenter.chuyenTrang(trangHienTai + 1));
        paginationBox.getChildren().add(btnNext);
    }

    @Override
    public void capNhatTongKhachHang(int tongKhachHang) {
        lblTotalCustomers.setText(String.valueOf(tongKhachHang));
    }

    @Override
    public void capNhatKhachHangMoiTrongThang(int soKhachMoi) {
        lblNewCustomers.setText(String.valueOf(soKhachMoi));
    }

    @Override
    public void batTatTrangThaiBan(boolean ban) {
        customerTable.setDisable(ban);
        searchField.setDisable(ban);
        btnRefresh.setDisable(ban);
        btnToggleFilter.setDisable(ban);
        paginationBox.setDisable(ban);
    }

    @Override
    public void hienThiLoi(String tieuDe, String noiDung) {
        hienThiThongBaoLoi(tieuDe, noiDung);
    }

    @Override
    public void hienThiThanhCong(String tieuDe, String noiDung) {
        hienThiThongTin(tieuDe, noiDung);
    }

    @Override
    public void hienThiThongTin(String tieuDe, String noiDung) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    @Override
    public void capNhatCheDoThungRac(boolean cheDoThungRac) {
        this.cheDoThungRac = cheDoThungRac;
        if (btnCheDoThungRac != null) {
            btnCheDoThungRac.setText(cheDoThungRac ? "📋 Danh sách chính" : "🗑 Thùng rác");
        }
        customerTable.refresh();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getMaKH()).asObject());
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getHoTen()));
        colPhone.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSdt()));
        colAddress.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDiaChi()));
        colRegDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getNgayDangKy()));

        colPoints.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDiemTichLuy()));
        colPoints.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || cheDoThungRac) {
                    setGraphic(empty ? null : new Label(item == null ? "" : String.valueOf(item)));
                    return;
                }
                int diemHangKe = layDiemHangKeTiep(item);
                double tienDo = diemHangKe <= 0 ? 1.0 : Math.min((double) item / diemHangKe, 1.0);
                ProgressBar progressBar = new ProgressBar(tienDo);
                progressBar.setPrefWidth(80);
                Label lbl = new Label(String.valueOf(item));
                HBox box = new HBox(6, lbl, progressBar);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });

        colTier.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTenHang() == null ? "-" : cell.getValue().getTenHang()));
        colActions.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                KhachHangDTO kh = getTableRow().getItem();
                if (kh == null) {
                    setGraphic(null);
                    return;
                }

                if (cheDoThungRac) {
                    Button btnRestore = new Button("Khôi phục");
                    btnRestore.setOnAction(event -> presenter.khoiPhucKhachHang(kh.getMaKH()));
                    setGraphic(btnRestore);
                    return;
                }

                Button btnSua = new Button("Sửa");
                btnSua.setOnAction(event -> {
                    Optional<ThongTinNhapKhachHang> ketQua = hienThiDialogNhapKhachHang("Cập nhật khách hàng", kh);
                    ketQua.ifPresent(thongTin -> presenter.capNhatKhachHang(kh.getMaKH(), thongTin.hoTen(), thongTin.sdt(), thongTin.diaChi()));
                });

                Button btnXoa = new Button("Xóa");
                btnXoa.setOnAction(event -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Xóa khách hàng \"" + kh.getHoTen() + "\"?", ButtonType.OK, ButtonType.CANCEL);
                    if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        presenter.xoaKhachHang(kh.getMaKH(), SessionManager.getCurrentEmployeeId());
                    }
                });

                HBox box = new HBox(6, btnSua, btnXoa);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });
    }

    private void bindEvents() {
        searchField.textProperty().addListener((obs, oldValue, newValue) -> presenter.timKiem(newValue));
    }

    private Optional<ThongTinNhapKhachHang> hienThiDialogNhapKhachHang(String tieuDe, KhachHangDTO current) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(tieuDe);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        TextField txtHoTen = new TextField(current == null ? "" : macDinhRong(current.getHoTen()));
        TextField txtSdt = new TextField(current == null ? "" : macDinhRong(current.getSdt()));
        TextField txtDiaChi = new TextField(current == null ? "" : macDinhRong(current.getDiaChi()));

        grid.add(new Label("Họ tên:"), 0, 0);
        grid.add(txtHoTen, 1, 0);
        grid.add(new Label("SĐT:"), 0, 1);
        grid.add(txtSdt, 1, 1);
        grid.add(new Label("Địa chỉ:"), 0, 2);
        grid.add(txtDiaChi, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btnOk.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!hopLeThongTin(txtHoTen.getText(), txtSdt.getText(), txtDiaChi.getText())) {
                event.consume();
            }
        });

        Optional<ButtonType> ketQua = dialog.showAndWait();
        if (ketQua.isEmpty() || ketQua.get() != ButtonType.OK) {
            return Optional.empty();
        }
        return Optional.of(new ThongTinNhapKhachHang(
                txtHoTen.getText().trim(),
                txtSdt.getText().trim(),
                txtDiaChi.getText().trim()
        ));
    }

    private boolean hopLeThongTin(String hoTen, String sdt, String diaChi) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            hienThiLoi("Lỗi nhập liệu", "Họ tên không được để trống.");
            return false;
        }
        if (hoTen.trim().length() > 100) {
            hienThiLoi("Lỗi nhập liệu", "Họ tên tối đa 100 ký tự.");
            return false;
        }
        if (sdt == null || !sdt.trim().matches("^\\d{10}$")) {
            hienThiLoi("Lỗi nhập liệu", "SĐT phải đúng 10 chữ số.");
            return false;
        }
        if (diaChi != null && diaChi.trim().length() > 255) {
            hienThiLoi("Lỗi nhập liệu", "Địa chỉ tối đa 255 ký tự.");
            return false;
        }
        return true;
    }

    private int layDiemHangKeTiep(int diem) {
        for (HangThanhVienDTO hang : danhSachHang) {
            if (hang.getDiemToiThieu() != null && hang.getDiemToiThieu() > diem) {
                return hang.getDiemToiThieu();
            }
        }
        return -1;
    }

    private void taiBoLocHang() {
        try {
            CustomerTierService customerTierService = new CustomerTierService();
            danhSachHang.clear();
            danhSachHang.addAll(customerTierService.getAllTiers());
            danhSachHang.sort(Comparator.comparingInt(HangThanhVienDTO::getDiemToiThieu));

            List<String> tenHang = new ArrayList<>();
            tenHang.add("Tất cả");
            for (HangThanhVienDTO hang : danhSachHang) {
                tenHang.add(hang.getTenHang());
            }

            String current = cbTierFilter.getValue();
            cbTierFilter.setItems(FXCollections.observableArrayList(tenHang));
            if (current != null && tenHang.contains(current)) {
                cbTierFilter.setValue(current);
            } else {
                cbTierFilter.setValue("Tất cả");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Khong tai duoc danh sach hang thanh vien", e);
            hienThiLoi("Lỗi", "Không tải được danh sách hạng thành viên.");
        }
    }

    private File chonTepExcel() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Lưu Excel");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fc.setInitialFileName("khach_hang_" + java.time.LocalDate.now() + ".xlsx");
        return fc.showSaveDialog(customerTable.getScene().getWindow());
    }

    private String macDinhRong(String value) {
        return value == null ? "" : value;
    }

    private record ThongTinNhapKhachHang(String hoTen, String sdt, String diaChi) {}
}
