package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.services.kho.NhaCungCapService;
import com.bakery.utils.SessionContext;
import com.bakery.views.controllers.BaseController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller cho màn hình Quản lý Nhà Cung Cấp.
 * CRUD đầy đủ + tìm kiếm realtime.
 * Mọi dữ liệu lấy từ DB qua NhaCungCapService — không có mock.
 */
public class QuanLyNhaCungCapViewFXMLController extends BaseController {

    @FXML private TableView<NhaCungCapDTO> tvNhaCungCap;
    @FXML private TableColumn<NhaCungCapDTO, String> colMaNCC;
    @FXML private TableColumn<NhaCungCapDTO, String> colTenNCC;
    @FXML private TableColumn<NhaCungCapDTO, String> colSdt;
    @FXML private TableColumn<NhaCungCapDTO, String> colDiaChi;

    @FXML private TextField txtMaNCC;
    @FXML private TextField txtTenNCC;
    @FXML private TextField txtSdt;
    @FXML private TextArea  txtDiaChi;
    @FXML private TextField txtTimKiem;

    @FXML private Button btnThem;
    @FXML private Button btnLuu;
    @FXML private Button btnHuy;
    @FXML private Button btnXoa;

    private final NhaCungCapService nhaCungCapService = new NhaCungCapService();
    private final ObservableList<NhaCungCapDTO> nccList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        taiDuLieu();
        // Chọn hàng → hiển thị chi tiết, bật nút Lưu/Xóa
        tvNhaCungCap.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> hienThiChiTiet(newVal));
        capNhatTrangThai(false);
    }

    // ── Setup ──────────────────────────────────────────────────────────

    private void setupTable() {
        colMaNCC.setCellValueFactory(c  -> new SimpleStringProperty(String.valueOf(c.getValue().getMaNCC())));
        colTenNCC.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenNCC()));
        colSdt.setCellValueFactory(c    -> new SimpleStringProperty(nvl(c.getValue().getSdt())));
        colDiaChi.setCellValueFactory(c -> new SimpleStringProperty(nvl(c.getValue().getDiaChi())));
        tvNhaCungCap.setItems(nccList);
        tvNhaCungCap.setPlaceholder(new Label("Chưa có nhà cung cấp nào."));
    }

    // ── Load dữ liệu (background thread) ───────────────────────────────

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

    // ── Hiển thị chi tiết ──────────────────────────────────────────────

    private void hienThiChiTiet(NhaCungCapDTO ncc) {
        if (ncc == null) {
            xoaTrang();
            capNhatTrangThai(false);
            return;
        }
        txtMaNCC.setText(String.valueOf(ncc.getMaNCC()));
        txtTenNCC.setText(nvl(ncc.getTenNCC()));
        txtSdt.setText(nvl(ncc.getSdt()));
        txtDiaChi.setText(nvl(ncc.getDiaChi()));
        capNhatTrangThai(true);
    }

    /** Bật/tắt nút Lưu và Xóa theo trạng thái chọn hàng. */
    private void capNhatTrangThai(boolean daChon) {
        btnLuu.setDisable(!daChon);
        btnXoa.setDisable(!daChon);
    }

    // ── Handlers ────────────────────────────────────────────────────────

    @FXML
    private void onThem() {
        NhaCungCapDTO ncc = layDuLieuTuForm();
        if (ncc == null) return;

        Thread t = new Thread(() -> {
            try {
                nhaCungCapService.themNhaCungCap(ncc);
                Platform.runLater(() -> {
                    hienThiThanhCongLabel("✅ Thêm nhà cung cấp thành công.");
                    xoaTrang();
                    taiDuLieu();
                });
            } catch (Exception e) {
                Platform.runLater(() -> hienThiLoiLabel("Lỗi: " + e.getMessage()));
            }
        }, "ncc-them");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onLuu() {
        NhaCungCapDTO selected = tvNhaCungCap.getSelectionModel().getSelectedItem();
        if (selected == null) { hienThiLoiLabel("Vui lòng chọn nhà cung cấp cần sửa."); return; }

        NhaCungCapDTO ncc = layDuLieuTuForm();
        if (ncc == null) return;
        ncc.setMaNCC(selected.getMaNCC());

        Thread t = new Thread(() -> {
            try {
                nhaCungCapService.suaNhaCungCap(ncc);
                Platform.runLater(() -> {
                    hienThiThanhCongLabel("✅ Cập nhật nhà cung cấp thành công.");
                    taiDuLieu();
                });
            } catch (Exception e) {
                Platform.runLater(() -> hienThiLoiLabel("Lỗi: " + e.getMessage()));
            }
        }, "ncc-sua");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onXoa() {
        NhaCungCapDTO selected = tvNhaCungCap.getSelectionModel().getSelectedItem();
        if (selected == null) { hienThiLoiLabel("Vui lòng chọn nhà cung cấp cần xóa."); return; }

        // Confirm dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa nhà cung cấp: " + selected.getTenNCC() + "?");
        confirm.setContentText("Thao tác này sẽ ngừng giao dịch với nhà cung cấp này. Dữ liệu lịch sử nhập kho vẫn được giữ nguyên.");
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) thucHienXoa(selected);
        });
    }

    private void thucHienXoa(NhaCungCapDTO ncc) {
        int maNV = SessionContext.getInstance().getMaNV();
        Thread t = new Thread(() -> {
            try {
                nhaCungCapService.xoaNhaCungCap(ncc.getMaNCC(), maNV);
                Platform.runLater(() -> {
                    hienThiThanhCongLabel("✅ Đã ngừng giao dịch với \"" + ncc.getTenNCC() + "\".");
                    xoaTrang();
                    taiDuLieu();
                });
            } catch (Exception e) {
                Platform.runLater(() -> hienThiLoiLabel("Lỗi: " + e.getMessage()));
            }
        }, "ncc-xoa");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onHuy() {
        xoaTrang();
        tvNhaCungCap.getSelectionModel().clearSelection();
        capNhatTrangThai(false);
    }

    @FXML
    private void onTaiLai() {
        txtTimKiem.clear();
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

    // ── Helpers ─────────────────────────────────────────────────────────

    private NhaCungCapDTO layDuLieuTuForm() {
        String ten = txtTenNCC.getText();
        if (ten == null || ten.isBlank()) {
            hienThiLoiLabel("Tên nhà cung cấp không được để trống.");
            return null;
        }
        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setTenNCC(ten.trim());
        ncc.setSdt(txtSdt.getText() != null ? txtSdt.getText().trim() : "");
        ncc.setDiaChi(txtDiaChi.getText() != null ? txtDiaChi.getText().trim() : "");
        return ncc;
    }

    private void xoaTrang() {
        txtMaNCC.clear();
        txtTenNCC.clear();
        txtSdt.clear();
        txtDiaChi.clear();
    }

    private static String nvl(String s) { return s != null ? s : ""; }
}
