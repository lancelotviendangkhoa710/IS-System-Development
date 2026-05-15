package com.bakery.views.controllers.banhang;

import com.bakery.model.dto.banhang.CTDonHangDTO;
import com.bakery.model.dto.banhang.HoaDonDTO;
import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.utils.CurrencyFormatter;
import com.bakery.utils.JasperReportUtils;
import com.bakery.utils.ReportPathUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Controller hiển thị và in hóa đơn bán hàng.
 * In bằng JasperReports (template hoa_don_ban_hang.jrxml — khổ A5).
 */
public class HoaDonViewFXMLController extends BaseController {

    private static final Logger LOGGER = Logger.getLogger(HoaDonViewFXMLController.class.getName());

    @FXML private VBox   receiptContainer;
    @FXML private Label  lblBakeryName;
    @FXML private Label  lblTieuDe;
    @FXML private Label  lblMaDon;
    @FXML private Label  lblMaHoaDon;
    @FXML private Label  lblNgayLap;
    @FXML private Label  lblTenKhach;
    @FXML private VBox   vboxItems;
    @FXML private Label  lblTongTien;
    @FXML private Label  lblGiamGia;
    @FXML private Label  lblGiamGiaLabel;
    @FXML private Label  lblDaThu;
    @FXML private Label  lblPayLabel;
    @FXML private Label  lblTienKhachDua;
    @FXML private Label  lblTienThua;
    @FXML private Label  lblThueVAT;
    @FXML private Label  lblDocChuThanhToan;
    @FXML private Button btnPrint;
    @FXML private Button btnClose;

