package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.services.kho.NhaCungCapService;
import com.bakery.utils.DialogHelper;
import com.bakery.utils.SessionContext;
import com.bakery.views.controllers.BaseController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

/**
 * Controller cho màn hình Quản lý Nhà Cung Cấp.
 * Thêm/Sửa qua dialog — không còn inline form.
 */
public class QuanLyNhaCungCapViewFXMLController extends BaseController {

    @FXML private TableView<NhaCungCapDTO>       tvNhaCungCap;
    @FXML private TableColumn<NhaCungCapDTO, String> colMaNCC;
    @FXML private TableColumn<NhaCungCapDTO, String> colTenNCC;
    @FXML private TableColumn<NhaCungCapDTO, String> colSdt;
    @FXML private TableColumn<NhaCungCapDTO, String> colDiaChi;
    @FXML private TextField txtTimKiem;
    @FXML private Button    btnSua;
    @FXML private Button    btnXoa;

    private final NhaCungCapService nhaCungCapService = new NhaCungCapService();
    private final ObservableList<NhaCungCapDTO> nccList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        taiDuLieu();
        // Enable Sửa/Xóa chỉ khi có dòng được chọn
        tvNhaCungCap.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> {
                    boolean coChon = newVal != null;
                    if (btnSua != null) btnSua.setDisable(!coChon);
                    if (btnXoa != null) btnXoa.setDisable(!coChon);
                });
    }

    // ── Setup bảng ─────────────────────────────────────────────────────

    private void setupTable() {
        colMaNCC.setCellValueFactory(c  -> new SimpleStringProperty(String.valueOf(c.getValue().getMaNCC())));
        colTenNCC.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getTenNCC())));
        colSdt.setCellValueFactory(c    -> new SimpleStringProperty(nvl(c.getValue().getSdt())));
        colDiaChi.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getDiaChi())));
        tvNhaCungCap.setItems(nccList);
        tvNhaCungCap.setPlaceholder(new Label("Chưa có nhà cung cấp nào."));

        // Double-click → mở dialog Sửa
        tvNhaCungCap.setRowFactory(tv -> {
            TableRow<NhaCungCapDTO> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) onSua(row.getItem());
            });
            return row;
        });
    }


    // ── Load dữ liệu ───────────────────────────────────────────────────

    private void taiDuLieu() {
        hienThiThanhCongLabel("Đang tải...");
        Thread t = new Thread(() -> {
            try {
                List<NhaCungCapDTO> list = nhaCungCapService.layDanhSachNhaCungCap();
                Platform.runLater(() -> {
                    nccList.setAll(list);
                    hienThiThanhCongLabel("Tải xong — " + list.size() + " nhà cung cấp.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> hienThiLoiLabel("Lỗi tải dữ liệu: " + e.getMessage()));
            }
        }, "ncc-tai-du-lieu");
        t.setDaemon(true);
        t.start();
    }

    // ── Handlers (toolbar) ──────────────────────────────────────────────

    /** Mở dialog Thêm mới. */
    @FXML
    private void onThem() {
        moDialog(null);
    }

    @FXML
    private void onTaiLai() {
        if (txtTimKiem != null) txtTimKiem.clear();
        taiDuLieu();
    }

    @FXML
    private void onTimKiem() {
        String kw = txtTimKiem != null ? txtTimKiem.getText() : "";
        Thread t = new Thread(() -> {
            try {
                List<NhaCungCapDTO> ketQua = nhaCungCapService.timKiemNhaCungCap(kw);
                Platform.runLater(() -> nccList.setAll(ketQua));
            } catch (Exception e) {
                Platform.runLater(() -> hienThiLoiLabel("Lỗi tìm kiếm: " + e.getMessage()));
            }
        }, "ncc-tim-kiem");
        t.setDaemon(true);
        t.start();
    }

    // ── Handlers (row-level) ────────────────────────────────────────────

    /** Mở dialog Sửa với NCC đã chọn. */
    private void onSua(NhaCungCapDTO ncc) {
        moDialog(ncc);
    }

    /** Xác nhận rồi xóa mềm. */
    private void onXoa(NhaCungCapDTO ncc) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa nhà cung cấp: " + ncc.getTenNCC() + "?");
        confirm.setContentText("Dữ liệu lịch sử nhập kho vẫn được giữ nguyên.");
        DialogHelper.applyBakeryTheme(confirm);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) thucHienXoa(ncc);
        });
    }

    private void thucHienXoa(NhaCungCapDTO ncc) {
        int maNV = SessionContext.getInstance().getMaNV();
        Thread t = new Thread(() -> {
            try {
                nhaCungCapService.xoaNhaCungCap(ncc.getMaNCC(), maNV);
                Platform.runLater(() -> {
                    hienThiThanhCongLabel("✅ Đã ngừng giao dịch với \"" + ncc.getTenNCC() + "\".");
                    taiDuLieu();
                });
            } catch (Exception e) {
                Platform.runLater(() -> hienThiLoiLabel("Lỗi: " + e.getMessage()));
            }
        }, "ncc-xoa");
        t.setDaemon(true);
        t.start();
    }

    // ── Dialog helper ────────────────────────────────────────────────────

    /**
     * Mở dialog Thêm (ncc = null) hoặc Sửa (ncc != null).
     * Sau khi user nhấn Lưu → lưu xuống DB rồi reload bảng.
     */
    private void moDialog(NhaCungCapDTO ncc) {
        try {
            URL url = getClass().getResource("/fxml/kho/NhaCungCapDialog.fxml");
            if (url == null) throw new RuntimeException("Không tìm thấy NhaCungCapDialog.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            URL css = getClass().getResource("/css/bakery.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            NhaCungCapDialogController ctrl = loader.getController();
            if (ncc != null) ctrl.khoiTaoSua(ncc);

            Stage stage = new Stage();
            stage.setTitle(ncc == null ? "H3K Bakery — Thêm Nhà Cung Cấp" : "H3K Bakery — Sửa Nhà Cung Cấp");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tvNhaCungCap.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

            NhaCungCapDTO ketQua = ctrl.getKetQua();
            if (ketQua == null) return; // user bấm Hủy

            // Lưu xuống DB
            Thread t = new Thread(() -> {
                try {
                    if (ncc == null) {
                        nhaCungCapService.themNhaCungCap(ketQua);
                        Platform.runLater(() -> {
                            hienThiThanhCongLabel("✅ Thêm nhà cung cấp thành công.");
                            taiDuLieu();
                        });
                    } else {
                        nhaCungCapService.suaNhaCungCap(ketQua);
                        Platform.runLater(() -> {
                            hienThiThanhCongLabel("✅ Cập nhật nhà cung cấp thành công.");
                            taiDuLieu();
                        });
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> hienThiLoiLabel("Lỗi: " + e.getMessage()));
                }
            }, ncc == null ? "ncc-them" : "ncc-sua");
            t.setDaemon(true);
            t.start();

        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog: " + e.getMessage());
        }
    }

    /** Mở dialog Sửa với NCC đang chọn trên bảng (nút toolbar). */
    @FXML
    private void onSuaAction() {
        NhaCungCapDTO selected = tvNhaCungCap.getSelectionModel().getSelectedItem();
        if (selected != null) onSua(selected);
    }

    /** Xác nhận rồi xóa NCC đang chọn trên bảng (nút toolbar). */
    @FXML
    private void onXoaAction() {
        NhaCungCapDTO selected = tvNhaCungCap.getSelectionModel().getSelectedItem();
        if (selected != null) onXoa(selected);
    }

    private static String nvl(String s) { return s != null ? s : ""; }
}
