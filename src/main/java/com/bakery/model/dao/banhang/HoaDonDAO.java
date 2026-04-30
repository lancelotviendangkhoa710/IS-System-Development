package com.bakery.model.dao.banhang;

import com.bakery.model.dto.banhang.HoaDonDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO extends BaseDAO {

    public List<HoaDonDTO> layDanhSachHoaDon() throws Exception {
        List<HoaDonDTO> ds = new ArrayList<>();
        String sql = "SELECT * FROM HOADON";

        try (Connection conn = moKetNoi();
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
                hd.setTongTienThanhToan(BigDecimal.valueOf(rs.getDouble("TONGTIENTHANHTOAN")));
                hd.setMaPTTT(rs.getInt("MAPTTT"));
                hd.setLoaiHD(rs.getString("LOAIHD"));
                String tt = rs.getString("TRANGTHAI");
                hd.setTrangThai(tt != null ? tt : "active");

                ds.add(hd);
            }
        } catch (SQLException e) {
            handleException("layDanhSachHoaDon", e);
        }
        return ds;
    }

    public int themHoaDonMoi(HoaDonDTO hd) throws Exception {
        String sql = "INSERT INTO HOADON (MADON, MACA, THUEVAT, TONGTIENTHANHTOAN, MAPTTT, LOAIHD) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql, new String[] { "MAHD" })) {

            if (hd.getMaDon() != null)
                pstmt.setInt(1, hd.getMaDon());
            else
                pstmt.setNull(1, java.sql.Types.NUMERIC);

            pstmt.setInt(2, hd.getMaCa());
            pstmt.setDouble(3, hd.getThueVAT());
            pstmt.setBigDecimal(4, hd.getTongTienThanhToan());
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
            handleException("themHoaDonMoi", e);
        }
        return -1;
    }

    public HoaDonDTO layHoaDonTheoMa(int maHD) throws Exception {
        String sql = "SELECT MAHD, MADON, MACA, NGAYXUATHD, THUEVAT, TONGTIENTHANHTOAN, MAPTTT, LOAIHD " +
                "FROM HOADON WHERE MAHD = ?";

        try (Connection conn = moKetNoi();
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
                    hd.setTongTienThanhToan(rs.getBigDecimal("TONGTIENTHANHTOAN"));
                    hd.setMaPTTT(rs.getInt("MAPTTT"));
                    hd.setLoaiHD(rs.getString("LOAIHD"));
                    return hd;
                }
            }
        } catch (SQLException e) {
            handleException("layHoaDonTheoMa", e);
        }
        return null;
    }

    public void huyHoaDon(int maHD) throws Exception {
        String sql = "UPDATE HOADON SET TRANGTHAI = 'cancelled' WHERE MAHD = ?";
        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maHD);
            int rows = pstmt.executeUpdate();
            if (rows == 0)
                throw new Exception("Không tìm thấy hóa đơn MAHD=" + maHD);
        } catch (SQLException e) {
            handleException("huyHoaDon", e);
        }
    }

    public boolean capNhatHoaDon(HoaDonDTO hd) throws Exception {
        String sql = "UPDATE HOADON SET MADON = ?, MACA = ?, THUEVAT = ?, TONGTIENTHANHTOAN = ?, MAPTTT = ?, LOAIHD = ? WHERE MAHD = ?";

        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (hd.getMaDon() != null)
                pstmt.setInt(1, hd.getMaDon());
            else
                pstmt.setNull(1, java.sql.Types.NUMERIC);

            pstmt.setInt(2, hd.getMaCa());
            pstmt.setDouble(3, hd.getThueVAT());
            pstmt.setBigDecimal(4, hd.getTongTienThanhToan());
            pstmt.setInt(5, hd.getMaPTTT());
            pstmt.setString(6, hd.getLoaiHD());
            pstmt.setInt(7, hd.getMaHD());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("capNhatHoaDon", e);
        }
        return false;
    }

    public void thanhToanVaThangHang(int maHD, Integer maKH, double soTienThanhToan) throws Exception {
        String sql = "{CALL PROC_THANHTOANVATHANGHANG(?, ?, ?)}";

        try (Connection conn = moKetNoi()) {
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
            handleException("thanhToanVaThangHang", e);
        }
    }
}
