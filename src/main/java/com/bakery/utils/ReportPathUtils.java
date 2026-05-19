package com.bakery.utils;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Tiện ích tạo đường dẫn output cho PDF và báo cáo.
 * Thư mục lưu: [thư_mục_chạy_app]/report/
 * Tự động tạo thư mục nếu chưa tồn tại.
 */
public final class ReportPathUtils {

    private static final Logger LOGGER = Logger.getLogger(ReportPathUtils.class.getName());
    private static final String REPORT_SUBDIR = "report";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private ReportPathUtils() {
    }

    public static File getReportDir() {
        File dir = new File(System.getProperty("user.dir"), REPORT_SUBDIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                LOGGER.info("[ReportPathUtils] Đã tạo thư mục report: " + dir.getAbsolutePath());
            }
        }
        return dir;
    }

    /**
     * Tạo đường dẫn file PDF với tên chuẩn.
     * 
     * @param prefix Tiền tố tên file, VD: "HoaDon", "BaoCao", "LichSuMuaHang"
     * @param suffix Hậu tố nhận dạng, VD: "INV-001", "KH-5", "THANG_05"
     * @return File đại diện đường dẫn đầy đủ
     */
    public static File buildPdfPath(String prefix, String suffix) {
        String dateStr = LocalDate.now().format(DATE_FMT);
        String fileName = prefix + "_" + suffix + "_" + dateStr + ".pdf";
        // Loại bỏ ký tự không hợp lệ trong tên file
        fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        return new File(getReportDir(), fileName);
    }

    /**
     * Trả về đường dẫn tuyệt đối dạng String để hiển thị cho người dùng.
     */
    public static String buildPdfPathString(String prefix, String suffix) {
        return buildPdfPath(prefix, suffix).getAbsolutePath();
    }

    /**
     * Tạo đường dẫn file Excel (.xlsx) với tên chuẩn.
     */
    public static File buildExcelPath(String prefix, String suffix) {
        String dateStr = LocalDate.now().format(DATE_FMT);
        String fileName = prefix + "_" + suffix + "_" + dateStr + ".xlsx";
        fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        return new File(getReportDir(), fileName);
    }

    /**
     * Tạo đường dẫn file CSV với tên chuẩn.
     */
    public static File buildCsvPath(String prefix, String suffix) {
        String dateStr = LocalDate.now().format(DATE_FMT);
        String fileName = prefix + "_" + suffix + "_" + dateStr + ".csv";
        fileName = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        return new File(getReportDir(), fileName);
    }
}
