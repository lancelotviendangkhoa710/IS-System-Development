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
     * Xử lý ngoại lệ tập trung cho tầng DAO.
     * Log lỗi ra console và ném lại ngoại lệ với thông báo thân thiện.
     * 
     * @param methodName Tên phương thức xảy ra lỗi
     * @param e Ngoại lệ bắt được
     * @throws Exception Ngoại lệ đã qua xử lý
     */
    protected void handleException(String methodName, Exception e) throws Exception {
        System.err.println("Lỗi DAO [" + this.getClass().getSimpleName() + "." + methodName + "]: " + e.getMessage());
        if (e instanceof SQLException) {
            // Có thể bổ sung phân tích SQLState hoặc ErrorCode tại đây để đưa ra thông báo chi tiết hơn
            throw new Exception("Lỗi truy xuất dữ liệu hệ thống: " + e.getMessage());
        }
        throw e;
    }
}
