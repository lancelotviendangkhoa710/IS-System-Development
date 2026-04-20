package com.bakery.model.dao;

import com.bakery.model.dto.HoaDonDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    public List<HoaDonDTO> layDanhSachHoaDon() {
        List<HoaDonDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM HOADON";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                HoaDonDTO hd = new HoaDonDTO();
                hd.setMaHD(rs.getInt("MAHD"));

                int maDon = rs.getInt("MADON");
                if (!rs.wasNull())
                    hd.setMaDon(maDon);

                hd.setMaCa(rs.getInt("MACA"));

                if (rs.getTimestamp("NGAYXUATHD") != null) {
                    hd.setNgayXuatHd(rs.getTimestamp("NGAYXUATHD").toLocalDateTime());
                }

                hd.setThueVAT(rs.getDouble("THUEVAT"));
                hd.setTongTienThanhToan(rs.getDouble("TONGTIENTHANHTOAN"));
                hd.setMaPTTT(rs.getInt("MAPTTT"));
                hd.setLoaiHD(rs.getString("LOAIHD"));

                ds.add(hd);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - layDanhSachHoaDon: " + e.getMessage());
        }
        return ds;
    }

    public int themHoaDonMoi(HoaDonDTO hd) {
        String sql = "INSERT INTO HOADON (MADON, MACA, THUEVAT, TONGTIENTHANHTOAN, MAPTTT, LOAIHD) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, new String[] { "MAHD" })) {

            if (hd.getMaDon() != null)
                pstmt.setInt(1, hd.getMaDon());
            else
                pstmt.setNull(1, java.sql.Types.NUMERIC);

            pstmt.setInt(2, hd.getMaCa());
            pstmt.setDouble(3, hd.getThueVAT());
            pstmt.setDouble(4, hd.getTongTienThanhToan());
            pstmt.setInt(5, hd.getMaPTTT());
            pstmt.setString(6, hd.getLoaiHD());

            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - themHoaDonMoi: " + e.getMessage());
        }
        return -1;
    }

    public HoaDonDTO layHoaDonTheoMa(int maHD) {
        String sql = "SELECT MAHD, MADON, MACA, NGAYXUATHD, THUEVAT, TONGTIENTHANHTOAN, MAPTTT, LOAIHD " +
                "FROM HOADON WHERE MAHD = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maHD);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    HoaDonDTO hd = new HoaDonDTO();
                    hd.setMaHD(rs.getInt("MAHD"));

                    int maDon = rs.getInt("MADON");
                    if (!rs.wasNull()) {
                        hd.setMaDon(maDon);
                    }

                    hd.setMaCa(rs.getInt("MACA"));
                    if (rs.getTimestamp("NGAYXUATHD") != null) {
                        hd.setNgayXuatHd(rs.getTimestamp("NGAYXUATHD").toLocalDateTime());
                    }
                    hd.setThueVAT(rs.getDouble("THUEVAT"));
                    hd.setTongTienThanhToan(rs.getDouble("TONGTIENTHANHTOAN"));
                    hd.setMaPTTT(rs.getInt("MAPTTT"));
                    hd.setLoaiHD(rs.getString("LOAIHD"));
                    return hd;
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - layHoaDonTheoMa: " + e.getMessage());
        }
        return null;
    }

    public boolean capNhatHoaDon(HoaDonDTO hd) {
        String sql = "UPDATE HOADON SET MADON = ?, MACA = ?, THUEVAT = ?, TONGTIENTHANHTOAN = ?, MAPTTT = ?, LOAIHD = ? WHERE MAHD = ?";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (hd.getMaDon() != null)
                pstmt.setInt(1, hd.getMaDon());
            else
                pstmt.setNull(1, java.sql.Types.NUMERIC);

            pstmt.setInt(2, hd.getMaCa());
            pstmt.setDouble(3, hd.getThueVAT());
            pstmt.setDouble(4, hd.getTongTienThanhToan());
            pstmt.setInt(5, hd.getMaPTTT());
            pstmt.setString(6, hd.getLoaiHD());
            pstmt.setInt(7, hd.getMaHD());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - capNhatHoaDon: " + e.getMessage());
        }
        return false;
    }

    public void thanhToanVaThangHang(int maHD, Integer maKH, double soTienThanhToan) throws SQLException {
        String sql = "{CALL PROC_THANHTOANVATHANGHANG(?, ?, ?)}";

        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) {
                throw new SQLException("Khong the ket noi CSDL.");
            }

            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.setInt(1, maHD);
                if (maKH != null) {
                    cstmt.setInt(2, maKH);
                } else {
                    cstmt.setNull(2, Types.NUMERIC);
                }
                cstmt.setDouble(3, soTienThanhToan);
                cstmt.execute();
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - thanhToanVaThangHang: " + e.getMessage());
            throw e;
        }
    }
}
