package com.bakery.views.controllers.nhansu;

import com.bakery.model.dto.hethong.CaLamViecDTO;
import com.bakery.services.hethong.ChamCongService;
import com.bakery.utils.UserSession;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller màn hình Chấm công — tái sử dụng CALAMVIEC.
 * MVP: View chỉ nhận/hiển thị, mọi nghiệp vụ qua ChamCongService.
 */
public class ChamCongViewFXMLController {

    private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_GIO  = DateTimeFormatter.ofPattern("HH:mm");

    // ─── FXML bindings ────────────────────────────────────────────────────────
    @FXML private ComboBox<Integer> cbThang;
    @FXML private ComboBox<Integer> cbNam;

    @FXML private Label  lblTrangThaiCa;
    @FXML private Label  lblGioVao;
    @FXML private Label  lblThoiGianLam;
    @FXML private Label  lblThongBao;
    @FXML private Button btnCheckIn;
    @FXML private Button btnCheckOut;

    @FXML private Label lblNgayLam;
    @FXML private Label lblTongGio;

    @FXML private TableView<CaLamViecDTO>           tablelichSu;
    @FXML private TableColumn<CaLamViecDTO, String> colNgay;
    @FXML private TableColumn<CaLamViecDTO, String> colGioVao;
    @FXML private TableColumn<CaLamViecDTO, String> colGioRa;
    @FXML private TableColumn<CaLamViecDTO, String> colTongGio;
    @FXML private TableColumn<CaLamViecDTO, String> colLoai;
    @FXML private TableColumn<CaLamViecDTO, String> colTrangThai;

    // ─── State ───────────────────────────────────────────────────────────────
    private final ChamCongService service = new ChamCongService();
    private int maNVHienTai = -1;

