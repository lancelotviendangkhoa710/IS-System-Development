package com.bakery.model.dao;

import com.bakery.utils.DBConnect;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Lớp cơ sở cho các lớp DAO trong hệ thống.
 * Cung cấp các phương thức tiện ích để quản lý kết nối và xử lý lỗi DB.
 */
public abstract class BaseDAO {

    /**
     * Mở kết nối tới Database.
     *
     * @return Connection đối tượng kết nối
     * @throws Exception nếu không thể kết nối
     */
    protected Connection moKetNoi() throws Exception {
        Connection connection = DBConnect.getConnection();
        if (connection == null) {
            throw new Exception("Không thể kết nối CSDL. Vui lòng kiểm tra lại cấu hình server.");
        }
        return connection;
    }

    /**
     * Làm sạch thông báo lỗi Oracle — loại bỏ prefix "ORA-xxxxx: " và stack trace.
     * Ví dụ: "ORA-20004: Thông báo lỗi\nORA-06512: at..." → "Thông báo lỗi"
     */
    private static String lamSachThongBaoOracle(String msg) {
        if (msg == null) return "";
        // Tách dòng đầu tiên (bỏ stack trace ORA-06512)
        String dongDau = msg.split("\n")[0].trim();
        // Bỏ tiền tố "ORA-xxxxx: "
        return dongDau.replaceAll("^ORA-\\d+:\\s*", "").trim();
    }

    protected void handleException(String methodName, Exception e) throws Exception {
        System.err.println("Loi DAO [" + this.getClass().getSimpleName() + "." + methodName + "]: " + e.getMessage());
        if (e instanceof SQLException sqle) {
            int code = sqle.getErrorCode();
            String msg = e.getMessage() != null ? e.getMessage() : "";

            if (code == 60 || msg.contains("ORA-00060")) {
                // ORA-00060: deadlock detected (trực tiếp hoặc được gói trong SQLERRM)
                throw new Exception("⚠ Deadlock phát hiện! Giao dịch bị Oracle rollback " +
                        "vì xung đột khóa với phiên khác. Vui lòng thử lại.");
            }

            // ORA-30006 trực tiếp — không qua RAISE_APPLICATION_ERROR (hiếm)
            if (code == 30006 || msg.contains("ORA-30006")) {
                throw new Exception("⏱ Nguyên liệu đang bị phiên khác sử dụng! " +
                        "Hệ thống đã tự rollback. Vui lòng thử lại sau vài giây.");
            }

            // ORA-20004 (ERR_HUY_XUAT_KHO) với marker LOCK_TIMEOUT|<tên NL>
            // Được ném bởi PROC_XUATKHOSANXUAT khi FOR UPDATE WAIT 5 hết thời gian
            if (code == 20004 && msg.contains("LOCK_TIMEOUT|")) {
                String tenNL = trichXuatTenNguyenLieu(msg);
                throw new Exception("⏱ Deadlock — Phiên khác đang khóa nguyên liệu \"" + tenNL + "\"!\n" +
                        "Giao dịch đã tự động rollback sau 5 giây chờ.\n" +
                        "Vui lòng thử lại sau vài giây.");
            }

            if (code == 8177 || msg.contains("ORA-08177") || msg.contains("serialize access")) {
                // ORA-08177: can't serialize access
                throw new Exception("⚠ Xung đột dữ liệu đồng thời! Sản phẩm vừa được cập nhật ở một phiên làm việc khác. " +
                        "Giao dịch tạo đơn hàng bị hủy để bảo vệ tính toàn vẹn. Vui lòng làm mới trang và thử lại.");
            }
            if (code == 2290 || msg.contains("CK_SP_SOLUONGTON")) {
                throw new Exception("⚠ Số lượng sản phẩm trong kho không đủ để thực hiện giao dịch này! " +
                        "Vui lòng cập nhật lại giỏ hàng. (Chi tiết: CK_SP_SOLUONGTON)");
            }

            // Các lỗi RAISE_APPLICATION_ERROR khác (ORA-20xxx) — làm sạch rồi trả thẳng
            if (code >= 20000 && code <= 20999) {
                throw new Exception(lamSachThongBaoOracle(msg));
            }

            throw new Exception("Lỗi truy xuất dữ liệu hệ thống: " + msg);
        }
        throw e;
    }

    /**
     * Trích xuất tên nguyên liệu từ marker LOCK_TIMEOUT trong message Oracle.
     * Format: "ORA-20004: LOCK_TIMEOUT|Đường cát trắng\nORA-06512:..."
     */
    private static String trichXuatTenNguyenLieu(String msg) {
        try {
            int idx = msg.indexOf("LOCK_TIMEOUT|");
            if (idx < 0) return "không xác định";
            String phanSau = msg.substring(idx + "LOCK_TIMEOUT|".length());
            // Cắt tại dòng mới (ORA-06512 stack trace)
            int cuoi = phanSau.indexOf('\n');
            return cuoi > 0 ? phanSau.substring(0, cuoi).trim() : phanSau.trim();
        } catch (Exception ex) {
            return "không xác định";
        }
    }
}

