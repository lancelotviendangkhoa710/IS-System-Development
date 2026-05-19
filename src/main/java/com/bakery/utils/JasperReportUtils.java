package com.bakery.utils;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Tiện ích xuất báo cáo chuyên nghiệp bằng JasperReports.
 * <p>
 * Hỗ trợ xuất sang PDF và Excel (.xlsx) từ template .jrxml.
 * Template được tổ chức trong {@code /reports/} theo subfolder:
 * <ul>
 *   <li>{@code /reports/hoa_don/}    — Hóa đơn bán hàng</li>
 *   <li>{@code /reports/bao_cao/}    — Báo cáo kinh doanh</li>
 *   <li>{@code /reports/kho/}        — Phiếu nhập/xuất kho</li>
 *   <li>{@code /reports/khach_hang/} — Lịch sử mua hàng</li>
 * </ul>
 */
public final class JasperReportUtils {

    /** Định dạng ngày hiển thị trên báo cáo. */
    private static final DateTimeFormatter DF_DAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JasperReportUtils() {}

    // ─── BÁO CÁO KINH DOANH (PDF + Excel) ───────────────────────────────────

    /**
     * Xuất báo cáo kinh doanh sang PDF bằng JasperReports.
     *
     * @param outputFile   File PDF đích
     * @param tieuDe       Tiêu đề báo cáo (ví dụ: "Báo cáo kinh doanh — Tháng 5/2026")
     * @param kyBaoCao     Kỳ báo cáo (ví dụ: "05/2026")
     * @param doanhThu     Doanh thu dạng chuỗi đã format
     * @param giaVon       Giá vốn dạng chuỗi đã format
     * @param loiNhuan     Lợi nhuận dạng chuỗi đã format
     * @param tongGiaoDich Tổng số giao dịch
     * @param nguoiXuat    Họ tên người xuất báo cáo
     * @param chiTietRows  Danh sách giao dịch: mỗi phần tử là String[]{maDon, tenKhach, monHang, soTien, trangThai}
     * @throws JRException nếu compile/fill thất bại
     */
    public static void xuatPDF(
            File outputFile,
            String tieuDe,
            String kyBaoCao,
            String doanhThu,
            String giaVon,
            String loiNhuan,
            String tongGiaoDich,
            String nguoiXuat,
            List<String[]> chiTietRows) throws JRException {
        xuatPDF(outputFile, tieuDe, kyBaoCao, doanhThu, giaVon,
                loiNhuan, tongGiaoDich, nguoiXuat, chiTietRows, null);
    }

    /**
     * Xuất báo cáo kinh doanh sang PDF kèm biểu đồ LineChart.
     *
     * @param chartStream InputStream PNG của biểu đồ (null = không đính kèm)
     */
    public static void xuatPDF(
            File outputFile,
            String tieuDe,
            String kyBaoCao,
            String doanhThu,
            String giaVon,
            String loiNhuan,
            String tongGiaoDich,
            String nguoiXuat,
            List<String[]> chiTietRows,
            InputStream chartStream) throws JRException {

        JasperPrint print = buildPrintKinhDoanh(tieuDe, kyBaoCao, doanhThu, giaVon,
                loiNhuan, tongGiaoDich, nguoiXuat, chiTietRows, chartStream);
        exportToPdf(print, outputFile, "Báo cáo kinh doanh H3K Bakery");
    }

    /**
     * Xuất báo cáo kinh doanh sang Excel (.xlsx).
     */
    public static void xuatExcel(
            File outputFile,
            String tieuDe,
            String kyBaoCao,
            String doanhThu,
            String giaVon,
            String loiNhuan,
            String tongGiaoDich,
            String nguoiXuat,
            List<String[]> chiTietRows) throws JRException {

        JasperPrint print = buildPrintKinhDoanh(tieuDe, kyBaoCao, doanhThu, giaVon,
                loiNhuan, tongGiaoDich, nguoiXuat, chiTietRows, null); // Excel: không đính biểu đồ
        exportToXlsx(print, outputFile);
    }

    // ─── HÓA ĐƠN BÁN HÀNG (PDF) ─────────────────────────────────────────────

