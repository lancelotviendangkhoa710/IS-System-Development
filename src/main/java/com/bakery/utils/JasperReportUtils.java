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
 * Tất cả report template đặt trong {@code /reports/} trên classpath.
 */
public final class JasperReportUtils {

    /** Định dạng ngày hiển thị trên báo cáo. */
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JasperReportUtils() {}

    // ─── XUẤT PDF ────────────────────────────────────────────────────────────

    /**
     * Xuất báo cáo kinh doanh sang PDF.
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

        JasperPrint print = buildPrint(tieuDe, kyBaoCao, doanhThu, giaVon, loiNhuan,
                tongGiaoDich, nguoiXuat, chiTietRows);
        exportToPdf(print, outputFile);
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

        JasperPrint print = buildPrint(tieuDe, kyBaoCao, doanhThu, giaVon, loiNhuan,
                tongGiaoDich, nguoiXuat, chiTietRows);
        exportToXlsx(print, outputFile);
    }

    // ─── PRIVATE HELPERS ─────────────────────────────────────────────────────

    /**
     * Compile template .jrxml và fill dữ liệu → trả về {@link JasperPrint}.
     */
    private static JasperPrint buildPrint(
            String tieuDe, String kyBaoCao,
            String doanhThu, String giaVon, String loiNhuan,
            String tongGiaoDich, String nguoiXuat,
            List<String[]> chiTietRows) throws JRException {

        // 1. Tải template từ classpath
        InputStream jrxmlStream = JasperReportUtils.class
                .getResourceAsStream("/reports/bao_cao_kinh_doanh.jrxml");
        if (jrxmlStream == null) {
            throw new JRException("Không tìm thấy template báo cáo: /reports/bao_cao_kinh_doanh.jrxml");
        }

        // 2. Compile JRXML → JasperReport (compile lúc runtime, nhanh)
        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

        // 3. Chuẩn bị parameters
        Map<String, Object> params = new HashMap<>();
        params.put("P_TIEU_DE",         tieuDe != null ? tieuDe : "Báo cáo kinh doanh");
        params.put("P_KY_BAO_CAO",      kyBaoCao != null ? kyBaoCao : "");
        params.put("P_DOANH_THU",       doanhThu != null ? doanhThu : "0 ₫");
        params.put("P_GIA_VON",         giaVon != null ? giaVon : "0 ₫");
        params.put("P_LOI_NHUAN",       loiNhuan != null ? loiNhuan : "0 ₫");
        params.put("P_TONG_GIAO_DICH",  tongGiaoDich != null ? tongGiaoDich : "0");
        params.put("P_NGAY_XUAT",       LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        params.put("P_NGUOI_XUAT",      nguoiXuat != null ? nguoiXuat : "Hệ thống");

        // 4. Chuẩn bị DataSource từ List<String[]>
        //    String[] = {maDon, tenKhach, monHang, soTien, trangThai}
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

        JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(rows);

        // 5. Fill report
        return JasperFillManager.fillReport(jasperReport, params, dataSource);
    }

    /** Xuất JasperPrint → PDF file. */
    private static void exportToPdf(JasperPrint print, File outputFile) throws JRException {
        outputFile.getParentFile().mkdirs();

        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));

        SimplePdfExporterConfiguration config = new SimplePdfExporterConfiguration();
        config.setCreatorInfo("H3K Bakery Management System");
        config.setMetadataTitle("Báo cáo kinh doanh H3K Bakery");
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
}
