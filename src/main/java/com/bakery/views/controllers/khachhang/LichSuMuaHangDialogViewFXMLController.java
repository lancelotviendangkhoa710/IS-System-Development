package com.bakery.views.controllers.khachhang;

import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Controller dialog lịch sử mua hàng — thuần UI, có xuất Excel + PDF. */
public class LichSuMuaHangDialogViewFXMLController {

    private static final DateTimeFormatter FMT_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat FMT_TIEN = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));

    static {
        FMT_TIEN.setMaximumFractionDigits(0);
    }

    @FXML private Label  lblTieuDe;
    @FXML private Label  lblTenKhachHang;
    @FXML private Label  lblTongDon;
    @FXML private Label  lblTongChiTieu;
    @FXML private Label  lblTongCoc;
    @FXML private Label  lblThongBao;
    @FXML private Button btnXuatExcel;
    @FXML private Button btnXuatPdf;

    @FXML private TableView<DonDatHangDTO>           tblLichSu;
    @FXML private TableColumn<DonDatHangDTO, String> colMaDon;
    @FXML private TableColumn<DonDatHangDTO, String> colNgayNhan;
    @FXML private TableColumn<DonDatHangDTO, String> colTrangThai;
    @FXML private TableColumn<DonDatHangDTO, String> colHinhThuc;
    @FXML private TableColumn<DonDatHangDTO, String> colTongTien;
    @FXML private TableColumn<DonDatHangDTO, String> colDaCoc;
    @FXML private TableColumn<DonDatHangDTO, String> colConLai;

    private KhachHangDTO         khachHang;
    private List<DonDatHangDTO>  danhSachDon;

    @FXML
    public void initialize() {
        khoiTaoCotBang();
    }

    /** Nạp dữ liệu khách hàng và danh sách đơn vào dialog. */
    public void khoiTao(KhachHangDTO kh, List<DonDatHangDTO> dsDon) {
        this.khachHang    = kh;
        this.danhSachDon  = dsDon;

        lblTieuDe.setText("Lịch sử mua hàng");
        lblTenKhachHang.setText(kh.getHoTen() + " | " + kh.getSdt());

        if (dsDon == null || dsDon.isEmpty()) {
            tblLichSu.setPlaceholder(new Label("Khách hàng chưa có lịch sử giao dịch."));
            capNhatThongKe(List.of());
            return;
        }
        tblLichSu.setItems(FXCollections.observableArrayList(dsDon));
        capNhatThongKe(dsDon);
    }

    // ── FXML handlers ─────────────────────────────────────────────────────

    @FXML
    private void onXuatExcel() {
        if (danhSachDon == null || danhSachDon.isEmpty()) {
            lblThongBao.setText("⚠ Không có dữ liệu để xuất.");
            return;
        }
        FileChooser chooser = xayDungFileChooser("Excel (*.xlsx)", "*.xlsx",
                "LichSu_" + tenFileSach() + ".xlsx");
        File tepTin = chooser.showSaveDialog(layStage());
        if (tepTin == null) return;

        try {
            ghiExcel(tepTin);
            lblThongBao.setText("✅ Đã xuất Excel: " + tepTin.getName());
        } catch (Exception e) {
            lblThongBao.setText("❌ Lỗi xuất Excel: " + e.getMessage());
        }
    }

    @FXML
    private void onXuatPdf() {
        if (danhSachDon == null || danhSachDon.isEmpty()) {
            lblThongBao.setText("⚠ Không có dữ liệu để xuất.");
            return;
        }
        FileChooser chooser = xayDungFileChooser("PDF (*.pdf)", "*.pdf",
                "LichSu_" + tenFileSach() + ".pdf");
        File tepTin = chooser.showSaveDialog(layStage());
        if (tepTin == null) return;

        try {
            ghiPdf(tepTin);
            lblThongBao.setText("✅ Đã xuất PDF: " + tepTin.getName());
        } catch (Exception e) {
            lblThongBao.setText("❌ Lỗi xuất PDF: " + e.getMessage());
        }
    }

    @FXML
    private void onDong() {
        layStage().close();
    }

    // ── private: Excel ────────────────────────────────────────────────────

    private void ghiExcel(File tepTin) throws Exception {
        try (Workbook wb = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(tepTin)) {

            Sheet sheet = wb.createSheet("LichSuMuaHang");
            String[] dauCot = {"Mã đơn", "Ngày nhận", "Trạng thái", "Hình thức",
                               "Tổng tiền (đ)", "Đã cọc (đ)", "Còn lại (đ)"};

            sheet.createRow(0).createCell(0).setCellValue("Khách hàng: " + khachHang.getHoTen());
            sheet.createRow(1).createCell(0).setCellValue("SĐT: " + khachHang.getSdt());
            sheet.createRow(2).createCell(0).setCellValue(
                    "Hạng: " + (khachHang.getTenHang() != null ? khachHang.getTenHang() : "Thành viên"));
            sheet.createRow(3).createCell(0).setCellValue(
                    "Xuất lúc: " + LocalDateTime.now().format(FMT_NGAY_GIO));

            Row dongTieuDe = sheet.createRow(5);
            for (int i = 0; i < dauCot.length; i++) {
                dongTieuDe.createCell(i).setCellValue(dauCot[i]);
            }

            int dong = 6;
            for (DonDatHangDTO d : danhSachDon) {
                Row row = sheet.createRow(dong++);
                BigDecimal tong    = d.getTongTienHDBan() != null ? d.getTongTienHDBan() : BigDecimal.ZERO;
                BigDecimal coc     = d.getTienDaCoc()     != null ? d.getTienDaCoc()     : BigDecimal.ZERO;
                BigDecimal conLai  = tong.subtract(coc);
                row.createCell(0).setCellValue(d.getMaDon());
                row.createCell(1).setCellValue(d.getNgayGioNhanBanh() != null
                        ? d.getNgayGioNhanBanh().format(FMT_NGAY_GIO) : "");
                row.createCell(2).setCellValue(d.getTenTrangThai() != null ? d.getTenTrangThai() : "");
                row.createCell(3).setCellValue(dinhNghiaHinhThuc(d.getHinhThucNhan()));
                row.createCell(4).setCellValue(tong.doubleValue());
                row.createCell(5).setCellValue(coc.doubleValue());
                row.createCell(6).setCellValue(conLai.doubleValue());
            }
            for (int i = 0; i < dauCot.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
        }
    }

    // ── private: PDF ──────────────────────────────────────────────────────

    private void ghiPdf(File tepTin) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage trang = new PDPage(PDRectangle.A4);
            doc.addPage(trang);

            float lePhai   = trang.getMediaBox().getWidth()  - 50;
            float leTren   = trang.getMediaBox().getHeight() - 50;
            float buocDong = 18f;

            try (PDPageContentStream cs = new PDPageContentStream(doc, trang)) {
                PDType1Font fontThuong = PDType1Font.HELVETICA;
                PDType1Font fontDam    = PDType1Font.HELVETICA_BOLD;

                // Tiêu đề
                cs.beginText();
                cs.setFont(fontDam, 16);
                cs.newLineAtOffset(50, leTren);
                cs.showText("LICH SU MUA HANG");
                cs.endText();

                float y = leTren - 25;

                // Thông tin khách
                y = vietDongText(cs, fontDam, fontThuong, 12, 50, y,
                        "Khach hang: ", khachHang.getHoTen());
                y = vietDongText(cs, fontDam, fontThuong, 11, 50, y,
                        "SDT: ", khachHang.getSdt());
                y = vietDongText(cs, fontDam, fontThuong, 11, 50, y,
                        "Hang: ", khachHang.getTenHang() != null ? khachHang.getTenHang() : "Thanh vien");
                y = vietDongText(cs, fontDam, fontThuong, 11, 50, y,
                        "Xuat luc: ", LocalDateTime.now().format(FMT_NGAY_GIO));

                y -= 10;

                // Kẻ đường kẻ header
                cs.setLineWidth(0.5f);
                cs.moveTo(50, y); cs.lineTo(lePhai, y); cs.stroke();
                y -= 5;

                // Header bảng
                String[] headers = {"Ma don", "Ngay nhan", "Trang thai", "Tong tien", "Da coc", "Con lai"};
                float[]  widths  = {55f, 115f, 100f, 100f, 90f, 85f};
                cs.setFont(fontDam, 9);
                float x = 50;
                for (int i = 0; i < headers.length; i++) {
                    vietTextTaiVi(cs, headers[i], x + 2, y - buocDong + 5);
                    x += widths[i];
                }
                y -= buocDong;

                cs.setLineWidth(0.3f);
                cs.moveTo(50, y); cs.lineTo(lePhai, y); cs.stroke();

                // Dữ liệu
                cs.setFont(fontThuong, 9);
                for (DonDatHangDTO d : danhSachDon) {
                    y -= buocDong;
                    if (y < 60) { // trang mới nếu hết chỗ
                        cs.close();
                        break; // đơn giản hóa — không phân trang phức tạp
                    }
                    BigDecimal tong   = d.getTongTienHDBan() != null ? d.getTongTienHDBan() : BigDecimal.ZERO;
                    BigDecimal coc    = d.getTienDaCoc()     != null ? d.getTienDaCoc()     : BigDecimal.ZERO;
                    BigDecimal conLai = tong.subtract(coc);

                    String[] cells = {
                        String.valueOf(d.getMaDon()),
                        d.getNgayGioNhanBanh() != null ? d.getNgayGioNhanBanh().format(FMT_NGAY_GIO) : "",
                        d.getTenTrangThai() != null ? d.getTenTrangThai() : "",
                        FMT_TIEN.format(tong),
                        FMT_TIEN.format(coc),
                        FMT_TIEN.format(conLai)
                    };
                    x = 50;
                    for (int i = 0; i < cells.length; i++) {
                        vietTextTaiVi(cs, cells[i], x + 2, y);
                        x += widths[i];
                    }
                }

                // Tổng kết cuối trang
                y -= 10;
                cs.setLineWidth(0.5f);
                cs.moveTo(50, y); cs.lineTo(lePhai, y); cs.stroke();
                y -= buocDong;

                BigDecimal tongTien = danhSachDon.stream()
                        .map(d -> d.getTongTienHDBan() != null ? d.getTongTienHDBan() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                cs.setFont(fontDam, 10);
                vietTextTaiVi(cs, "Tong so don: " + danhSachDon.size(), 50, y);
                vietTextTaiVi(cs, "Tong chi tieu: " + FMT_TIEN.format(tongTien) + " d", 300, y);
            }

            doc.save(tepTin);
        }
    }

    /** Ghi một dòng "label: value" trả về y mới. */
    private float vietDongText(PDPageContentStream cs, PDType1Font fontDam, PDType1Font fontThuong,
                               float size, float x, float y, String label, String value) throws Exception {
        cs.beginText();
        cs.setFont(fontDam, size);
        cs.newLineAtOffset(x, y);
        cs.showText(label);
        cs.setFont(fontThuong, size);
        cs.showText(value != null ? value : "");
        cs.endText();
        return y - 18;
    }

    private void vietTextTaiVi(PDPageContentStream cs, String text, float x, float y) throws Exception {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(text != null ? text : "");
        cs.endText();
    }

    // ── private: UI helpers ───────────────────────────────────────────────

    private void khoiTaoCotBang() {
        colMaDon.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getMaDon())));
        colNgayNhan.setCellValueFactory(c -> {
            String val = c.getValue().getNgayGioNhanBanh() != null
                    ? c.getValue().getNgayGioNhanBanh().format(FMT_NGAY_GIO) : "—";
            return new SimpleStringProperty(val);
        });
        colTrangThai.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTenTrangThai() != null ? c.getValue().getTenTrangThai() : "—"));
        colHinhThuc.setCellValueFactory(c ->
                new SimpleStringProperty(dinhNghiaHinhThuc(c.getValue().getHinhThucNhan())));
        colTongTien.setCellValueFactory(c ->
                new SimpleStringProperty(dinhDangTien(c.getValue().getTongTienHDBan())));
        colDaCoc.setCellValueFactory(c ->
                new SimpleStringProperty(dinhDangTien(c.getValue().getTienDaCoc())));
        colConLai.setCellValueFactory(c -> {
            BigDecimal tong   = c.getValue().getTongTienHDBan();
            BigDecimal coc    = c.getValue().getTienDaCoc();
            if (tong == null) return new SimpleStringProperty("0 đ");
            return new SimpleStringProperty(dinhDangTien(tong.subtract(coc != null ? coc : BigDecimal.ZERO)));
        });
    }

    private void capNhatThongKe(List<DonDatHangDTO> ds) {
        lblTongDon.setText(String.valueOf(ds.size()));
        BigDecimal tongTien = ds.stream()
                .map(d -> d.getTongTienHDBan() != null ? d.getTongTienHDBan() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tongCoc = ds.stream()
                .map(d -> d.getTienDaCoc() != null ? d.getTienDaCoc() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTongChiTieu.setText(dinhDangTien(tongTien));
        lblTongCoc.setText(dinhDangTien(tongCoc));
    }

    private FileChooser xayDungFileChooser(String moTa, String duoiMoRong, String tenMacDinh) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Lưu báo cáo lịch sử mua hàng");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(moTa, duoiMoRong));
        chooser.setInitialFileName(tenMacDinh);
        return chooser;
    }

    private Stage layStage() {
        return (Stage) tblLichSu.getScene().getWindow();
    }

    private String tenFileSach() {
        return khachHang.getHoTen().replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    private String dinhDangTien(BigDecimal gia) {
        if (gia == null) return "0 đ";
        return FMT_TIEN.format(gia) + " đ";
    }

    private String dinhNghiaHinhThuc(Integer hinhThuc) {
        if (hinhThuc == null) return "—";
        return switch (hinhThuc) {
            case 1 -> "Giao hàng";
            case 2 -> "Tại cửa hàng";
            default -> "Khác";
        };
    }
}
