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
                // ORA-00060: deadlock thực sự — Oracle đã chọn phiên này làm nạn nhân và tự ROLLBACK.
                // Trong kịch bản BUG §4.4 với FOR UPDATE WAIT N: không đi vào đây (dùng ORA-30006).
                // Trong kịch bản deadlock thực sự: Oracle chỉ kill 1 nạn nhân — phiên kia vẫn tiếp tục.
                throw new Exception("⚠ Deadlock phát hiện! Giao dịch bị Oracle rollback " +
                        "vì xung đột khóa với phiên khác. Vui lòng thử lại.");
            }

            // ORA-20004 với marker DEADLOCK_DETECTED — gói từ ORA-00060 qua RAISE_APPLICATION_ERROR
            // Đảm bảo cả 2 phiên đều hiển Alert nhất quán khi có deadlock.
            if (code == 20004 && msg.contains("DEADLOCK_DETECTED|")) {
                String tenNL = trichXuatTenNguyenLieu(msg, "DEADLOCK_DETECTED|");
                throw new Exception("⚠ Deadlock — §4.4\n" +
                        "Giao dịch của bạn bị Oracle chọn làm nạn nhân (deadlock victim).\n" +
                        "Nguyên liệu đang khóa: \"" + tenNL + "\"\n" +
                        "❌ Giao dịch đã bị ROLLBACK hoàn toàn — không có dữ liệu nào được lưu.");
            }

            // ORA-30006 trực tiếp — không qua RAISE_APPLICATION_ERROR (hiếm)
            if (code == 30006 || msg.contains("ORA-30006")) {
                throw new Exception("⏱ Nguyên liệu đang bị phiên khác sử dụng! " +
                        "Hệ thống đã tự rollback. Vui lòng thử lại sau vài giây.");
            }

            // ORA-20004 với marker LOCK_TIMEOUT — FOR UPDATE WAIT N hết giờ.
            // Kịch bản BUG §4.4: cả 2 phiên đồng thời timeout → cả 2 ROLLBACK → cả 2 section báo lỗi này.
            if (code == 20004 && msg.contains("LOCK_TIMEOUT|")) {
                String tenNL = trichXuatTenNguyenLieu(msg, "LOCK_TIMEOUT|");
                throw new Exception("⏱ Deadlock — §4.4\n" +
                        "Nguyên liệu \"" + tenNL + "\" đang bị phiên khác giữ khóa.\n" +
                        "❌ Giao dịch của bạn đã bị ROLLBACK hoàn toàn — không có dữ liệu nào được lưu.\n" +
                        "(Phiên kia cũng nhận cùng lỗi này nếu chạy đồng thời.)");
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
     * Trích xuất phần tên nguyên liệu sau marker trong message Oracle.
     * Hỗ trợ 2 dạng: {@code LOCK_TIMEOUT|<tenNL>} và {@code DEADLOCK_DETECTED|<tenNL>}.
     *
     * @param msg    chuỗi message từ Oracle (có thể chứa ORA-06512 stack trace)
     * @param marker tiền tố cần tìm, ví dụ: {@code "LOCK_TIMEOUT|"}
     * @return tên nguyên liệu, hoặc {@code "không xác định"} nếu không tìm thấy
     */
    private static String trichXuatTenNguyenLieu(String msg, String marker) {
        try {
            int idx = msg.indexOf(marker);
            if (idx < 0) return "không xác định";
            String phanSau = msg.substring(idx + marker.length());
            // Cắt tại dòng mới (ORA-06512 stack trace)
            int cuoi = phanSau.indexOf('\n');
            return cuoi > 0 ? phanSau.substring(0, cuoi).trim() : phanSau.trim();
        } catch (Exception ex) {
            return "không xác định";
        }
    }
}

