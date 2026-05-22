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

    protected void handleException(String methodName, Exception e) throws Exception {
        System.err.println("Loi DAO [" + this.getClass().getSimpleName() + "." + methodName + "]: " + e.getMessage());
        if (e instanceof SQLException sqle) {
            int code = sqle.getErrorCode();
            if (code == 60) {
                // ORA-00060: deadlock detected
                throw new Exception("⚠ Deadlock phát hiện! Giao dịch bị Oracle rollback " +
                        "vì xung đột khóa với phiên khác. Vui lòng thử lại.");
            }
            if (code == 8177 || (e.getMessage() != null && (e.getMessage().contains("ORA-08177") || e.getMessage().contains("serialize access")))) {
                // ORA-08177: can't serialize access
                throw new Exception("⚠ Xung đột dữ liệu đồng thời! Sản phẩm vừa được cập nhật ở một phiên làm việc khác. " +
                        "Giao dịch tạo đơn hàng bị hủy để bảo vệ tính toàn vẹn. Vui lòng làm mới trang và thử lại.");
            }
            if (code == 2290 || (e.getMessage() != null && e.getMessage().contains("CK_SP_SOLUONGTON"))) {
                throw new Exception("⚠ Số lượng sản phẩm trong kho không đủ để thực hiện giao dịch này! " +
                        "Vui lòng cập nhật lại giỏ hàng. (Chi tiết: CK_SP_SOLUONGTON)");
            }
            throw new Exception("Lỗi truy xuất dữ liệu hệ thống: " + e.getMessage());
        }
        throw e;
    }
}
