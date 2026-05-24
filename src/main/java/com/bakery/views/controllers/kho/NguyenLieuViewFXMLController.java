package com.bakery.views.controllers.kho;

import com.bakery.model.dao.kho.NguyenLieuDAO;
import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.presenters.kho.NguyenLieuPresenter;
import com.bakery.utils.DialogHelper;
import com.bakery.views.controllers.BaseController;
import com.bakery.views.interfaces.kho.INguyenLieuView;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller màn hình Quản lý Nguyên liệu (Tab Kiểm kê kho).
 * Thêm / Sửa qua dialog — không còn inline form panel.
 * Cùng pattern với QuanLyNhaCungCapViewFXMLController.
 */
public class NguyenLieuViewFXMLController extends BaseController implements INguyenLieuView {

    // ── Table ────────────────────────────────────────────────────────────────
    @FXML private TableView<NguyenLieuDTO>          tblNguyenLieu;
    @FXML private TableColumn<NguyenLieuDTO, Double>  colSoLuongTon;
    @FXML private TableColumn<NguyenLieuDTO, String>  colTenNL;
    @FXML private TableColumn<NguyenLieuDTO, String>  colXuatXu;
    @FXML private TableColumn<NguyenLieuDTO, String>  colDVT;
    @FXML private TableColumn<NguyenLieuDTO, Double>  colMucTon;

    @FXML private TextField txtTimKiem;
    @FXML private Button    btnThemMoi;
    @FXML private Button    btnSua;
    @FXML private Button    btnXoa;
    @FXML private Button    btnLapBaoCao;

    // ── Cache ─────────────────────────────────────────────────────────────────
    private final ObservableList<NguyenLieuDTO> masterData    = FXCollections.observableArrayList();
    private List<DonViTinhDTO>  cachedDsDVT = new ArrayList<>();
    private List<NhaCungCapDTO> cachedDsNCC = new ArrayList<>();
    private NguyenLieuPresenter presenter;
    private final NguyenLieuDAO nguyenLieuDAO = new NguyenLieuDAO();

