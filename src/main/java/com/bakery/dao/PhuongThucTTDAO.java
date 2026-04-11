package com.bakery.dao;

import com.bakery.dto.PhuongThucTTDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhuongThucTTDAO {

    public List<PhuongThucTTDTO> layDanhSachPhuongThucTT() {
        List<PhuongThucTTDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM PHUONGTHUCTT WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PhuongThucTTDTO pttt = new PhuongThucTTDTO();
                pttt.setMaPTTT(rs.getInt("MAPTTT"));
                pttt.setTenPTTT(rs.getString("TENPTTT"));

                int maNX = rs.getInt("MANX");
                if (!rs.wasNull())
                    pttt.setMaNX(maNX);

                ds.add(pttt);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - layDanhSachPhuongThucTt: " + e.getMessage());
        }
        return ds;
    }

    public boolean themPhuongThucTTMoi(PhuongThucTTDTO pttt) {
        String sql = "INSERT INTO PHUONGTHUCTT (TENPTTT) VALUES (?)";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pttt.getTenPTTT());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - themPhuongThucTtMoi: " + e.getMessage());
        }
        return false;
    }

    public boolean capNhatPhuongThucTT(PhuongThucTTDTO pttt) {
        String sql = "UPDATE PHUONGTHUCTT SET TENPTTT = ? WHERE MAPTTT = ? AND THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pttt.getTenPTTT());
            pstmt.setInt(2, pttt.getMaPTTT());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - capNhatPhuongThucTt: " + e.getMessage());
        }
        return false;
    }

    public boolean xoaMemPhuongThucTT(int maPTTT, int maNX) {
        String sql = "UPDATE PHUONGTHUCTT SET THOIDIEMXOA = CURRENT_TIMESTAMP, MANX = ? WHERE MAPTTT = ?";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maNX);
            pstmt.setInt(2, maPTTT);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - xoaMemPhuongThucTt: " + e.getMessage());
        }
        return false;
    }
}