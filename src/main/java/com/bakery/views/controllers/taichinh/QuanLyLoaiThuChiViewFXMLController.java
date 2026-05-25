package com.bakery.views.controllers.taichinh;

import com.bakery.model.dto.hethong.LoaiThuChiDTO;
import com.bakery.services.hethong.SoQuyService;
import com.bakery.utils.DialogHelper;
import com.bakery.utils.SessionContext;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

/**
 * Controller màn hình Quản lý Loại Thu Chi (UC53–UC55).
 * Design pattern: giống NguyenLieuViewFXMLController.
 * Thêm / Sửa qua dialog. Xóa = vô hiệu hóa (soft-delete).
 */
public class QuanLyLoaiThuChiViewFXMLController extends BaseController {

    // ── Table ──────────────────────────────────────────────────────────────────
    @FXML private TableView<LoaiThuChiDTO>        tblLoaiThuChi;
    @FXML private TableColumn<LoaiThuChiDTO, String> colMa;
    @FXML private TableColumn<LoaiThuChiDTO, String> colTenLoai;
    @FXML private TableColumn<LoaiThuChiDTO, String> colPhanLoai;
    @FXML private TableColumn<LoaiThuChiDTO, String> colTrangThai;

    // ── Toolbar ────────────────────────────────────────────────────────────────
    @FXML private TextField          txtTimKiem;
    @FXML private ComboBox<String>   cmbLocTrangThai;
    @FXML private ComboBox<String>   cmbLocPhanLoai;
    @FXML private Button             btnThemMoi;
    @FXML private Button             btnSua;
    @FXML private Button             btnVoHieuHoa;

    // ── Footer ─────────────────────────────────────────────────────────────────
    @FXML private Label lblSoLuong;

    // ── State ──────────────────────────────────────────────────────────────────
    private final SoQuyService soQuySvc = new SoQuyService();
    private final ObservableList<LoaiThuChiDTO> masterData   = FXCollections.observableArrayList();
    private FilteredList<LoaiThuChiDTO>         filteredData;

