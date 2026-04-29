package com.bakery.model.dao;

import com.bakery.model.dto.PhieuThuChiDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhieuThuChiDAO {

    public List<PhieuThuChiDTO> layDanhSachPhieuThuChi() {
        List<PhieuThuChiDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM PHIEUTHUCHI";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                PhieuThuChiDTO ptc = new PhieuThuChiDTO();
                ptc.setMaPhieuTC(rs.getInt("MAPHIEUTC"));

                if (rs.getTimestamp("NGAYTAO") != null) {
                    ptc.setNgayTao(rs.getTimestamp("NGAYTAO").toLocalDateTime());
                }

                ptc.setMaLoaiThuChi(rs.getInt("MALOAITHUCHI"));
                ptc.setSoTien(rs.getDouble("SOTIEN"));
                ptc.setMaNV(rs.getInt("MANV"));

                int maHD = rs.getInt("MAHD");
                if (!rs.wasNull())
                    ptc.setMaHD(maHD);

                int maPN = rs.getInt("MAPN");
                if (!rs.wasNull())
                    ptc.setMaPN(maPN);

                ptc.setMaCa(rs.getInt("MACA"));
                ptc.setGhiChu(rs.getString("GHICHU"));

                ds.add(ptc);
            }
        } catch (SQLException e) {
            System.err.println("Lß╗ùi DAO - layDanhSachPhieuThuChi: " + e.getMessage());
        }
        return ds;
    }

    public boolean themPhieuThuChiMoi(Connection conn, PhieuThuChiDTO ptc) throws SQLException {
        String sql = "INSERT INTO PHIEUTHUCHI (MALOAITHUCHI, SOTIEN, MANV, MAHD, MAPN, MACA, GHICHU) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ptc.getMaLoaiThuChi());
            pstmt.setDouble(2, ptc.getSoTien());
            pstmt.setInt(3, ptc.getMaNV());
            if (ptc.getMaHD() != null)
                pstmt.setInt(4, ptc.getMaHD());
            else
                pstmt.setNull(4, java.sql.Types.NUMERIC);
            if (ptc.getMaPN() != null)
                pstmt.setInt(5, ptc.getMaPN());
            else
                pstmt.setNull(5, java.sql.Types.NUMERIC);
            pstmt.setInt(6, ptc.getMaCa());
            pstmt.setString(7, ptc.getGhiChu());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean themPhieuThuChiMoi(PhieuThuChiDTO ptc) {
        try (Connection conn = DBConnect.getConnection()) {
            return themPhieuThuChiMoi(conn, ptc);
        } catch (SQLException e) {
            System.err.println("Lß╗ùi DAO - themPhieuThuChiMoi: " + e.getMessage());
        }
        return false;
    }

    public boolean capNhatPhieuThuChi(Connection conn, PhieuThuChiDTO ptc) throws SQLException {
        String sql = "UPDATE PHIEUTHUCHI SET MALOAITHUCHI = ?, SOTIEN = ?, MANV = ?, MAHD = ?, MAPN = ?, MACA = ?, GHICHU = ? WHERE MAPHIEUTC = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ptc.getMaLoaiThuChi());
            pstmt.setDouble(2, ptc.getSoTien());
            pstmt.setInt(3, ptc.getMaNV());
            if (ptc.getMaHD() != null)
                pstmt.setInt(4, ptc.getMaHD());
            else
                pstmt.setNull(4, java.sql.Types.NUMERIC);
            if (ptc.getMaPN() != null)
                pstmt.setInt(5, ptc.getMaPN());
            else
                pstmt.setNull(5, java.sql.Types.NUMERIC);
            pstmt.setInt(6, ptc.getMaCa());
            pstmt.setString(7, ptc.getGhiChu());
            pstmt.setInt(8, ptc.getMaPhieuTC());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean capNhatPhieuThuChi(PhieuThuChiDTO ptc) {
        try (Connection conn = DBConnect.getConnection()) {
            return capNhatPhieuThuChi(conn, ptc);
        } catch (SQLException e) {
            System.err.println("Lß╗ùi DAO - capNhatPhieuThuChi: " + e.getMessage());
        }
        return false;
    }
}
