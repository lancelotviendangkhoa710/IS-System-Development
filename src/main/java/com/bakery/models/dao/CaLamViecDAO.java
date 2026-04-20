package com.bakery.models.dao;

import com.bakery.models.dto.CaLamViecDTO;
import com.bakery.utils.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CaLamViecDAO {

    public boolean moCa(int maNV, String maMayPOS) {
        String sql = "INSERT INTO CALAMVIEC (MANV, MAMAYPOS, TRANGTHAI) VALUES (?, ?, 'Đang mở')";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maNV);
            pstmt.setString(2, maMayPOS);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - moCa: " + e.getMessage());
        }
        return false;
    }

    public CaLamViecDTO kiemTraCaDangMo(int maNV) {
        String sql = "SELECT * FROM CALAMVIEC WHERE MANV = ? AND TRANGTHAI = 'Đang mở'";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maNV);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    CaLamViecDTO ca = new CaLamViecDTO();
                    ca.setMaCa(rs.getInt("MACA"));
                    ca.setMaNV(rs.getInt("MANV"));
                    ca.setMaMayPOS(rs.getString("MAMAYPOS"));
                    if (rs.getTimestamp("THOIGIANMOCA") != null) {
                        ca.setThoiGianMoCa(rs.getTimestamp("THOIGIANMOCA").toLocalDateTime());
                    }
                    ca.setTrangThai(rs.getString("TRANGTHAI"));
                    return ca;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - kiemTraCaDangMo: " + e.getMessage());
        }
        return null;
    }
}