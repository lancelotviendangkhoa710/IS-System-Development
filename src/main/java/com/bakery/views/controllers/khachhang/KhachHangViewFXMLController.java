package com.bakery.views.controllers.khachhang;

import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.khachhang.HangThanhVienDTO;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.presenters.khachhang.KhachHangPresenter;
import com.bakery.services.nhansu.PhanQuyenService;
import com.bakery.utils.DialogHelper;
import com.bakery.utils.ReportPathUtils;
import com.bakery.utils.UserSession;
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

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class KhachHangViewFXMLController extends BaseController implements KhachHangView {

    @FXML private TableView<KhachHangDTO>          customerTable;
    @FXML private TableColumn<KhachHangDTO, Integer>   colId;
    @FXML private TableColumn<KhachHangDTO, String>    colName;
    @FXML private TableColumn<KhachHangDTO, String>    colPhone;
    @FXML private TableColumn<KhachHangDTO, String>    colAddress;
    @FXML private TableColumn<KhachHangDTO, LocalDate> colRegDate;
    @FXML private TableColumn<KhachHangDTO, String>    colPoints;
    @FXML private TableColumn<KhachHangDTO, String>    colTier;
    @FXML private TableColumn<KhachHangDTO, Void>      colActions;

    @FXML private Label       lblTotalCustomers;
    @FXML private Label       lblNewCustomers;
    @FXML private Label       lblThongBao;
    @FXML private TextField   searchField;
    @FXML private HBox        paginationBox;
    @FXML private Button      btnRefresh;
    @FXML private Button      btnCheDoThungRac;
    @FXML private ComboBox<String> cbTierFilter;
    @FXML private Tab         tabHangThanhVien;

    // Cache toàn bộ danh sách để lọc client-side
    private java.util.List<KhachHangDTO> allCustomers = java.util.Collections.emptyList();
    // Cache danh sách hạng để tính ngưỡng điểm tiếp theo
    private java.util.List<HangThanhVienDTO> tierList = java.util.Collections.emptyList();
    // Trạng thái chế độ thùng rác
    private boolean dangCheDoChuaXoa = false;

    private final KhachHangPresenter presenter    = new KhachHangPresenter(this);
    private final PhanQuyenService   phanQuyenSvc = new PhanQuyenService();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupActionsColumn();
        setupDoubleClickEdit();
        setupSearchListener();
        khoiTaoPhanQuyen();
        presenter.taiDuLieu();
        presenter.taiDanhSachHangThanhVien();
        // Auto-refresh: mỗi 10s tự query DB — khi thu ngân cộng điểm KH, quản lý sẽ thấy ngay
        batDauAutoRefresh(customerTable, () -> presenter.taiDuLieu(), 10);
    }

    // ── KhachHangView interface ──────────────────────────────────────────

    @Override
    public void hienThiDanhSachKhachHang(List<KhachHangDTO> danhSach) {
        allCustomers = danhSach != null ? danhSach : java.util.Collections.emptyList();
        applyFilter();
    }

    private static final String TIER_TAT_CA = "Tất cả hạng";

    /** Nhận List<HangThanhVienDTO> từ Presenter — cập nhật cache và ComboBox lọc. */
    @Override
    public void hienThiDanhSachHang(List<HangThanhVienDTO> dsHang) {
        tierList = dsHang != null ? dsHang : java.util.Collections.emptyList();
        if (cbTierFilter == null) return;
        String current = cbTierFilter.getValue();
        java.util.List<String> tenHangList = tierList.stream()
                .map(HangThanhVienDTO::getTenHang)
                .filter(java.util.Objects::nonNull)
                .sorted()
                .toList();

        // Luôn có "Tất cả hạng" làm item đầu tiên để user có thể reset filter
        java.util.List<String> allItems = new java.util.ArrayList<>();
        allItems.add(TIER_TAT_CA);
        allItems.addAll(tenHangList);
        cbTierFilter.getItems().setAll(allItems);

        // Giữ lại lựa chọn cũ nếu còn hợp lệ
        if (current != null && cbTierFilter.getItems().contains(current)) {
            cbTierFilter.setValue(current);
        } else {
            cbTierFilter.setValue(TIER_TAT_CA);
        }
        // Refresh bảng để cột điểm tính lại ngưỡng thăng hạng
        if (!allCustomers.isEmpty()) applyFilter();
    }

    @Override public void capNhatThongTinPhanTrang(String thongTin) {}
    @Override public void capNhatDieuKhienPhanTrang(int trangHienTai, int tongTrang) {}

    @Override
    public void capNhatTongKhachHang(int tongKhachHang) {
        if (lblTotalCustomers != null) lblTotalCustomers.setText(String.valueOf(tongKhachHang));
    }

    @Override
    public void capNhatKhachHangMoiTrongThang(int soKhachMoi) {
        if (lblNewCustomers != null) lblNewCustomers.setText(String.valueOf(soKhachMoi));
    }

    @Override public void batTatTrangThaiBan(boolean ban) {}

    @Override
    public void hienThiLoi(String tieuDe, String noiDung) {
        hienThiLoiLabel(noiDung);
    }

    @Override
    public void hienThiThanhCong(String tieuDe, String noiDung) {
        hienThiThanhCongLabel(noiDung);
    }

    @Override public void hienThiThongTin(String tieuDe, String noiDung) {}

    /**
     * Cập nhật UI khi chuyển chế độ thùng rác:
     * - Nút đổi nhãn và style
     * - Cột actions hiển thị nút "Khôi phục" thay vì "Lịch sử"
     */
    @Override
    public void capNhatCheDoThungRac(boolean cheDoThungRac) {
        dangCheDoChuaXoa = cheDoThungRac;
        if (btnCheDoThungRac != null) {
            if (cheDoThungRac) {
                btnCheDoThungRac.setText("🗂 Xem tất cả");
                btnCheDoThungRac.getStyleClass().removeAll("btn-secondary");
                btnCheDoThungRac.getStyleClass().add("btn-danger");
            } else {
                btnCheDoThungRac.setText("🗑 Thùng rác");
                btnCheDoThungRac.getStyleClass().removeAll("btn-danger");
                btnCheDoThungRac.getStyleClass().add("btn-secondary");
            }
        }
        // Làm mới cột actions để hiện/ẩn nút Khôi phục
        setupActionsColumn();
        customerTable.refresh();
    }

    /**
     * Mở dialog lịch sử mua hàng dựa trên FXML — kế thừa stylesheet từ Scene gốc.
     */
    @Override
    public void hienThiLichSuMuaHang(KhachHangDTO kh, List<DonDatHangDTO> dsDon) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/khachhang/LichSuMuaHangDialog.fxml"));
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

    @FXML
    private void onSearch() {
        applyFilter();
    }

    @FXML
    private void onTierFilterChanged() {
        applyFilter();
    }

    /** Toggle giữa chế độ thùng rác và chế độ bình thường. */
    @FXML
    private void onDeletedViewClicked() {
        presenter.chuyenCheDoThungRac(!dangCheDoChuaXoa);
    }

    /** Xuất danh sách khách hàng ra Excel — lưu tự động vào thư mục report/. */
    @FXML
    private void onExportExcelClicked() {
        File tepTin = ReportPathUtils.buildExcelPath("DanhSachKhachHang", "KH");
        presenter.xuatExcel(tepTin);
    }

    @FXML
    private void onClearFilterClicked() {
        if (cbTierFilter != null) cbTierFilter.setValue(TIER_TAT_CA);
        if (searchField != null)  searchField.clear();
        applyFilter();
    }

    // ── private helpers ──────────────────────────────────────────────────

    private void setupSearchListener() {
        // onKeyReleased đã wire từ FXML → onSearch()
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMaKH()).asObject());
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoTen()));
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSdt()));
        colAddress.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDiaChi()));
        colRegDate.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getNgayDangKy()));

        // Hiển thị điểm dạng "hiện tại / ngưỡng tiếp theo"
        colPoints.setCellValueFactory(c -> {
            KhachHangDTO kh = c.getValue();
            int diem = kh.getDiemTichLuy() != null ? kh.getDiemTichLuy() : 0;
            int nguong = tierList.stream()
                    .filter(t -> t.getDiemToiThieu() != null && t.getDiemToiThieu() > diem)
                    .mapToInt(t -> t.getDiemToiThieu())
                    .min()
                    .orElse(-1);
            String display = nguong < 0 ? diem + " ★" : diem + "/" + nguong;
            return new SimpleStringProperty(display);
        });

        colTier.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTenHang() != null ? c.getValue().getTenHang() : "Thành viên"));
    }

    /** Ẩn tab Hạng thành viên nếu người dùng không phải Quản lý. */
    private void khoiTaoPhanQuyen() {
        if (tabHangThanhVien == null) return;
        var user = UserSession.getCurrentUser();
        boolean laQuanLy = phanQuyenSvc.laQuanLy(user) || phanQuyenSvc.laAdmin(user);
        tabHangThanhVien.setDisable(!laQuanLy);
        tabHangThanhVien.setStyle(laQuanLy ? "" : "-fx-opacity: 0.4;");
    }

    /** Lọc client-side theo từ khóa tìm kiếm và hạng thành viên. */
    private void applyFilter() {
        String keyword = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String tierRaw = cbTierFilter != null ? cbTierFilter.getValue() : null;
        // "Tất cả hạng" = không lọc theo hạng
        String tier = (tierRaw == null || TIER_TAT_CA.equals(tierRaw)) ? null : tierRaw;

        java.util.List<KhachHangDTO> filtered = allCustomers.stream()
                .filter(kh -> {
                    boolean matchKw = keyword.isEmpty()
                            || (kh.getHoTen()  != null && kh.getHoTen().toLowerCase().contains(keyword))
                            || (kh.getSdt()    != null && kh.getSdt().contains(keyword))
                            || String.valueOf(kh.getMaKH()).contains(keyword);
                    boolean matchTier = tier == null
                            || tier.equals(kh.getTenHang())
                            || (tier.equals("Thành viên") && kh.getTenHang() == null);
                    return matchKw && matchTier;
                })
                .toList();

        customerTable.setItems(FXCollections.observableArrayList(filtered));
        if (filtered.isEmpty()) hienThiLoiLabel("Không tìm thấy khách hàng phù hợp.");
    }

    /**
     * Cột Actions:
     * - Chế độ bình thường: nút "📋 Lịch sử"
     * - Chế độ thùng rác:   nút "♻ Khôi phục" + xác nhận qua Dialog có Amber theme
     */
    private void setupActionsColumn() {
        if (colActions == null) return;
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnHanhDong = new Button();

            {
                btnHanhDong.getStyleClass().add("btn-secondary");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                KhachHangDTO kh = getTableView().getItems().get(getIndex());
                if (dangCheDoChuaXoa) {
                    btnHanhDong.setText("♻ Khôi phục");
                    btnHanhDong.setOnAction(e -> xacNhanKhoiPhuc(kh));
                } else {
                    btnHanhDong.setText("📋 Lịch sử");
                    btnHanhDong.setOnAction(e -> presenter.xemLichSuMuaHang(kh));
                }
                setGraphic(btnHanhDong);
            }
        });
    }

    /** Hiển thị dialog xác nhận trước khi khôi phục khách hàng. */
    private void xacNhanKhoiPhuc(KhachHangDTO kh) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Khôi phục khách hàng \"" + kh.getHoTen() + "\"?\nHọ sẽ xuất hiện lại trong danh sách hoạt động.",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Xác nhận khôi phục");
        DialogHelper.applyBakeryTheme(confirm);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) presenter.khoiPhucKhachHang(kh.getMaKH());
        });
    }

    private void setupDoubleClickEdit() {
        customerTable.setRowFactory(tv -> {
            TableRow<KhachHangDTO> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty() && !dangCheDoChuaXoa) {
                    moDialogKhachHang(row.getItem());
                }
            });
            return row;
        });
    }

    private void moDialogKhachHang(KhachHangDTO kh) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/khachhang/KhachHangDialog.fxml"));
            Parent root = loader.load();
            KhachHangDialogViewFXMLController controller = loader.getController();
            if (kh != null) controller.khoiTaoChinhSua(kh);

            Scene scene = new Scene(root);
            // Load CSS để dialog-header, btn-success, lbl-body-bold hiển thị đúng
            java.net.URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(kh == null ? "Thêm Khách Hàng" : "Chỉnh sửa Khách Hàng");
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.getKetQua() != null) presenter.taiDuLieu();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog: " + e.getMessage());
        }
    }
}
