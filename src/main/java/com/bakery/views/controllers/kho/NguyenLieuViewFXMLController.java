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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller màn hình Quản lý Nguyên liệu (Tab Quản lý Nguyên liệu).
 * Thêm / Sửa qua dialog — không còn inline form panel.
 */
public class NguyenLieuViewFXMLController extends BaseController implements INguyenLieuView {

    // ── Table ─────────────────────────────────────────────────────────────────
    @FXML
    private TableView<NguyenLieuDTO> tblNguyenLieu;
    @FXML
    private TableColumn<NguyenLieuDTO, Double> colSoLuongTon;
    @FXML
    private TableColumn<NguyenLieuDTO, String> colTenNL;
    @FXML
    private TableColumn<NguyenLieuDTO, String> colXuatXu;
    @FXML
    private TableColumn<NguyenLieuDTO, String> colDVT;
    @FXML
    private TableColumn<NguyenLieuDTO, Double> colMucTon;

    @FXML
    private TextField txtTimKiem;
    @FXML
    private Button btnThemMoi;
    @FXML
    private Button btnSua;
    @FXML
    private Button btnXoa;
    @FXML
    private Button btnLapBaoCao;

    // ── Cache ──────────────────────────────────────────────────────────────────
    private final ObservableList<NguyenLieuDTO> masterData = FXCollections.observableArrayList();
    private List<DonViTinhDTO> cachedDsDVT = new ArrayList<>();
    private List<NhaCungCapDTO> cachedDsNCC = new ArrayList<>();
    private NguyenLieuPresenter presenter;
    private final NguyenLieuDAO nguyenLieuDAO = new NguyenLieuDAO();