    // ── Init ──────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        setupTable();
        presenter = new NguyenLieuPresenter(this, layMaNvHienTai());
        presenter.khoiTao();
        apDungPhanQuyenCUD();
        // Disable Sửa/Xóa khi chưa chọn dòng
        tblNguyenLieu.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> {
                    boolean coChon = newVal != null;
                    if (btnSua != null) btnSua.setDisable(!coChon);
                    if (btnXoa != null) btnXoa.setDisable(!coChon);
                });
        // Auto-refresh mỗi 10s
        batDauAutoRefresh(tblNguyenLieu, () -> presenter.taiDanhSach(), 10);
    }

    /** Lấy mã nhân viên hiện tại từ Session; fallback = 1 nếu chưa đăng nhập. */
    private int layMaNvHienTai() {
        try {
            com.bakery.model.dto.nhansu.NhanVienDTO user =
                    com.bakery.utils.UserSession.getCurrentUser();
            return (user != null) ? user.getMaNV() : 1;
        } catch (Exception e) {
            return 1;
        }
    }

    // ── Table setup ───────────────────────────────────────────────────────────

    private void setupTable() {
        colSoLuongTon.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getSoLuongTonTong() != null ? c.getValue().getSoLuongTonTong() : 0.0).asObject());
        colSoLuongTon.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); return; }
                NguyenLieuDTO nl = getTableRow() != null
                        ? (NguyenLieuDTO) getTableRow().getItem() : null;
                String dvt = (nl != null && !nl.getTenDVT().isEmpty())
                        ? " " + nl.getTenDVT() : "";
                setText(val % 1 == 0
                        ? String.valueOf((long)(double)val) + dvt
                        : val + dvt);
            }
        });
        colTenNL.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTenNL()));
        colXuatXu.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getXuatXu() != null
                        ? c.getValue().getXuatXu() : "Việt Nam"));
        colDVT.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTenDVT()));
        colMucTon.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getMucTonAnToan()).asObject());
        colMucTon.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); return; }
                NguyenLieuDTO nl = getTableRow() != null
                        ? (NguyenLieuDTO) getTableRow().getItem() : null;
                String dvt = (nl != null && !nl.getTenDVT().isEmpty())
                        ? " " + nl.getTenDVT() : "";
                setText(val % 1 == 0
                        ? String.valueOf((long)(double)val) + dvt
                        : val + dvt);
            }
        });
        tblNguyenLieu.setItems(masterData);
        tblNguyenLieu.setPlaceholder(new Label("Chưa có nguyên liệu nào."));

        // Double-click → mở dialog Sửa
        tblNguyenLieu.setRowFactory(tv -> {
            TableRow<NguyenLieuDTO> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) onSua(row.getItem());
            });
            return row;
        });
    }

    /** Cột Hành động đã được thay thế bằng nút toolbar — phương thức này không còn dùng. */
    // setupActionsColumn() removed — replaced by btnSua/btnXoa in header toolbar

    // ── Phân quyền ───────────────────────────────────────────────────────────

    private void apDungPhanQuyenCUD() {
        com.bakery.model.dto.nhansu.NhanVienDTO user =
                com.bakery.utils.UserSession.getCurrentUser();
        if (user == null) return;

        com.bakery.services.nhansu.PhanQuyenService svc =
                new com.bakery.services.nhansu.PhanQuyenService();
        boolean coQuyenCUD = svc.laAdmin(user) || svc.laQuanLy(user) || svc.laThuKho(user);

        if (!coQuyenCUD) {
            if (btnThemMoi != null) { btnThemMoi.setVisible(false); btnThemMoi.setManaged(false); }
            if (btnSua    != null) { btnSua.setVisible(false);     btnSua.setManaged(false); }
            if (btnXoa    != null) { btnXoa.setVisible(false);     btnXoa.setManaged(false); }
        }
    }

    // ── INguyenLieuView ───────────────────────────────────────────────────────

    @Override
    public void hienThiDanhSach(List<NguyenLieuDTO> ds) {
        if (ds == null || ds.isEmpty()) {
            masterData.clear();
            hienThiLoiLabel("Không có dữ liệu nguyên liệu.");
            return;
        }
        masterData.setAll(ds);
    }

    @Override
    public void napDanhSachDonViTinh(List<DonViTinhDTO> dsDVT) {
        if (dsDVT != null) cachedDsDVT = dsDVT;
    }

    @Override
    public void napDanhSachNhaCungCap(List<NhaCungCapDTO> dsNCC) {
        if (dsNCC != null) cachedDsNCC = dsNCC;
    }

    /**
     * Không còn dùng để điền inline form — giữ lại để tương thích interface.
     * Double-click hàng sẽ mở dialog Sửa trực tiếp qua setRowFactory.
     */
    @Override
    public void hienThiChiTiet(NguyenLieuDTO nl) { /* no-op: dialog-based */ }

    @Override
    public void hienThiLoi(String msg) { hienThiLoiLabel(msg); }

    @Override
    public void hienThiThanhCong(String msg) { hienThiThanhCongLabel(msg); }

    @Override
    public void xoaLoi() {
        if (lblThongBao != null) lblThongBao.setText("");
    }

    @Override
    public void setLoading(boolean l) { tblNguyenLieu.setDisable(l); }

    /** Không còn sidebar form → chỉ bỏ selection. */
    @Override
    public void lamMoiForm() {
        tblNguyenLieu.getSelectionModel().clearSelection();
    }

    @Override
    public NguyenLieuDTO getSelectedNguyenLieu() {
        return tblNguyenLieu.getSelectionModel().getSelectedItem();
    }

    // Các getter phục vụ flow inline cũ — giữ lại cho tương thích presenter
    @Override public String getTenNLInput()          { return ""; }
    @Override public String getXuatXuInput()         { return ""; }
    @Override public double getMucTonAnToanInput()   { return 0; }
    @Override public DonViTinhDTO getDonViTinhSelected() { return null; }
    @Override public String getTuKhoaTimKiemInput()  {
        return txtTimKiem != null ? txtTimKiem.getText().trim() : "";
    }

    // ── FXML Actions ──────────────────────────────────────────────────────────

    /** Mở dialog Thêm nguyên liệu mới (kèm nhập kho lần đầu). */
    @FXML
    private void onThemMoi() {
        if (presenter == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/kho/ThemNguyenLieuDialog.fxml"));
            Parent root = loader.load();

            ThemNguyenLieuDialogController dialogCtrl = loader.getController();
            dialogCtrl.khoiTao(cachedDsDVT, cachedDsNCC);

            Stage stage = new Stage();
            stage.setTitle("H3K Bakery — Thêm nguyên liệu mới");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tblNguyenLieu.getScene().getWindow());
            stage.setResizable(false);

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();

            if (dialogCtrl.isConfirmed()) {
                presenter.themNguyenLieuVaNhapKho(
                        dialogCtrl.getTenNL(),
                        dialogCtrl.getXuatXu(),
                        dialogCtrl.getMucTon(),
                        dialogCtrl.getDonViTinh() != null ? dialogCtrl.getDonViTinh().getMaDVT() : 0,
                        dialogCtrl.getNhaCungCap() != null ? dialogCtrl.getNhaCungCap().getMaNCC() : 0,
                        dialogCtrl.getSoLuong(),
                        dialogCtrl.getDonGia(),
                        dialogCtrl.getNgaySanXuat(),
                        dialogCtrl.getHanSuDung());
            }
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog thêm nguyên liệu: " + e.getMessage());
        }
    }

    /** Mở dialog Sửa với nguyên liệu đã chọn. */
    private void onSua(NguyenLieuDTO nl) {
        if (nl == null) return;
        try {
            URL url = getClass().getResource("/fxml/kho/SuaNguyenLieuDialog.fxml");
            if (url == null) throw new RuntimeException("Không tìm thấy SuaNguyenLieuDialog.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            URL css = getClass().getResource("/css/bakery.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            SuaNguyenLieuDialogController ctrl = loader.getController();
            ctrl.khoiTaoSua(nl, cachedDsDVT);

            Stage stage = new Stage();
            stage.setTitle("H3K Bakery — Sửa nguyên liệu");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tblNguyenLieu.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

            NguyenLieuDTO ketQua = ctrl.getKetQua();
            if (ketQua == null) return; // user bấm Hủy

            presenter.suaNguyenLieuTuDialog(
                    ketQua.getMaNL(),
                    ketQua.getTenNL(),
                    ketQua.getXuatXu(),
                    ketQua.getMucTonAnToan(),
                    ketQua.getMaDVT());

        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog sửa nguyên liệu: " + e.getMessage());
        }
    }

    /** Hỏi xác nhận rồi xóa nguyên liệu. */
    private void onXoaHoiConfirm(NguyenLieuDTO nl) {
        if (nl == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa nguyên liệu: " + nl.getTenNL() + "?");
        confirm.setContentText("Dữ liệu lịch sử nhập kho liên quan vẫn được giữ nguyên.");
        DialogHelper.applyBakeryTheme(confirm);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                presenter.xoaNguyenLieuTheoMa(nl.getMaNL(), nl.getTenNL());
            }
        });
    }

    @FXML
    private void onTimKiem() {
        if (presenter != null) presenter.timKiem();
    }

    @FXML
    private void onLamMoi() {
        if (txtTimKiem != null) txtTimKiem.clear();
        if (presenter != null) presenter.taiDanhSach();
    }

    /** Handler cho nút ✏️ Sửa trên toolbar — mở dialog sửa NL đang chọn. */
    @FXML
    private void onSuaAction() {
        onSua(tblNguyenLieu.getSelectionModel().getSelectedItem());
    }

    /** Handler cho nút 🗑 Xóa trên toolbar — xác nhận rồi xóa NL đang chọn. */
    @FXML
    private void onXoaAction() {
        onXoaHoiConfirm(tblNguyenLieu.getSelectionModel().getSelectedItem());
    }

    /**
     * Demo Phantom Read §4.3: gọi PROC_LAPBAOCAOPHIEUNHAP.
     * Bước 1: Đếm nhanh N phiếu → hỏi xác nhận.
     * Bước 2: Procedure delay 20s → phiên khác có thể nhập kho.
     * Bước 3: So sánh soPhieuDaDem vs danhSachPhieu.size() → xuất PDF Jasper.
     *
     * Kịch bản BUG/FIX được toggle bằng comment/uncomment TRONG procedure.
     */
    @FXML
    private void onLapBaoCao() {
        // Bước 1: Đếm nhanh
        final com.bakery.model.dao.kho.PhieuNhapKhoDAO phieuDAO =
                new com.bakery.model.dao.kho.PhieuNhapKhoDAO();
        int soPhieuHienTai;
        try {
            soPhieuHienTai = phieuDAO.layDanhSachPhieuNhap().size();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể truy vấn danh sách phiếu nhập: " + e.getMessage());
            return;
        }

        // Hỏi xác nhận
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Lập Báo Cáo Kiểm Kê Phiếu Nhập");
        confirm.setHeaderText("\uD83D\uDCCB  Hệ thống tìm thấy " + soPhieuHienTai + " phiếu nhập kho.");
        confirm.setContentText(
                "Bạn có muốn lập báo cáo kiểm kê không?\n\n" +
                "\u26A0  Quá trình lập báo cáo mất khoảng 20 giây.\n" +
                "    Trong thời gian đó, hãy dùng cửa sổ khác\n" +
                "    để tạo thêm một phiếu nhập mới nhằm demo\n" +
                "    hiện tượng Phantom Read (\u00a74.3).");
        DialogHelper.applyBakeryTheme(confirm);

        java.util.Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        // Bước 2: Disable nút, chạy procedure trên background
        if (btnLapBaoCao != null) btnLapBaoCao.setDisable(true);
        hienThiThanhCongLabel("\u23F3  Đang lập báo cáo kiểm kê... (delay 20s)");

        final int soPhieuXacNhan = soPhieuHienTai;
        final String nguoiLap = com.bakery.utils.UserSession.getCurrentUser() != null
                ? com.bakery.utils.UserSession.getCurrentUser().getHoTen() : "Hệ thống";
        final String ngayLap = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        Thread t = new Thread(() -> {
            try {
                // Gọi PROC_LAPBAOCAOPHIEUNHAP — chặn ~20s
                com.bakery.model.dto.kho.KetQuaKiemKeDTO ketQua = phieuDAO.lapBaoCaoPhieuNhap();

                // Map DTO → Jasper rows
                java.text.NumberFormat fmtTien =
                        java.text.NumberFormat.getNumberInstance(java.util.Locale.of("vi", "VN"));
                fmtTien.setMaximumFractionDigits(0);

                java.math.BigDecimal tongTien = ketQua.getDanhSachPhieu().stream()
                        .filter(p -> p.getTongTienNhap() != null)
                        .map(com.bakery.model.dto.kho.PhieuNhapKhoDTO::getTongTienNhap)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                java.time.format.DateTimeFormatter fmtDt =
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                java.util.List<String[]> jasperRows = new java.util.ArrayList<>();
                for (com.bakery.model.dto.kho.PhieuNhapKhoDTO dto : ketQua.getDanhSachPhieu()) {
                    String ngayNhap = dto.getNgayNhap() != null ? dto.getNgayNhap().format(fmtDt) : "\u2014";
                    String tienStr = dto.getTongTienNhap() != null
                            ? fmtTien.format(dto.getTongTienNhap()) + " \u20ab" : "0 \u20ab";
                    jasperRows.add(new String[]{
                        String.valueOf(dto.getMaPN()), ngayNhap,
                        dto.getTenNhaCungCap() != null ? dto.getTenNhaCungCap() : "\u2014",
                        dto.getTenNhanVien()   != null ? dto.getTenNhanVien()   : "\u2014",
                        tienStr
                    });
                }

                // Xuất PDF
                java.io.File outputFile = com.bakery.utils.ReportPathUtils
                        .buildPdfPath("BaoCaoKiemKe", "PHIEUNHAP");
                com.bakery.utils.JasperReportUtils.xuatBaoCaoKiemKePhieuNhapPDF(
                        outputFile,
                        String.valueOf(soPhieuXacNhan),
                        fmtTien.format(tongTien) + " \u20ab",
                        nguoiLap, ngayLap, jasperRows);

                final boolean coPhantom = ketQua.coPhantomRead();
                final int soThucTe = ketQua.getDanhSachPhieu().size();
                final String pdfPath = outputFile.getAbsolutePath();

                Platform.runLater(() -> {
                    if (btnLapBaoCao != null) btnLapBaoCao.setDisable(false);
                    if (coPhantom) {
                        hienThiLoiLabel("\u274C Phantom Read! Đếm = " + soPhieuXacNhan
                                + " nhưng báo cáo có " + soThucTe + " dòng.");
                        hienThiThongBaoLoi("\u26A1 Phát Hiện PHANTOM READ — \u00a74.3",
                            "Phase 1 đếm: " + soPhieuXacNhan + " phiếu\n" +
                            "Phase 3 đọc: " + soThucTe + " dòng\n\n" +
                            "\u2192 Phantom Read (READ COMMITTED).\n\n" +
                            "\uD83D\uDCC4 PDF: " + pdfPath);
                    } else {
                        hienThiThanhCongLabel("\u2705 Báo cáo đồng nhất! " + soThucTe + " phiếu.");
                        hienThiThongTin("\u2705 Báo Cáo Nhất Quán — \u00a74.3",
                            "Phase 1 đếm: " + soPhieuXacNhan + " phiếu\n" +
                            "Phase 3 đọc: " + soThucTe + " dòng\n\n" +
                            "\u2192 Không Phantom Read (SERIALIZABLE).\n\n" +
                            "\uD83D\uDCC4 PDF: " + pdfPath);
                    }
                    try {
                        if (java.awt.Desktop.isDesktopSupported()) {
                            java.awt.Desktop.getDesktop().open(outputFile);
                        }
                    } catch (Exception ignored) {}
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (btnLapBaoCao != null) btnLapBaoCao.setDisable(false);
                    hienThiLoiLabel("Lỗi lập báo cáo: " + e.getMessage());
                    hienThiThongBaoLoi("Lỗi Lập Báo Cáo Kiểm Kê", e.getMessage());
                });
            }
        }, "thread-lap-bao-cao-nguyen-lieu-phieunhap");
        t.setDaemon(true);
        t.start();
    }
}