    /**
     * Xuất hóa đơn bán hàng sang PDF — thay thế PDFBox snapshot.
     * Template: {@code /reports/hoa_don/hoa_don_ban_hang.jrxml} (khổ A5)
     *
     * @param outputFile      File PDF đích
     * @param maHoaDon        Mã hóa đơn (ví dụ "#INV-123")
     * @param maDon           Mã đơn hàng (ví dụ "#ORD-45")
     * @param ngayLap         Ngày lập đã format
     * @param tenKhach        Tên khách hàng
     * @param tieuDe          Tiêu đề hóa đơn (vd: "HÓA ĐƠN BÁN HÀNG" hoặc "PHIẾU ĐẶT CỌC")
     * @param tongHang        Tổng tiền hàng đã format
     * @param thueVAT         Tiền thuế đã format
     * @param giamGia         Số tiền giảm giá đã format
     * @param tongThanhToan   Tổng thanh toán đã format
     * @param tienKhachDua    Tiền khách đưa đã format
     * @param tienThua        Tiền thừa đã format
     * @param docChu          Đọc số thành chữ
     * @param rows            Mỗi row = String[]{tenSP, soLuong, donGia, thanhTien}
     * @throws JRException nếu compile/fill thất bại
     */
    public static void xuatHoaDonPDF(
            File outputFile,
            String maHoaDon, String maDon, String ngayLap, String tenKhach, String tieuDe,
            String tongHang, String thueVAT, String giamGia,
            String tongThanhToan, String tienKhachDua, String tienThua, String docChu,
            List<String[]> rows) throws JRException {

        InputStream stream = loadTemplate("/reports/hoa_don/hoa_don_ban_hang.jrxml");
        JasperReport report = JasperCompileManager.compileReport(stream);

        Map<String, Object> params = new HashMap<>();
        params.put("P_MA_HOA_DON",     nvlParam(maHoaDon,      "—"));
        params.put("P_MA_DON",         nvlParam(maDon,         "—"));
        params.put("P_NGAY_LAP",       nvlParam(ngayLap,       "—"));
        params.put("P_TEN_KHACH",      nvlParam(tenKhach,      "Khách lẻ"));
        params.put("P_TIEU_DE",        nvlParam(tieuDe,        "HÓA ĐƠN BÁN HÀNG"));
        params.put("P_TONG_HANG",      nvlParam(tongHang,      "0 đ"));
        params.put("P_THUE_VAT",       nvlParam(thueVAT,       "0 đ"));
        params.put("P_GIAM_GIA",       nvlParam(giamGia,       "0 đ"));
        params.put("P_TONG_THANH_TOAN",nvlParam(tongThanhToan, "0 đ"));
        params.put("P_TIEN_KHACH_DUA", nvlParam(tienKhachDua,  "0 đ"));
        params.put("P_TIEN_THUA",      nvlParam(tienThua,      "0 đ"));
        params.put("P_DOC_CHU",        nvlParam(docChu,        "Không đồng"));

        // row = {tenSP, soLuong, donGia, thanhTien}
        List<Map<String, ?>> dataRows = new ArrayList<>();
        int stt = 1;
        for (String[] r : rows) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("STT",       String.valueOf(stt++));
            m.put("TEN_SP",    safe(r, 0));
            m.put("SO_LUONG",  safe(r, 1));
            m.put("DON_GIA",   safe(r, 2));
            m.put("THANH_TIEN",safe(r, 3));
            dataRows.add(m);
        }