    // ── Init ───────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        caiDatBangLoai();
        caiDatBoLoc();
        caiDatChonHang();
        taiDuLieu();
        batDauAutoRefresh(tblLoaiThuChi, this::taiDuLieu, 15);
    }

    // ── Cài đặt bảng ──────────────────────────────────────────────────────────

    private void caiDatBangLoai() {
        colMa.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("LTC%02d", c.getValue().getMaLoaiThuChi())));

        colTenLoai.setCellValueFactory(c ->
                new SimpleStringProperty(nvl(c.getValue().getTenLoaiThuChi())));

        colPhanLoai.setCellValueFactory(c ->
                new SimpleStringProperty(nvl(c.getValue().getPhanLoai())));
        colPhanLoai.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                badge.getStyleClass().addAll("badge",
                        "Thu".equals(item) ? "badge-done" : "badge-processing");
                setGraphic(badge);
                setText(null);
            }
        });

        colTrangThai.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getThoiDiemXoa() == null ? "Đang dùng" : "Đã khoá"));
        colTrangThai.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                badge.getStyleClass().addAll("badge",
                        "Đang dùng".equals(item) ? "badge-done" : "badge-cancelled");
                setGraphic(badge);
                setText(null);
            }
        });

        filteredData = new FilteredList<>(masterData, p -> true);
        tblLoaiThuChi.setItems(filteredData);
        tblLoaiThuChi.setPlaceholder(new Label("Chưa có hạng mục nào."));

        // Double-click → sửa
        tblLoaiThuChi.setRowFactory(tv -> {
            TableRow<LoaiThuChiDTO> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty())
                    moDialogSua(row.getItem());
            });
            return row;
        });
    }

    // ── Cài đặt bộ lọc ────────────────────────────────────────────────────────

    private void caiDatBoLoc() {
        cmbLocTrangThai.setItems(FXCollections.observableArrayList("Tất cả", "Đang dùng", "Đã khoá"));
        cmbLocTrangThai.getSelectionModel().selectFirst();

        cmbLocPhanLoai.setItems(FXCollections.observableArrayList("Tất cả", "Thu", "Chi"));
        cmbLocPhanLoai.getSelectionModel().selectFirst();
    }

    // ── Cài đặt chọn hàng ─────────────────────────────────────────────────────

    private void caiDatChonHang() {
        tblLoaiThuChi.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean coChon = newVal != null;
            btnSua.setDisable(!coChon);
            if (coChon) {
                boolean dangKhoa = newVal.getThoiDiemXoa() != null;
                btnVoHieuHoa.setDisable(false);
                btnVoHieuHoa.setText(dangKhoa ? "🔓 Mở khoá" : "🔒 Vô hiệu hóa");
                btnVoHieuHoa.getStyleClass().removeAll("btn-danger", "btn-success");
                btnVoHieuHoa.getStyleClass().add(dangKhoa ? "btn-success" : "btn-danger");
            } else {
                btnVoHieuHoa.setDisable(true);
                btnVoHieuHoa.setText("🔒 Vô hiệu hóa");
                btnVoHieuHoa.getStyleClass().removeAll("btn-success");
                if (!btnVoHieuHoa.getStyleClass().contains("btn-danger"))
                    btnVoHieuHoa.getStyleClass().add("btn-danger");
            }
        });
    }

    // ── Tải dữ liệu ───────────────────────────────────────────────────────────

    private void taiDuLieu() {
        Thread t = new Thread(() -> {
            try {
                List<LoaiThuChiDTO> ds = soQuySvc.layTatCaDanhSachLoai();
                javafx.application.Platform.runLater(() -> {
                    masterData.setAll(ds);
                    apDungBoLoc();
                    capNhatFooter();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel("Lỗi tải dữ liệu: " + e.getMessage()));
            }
        }, "loai-thu-chi-tai");
        t.setDaemon(true);
        t.start();
    }

    // ── Áp dụng bộ lọc ────────────────────────────────────────────────────────

    private void apDungBoLoc() {
        String tuKhoa    = txtTimKiem != null ? txtTimKiem.getText().trim().toLowerCase() : "";
        String trangThai = cmbLocTrangThai.getValue();
        String phanLoai  = cmbLocPhanLoai.getValue();

        filteredData.setPredicate(dto -> {
            // Lọc tên
            if (!tuKhoa.isEmpty()) {
                String ten = nvl(dto.getTenLoaiThuChi()).toLowerCase();
                if (!ten.contains(tuKhoa)) return false;
            }
            // Lọc trạng thái
            if ("Đang dùng".equals(trangThai) && dto.getThoiDiemXoa() != null) return false;
            if ("Đã khoá".equals(trangThai) && dto.getThoiDiemXoa() == null)  return false;
            // Lọc phân loại
            if (!"Tất cả".equals(phanLoai) && !phanLoai.equals(dto.getPhanLoai())) return false;
            return true;
        });
        capNhatFooter();
    }

    private void capNhatFooter() {
        if (lblSoLuong != null)
            lblSoLuong.setText("Hiển thị " + filteredData.size()
                    + " / " + masterData.size() + " hạng mục");
    }

    // ── Handlers FXML ─────────────────────────────────────────────────────────

    @FXML
    private void onTimKiem() {
        apDungBoLoc();
    }

    @FXML
    private void onLocTrangThai() {
        apDungBoLoc();
    }

    @FXML
    private void onLocPhanLoai() {
        apDungBoLoc();
    }

    @FXML
    private void onLamMoi() {
        if (txtTimKiem != null) txtTimKiem.clear();
        cmbLocTrangThai.getSelectionModel().selectFirst();
        cmbLocPhanLoai.getSelectionModel().selectFirst();
        tblLoaiThuChi.getSelectionModel().clearSelection();
        taiDuLieu();
    }

    @FXML
    private void onThemMoi() {
        moDialog(null);
    }

    @FXML
    private void onSuaAction() {
        moDialogSua(tblLoaiThuChi.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onVoHieuHoaAction() {
        LoaiThuChiDTO sel = tblLoaiThuChi.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        boolean dangKhoa = sel.getThoiDiemXoa() != null;
        if (dangKhoa) {
            // Mở khoá
            xacNhanMoKhoa(sel);
        } else {
            // Vô hiệu hóa
            xacNhanVoHieuHoa(sel);
        }
    }

    // ── Dialog Thêm / Sửa ─────────────────────────────────────────────────────

    private void moDialogSua(LoaiThuChiDTO dto) {
        if (dto == null) return;
        moDialog(dto);
    }

    private void moDialog(LoaiThuChiDTO existing) {
        try {
            URL url = getClass().getResource("/fxml/taichinh/ThemSuaLoaiThuChiDialog.fxml");
            if (url == null) throw new RuntimeException("Không tìm thấy ThemSuaLoaiThuChiDialog.fxml");

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            ThemSuaLoaiThuChiDialogController ctrl = loader.getController();
            ctrl.khoiTao(existing);

            Scene scene = new Scene(root);
            URL css = getClass().getResource("/css/bakery.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            Stage stage = new Stage();
            stage.setTitle(existing == null
                    ? "H3K Bakery — Thêm hạng mục mới"
                    : "H3K Bakery — Sửa hạng mục");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tblLoaiThuChi.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

            if (!ctrl.isConfirmed()) return;

            String ten      = ctrl.getTenLoai();
            String phanLoai = ctrl.getPhanLoai();

            runAsync(() -> {
                if (existing == null) {
                    soQuySvc.themLoai(ten, phanLoai);
                } else {
                    soQuySvc.suaLoai(existing.getMaLoaiThuChi(), ten, phanLoai);
                }
            }, existing == null
                    ? "Đã thêm hạng mục \"" + ten + "\"."
                    : "Đã cập nhật hạng mục \"" + ten + "\".");

        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog: " + e.getMessage());
        }
    }

    // ── Xác nhận vô hiệu hóa / mở khoá ───────────────────────────────────────

    private void xacNhanVoHieuHoa(LoaiThuChiDTO dto) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận vô hiệu hóa");
        confirm.setHeaderText("Vô hiệu hóa hạng mục: \"" + dto.getTenLoaiThuChi() + "\"?");
        confirm.setContentText("Hạng mục sẽ không xuất hiện trong danh sách lập phiếu mới.\n"
                + "Các phiếu cũ vẫn được giữ nguyên.");
        DialogHelper.applyBakeryTheme(confirm);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                int maNV = SessionContext.getInstance().getMaNV();
                runAsync(() -> soQuySvc.xoaLoai(dto.getMaLoaiThuChi(), maNV),
                        "Đã vô hiệu hóa hạng mục \"" + dto.getTenLoaiThuChi() + "\".");
            }
        });
    }

    private void xacNhanMoKhoa(LoaiThuChiDTO dto) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận mở khoá");
        confirm.setHeaderText("Mở khoá hạng mục: \"" + dto.getTenLoaiThuChi() + "\"?");
        confirm.setContentText("Hạng mục sẽ xuất hiện trở lại trong danh sách lập phiếu.");
        DialogHelper.applyBakeryTheme(confirm);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                runAsync(() -> soQuySvc.moKhoaLoai(dto.getMaLoaiThuChi()),
                        "Đã mở khoá hạng mục \"" + dto.getTenLoaiThuChi() + "\".");
            }
        });
    }

    // ── Async helper ──────────────────────────────────────────────────────────

    private void runAsync(ThrowingRunnable task, String successMsg) {
        Thread t = new Thread(() -> {
            try {
                task.run();
                javafx.application.Platform.runLater(() -> {
                    hienThiThanhCongLabel(successMsg);
                    taiDuLieu();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel("Lỗi: " + e.getMessage()));
            }
        }, "loai-thu-chi-async");
        t.setDaemon(true);
        t.start();
    }

    @FunctionalInterface
    private interface ThrowingRunnable { void run() throws Exception; }

    private static String nvl(String s) { return s != null ? s : ""; }
}
