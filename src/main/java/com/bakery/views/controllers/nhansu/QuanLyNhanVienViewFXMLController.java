package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.nhansu.NhanVienService;
import com.bakery.utils.DialogHelper;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Comparator;
import java.util.List;

/**
 * Controller cho QuanLyNhanVienView (tab content trong NhanSuView).
 * Layout theo khuôn SanPhamView: toolbar nút hành động trên bảng, không có cột inline.
 * Thêm/Sửa qua ThemNhanVienDialog.fxml (dual-mode).
 */
public class QuanLyNhanVienViewFXMLController extends BaseController {

    @FXML private TableView<NhanVienDTO>           tblNhanVien;
    @FXML private TableColumn<NhanVienDTO, Integer> colMaNV;
    @FXML private TableColumn<NhanVienDTO, String>  colHoTen;
    @FXML private TableColumn<NhanVienDTO, String>  colSdt;
    @FXML private TableColumn<NhanVienDTO, String>  colVaiTro;
    @FXML private TableColumn<NhanVienDTO, String>  colTenDangNhap;
    @FXML private TableColumn<NhanVienDTO, String>  colTrangThai;

    @FXML private TextField       txtTimKiem;
    @FXML private ComboBox<String> cmbLocTrangThai;
    @FXML private Button           btnSua;
    @FXML private Button           btnThoiViec;

