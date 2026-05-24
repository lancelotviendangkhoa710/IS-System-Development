package com.bakery.views.controllers.kho;

import com.bakery.model.dao.kho.NguyenLieuDAO;
import com.bakery.model.dao.kho.PhieuNhapKhoDAO;
import com.bakery.model.dao.kho.SanPhamDAO;
import com.bakery.model.dto.kho.KetQuaKiemKeDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.PhieuNhapKhoDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.utils.DialogHelper;
import com.bakery.utils.JasperReportUtils;
import com.bakery.utils.ReportPathUtils;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controller Kiểm Kê Kho.
 * Hiển thị tổng hợp tồn kho thực tế từ DB:
 *  - Nguyên liệu (NGUYENLIEU.SOLUONGTONTONG)
 *  - Thành phẩm  (SANPHAM.SOLUONGTON)
 * Không gọi mock data.
 */
public class KiemKeKhoViewFXMLController extends BaseController {

    @FXML private Label lblTitle;
    @FXML private TableView<TonKhoRow> tblData;
    @FXML private TableColumn<TonKhoRow, String> colDate;    // tai dung: "Loai"
    @FXML private TableColumn<TonKhoRow, String> colUser;    // tai dung: "Ten hang"
    @FXML private TableColumn<TonKhoRow, String> colDonVi;   // Don vi tinh
    @FXML private TableColumn<TonKhoRow, String> colContent; // tai dung: "Ton kho"
    @FXML private TableColumn<TonKhoRow, String> colStatus;  // "Trang thai"
    @FXML private Button btnLapBaoCao;

    private static final NumberFormat FMT = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
    static { FMT.setMaximumFractionDigits(2); }

    private static final NumberFormat FMT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
    static { FMT_TIEN.setMaximumFractionDigits(0); }

    private static final DateTimeFormatter FMT_DT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Row hien thi tong hop ton kho — gop NL lan SP. */
    public record TonKhoRow(String loai, String ten, String donViTinh, String tonKho, String trangThai) {}

