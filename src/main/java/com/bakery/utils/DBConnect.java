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
            e.printStackTrace();
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

    /**
     * Hàm main dùng để test luồng kết nối độc lập
     */
    public static void main(String[] args) {
        System.out.println("Đang kiểm tra kết nối đến Oracle Database...");
        Connection conn = getConnection();

        if (conn != null) {
            System.out.println("Kết nối cơ sở dữ liệu thành công!");

            Statement stmt = null;
            ResultSet rs = null;
            try {
                // Khởi tạo Statement và chạy thử một truy vấn lấy thời gian hệ thống từ Oracle
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT SYSDATE FROM DUAL");

                if (rs.next()) {
                    System.out.println("Truy vấn thử nghiệm thành công. Thời gian Database: " + rs.getTimestamp(1));
                }
            } catch (SQLException e) {
                System.err.println(" Lỗi khi chạy truy vấn test: " + e.getMessage());
            } finally {
                // Đảm bảo tài nguyên được giải phóng bất kể quá trình test có lỗi hay không
                System.out.println("Đang dọn dẹp và đóng các kết nối...");
                closeResultSet(rs);
                closeStatement(stmt);
                closeConnection(conn);
                System.out.println("Đã giải phóng tài nguyên an toàn.");
            }
        } else {
            System.err.println(" Kết nối thất bại. Vui lòng kiểm tra lại URL, Username, Password hoặc trạng thái Service của Oracle.");
        }
    }
}