    private final NhanVienService             nhanVienService = new NhanVienService();
    private final ObservableList<NhanVienDTO> masterData      = FXCollections.observableArrayList();
    private FilteredList<NhanVienDTO>         filteredData;

    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        setupToolbarButtons();
        loadData();
        // Auto-refresh mỗi 30 giây
        batDauAutoRefresh(tblNhanVien, this::loadData, 30);
    }

    // ── Setup ──────────────────────────────────────────────────────────────

    private void setupTable() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        colVaiTro.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenVaiTroHienThi()));
        colTenDangNhap.setCellValueFactory(new PropertyValueFactory<>("tenDangNhap"));
        colTrangThai.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTrangThaiLamViec() == 1 ? "✅ Đang làm việc" : "🔴 Đã thôi việc"));

        // Double-click → mở dialog Sửa
        tblNhanVien.setRowFactory(tv -> {
            TableRow<NhanVienDTO> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) moDialogSua(row.getItem());
            });
            return row;
        });
    }

    /** Bind nút toolbar theo item được chọn trong bảng. */
    private void setupToolbarButtons() {
        // Mặc định disable, chỉ enable khi có hàng được chọn
        btnSua.disableProperty().bind(
                tblNhanVien.getSelectionModel().selectedItemProperty().isNull()
        );

        // Thôi việc: disable khi chưa chọn HOẶC NV đã thôi việc rồi
        tblNhanVien.getSelectionModel().selectedItemProperty().addListener((obs, oldNv, newNv) -> {
            if (newNv == null) {
                btnThoiViec.setDisable(true);
            } else {
                btnThoiViec.setDisable(newNv.getTrangThaiLamViec() == 0);
            }
        });
    }

    private void setupFilters() {
        cmbLocTrangThai.setItems(FXCollections.observableArrayList("Tất cả", "Đang làm việc", "Đã thôi việc"));
        cmbLocTrangThai.getSelectionModel().selectFirst();

        filteredData = new FilteredList<>(masterData, p -> true);
        tblNhanVien.setItems(filteredData);

        txtTimKiem.textProperty().addListener((obs, o, v) -> applyFilter());
        cmbLocTrangThai.getSelectionModel().selectedItemProperty().addListener((obs, o, v) -> applyFilter());
    }

    private void applyFilter() {
        String keyword   = txtTimKiem.getText() == null ? "" : txtTimKiem.getText().trim().toLowerCase();
        String trangThai = cmbLocTrangThai.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(nv -> {
            boolean matchKw = keyword.isBlank()
                    || nv.getHoTen().toLowerCase().contains(keyword)
                    || nv.getSdt().contains(keyword)
                    || nv.getTenDangNhap().toLowerCase().contains(keyword);

            boolean matchTS = switch (trangThai == null ? "Tất cả" : trangThai) {
                case "Đang làm việc" -> nv.getTrangThaiLamViec() == 1;
                case "Đã thôi việc"  -> nv.getTrangThaiLamViec() == 0;
                default              -> true;
            };
            return matchKw && matchTS;
        });
    }

    // ── Load data ──────────────────────────────────────────────────────────

    private void loadData() {
        try {
            List<NhanVienDTO> list = nhanVienService.layTatCaNhanVien();
            if (list == null || list.isEmpty()) {
                masterData.clear();
                hienThiLoiLabel("Chưa có nhân viên nào trong hệ thống.");
            } else {
                list.sort(Comparator.comparing(NhanVienDTO::getMaNV));
                masterData.setAll(list);
                hienThiThanhCongLabel("Đã tải " + masterData.size() + " nhân viên.");
            }
            applyFilter();
        } catch (Exception e) {
            masterData.clear();
            hienThiLoiLabel("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // ── FXML handlers ──────────────────────────────────────────────────────

    @FXML private void onThemMoi()  { moDialog(null); }
    @FXML private void onLamMoi()   { tblNhanVien.getSelectionModel().clearSelection(); loadData(); }

    /** Sửa NV đang được chọn trong bảng. */
    @FXML
    private void onSua() {
        NhanVienDTO selected = tblNhanVien.getSelectionModel().getSelectedItem();
        if (selected != null) moDialogSua(selected);
    }

    /** Cho thôi việc NV đang được chọn trong bảng. */
    @FXML
    private void onThoiViec() {
        NhanVienDTO selected = tblNhanVien.getSelectionModel().getSelectedItem();
        if (selected != null) xuLyChoThoiViec(selected);
    }

    // ── Dialog helpers ─────────────────────────────────────────────────────

    /** Mở dialog Thêm mới (nv=null) hoặc Sửa (nv != null). */
    private void moDialog(NhanVienDTO nv) {
        try {
            URL url = getClass().getResource("/fxml/nhansu/ThemNhanVienDialog.fxml");
            if (url == null) throw new RuntimeException("Không tìm thấy ThemNhanVienDialog.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            URL css = getClass().getResource("/css/bakery.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            ThemNhanVienDialogController ctrl = loader.getController();
            if (nv != null) ctrl.khoiTaoSua(nv);
            ctrl.setOnThemThanhCong(() -> {
                loadData();
                hienThiThanhCongLabel(nv == null ? "✅ Đã thêm nhân viên mới." : "✅ Đã cập nhật nhân viên.");
            });

            Stage dialog = new Stage();
            dialog.setTitle(nv == null ? "H3K Bakery — Thêm nhân viên" : "H3K Bakery — Sửa nhân viên");
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(tblNhanVien.getScene().getWindow());
            dialog.setResizable(false);
            dialog.showAndWait();
        } catch (Exception e) {
            hienThiLoiLabel("❌ Lỗi mở dialog: " + e.getMessage());
        }
    }

    private void moDialogSua(NhanVienDTO nv) { moDialog(nv); }

    /** Xác nhận và thực hiện cho thôi việc. */
    private void xuLyChoThoiViec(NhanVienDTO nv) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Cho nhân viên \"" + nv.getHoTen() + "\" thôi việc?\n" +
                "Tài khoản đăng nhập sẽ bị khóa nhưng lịch sử được giữ lại.",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận cho thôi việc");
        DialogHelper.applyBakeryTheme(confirm);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    nhanVienService.thoiViec(nv.getMaNV());
                    hienThiThanhCongLabel("✅ Đã cho nhân viên thôi việc thành công.");
                    loadData();
                } catch (Exception e) {
                    hienThiLoiLabel("❌ Lỗi: " + e.getMessage());
                }
            }
        });
    }
}