    // ── Init ───────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        setupTable();
        presenter = new NguyenLieuPresenter(this, layMaNvHienTai());
        presenter.khoiTao();
        apDungPhanQuyenCUD();
        tblNguyenLieu.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> {
                    boolean coChon = newVal != null;
                    if (btnSua != null)
                        btnSua.setDisable(!coChon);
                    if (btnXoa != null)
                        btnXoa.setDisable(!coChon);
                });
        batDauAutoRefresh(tblNguyenLieu, () -> presenter.taiDanhSach(), 10);
    }

    private int layMaNvHienTai() {
        try {
            com.bakery.model.dto.nhansu.NhanVienDTO user = com.bakery.utils.UserSession.getCurrentUser();
            return (user != null) ? user.getMaNV() : 1;
        } catch (Exception e) {
            return 1;
        }
    }

    // ── Table setup ────────────────────────────────────────────────────────────

    private void setupTable() {
        colSoLuongTon.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSoLuongTonTong() != null
                ? c.getValue().getSoLuongTonTong()
                : 0.0).asObject());
        colSoLuongTon.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    return;
                }
                NguyenLieuDTO nl = getTableRow() != null ? (NguyenLieuDTO) getTableRow().getItem() : null;
                String dvt = (nl != null && !nl.getTenDVT().isEmpty()) ? " " + nl.getTenDVT() : "";
                setText(val % 1 == 0 ? String.valueOf((long) (double) val) + dvt : val + dvt);
            }
        });
        colTenNL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenNL()));
        colXuatXu.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getXuatXu() != null ? c.getValue().getXuatXu() : "Việt Nam"));
        colDVT.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenDVT()));
        colMucTon.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getMucTonAnToan()).asObject());
        colMucTon.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    return;
                }
                NguyenLieuDTO nl = getTableRow() != null ? (NguyenLieuDTO) getTableRow().getItem() : null;
                String dvt = (nl != null && !nl.getTenDVT().isEmpty()) ? " " + nl.getTenDVT() : "";
                setText(val % 1 == 0 ? String.valueOf((long) (double) val) + dvt : val + dvt);
            }
        });
        tblNguyenLieu.setItems(masterData);
        tblNguyenLieu.setPlaceholder(new Label("Chưa có nguyên liệu nào."));
        tblNguyenLieu.setRowFactory(tv -> {
            TableRow<NguyenLieuDTO> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty())
                    onSua(row.getItem());
            });
            return row;
        });
    }

    // ── Phân quyền ────────────────────────────────────────────────────────────

    private void apDungPhanQuyenCUD() {
        com.bakery.model.dto.nhansu.NhanVienDTO user = com.bakery.utils.UserSession.getCurrentUser();
        if (user == null)
            return;
        com.bakery.services.nhansu.PhanQuyenService svc = new com.bakery.services.nhansu.PhanQuyenService();
        boolean coQuyenCUD = svc.laAdmin(user) || svc.laQuanLy(user) || svc.laThuKho(user);
        if (!coQuyenCUD) {
            if (btnThemMoi != null) {
                btnThemMoi.setVisible(false);
                btnThemMoi.setManaged(false);
            }
            if (btnSua != null) {
                btnSua.setVisible(false);
                btnSua.setManaged(false);
            }
            if (btnXoa != null) {
                btnXoa.setVisible(false);
                btnXoa.setManaged(false);
            }
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
        if (dsDVT != null)
            cachedDsDVT = dsDVT;
    }

    @Override
    public void napDanhSachNhaCungCap(List<NhaCungCapDTO> dsNCC) {
        if (dsNCC != null)
            cachedDsNCC = dsNCC;
    }

    @Override
    public void hienThiChiTiet(NguyenLieuDTO nl) {
        /* dialog-based */ }

    @Override
    public void hienThiLoi(String msg) {
        hienThiLoiLabel(msg);
    }

    @Override
    public void hienThiThanhCong(String msg) {
        hienThiThanhCongLabel(msg);
    }

    @Override
    public void xoaLoi() {
        if (lblThongBao != null)
            lblThongBao.setText("");
    }

    @Override
    public void setLoading(boolean l) {
        tblNguyenLieu.setDisable(l);
    }

    @Override
    public void lamMoiForm() {
        tblNguyenLieu.getSelectionModel().clearSelection();
    }

    @Override
    public NguyenLieuDTO getSelectedNguyenLieu() {
        return tblNguyenLieu.getSelectionModel().getSelectedItem();
    }

    @Override
    public String getTenNLInput() {
        return "";
    }

    @Override
    public String getXuatXuInput() {
        return "";
    }

    @Override
    public double getMucTonAnToanInput() {
        return 0;
    }

    @Override
    public DonViTinhDTO getDonViTinhSelected() {
        return null;
    }

    @Override
    public String getTuKhoaTimKiemInput() {
        return txtTimKiem != null ? txtTimKiem.getText().trim() : "";
    }

    // ── FXML Actions ──────────────────────────────────────────────────────────

    @FXML
    private void onThemMoi() {
        if (presenter == null)
            return;
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
            if (cssUrl != null)
                scene.getStylesheets().add(cssUrl.toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();
            if (dialogCtrl.isConfirmed()) {
                presenter.themNguyenLieuVaNhapKho(
                        dialogCtrl.getTenNL(), dialogCtrl.getXuatXu(), dialogCtrl.getMucTon(),
                        dialogCtrl.getDonViTinh() != null ? dialogCtrl.getDonViTinh().getMaDVT() : 0,
                        dialogCtrl.getNhaCungCap() != null ? dialogCtrl.getNhaCungCap().getMaNCC() : 0,
                        dialogCtrl.getSoLuong(), dialogCtrl.getDonGia(),
                        dialogCtrl.getNgaySanXuat(), dialogCtrl.getHanSuDung());
            }
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog thêm nguyên liệu: " + e.getMessage());
        }
    }

    private void onSua(NguyenLieuDTO nl) {
        if (nl == null)
            return;
        try {
            URL url = getClass().getResource("/fxml/kho/SuaNguyenLieuDialog.fxml");
            if (url == null)
                throw new RuntimeException("Không tìm thấy SuaNguyenLieuDialog.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            URL css = getClass().getResource("/css/bakery.css");
            if (css != null)
                scene.getStylesheets().add(css.toExternalForm());
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
            if (ketQua == null)
                return;
            presenter.suaNguyenLieuTuDialog(
                    ketQua.getMaNL(), ketQua.getTenNL(), ketQua.getXuatXu(),
                    ketQua.getMucTonAnToan(), ketQua.getMaDVT());
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog sửa nguyên liệu: " + e.getMessage());
        }
    }

    private void onXoaHoiConfirm(NguyenLieuDTO nl) {
        if (nl == null)
            return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa nguyên liệu: " + nl.getTenNL() + "?");
        confirm.setContentText("Dữ liệu lịch sử nhập kho liên quan vẫn được giữ nguyên.");
        DialogHelper.applyBakeryTheme(confirm);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK)
                presenter.xoaNguyenLieuTheoMa(nl.getMaNL(), nl.getTenNL());
        });
    }

    @FXML
    private void onTimKiem() {
        if (presenter != null)
            presenter.timKiem();
    }

    @FXML
    private void onLamMoi() {
        if (txtTimKiem != null)
            txtTimKiem.clear();
        if (presenter != null)
            presenter.taiDanhSach();
    }

    @FXML
    private void onSuaAction() {
        onSua(tblNguyenLieu.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onXoaAction() {
        onXoaHoiConfirm(tblNguyenLieu.getSelectionModel().getSelectedItem());
    }

    /**
     * Lập báo cáo phiếu nhập kho — lọc theo tháng/năm, xuất PDF JasperReports.
     */
    @FXML
    private void onLapBaoCao() {
        final com.bakery.model.dao.kho.PhieuNhapKhoDAO phieuDAO = new com.bakery.model.dao.kho.PhieuNhapKhoDAO();

        // Đếm nhanh tổng số phiếu trong hệ thống
        int tongPhieu;
        try {
            tongPhieu = phieuDAO.layDanhSachPhieuNhap().size();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể truy vấn danh sách phiếu nhập: " + e.getMessage());
            return;
        }

        // ── Dialog chọn tháng/năm ────────────────────────────────────────────
        LocalDate now = LocalDate.now();

        ComboBox<Integer> cboThang = new ComboBox<>();
        for (int i = 1; i <= 12; i++)
            cboThang.getItems().add(i);
        cboThang.setValue(now.getMonthValue()); // mặc định tháng hiện tại

        ComboBox<Integer> cboNam = new ComboBox<>();
        for (int y = now.getYear(); y >= now.getYear() - 4; y--)
            cboNam.getItems().add(y);
        cboNam.setValue(now.getYear());

        javafx.scene.layout.HBox hboxLoc = new javafx.scene.layout.HBox(8,
                new Label("Tháng:"), cboThang,
                new Label("Năm:"), cboNam);
        hboxLoc.setAlignment(Pos.CENTER_LEFT);

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(12,
                new Label("Hệ thống hiện có " + tongPhieu + " phiếu nhập trong kho."),
                new Label("Lọc báo cáo theo:"),
                hboxLoc);
        content.setPadding(new Insets(4, 0, 0, 0));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Lập Báo Cáo Phiếu Nhập Kho");
        dialog.setHeaderText("📋  Lập báo cáo phiếu nhập kho");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        DialogHelper.applyBakeryTheme(dialog);

        java.util.Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK)
            return;

        final int thang = cboThang.getValue();
        final int nam = cboNam.getValue();

        // ── Background: gọi procedure + lọc tháng + xuất PDF ────────────────
        if (btnLapBaoCao != null)
            btnLapBaoCao.setDisable(true);
        hienThiThanhCongLabel("⏳  Đang lập báo cáo tháng " + thang + "/" + nam + "...");

        final String nguoiLap = com.bakery.utils.UserSession.getCurrentUser() != null
                ? com.bakery.utils.UserSession.getCurrentUser().getHoTen()
                : "Hệ thống";
        final String ngayLap = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        Thread t = new Thread(() -> {
            try {
                com.bakery.model.dto.kho.KetQuaKiemKeDTO ketQua = phieuDAO.lapBaoCaoPhieuNhap();

                java.text.NumberFormat fmtTien = java.text.NumberFormat
                        .getNumberInstance(java.util.Locale.of("vi", "VN"));
                fmtTien.setMaximumFractionDigits(0);
                DateTimeFormatter fmtDt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                // Lọc theo tháng/năm đã chọn
                List<com.bakery.model.dto.kho.PhieuNhapKhoDTO> dsLoc = ketQua.getDanhSachPhieu().stream()
                        .filter(p -> p.getNgayNhap() != null
                                && p.getNgayNhap().getMonthValue() == thang
                                && p.getNgayNhap().getYear() == nam)
                        .collect(Collectors.toList());

                // Tổng tiền cả kỳ
                java.math.BigDecimal tongTien = dsLoc.stream()
                        .filter(p -> p.getTongTienNhap() != null)
                        .map(com.bakery.model.dto.kho.PhieuNhapKhoDTO::getTongTienNhap)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                List<Integer> maPhieuList = dsLoc.stream()
                        .map(com.bakery.model.dto.kho.PhieuNhapKhoDTO::getMaPN)
                        .collect(Collectors.toList());
                java.util.Map<Integer, List<String[]>> ctMap = phieuDAO.layChiTietNhieuPhieuNhap(maPhieuList);

                // Sort: theo NCC rồi mã phiếu tăng dần
                dsLoc.sort(java.util.Comparator
                        .comparing((com.bakery.model.dto.kho.PhieuNhapKhoDTO p) -> p.getTenNhaCungCap() != null
                                ? p.getTenNhaCungCap()
                                : "")
                        .thenComparingInt(com.bakery.model.dto.kho.PhieuNhapKhoDTO::getMaPN));

                // Build flat rows: mỗi row = 1 dòng CTPHIEUNHAP
                // String[]{maPhieu, ngayNhap, nhaCungCap, nguoiNhap,
                // tongTienPhieu, tenNL, soLuong, tenDVT, donGia, thanhTien}
                List<String[]> jasperRows = new ArrayList<>();
                for (com.bakery.model.dto.kho.PhieuNhapKhoDTO dto : dsLoc) {
                    String tongTienPhieu = dto.getTongTienNhap() != null
                            ? fmtTien.format(dto.getTongTienNhap()) + " ₫"
                            : "0 ₫";
                    String ngayNhapStr = dto.getNgayNhap() != null
                            ? dto.getNgayNhap().format(fmtDt)
                            : "—";
                    String ncc = dto.getTenNhaCungCap() != null ? dto.getTenNhaCungCap() : "—";
                    String nguoiNhap = dto.getTenNhanVien() != null ? dto.getTenNhanVien() : "—";
                    String maPhieuStr = String.valueOf(dto.getMaPN());

                    List<String[]> ctLines = ctMap.getOrDefault(dto.getMaPN(), List.of());
                    if (ctLines.isEmpty()) {
                        jasperRows.add(new String[] {
                                maPhieuStr, ngayNhapStr, ncc, nguoiNhap, tongTienPhieu,
                                "(Kh\u00f4ng c\u00f3 chi ti\u1ebft)", "", "", "0 ₫", "0 ₫"
                        });
                    } else {
                        for (String[] ct : ctLines) {
                            // ct = {TENNL, soLuong, TENDVT, donGia_raw, thanhTien_raw}
                            String donGiaFmt = fmtTien.format(Long.parseLong(ct[3])) + " ₫";
                            String thanhTienFmt = fmtTien.format(Long.parseLong(ct[4])) + " ₫";
                            jasperRows.add(new String[] {
                                    maPhieuStr, ngayNhapStr, ncc, nguoiNhap, tongTienPhieu,
                                    ct[0], ct[1], ct[2], donGiaFmt, thanhTienFmt
                            });
                        }
                    }
                }

                java.io.File outputFile = com.bakery.utils.ReportPathUtils
                        .buildPdfPath("BaoCaoPhieuNhap", thang + "-" + nam);
                com.bakery.utils.JasperReportUtils.xuatBaoCaoKiemKePhieuNhapPDF(
                        outputFile,
                        String.valueOf(dsLoc.size()),
                        fmtTien.format(tongTien) + " ₫",
                        nguoiLap, ngayLap, jasperRows);

                final String folder = outputFile.getParent();
                final String tenFile = outputFile.getName();

                Platform.runLater(() -> {
                    if (btnLapBaoCao != null)
                        btnLapBaoCao.setDisable(false);
                    hienThiThanhCongLabel("✅ Đã lưu báo cáo: " + tenFile);
                    hienThiThongTin("Lập báo cáo thành công",
                            "Báo cáo phiếu nhập tháng " + thang + "/" + nam
                                    + " (" + dsLoc.size() + " phiếu) đã được lưu vào:\n" + folder);
                    try {
                        if (java.awt.Desktop.isDesktopSupported())
                            java.awt.Desktop.getDesktop().open(outputFile);
                    } catch (Exception ignored) {
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (btnLapBaoCao != null)
                        btnLapBaoCao.setDisable(false);
                    hienThiLoiLabel("Lỗi lập báo cáo: " + e.getMessage());
                    hienThiThongBaoLoi("Lỗi Lập Báo Cáo", e.getMessage());
                });
            }
        }, "thread-lap-bao-cao-phieunhap");
        t.setDaemon(true);
        t.start();
    }
}