        JasperPrint print = JasperFillManager.fillReport(report, params,
                new JRMapCollectionDataSource(dataRows));
        exportToPdf(print, outputFile, "Hóa đơn bán hàng H3K Bakery");
    }

    // ─── LỊCH SỬ MUA HÀNG (PDF) ──────────────────────────────────────────────

    /**
     * Xuất báo cáo lịch sử mua hàng sang PDF.
     * Template: {@code /reports/khach_hang/lich_su_mua_hang.jrxml}
     *
     * @param outputFile File PDF đích
     * @param tieuDe     Tiêu đề báo cáo
     * @param tuNgay     Từ ngày (dd/MM/yyyy)
     * @param denNgay    Đến ngày (dd/MM/yyyy)
     * @param tongDon    Tổng số đơn hàng
     * @param tongTien   Tổng tiền đã format
     * @param nguoiXuat  Người xuất báo cáo
     * @param rows       Mỗi row = String[]{maDon, ngayMua, tenKhach, monHang, soLuong, soTien, trangThai}
     * @throws JRException nếu compile/fill thất bại
     */
    public static void xuatLichSuMuaHangPDF(
            File outputFile,
            String tieuDe, String tuNgay, String denNgay,
            String tongDon, String tongTien, String nguoiXuat,
            List<String[]> rows) throws JRException {

        InputStream stream = loadTemplate("/reports/khach_hang/lich_su_mua_hang.jrxml");
        JasperReport report = JasperCompileManager.compileReport(stream);

        Map<String, Object> params = new HashMap<>();
        params.put("P_TIEU_DE",    nvlParam(tieuDe,    "Lịch sử mua hàng"));
        params.put("P_TU_NGAY",    nvlParam(tuNgay,    "—"));
        params.put("P_DEN_NGAY",   nvlParam(denNgay,   "—"));
        params.put("P_TONG_DON",   nvlParam(tongDon,   "0"));
        params.put("P_TONG_TIEN",  nvlParam(tongTien,  "0 ₫"));
        params.put("P_NGAY_XUAT",  LocalDate.now().format(DF_DAY));
        params.put("P_NGUOI_XUAT", nvlParam(nguoiXuat, "Hệ thống"));

        // row = {maDon, ngayMua, tenKhach, monHang, soLuong, soTien, trangThai}
        List<Map<String, ?>> dataRows = new ArrayList<>();
        int stt = 1;
        for (String[] r : rows) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("STT",        String.valueOf(stt++));
            m.put("MA_DON",     safe(r, 0));
            m.put("NGAY_MUA",   safe(r, 1));
            m.put("TEN_KHACH",  safe(r, 2));
            m.put("MON_HANG",   safe(r, 3));
            m.put("SO_LUONG",   safe(r, 4));
            m.put("SO_TIEN",    safe(r, 5));
            m.put("TRANG_THAI", safe(r, 6));
            dataRows.add(m);
        }

        JasperPrint print = JasperFillManager.fillReport(report, params,
                new JRMapCollectionDataSource(dataRows));
        exportToPdf(print, outputFile, "Lịch sử mua hàng H3K Bakery");
    }

    // ─── PHIẾU NHẬP KHO (PDF) ────────────────────────────────────────────────

    /**
     * Xuất phiếu nhập kho sang PDF.
     * Template: {@code /reports/kho/phieu_nhap_kho.jrxml}
     *
     * @param outputFile  File PDF đích
     * @param maPhieu     Mã phiếu nhập
     * @param ngayNhap    Ngày nhập đã format (dd/MM/yyyy HH:mm)
     * @param nhaCungCap  Tên nhà cung cấp
     * @param nguoiNhap   Người lập phiếu
     * @param tongTien    Tổng tiền lô hàng đã format
     * @param rows        Mỗi row = String[]{tenNL, soLuong, donGia, thanhTien, hanSuDung, ghiChu}
     * @throws JRException nếu compile/fill thất bại
     */
    public static void xuatPhieuNhapKhoPDF(
            File outputFile,
            String maPhieu, String ngayNhap, String nhaCungCap,
            String nguoiNhap, String tongTien,
            List<String[]> rows) throws JRException {

        InputStream stream = loadTemplate("/reports/kho/phieu_nhap_kho.jrxml");
        JasperReport report = JasperCompileManager.compileReport(stream);

        Map<String, Object> params = new HashMap<>();
        params.put("P_MA_PHIEU",     nvlParam(maPhieu,     "—"));
        params.put("P_NGAY_NHAP",    nvlParam(ngayNhap,    "—"));
        params.put("P_NHA_CUNG_CAP", nvlParam(nhaCungCap,  "—"));
        params.put("P_NGUOI_NHAP",   nvlParam(nguoiNhap,   "—"));
        params.put("P_TONG_TIEN",    nvlParam(tongTien,    "0 ₫"));
        params.put("P_TONG_DONG",    String.valueOf(rows.size()));
        params.put("P_NGAY_IN",      LocalDate.now().format(DF_DAY));

        // row = {tenNL, soLuong, donGia, thanhTien, hanSuDung, ghiChu}
        List<Map<String, ?>> dataRows = new ArrayList<>();
        int stt = 1;
        for (String[] r : rows) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("STT",        String.valueOf(stt++));
            m.put("TEN_NL",     safe(r, 0));
            m.put("SO_LUONG",   safe(r, 1));
            m.put("DON_GIA",    safe(r, 2));
            m.put("THANH_TIEN", safe(r, 3));
            m.put("HAN_SD",     safe(r, 4));
            m.put("GHI_CHU",    safe(r, 5));
            dataRows.add(m);
        }

        JasperPrint print = JasperFillManager.fillReport(report, params,
                new JRMapCollectionDataSource(dataRows));
        exportToPdf(print, outputFile, "Phiếu nhập kho #" + maPhieu);
    }

    // ─── PHIẾU XUẤT KHO (PDF) ────────────────────────────────────────────────

    /**
     * Xuất phiếu xuất kho sang PDF.
     * Template: {@code /reports/kho/phieu_xuat_kho.jrxml}
     *
     * @param outputFile File PDF đích
     * @param maPhieu    Mã phiếu xuất
     * @param ngayXuat   Ngày xuất đã format
     * @param lyDo       Lý do xuất kho
     * @param nguoiXuat  Người lập phiếu
     * @param ghiChu     Ghi chú thêm (có thể null)
     * @param rows       Mỗi row = String[]{tenHang, loaiHang, soLuong, donVi, ghiChu}
     * @throws JRException nếu compile/fill thất bại
     */
    public static void xuatPhieuXuatKhoPDF(
            File outputFile,
            String maPhieu, String ngayXuat, String lyDo,
            String nguoiXuat, String ghiChu,
            List<String[]> rows) throws JRException {

        InputStream stream = loadTemplate("/reports/kho/phieu_xuat_kho.jrxml");
        JasperReport report = JasperCompileManager.compileReport(stream);

        Map<String, Object> params = new HashMap<>();
        params.put("P_MA_PHIEU",   nvlParam(maPhieu,   "—"));
        params.put("P_NGAY_XUAT",  nvlParam(ngayXuat,  "—"));
        params.put("P_LY_DO",      nvlParam(lyDo,      "—"));
        params.put("P_NGUOI_XUAT", nvlParam(nguoiXuat, "—"));
        params.put("P_TONG_DONG",  String.valueOf(rows.size()));
        params.put("P_GHI_CHU",    nvlParam(ghiChu,    "Không có ghi chú"));
        params.put("P_NGAY_IN",    LocalDate.now().format(DF_DAY));

        // row = {tenHang, loaiHang, soLuong, donVi, ghiChu}
        List<Map<String, ?>> dataRows = new ArrayList<>();
        int stt = 1;
        for (String[] r : rows) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("STT",       String.valueOf(stt++));
            m.put("TEN_HANG",  safe(r, 0));
            m.put("LOAI_HANG", safe(r, 1));
            m.put("SO_LUONG",  safe(r, 2));
            m.put("DON_VI",    safe(r, 3));
            m.put("GHI_CHU",   safe(r, 4));
            dataRows.add(m);
        }

        JasperPrint print = JasperFillManager.fillReport(report, params,
                new JRMapCollectionDataSource(dataRows));
        exportToPdf(print, outputFile, "Phiếu xuất kho #" + maPhieu);
    }

    // ─── PRIVATE HELPERS ─────────────────────────────────────────────────────

    /**
     * Compile template báo cáo kinh doanh + fill data → JasperPrint.
     */
    private static JasperPrint buildPrintKinhDoanh(
            String tieuDe, String kyBaoCao,
            String doanhThu, String giaVon, String loiNhuan,
            String tongGiaoDich, String nguoiXuat,
            List<String[]> chiTietRows,
            InputStream chartStream) throws JRException {

        InputStream jrxmlStream = loadTemplate("/reports/bao_cao/bao_cao_kinh_doanh.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

        Map<String, Object> params = new HashMap<>();
        params.put("P_TIEU_DE",        nvlParam(tieuDe,       "Báo cáo kinh doanh"));
        params.put("P_KY_BAO_CAO",     nvlParam(kyBaoCao,     ""));
        params.put("P_DOANH_THU",      nvlParam(doanhThu,     "0 ₫"));
        params.put("P_GIA_VON",        nvlParam(giaVon,       "0 ₫"));
        params.put("P_LOI_NHUAN",      nvlParam(loiNhuan,     "0 ₫"));
        params.put("P_TONG_GIAO_DICH", nvlParam(tongGiaoDich, "0"));
        params.put("P_NGAY_XUAT",      LocalDate.now().format(DF_DAY));
        params.put("P_NGUOI_XUAT",     nvlParam(nguoiXuat,    "Hệ thống"));
        params.put("P_CHART_IMAGE",    chartStream); // null = không hiển thị biểu đồ

        List<Map<String, ?>> rows = new ArrayList<>();
        if (chiTietRows != null) {
            int stt = 1;
            for (String[] row : chiTietRows) {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("STT",        String.valueOf(stt++));
                map.put("MA_DON",     safe(row, 0));
                map.put("TEN_KHACH",  safe(row, 1));
                map.put("MON_HANG",   safe(row, 2));
                map.put("SO_TIEN",    safe(row, 3));
                map.put("TRANG_THAI", safe(row, 4));
                rows.add(map);
            }
        }

        return JasperFillManager.fillReport(jasperReport, params,
                new JRMapCollectionDataSource(rows));
    }

    /**
     * Load template JRXML từ classpath.
     * Dùng ClassLoader thay vì Class.getResourceAsStream để tránh
     * JPMS module-encapsulation block trên Java 17+ open module.
     * Fallback 3 cơ chế: context classloader → class classloader → class resource.
     *
     * @throws JRException nếu không tìm thấy template ở cả 3 cơ chế
     */
    private static InputStream loadTemplate(String path) throws JRException {
        // Chuẩn hóa path: ClassLoader.getResourceAsStream KHÔNG dùng leading '/'
        String clPath = path.startsWith("/") ? path.substring(1) : path;

        // Cơ chế 1: Context ClassLoader (tốt nhất cho JPMS module)
        InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(clPath);

        // Cơ chế 2: Class ClassLoader
        if (stream == null) {
            stream = JasperReportUtils.class.getClassLoader().getResourceAsStream(clPath);
        }

        // Cơ chế 3: Class.getResourceAsStream với leading slash (cũ)
        if (stream == null) {
            stream = JasperReportUtils.class.getResourceAsStream(path);
        }

        if (stream == null) {
            throw new JRException(
                "Không tìm thấy template báo cáo: '" + path + "'. "
                + "Kiểm tra file .jrxml có trong src/main/resources/reports/ "
                + "và Maven đã build resources (mvn process-resources).");
        }
        return stream;
    }

    /** Xuất JasperPrint → PDF file. */
    private static void exportToPdf(JasperPrint print, File outputFile,
                                     String metaTitle) throws JRException {
        outputFile.getParentFile().mkdirs();

        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));

        SimplePdfExporterConfiguration config = new SimplePdfExporterConfiguration();
        config.setMetadataCreator("H3K Bakery Management System");
        config.setMetadataTitle(metaTitle);
        exporter.setConfiguration(config);

        exporter.exportReport();
    }

    /** Xuất JasperPrint → Excel (.xlsx) file. */
    private static void exportToXlsx(JasperPrint print, File outputFile) throws JRException {
        outputFile.getParentFile().mkdirs();

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));

        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
        config.setOnePagePerSheet(false);
        config.setRemoveEmptySpaceBetweenRows(true);
        config.setDetectCellType(true);
        exporter.setConfiguration(config);

        exporter.exportReport();
    }

    /** Lấy phần tử an toàn từ mảng String (null-safe). */
    private static String safe(String[] arr, int index) {
        if (arr == null || index >= arr.length || arr[index] == null) return "";
        return arr[index];
    }

    /** Trả defaultValue khi param null hoặc rỗng. */
    private static String nvlParam(String val, String defaultValue) {
        return (val != null && !val.isEmpty()) ? val : defaultValue;
    }
}