    private static final NumberFormat FORMAT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));

    // Lưu dữ liệu để Jasper dùng khi in — không parse lại từ Label
    private String _maHoaDon, _maDon, _ngayLap, _tenKhach, _tieuDe;
    private String _tongHang, _thueVAT, _giamGia, _tongThanhToan, _tienKhachDua, _tienThua, _docChu;
    private List<String[]> _rows; // {tenSP, soLuong, donGia, thanhTien}

    public void setReceiptData(String tieuDe, HoaDonDTO hoaDon, DonDatHangDTO donHang,
            List<CTDonHangDTO> cart,
            List<SanPhamDTO> originData,
            String tenKhach, double khachDua, double tienThua, double soTienGiamGia, boolean laDonCoc) {

        if (lblTieuDe != null && tieuDe != null) {
            lblTieuDe.setText(tieuDe);
        }

        Integer maDon = (donHang != null) ? donHang.getMaDon() : hoaDon.getMaDon();
        String strMaDon    = maDon != null ? "#ORD-" + maDon : "N/A";
        String strMaHoaDon = "#INV-" + hoaDon.getMaHD();
        String strNgay = hoaDon.getNgayXuatHd() != null
                ? hoaDon.getNgayXuatHd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        lblMaDon.setText(strMaDon);
        lblMaHoaDon.setText(strMaHoaDon);
        lblNgayLap.setText(strNgay);
        lblTenKhach.setText(tenKhach != null ? tenKhach : "N/A");

        if (laDonCoc) {
            lblPayLabel.setText("TIỀN CỌC:");
            lblGiamGiaLabel.setText("KHẤU TRỪ:");
        } else {
            lblPayLabel.setText("THANH TOÁN:");
            lblGiamGiaLabel.setText("GIẢM GIÁ:");
        }

        double tongTienHD = hoaDon.getTongTienThanhToan() != null
                ? hoaDon.getTongTienThanhToan().doubleValue() : 0.0;

        // Tính toán lại tổng tiền hàng từ giỏ hàng để đảm bảo khớp hiển thị
        double tongHangThucTe = 0;
        if (cart != null) {
            for (CTDonHangDTO item : cart) {
                double dg = item.getDonGia() != null ? item.getDonGia().doubleValue() : 0.0;
                tongHangThucTe += item.getSoLuong() * dg;
            }
        }

        double tienThue      = tongHangThucTe * 0.085;
        double tongSauThue   = tongHangThucTe + tienThue;
        double giamGiaChuan  = Math.max(0, tongSauThue - tongTienHD);

        lblTongTien.setText(FORMAT_TIEN.format(tongHangThucTe) + " đ");
        lblThueVAT.setText(FORMAT_TIEN.format(tienThue) + " đ");
        lblGiamGia.setText("-" + FORMAT_TIEN.format(giamGiaChuan) + " đ");
        lblDaThu.setText(FORMAT_TIEN.format(tongTienHD) + " đ");
        lblTienKhachDua.setText(FORMAT_TIEN.format(khachDua) + " đ");
        lblTienThua.setText(FORMAT_TIEN.format(tienThua) + " đ");
        if (lblDocChuThanhToan != null) {
            lblDocChuThanhToan.setText(CurrencyFormatter.docSoTien(tongTienHD));
        }

        vboxItems.getChildren().clear();
        if (cart != null) {
            for (CTDonHangDTO item : cart) {
                vboxItems.getChildren().add(createItemRow(item, originData));
            }
        }

        // Lưu data cho Jasper print
        _maHoaDon      = strMaHoaDon;
        _maDon         = strMaDon;
        _ngayLap       = strNgay;
        _tenKhach      = tenKhach != null ? tenKhach : "Khách lẻ";
        _tieuDe        = tieuDe != null ? tieuDe : "HÓA ĐƠN BÁN HÀNG";
        _tongHang      = FORMAT_TIEN.format(tongHangThucTe) + " đ";
        _thueVAT       = FORMAT_TIEN.format(tienThue) + " đ";
        _giamGia       = FORMAT_TIEN.format(giamGiaChuan) + " đ";
        _tongThanhToan = FORMAT_TIEN.format(tongTienHD) + " đ";
        _tienKhachDua  = FORMAT_TIEN.format(khachDua) + " đ";
        _tienThua      = FORMAT_TIEN.format(tienThua) + " đ";
        _docChu        = CurrencyFormatter.docSoTien(tongTienHD);

        // Xây danh sách row cho Jasper
        _rows = new ArrayList<>();
        if (cart != null) {
            for (CTDonHangDTO item : cart) {
                String tenSP = "Sản phẩm #" + item.getMaSP();
                if (originData != null) {
                    for (SanPhamDTO sp : originData) {
                        if (sp.getMaSP() == item.getMaSP()) { tenSP = sp.getTenSP(); break; }
                    }
                }
                double dg        = item.getDonGia() != null ? item.getDonGia().doubleValue() : 0.0;
                double thanhTien = item.getSoLuong() * dg;
                _rows.add(new String[]{
                    tenSP,
                    String.valueOf(item.getSoLuong()),
                    FORMAT_TIEN.format(dg) + " đ",
                    FORMAT_TIEN.format(thanhTien) + " đ"
                });
            }
        }
    }

    private HBox createItemRow(CTDonHangDTO item, List<SanPhamDTO> originData) {
        String tenSP = "Sản phẩm #" + item.getMaSP();
        if (originData != null) {
            for (SanPhamDTO sp : originData) {
                if (sp.getMaSP() == item.getMaSP()) { tenSP = sp.getTenSP(); break; }
            }
        }

        HBox row = new HBox();
        row.setSpacing(5);
        row.setAlignment(Pos.CENTER_LEFT);

        Label lblTen = new Label(tenSP);
        lblTen.setPrefWidth(180);
        lblTen.setWrapText(true);
        lblTen.getStyleClass().add("receipt-item-name");

        Label lblSL = new Label("x" + item.getSoLuong());
        lblSL.setPrefWidth(40);
        lblSL.setAlignment(Pos.CENTER);
        lblSL.getStyleClass().add("lbl-body");

        double donGia = item.getDonGia() != null ? item.getDonGia().doubleValue() : 0.0;
        Label lblGia = new Label(FORMAT_TIEN.format(item.getSoLuong() * donGia) + " đ");
        lblGia.setPrefWidth(100);
        lblGia.setAlignment(Pos.CENTER_RIGHT);
        lblGia.getStyleClass().add("receipt-item-price");

        row.getChildren().addAll(lblTen, lblSL, lblGia);
        return row;
    }

    /**
     * In hóa đơn sang PDF bằng JasperReports.
     * File được lưu vào {user.dir}/report/ (do ReportPathUtils.buildPdfPath).
     */
    @FXML
    private void handlePrint() {
        if (_rows == null) {
            hienThiThongBaoLoi("Lỗi", "Chưa có dữ liệu hóa đơn để in.");
            return;
        }
        String maHD = _maHoaDon.replace("#", "").replace("INV-", "");
        File outputFile = ReportPathUtils.buildPdfPath("HoaDon", "INV-" + maHD);
        LOGGER.info("[HoaDon] Chuẩn bị xuất PDF → " + outputFile.getAbsolutePath());

        // JasperReports compile + fill trên background thread (tránh UI freeze)
        Thread thread = new Thread(() -> {
            try {
                JasperReportUtils.xuatHoaDonPDF(
                        outputFile,
                        _maHoaDon, _maDon, _ngayLap, _tenKhach, _tieuDe,
                        _tongHang, _thueVAT, _giamGia,
                        _tongThanhToan, _tienKhachDua, _tienThua, _docChu,
                        _rows);
                LOGGER.info("[HoaDon] Xuất PDF thành công: " + outputFile.getAbsolutePath());
                javafx.application.Platform.runLater(() -> {
                    hienThiThongTin("In hóa đơn thành công",
                            "PDF đã lưu tại:\n" + outputFile.getAbsolutePath());
                    handleClose();
                });
            } catch (Exception e) {
                // e.getMessage() có thể null khi JRException bọc IOException
                String msg = e.getMessage() != null ? e.getMessage()
                        : (e.getCause() != null ? e.getCause().getMessage() : e.getClass().getSimpleName());
                LOGGER.severe("[HoaDon] Lỗi xuất PDF: " + msg);
                javafx.application.Platform.runLater(() ->
                        hienThiThongBaoLoi("Lỗi in hóa đơn", msg));
            }
        }, "in-hoa-don-jasper");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
