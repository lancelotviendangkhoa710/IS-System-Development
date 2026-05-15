package com.bakery.views.controllers.khachhang;

import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.utils.JasperReportUtils;
import com.bakery.utils.ReportPathUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Controller dialog lịch sử mua hàng của khách hàng.
 * Hiển thị tổng đơn, tổng chi tiêu, bảng chi tiết và xuất PDF bằng JasperReports.
 * Template: /reports/lich_su_mua_hang.jrxml
 */
public class LichSuMuaHangDialogViewFXMLController {

    private static final NumberFormat FMT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));
    private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        FMT_TIEN.setMaximumFractionDigits(0);
    }

    @FXML private Label  lblTenKhachHang;
    @FXML private Label  lblTongDon;
    @FXML private Label  lblTongChiTieu;
    @FXML private Label  lblThongBao;
    @FXML private Button btnXuatPDF;

    @FXML private TableView<DonDatHangDTO>           tableDoanhSach;
    @FXML private TableColumn<DonDatHangDTO, String> colMaDon;
    @FXML private TableColumn<DonDatHangDTO, String> colNgayDat;
    @FXML private TableColumn<DonDatHangDTO, String> colTongTien;
    @FXML private TableColumn<DonDatHangDTO, String> colTrangThai;

    // Data được lưu để truyền cho Jasper khi xuất PDF
    private int    maKH = 0;
    private String tenKhach;
    private List<DonDatHangDTO> dsDon;

    @FXML
    public void initialize() {
        colMaDon.setCellValueFactory(c ->
                new SimpleStringProperty("#ORD-" + c.getValue().getMaDon()));

        colNgayDat.setCellValueFactory(c -> {
            if (c.getValue().getNgayLap() != null)
                return new SimpleStringProperty(c.getValue().getNgayLap().format(FMT_NGAY));
            return new SimpleStringProperty("—");
        });

        colTongTien.setCellValueFactory(c -> {
            BigDecimal tien = c.getValue().getTongTienHDBan();
            if (tien == null) tien = BigDecimal.ZERO;
            return new SimpleStringProperty(FMT_TIEN.format(tien) + " đ");
        });

        colTrangThai.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTenTrangThai() != null
                        ? c.getValue().getTenTrangThai() : "—"));

        // Tô màu theo trạng thái
        tableDoanhSach.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(DonDatHangDTO item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("row-done", "row-cancelled", "row-processing");
                if (!empty && item != null && item.getTenTrangThai() != null) {
                    switch (item.getTenTrangThai()) {
                        case "Hoàn thành" -> getStyleClass().add("row-done");
                        case "Đã hủy"     -> getStyleClass().add("row-cancelled");
                        case "Đang xử lý" -> getStyleClass().add("row-processing");
                    }
                }
            }
        });
    }

    /** Nạp dữ liệu khách hàng và danh sách đơn vào dialog. */
    public void khoiTao(KhachHangDTO kh, List<DonDatHangDTO> danhSachDon) {
        maKH     = kh.getMaKH();
        tenKhach = kh.getHoTen() + " | " + kh.getSdt();
        dsDon    = (danhSachDon != null) ? danhSachDon : List.of();

        lblTenKhachHang.setText(tenKhach);
        lblTongDon.setText(String.valueOf(dsDon.size()));

        BigDecimal tongTien = dsDon.stream()
                .map(d -> d.getTongTienHDBan() != null ? d.getTongTienHDBan() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTongChiTieu.setText(FMT_TIEN.format(tongTien) + " đ");

        tableDoanhSach.setItems(FXCollections.observableArrayList(dsDon));

        if (dsDon.isEmpty()) {
            lblThongBao.setText("Khách hàng chưa có lịch sử giao dịch.");
            if (btnXuatPDF != null) btnXuatPDF.setDisable(true);
        }
    }

    /**
     * Xuất lịch sử mua hàng của khách sang PDF bằng JasperReports.
     * Thay thế hoàn toàn cơ chế PDFBox snapshot cũ.
     */
    @FXML
    private void onXuatPDF() {
        if (dsDon == null || dsDon.isEmpty()) {
            lblThongBao.setText("Không có dữ liệu để xuất PDF.");
            return;
        }
        if (btnXuatPDF != null) btnXuatPDF.setDisable(true);
        lblThongBao.setText("Đang xuất PDF...");

        // Tính tổng tiền cho tham số báo cáo
        BigDecimal tongTien = dsDon.stream()
                .map(d -> d.getTongTienHDBan() != null ? d.getTongTienHDBan() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Map DonDatHangDTO → String[] row cho template lich_su_mua_hang.jrxml
        // row = {maDon, ngayMua, tenKhach, monHang, soLuong, soTien, trangThai}
        List<String[]> rows = new ArrayList<>();
        for (DonDatHangDTO don : dsDon) {
            String ngay = don.getNgayLap() != null ? don.getNgayLap().format(FMT_NGAY) : "—";
            BigDecimal tien = don.getTongTienHDBan() != null ? don.getTongTienHDBan() : BigDecimal.ZERO;
            rows.add(new String[]{
                "#ORD-" + don.getMaDon(),                  // maDon
                ngay,                                       // ngayMua
                tenKhach,                                   // tenKhach
                "(xem chi tiết đơn hàng)",                 // monHang
                "—",                                        // soLuong
                FMT_TIEN.format(tien) + " đ",              // soTien
                don.getTenTrangThai() != null ? don.getTenTrangThai() : "—" // trangThai
            });
        }

        String tieuDe   = "Lịch sử mua hàng — " + tenKhach;
        String tongTienStr = FMT_TIEN.format(tongTien) + " đ";
        File outputFile = ReportPathUtils.buildPdfPath("LichSuMuaHang", "KH-" + maKH);

        new Thread(() -> {
            try {
                JasperReportUtils.xuatLichSuMuaHangPDF(
                        outputFile,
                        tieuDe, "—", "—",
                        String.valueOf(dsDon.size()), tongTienStr,
                        tenKhach, rows);

                javafx.application.Platform.runLater(() -> {
                    lblThongBao.setText("✅ Đã lưu PDF tại: " + outputFile.getAbsolutePath());
                    if (btnXuatPDF != null) btnXuatPDF.setDisable(false);
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    lblThongBao.setText("❌ Lỗi xuất PDF: " + ex.getMessage());
                    if (btnXuatPDF != null) btnXuatPDF.setDisable(false);
                });
            }
        }, "lichsu-jasper-pdf").start();
    }

    @FXML
    private void onDong() {
        Stage stage = (Stage) lblTongDon.getScene().getWindow();
        stage.close();
    }
}
