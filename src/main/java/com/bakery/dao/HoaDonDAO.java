package com.bakery.dao;

import com.bakery.dto.HoaDonDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    public List<HoaDonDTO> layDanhSachHoaDon() {
        List<HoaDonDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM HOADON";

        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ds.add(mapHoaDon(rs));
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - layDanhSachHoaDon: " + e.getMessage());
        }
        return ds;
    }

    public List<HoaDonDTO> layHoaDonTheoKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgayKhongTinh) {
        List<HoaDonDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM HOADON WHERE NGAYXUATHD >= ? AND NGAYXUATHD < ? ORDER BY NGAYXUATHD, MAHD";

        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(tuNgay));
            pstmt.setTimestamp(2, Timestamp.valueOf(denNgayKhongTinh));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapHoaDon(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Khong the lay hoa don theo khoang thoi gian", e);
        }
        return ds;
    }

    public boolean themHoaDonMoi(HoaDonDTO hd) {
        String sql = "INSERT INTO HOADON (MADON, MACA, THUEVAT, TONGTIENTHANHTOAN, MAPTTT, LOAIHD) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (hd.getMaDon() != null) {
                pstmt.setInt(1, hd.getMaDon());
            } else {
                pstmt.setNull(1, java.sql.Types.NUMERIC);
            }

            pstmt.setInt(2, hd.getMaCa());
            pstmt.setDouble(3, hd.getThueVAT());
            pstmt.setDouble(4, hd.getTongTienThanhToan());
            pstmt.setInt(5, hd.getMaPTTT());
            pstmt.setString(6, hd.getLoaiHD());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi DAO - themHoaDonMoi: " + e.getMessage());
        }
        return false;
    }

    public boolean capNhatHoaDon(HoaDonDTO hd) {
        String sql = "UPDATE HOADON SET MADON = ?, MACA = ?, THUEVAT = ?, TONGTIENTHANHTOAN = ?, MAPTTT = ?, LOAIHD = ? WHERE MAHD = ?";

        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (hd.getMaDon() != null) {
                pstmt.setInt(1, hd.getMaDon());
            } else {
                pstmt.setNull(1, java.sql.Types.NUMERIC);
            }

            pstmt.setInt(2, hd.getMaCa());
            pstmt.setDouble(3, hd.getThueVAT());
            pstmt.setDouble(4, hd.getTongTienThanhToan());
            pstmt.setInt(5, hd.getMaPTTT());
            pstmt.setString(6, hd.getLoaiHD());
            pstmt.setInt(7, hd.getMaHD());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi DAO - capNhatHoaDon: " + e.getMessage());
        }
        return false;
    }

    private static Connection openConnection() throws SQLException {
        Connection conn = DBConnect.getConnection();
        if (conn == null) {
            throw new SQLException("Khong the ket noi Oracle Database");
        }
        return conn;
    }

    private static HoaDonDTO mapHoaDon(ResultSet rs) throws SQLException {
        HoaDonDTO hd = new HoaDonDTO();
        hd.setMaHD(rs.getInt("MAHD"));

        int maDon = rs.getInt("MADON");
        if (!rs.wasNull()) {
            hd.setMaDon(maDon);
        }

        hd.setMaCa(rs.getInt("MACA"));

        Timestamp ngayXuat = rs.getTimestamp("NGAYXUATHD");
        if (ngayXuat != null) {
            hd.setNgayXuatHd(ngayXuat.toLocalDateTime());
        }

        hd.setThueVAT(rs.getDouble("THUEVAT"));
        hd.setTongTienThanhToan(rs.getDouble("TONGTIENTHANHTOAN"));
        hd.setMaPTTT(rs.getInt("MAPTTT"));
        hd.setLoaiHD(rs.getString("LOAIHD"));
        return hd;
    }
}
