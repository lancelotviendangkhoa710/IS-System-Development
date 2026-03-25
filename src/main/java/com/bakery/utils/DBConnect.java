package com.bakery.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
            
            String dbUrl = props.getProperty("db.url", "jdbc:oracle:thin:@localhost:1521:xe");
            String dbUser = props.getProperty("db.user", "BAKERY_ADMIN");
            String dbPass = props.getProperty("db.password", "Admin123");

            return DriverManager.getConnection(dbUrl, dbUser, dbPass);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("Kết nối CSDL thất bại! Kiểm tra Oracle Database đang chạy.");
            return null;
        }
    }
}