    private final NguyenLieuDAO nguyenLieuDAO     = new NguyenLieuDAO();
    private final SanPhamDAO    sanPhamDAO         = new SanPhamDAO();
    private final PhieuNhapKhoDAO phieuNhapKhoDAO = new PhieuNhapKhoDAO();
    private final ObservableList<TonKhoRow> rows   = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        lblTitle.setText("KIỂM KÊ KHO");
        setupTable();
        taiDuLieu();
    }

    private void setupTable() {
        colDate.setText("Loại");
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().loai()));

        colUser.setText("Tên hàng");
        colUser.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().ten()));

        colDonVi.setText("Đơn vị tính");
        colDonVi.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().donViTinh()));

        colContent.setText("Tồn kho");
        colContent.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tonKho()));

        colStatus.setText("Trạng thái");
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().trangThai()));

        tblData.setItems(rows);
        tblData.setPlaceholder(new Label("Đang tải dữ liệu kho..."));
    }

    private void taiDuLieu() {
        Thread t = new Thread(() -> {
            List<TonKhoRow> data = new ArrayList<>();
            try {
                // --- Nguyên liệu ---
                List<NguyenLieuDTO> dsNL = nguyenLieuDAO.layTatCaNguyenLieu();
                for (NguyenLieuDTO nl : dsNL) {
                    double ton = nl.getSoLuongTonTong();
                    String trang = ton <= 0 ? "\u26D4 Hết hàng"
                            : ton <= nl.getMucTonAnToan() ? "\u26A0 Sắp hết" : "\u2705 Đủ hàng";
                    String dvt = nl.getTenDVT().isBlank() ? "---" : nl.getTenDVT();
                    data.add(new TonKhoRow("Nguyên liệu", nl.getTenNL(),
                            dvt, FMT.format(ton), trang));
                }

                // --- Thành phẩm ---
                List<SanPhamDTO> dsSP = sanPhamDAO.layTatCaSanPhamQuanLy();
                for (SanPhamDTO sp : dsSP) {
                    double ton = sp.getSoLuongTon();
                    String trang = ton <= 0 ? "\u26D4 Hết hàng"
                            : ton < 5 ? "\u26A0 Sắp hết" : "\u2705 Đủ hàng";
                    data.add(new TonKhoRow("Thành phẩm", sp.getTenSP(),
                            "cái", FMT.format(ton), trang));
                }
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel("Lỗi tải dữ liệu kho: " + e.getMessage()));
                return;
            }
            final List<TonKhoRow> finalData = data;
            javafx.application.Platform.runLater(() -> {
                rows.setAll(finalData);
                if (finalData.isEmpty()) {
                    tblData.setPlaceholder(new Label("Kho chưa có hàng."));
                }
            });
        }, "kiem-ke-tai-du-lieu");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onAction() {
        taiDuLieu();
        hienThiThanhCongLabel("Đã làm mới dữ liệu kiểm kê.");
    }

    // ── Lập Báo Cáo Kiểm Kê Phiếu Nhập — Demo §4.3 Phantom Read ────────────

    /**
     * Xử lý nút "Lập Báo Cáo Kiểm Kê".
     *
     * <p><b>Bước 1 (FX thread):</b> Đếm nhanh N phiếu nhập → hỏi xác nhận user.
     * <p><b>Bước 2 (Background thread):</b> Gọi {@code PROC_LAPBAOCAOPHIEUNHAP}
     *    có delay 20 giây bên trong — thời gian để phiên khác nhập kho.
     * <p><b>Bước 3 (FX thread):</b> So sánh soPhieuDaDem vs danhSachPhieu.size()
     *    → xuất PDF JasperReports, thông báo BUG hoặc FIX.
     *
     * <p>Kịch bản BUG/FIX được toggle bằng comment/uncomment TRONG procedure:
     * <ul>
     *   <li><b>BUG</b> (mặc định): dòng SET TRANSACTION bị comment → READ COMMITTED
     *       → Phase 3 cursor thấy phiếu mới → N+1 dòng → phantom row</li>
     *   <li><b>FIX</b>: bỏ comment dòng SET TRANSACTION → SERIALIZABLE
     *       → Phase 3 cursor dùng snapshot cũ → đúng N dòng</li>
     * </ul>
     */
    @FXML
    private void onLapBaoCao() {
        // ── Bước 1: Đếm nhanh số phiếu nhập hiện tại (FX thread) ─────────────
        int soPhieuHienTai;
        try {
            soPhieuHienTai = phieuNhapKhoDAO.layDanhSachPhieuNhap().size();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể truy vấn danh sách phiếu nhập: " + e.getMessage());
            return;
        }

        // ── Hỏi xác nhận user ────────────────────────────────────────────────
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

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        // ── Bước 2: Disable nút, chạy PROC trên background thread ────────────
        btnLapBaoCao.setDisable(true);
        hienThiThanhCongLabel("\u23F3  Đang lập báo cáo kiểm kê... (Phase 1 đếm xong, đang delay 20s)");

        final int soPhieuXacNhan = soPhieuHienTai;
        final String nguoiLap = UserSession.getCurrentUser() != null
                ? UserSession.getCurrentUser().getHoTen() : "Hệ thống";
        final String ngayLap = LocalDateTime.now().format(FMT_DT);

        Thread thread = new Thread(() -> {
            try {
                // Gọi PROC_LAPBAOCAOPHIEUNHAP — chặn ~20s (delay bên trong procedure)
                // Isolation level do procedure quyết định (comment/uncomment SET TRANSACTION)
                KetQuaKiemKeDTO ketQua = phieuNhapKhoDAO.lapBaoCaoPhieuNhap();

                // ── Bước 3: Map DTO → Jasper rows ─────────────────────────────
                BigDecimal tongTien = ketQua.getDanhSachPhieu().stream()
                        .filter(p -> p.getTongTienNhap() != null)
                        .map(PhieuNhapKhoDTO::getTongTienNhap)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                List<String[]> jasperRows = new ArrayList<>();
                for (PhieuNhapKhoDTO dto : ketQua.getDanhSachPhieu()) {
                    String ngayNhap = dto.getNgayNhap() != null
                            ? dto.getNgayNhap().format(FMT_DT) : "\u2014";
                    String tienStr = dto.getTongTienNhap() != null
                            ? FMT_TIEN.format(dto.getTongTienNhap()) + " \u20ab" : "0 \u20ab";
                    jasperRows.add(new String[]{
                        String.valueOf(dto.getMaPN()),
                        ngayNhap,
                        dto.getTenNhaCungCap() != null ? dto.getTenNhaCungCap() : "\u2014",
                        dto.getTenNhanVien()   != null ? dto.getTenNhanVien()   : "\u2014",
                        tienStr
                    });
                }

                // Xuất PDF Jasper
                File outputFile = ReportPathUtils.buildPdfPath("BaoCaoKiemKe", "PHIEUNHAP");
                JasperReportUtils.xuatBaoCaoKiemKePhieuNhapPDF(
                        outputFile,
                        String.valueOf(soPhieuXacNhan),
                        FMT_TIEN.format(tongTien) + " \u20ab",
                        nguoiLap,
                        ngayLap,
                        jasperRows);

                // So sánh kết quả
                final boolean coPhantom  = ketQua.coPhantomRead();
                final int soThucTe       = ketQua.getDanhSachPhieu().size();
                final String pdfPath     = outputFile.getAbsolutePath();

                javafx.application.Platform.runLater(() -> {
                    btnLapBaoCao.setDisable(false);
                    if (coPhantom) {
                        // ❌ BUG — Phantom Read xảy ra
                        hienThiLoiLabel(
                            "\u274C Phantom Read! Đếm ban đầu = " + soPhieuXacNhan +
                            " nhưng báo cáo có " + soThucTe + " dòng.");
                        hienThiThongBaoLoi(
                            "\u26A1 Phát Hiện PHANTOM READ — \u00a74.3",
                            "Phase 1 đếm  : " + soPhieuXacNhan + " phiếu\n" +
                            "Phase 3 cursor: " + soThucTe + " dòng trong báo cáo\n\n" +
                            "\u2192 Có phiếu nhập mới được COMMIT trong lúc delay!\n" +
                            "\u2192 Đây là hiện tượng Phantom Read (READ COMMITTED).\n\n" +
                            "\uD83D\uDCC4 Mở PDF để thấy header ghi " + soPhieuXacNhan
                            + " nhưng bảng có " + soThucTe + " dòng:\n" + pdfPath);
                    } else {
                        // ✅ FIX — Nhất quán
                        hienThiThanhCongLabel(
                            "\u2705 Báo cáo đồng nhất! " + soThucTe +
                            " phiếu — Không phát hiện Phantom Read.");
                        hienThiThongTin(
                            "\u2705 Báo Cáo Nhất Quán — \u00a74.3",
                            "Phase 1 đếm  : " + soPhieuXacNhan + " phiếu\n" +
                            "Phase 3 cursor: " + soThucTe + " dòng trong báo cáo\n\n" +
                            "\u2192 Không phát hiện Phantom Read.\n" +
                            "   (SERIALIZABLE đang bật — snapshot cố định)\n\n" +
                            "\uD83D\uDCC4 PDF báo cáo:\n" + pdfPath);
                    }
                    // Mở PDF tự động
                    try {
                        if (java.awt.Desktop.isDesktopSupported()) {
                            java.awt.Desktop.getDesktop().open(outputFile);
                        }
                    } catch (Exception ignored) { /* không bắt buộc */ }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    btnLapBaoCao.setDisable(false);
                    hienThiLoiLabel("Lỗi lập báo cáo: " + e.getMessage());
                    hienThiThongBaoLoi("Lỗi Lập Báo Cáo Kiểm Kê", e.getMessage());
                });
            }
        }, "thread-lap-bao-cao-kiem-ke-phieunhap");
        thread.setDaemon(true);
        thread.start();
    }
}
