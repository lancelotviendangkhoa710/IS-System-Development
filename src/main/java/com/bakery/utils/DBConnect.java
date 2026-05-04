package com.bakery.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DBConnect {

    public static Connection getConnection() {
        Properties props = new Properties();
        try (InputStream in = DBConnect.class.getResourceAsStream("/application.properties")) {
            if (in != null) {
                props.load(in);
            }

            String dbUrl = props.getProperty("db.url", "jdbc:oracle:thin:@localhost:1521:orcl");
            String dbUser = props.getProperty("db.user", "BAKERY_MANAGER");// Mỗi người có 1 tự thay đổi username cho khớp với local
            String dbPass = props.getProperty("db.password", "Admin123");

            return DriverManager.getConnection(dbUrl, dbUser, dbPass);
        } catch (SQLException | IOException e) {
            System.err.println("[DBConnect] Lỗi: " + e.getMessage());
            System.err.println("Kết nối CSDL thất bại! Kiểm tra Oracle Database đang chạy.");
            return null;
        }
    }

    /**
     * Đóng ResultSet để giải phóng bộ nhớ
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng ResultSet: " + e.getMessage());
            }
        }
    }

    /**
     * hàm đóng Statement.PreparedStatement và CallableStatement đều kế thừa từ Statement,
     * nên hàm này áp dụng được cho cả 3 loại.
     */
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng Statement: " + e.getMessage());
            }
        }
    }

    /**
     * hàm đóng Connection sau khi hoàn tất phiên giao dịch
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng Connection: " + e.getMessage());
            }
        }
    }

}
