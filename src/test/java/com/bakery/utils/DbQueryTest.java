package com.bakery.utils;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbQueryTest {
    @Test
    public void testQueryNguyenLieu() {
        System.out.println("=== QUERY NGUYENLIEU ===");
        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) {
                System.out.println("Connection is null!");
                return;
            }
            String sql = "SELECT nl.MANL, nl.TENNL, nl.SOLUONGTONTONG, nl.HESOQUYDOI, nl.MADVT, dvt.TENDVT " +
                    "FROM NGUYENLIEU nl " +
                    "JOIN DONVITINH dvt ON nl.MADVT = dvt.MADVT " +
                    "WHERE nl.MANL = 2";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("MANL: " + rs.getInt("MANL"));
                    System.out.println("TENNL: " + rs.getString("TENNL"));
                    System.out.println("SOLUONGTONTONG: " + rs.getDouble("SOLUONGTONTONG"));
                    System.out.println("HESOQUYDOI: " + rs.getDouble("HESOQUYDOI"));
                    System.out.println("MADVT: " + rs.getInt("MADVT"));
                    System.out.println("TENDVT: " + rs.getString("TENDVT"));
                } else {
                    System.out.println("Nguyen lieu with MANL = 5 not found!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