    // ─── Initialize ──────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        khoiTaoDropdown();
        setupTableColumns();
        loadMaNV();
        lamMoiTrangThaiCa();
        onXemLichSu();
    }

    private void loadMaNV() {
        if (UserSession.getCurrentUser() != null) {
            maNVHienTai = UserSession.getCurrentUser().getMaNV();
        }
    }

    private void khoiTaoDropdown() {
        LocalDate today = LocalDate.now();
        for (int i = 1; i <= 12; i++) cbThang.getItems().add(i);
        for (int y = today.getYear(); y >= 2024; y--) cbNam.getItems().add(y);
        cbThang.setValue(today.getMonthValue());
        cbNam.setValue(today.getYear());
    }

    private void setupTableColumns() {
        colNgay.setCellValueFactory(c -> {
            if (c.getValue().getThoiGianMoCa() != null)
                return new SimpleStringProperty(c.getValue().getThoiGianMoCa().format(FMT_NGAY));
            return new SimpleStringProperty("—");
        });
        colGioVao.setCellValueFactory(c -> {
            if (c.getValue().getThoiGianMoCa() != null)
                return new SimpleStringProperty(c.getValue().getThoiGianMoCa().format(FMT_GIO));
            return new SimpleStringProperty("—");
        });
        colGioRa.setCellValueFactory(c -> {
            if (c.getValue().getThoiGianDongCa() != null)
                return new SimpleStringProperty(c.getValue().getThoiGianDongCa().format(FMT_GIO));
            return new SimpleStringProperty("Đang làm");
        });
        colTongGio.setCellValueFactory(c -> {
            var mo = c.getValue().getThoiGianMoCa();
            var dong = c.getValue().getThoiGianDongCa();
            if (mo != null && dong != null) {
                long phut = Duration.between(mo, dong).toMinutes();
                if (phut > 0) return new SimpleStringProperty(phut / 60 + "h" + phut % 60 + "m");
            }
            return new SimpleStringProperty("—");
        });
        colLoai.setCellValueFactory(c -> {
            String pos = c.getValue().getMaMayPOS();
            return new SimpleStringProperty(pos != null ? "Thu ngân (" + pos + ")" : "Nhân viên");
        });
        colTrangThai.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTrangThai() != null
                        ? c.getValue().getTrangThai() : "—"));

        // Tô màu row: đang mở = vàng, đã đóng = xanh
        tablelichSu.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(CaLamViecDTO item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("row-done", "row-warning");
                if (!empty && item != null) {
                    if ("Đang mở".equals(item.getTrangThai()))
                        getStyleClass().add("row-warning");
                    else if ("Đã đóng".equals(item.getTrangThai()))
                        getStyleClass().add("row-done");
                }
            }
        });
    }

    // ─── Actions ─────────────────────────────────────────────────────────────

    @FXML
    private void onCheckIn() {
        if (maNVHienTai < 0) { hienThiLoi("Không xác định được nhân viên."); return; }
        btnCheckIn.setDisable(true);
        lblThongBao.setText("Đang xử lý...");

        new Thread(() -> {
            try {
                service.checkIn(maNVHienTai);
                Platform.runLater(() -> {
                    lblThongBao.setText("✅ Check-in thành công!");
                    lamMoiTrangThaiCa();
                    onXemLichSu();
                });
            } catch (Exception e) {
                Platform.runLater(() -> hienThiLoi(e.getMessage()));
            } finally {
                Platform.runLater(() -> btnCheckIn.setDisable(false));
            }
        }, "chamcong-checkin").start();
    }

    @FXML
    private void onCheckOut() {
        if (maNVHienTai < 0) { hienThiLoi("Không xác định được nhân viên."); return; }
        btnCheckOut.setDisable(true);
        lblThongBao.setText("Đang xử lý...");

        new Thread(() -> {
            try {
                service.checkOut(maNVHienTai);
                Platform.runLater(() -> {
                    lblThongBao.setText("✅ Check-out thành công!");
                    lamMoiTrangThaiCa();
                    onXemLichSu();
                });
            } catch (Exception e) {
                Platform.runLater(() -> hienThiLoi(e.getMessage()));
            } finally {
                Platform.runLater(() -> btnCheckOut.setDisable(false));
            }
        }, "chamcong-checkout").start();
    }

    @FXML
    private void onLamMoi() {
        lamMoiTrangThaiCa();
        onXemLichSu();
        lblThongBao.setText("");
    }

    @FXML
    private void onXemLichSu() {
        if (maNVHienTai < 0) return;
        int thang = cbThang.getValue() != null ? cbThang.getValue() : LocalDate.now().getMonthValue();
        int nam   = cbNam.getValue()   != null ? cbNam.getValue()   : LocalDate.now().getYear();

        new Thread(() -> {
            try {
                List<CaLamViecDTO> ds = service.layLichSu(maNVHienTai, thang, nam);
                String tongGio = service.tinhTongGioLam(ds);
                long ngayLam   = service.demNgayLam(ds);
                Platform.runLater(() -> {
                    tablelichSu.setItems(FXCollections.observableArrayList(ds));
                    lblTongGio.setText(tongGio);
                    lblNgayLam.setText(String.valueOf(ngayLam));
                });
            } catch (Exception e) {
                Platform.runLater(() -> hienThiLoi("Lỗi tải lịch sử: " + e.getMessage()));
            }
        }, "chamcong-lichsu").start();
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void lamMoiTrangThaiCa() {
        if (maNVHienTai < 0) return;
        new Thread(() -> {
            try {
                CaLamViecDTO ca = service.layCaHienTai(maNVHienTai);
                Platform.runLater(() -> capNhatUiTrangThai(ca));
            } catch (Exception e) {
                Platform.runLater(() -> hienThiLoi("Lỗi kiểm tra ca: " + e.getMessage()));
            }
        }, "chamcong-trangThai").start();
    }

    private void capNhatUiTrangThai(CaLamViecDTO ca) {
        if (ca == null) {
            lblTrangThaiCa.setText("Chưa check-in");
            lblTrangThaiCa.getStyleClass().removeAll("lbl-success", "lbl-warning");
            lblGioVao.setText("—");
            lblThoiGianLam.setText("—");
            btnCheckIn.setDisable(false);
            btnCheckOut.setDisable(true);
        } else {
            lblTrangThaiCa.setText("Đang trong ca");
            lblTrangThaiCa.getStyleClass().add("lbl-success");
            lblGioVao.setText(ca.getThoiGianMoCa() != null ? ca.getThoiGianMoCa().format(FMT_GIO) : "—");
            // Tính thời gian đã làm từ giờ vào đến hiện tại
            if (ca.getThoiGianMoCa() != null) {
                long phut = Duration.between(ca.getThoiGianMoCa(), java.time.LocalDateTime.now()).toMinutes();
                lblThoiGianLam.setText(phut / 60 + " giờ " + phut % 60 + " phút");
            }
            btnCheckIn.setDisable(true);
            btnCheckOut.setDisable(false);
        }
    }

    private void hienThiLoi(String msg) {
        lblThongBao.getStyleClass().removeAll("lbl-success");
        lblThongBao.getStyleClass().add("lbl-danger");
        lblThongBao.setText("❌ " + msg);
    }
}
