package com.bakery.model.dao;

import com.bakery.model.dto.LoaiThuChiDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoaiThuChiDAO {

    public List<LoaiThuChiDTO> layDanhSachLoaiThuChi() {
        List<LoaiThuChiDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM LOAITHUCHI WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                LoaiThuChiDTO ltc = new LoaiThuChiDTO();
                ltc.setMaLoaiThuChi(rs.getInt("MALOAITHUCHI"));
                ltc.setTenLoaiThuChi(rs.getString("TENLOAITHUCHI"));
                ltc.setPhanLoai(rs.getString("PHANLOAI"));

                int maNX = rs.getInt("MANX");
                if (!rs.wasNull())
                    ltc.setMaNX(maNX);

                ds.add(ltc);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - layDanhSachLoaiThuChi: " + e.getMessage());
        }
        return ds;
    }

    public boolean themLoaiThuChiMoi(LoaiThuChiDTO ltc) {
        String sql = "INSERT INTO LOAITHUCHI (TENLOAITHUCHI, PHANLOAI) VALUES (?, ?)";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ltc.getTenLoaiThuChi());
            pstmt.setString(2, ltc.getPhanLoai());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - themLoaiThuChiMoi: " + e.getMessage());
        }
        return false;
    }

    public boolean capNhatLoaiThuChi(LoaiThuChiDTO ltc) {
        String sql = "UPDATE LOAITHUCHI SET TENLOAITHUCHI = ?, PHANLOAI = ? WHERE MALOAITHUCHI = ? AND THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ltc.getTenLoaiThuChi());
            pstmt.setString(2, ltc.getPhanLoai());
            pstmt.setInt(3, ltc.getMaLoaiThuChi());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - capNhatLoaiThuChi: " + e.getMessage());
        }
        return false;
    }

    public boolean xoaMemLoaiThuChi(int maLoaiThuChi, int maNX) {
        String sql = "UPDATE LOAITHUCHI SET THOIDIEMXOA = CURRENT_TIMESTAMP, MANX = ? WHERE MALOAITHUCHI = ?";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maNX);
            pstmt.setInt(2, maLoaiThuChi);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - xoaMemLoaiThuChi: " + e.getMessage());
        }
        return false;
    }
}