package com.bakery.model.dao.banhang;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.banhang.HoaDonDTO;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
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
                if (!rs.wasNull()) hd.setMaDon(maDon);
                hd.setMaCa(rs.getInt("MACA"));
                if (rs.getTimestamp("NGAYXUATHD") != null)
                    hd.setNgayXuatHd(rs.getTimestamp("NGAYXUATHD").toLocalDateTime());
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

    // ---------------------------------------------------------
    // themHoaDonMoi — 2 overloads: standalone & distributed-tx
    // ---------------------------------------------------------

    public int themHoaDonMoi(HoaDonDTO hd) throws Exception {
        try (Connection conn = moKetNoi()) {
            return themHoaDonMoiWithConn(conn, hd);
        } catch (SQLException e) {
            handleException("themHoaDonMoi", e);
        }
        return -1;
    }

    /** Dùng trong Distributed Transaction — Service quản lý Connection/COMMIT. */
    public int themHoaDonMoi(Connection conn, HoaDonDTO hd) throws Exception {
        try {
            return themHoaDonMoiWithConn(conn, hd);
        } catch (SQLException e) {
            handleException("themHoaDonMoi[tx]", e);
        }
        return -1;
    }

    private int themHoaDonMoiWithConn(Connection conn, HoaDonDTO hd) throws SQLException {
        String sql = "INSERT INTO HOADON (MADON, MACA, THUEVAT, TONGTIENTHANHTOAN, MAPTTT, LOAIHD) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"MAHD"})) {
            if (hd.getMaDon() != null) pstmt.setInt(1, hd.getMaDon()); else pstmt.setNull(1, Types.NUMERIC);
            pstmt.setInt(2, hd.getMaCa());
            pstmt.setDouble(3, hd.getThueVAT());
            pstmt.setBigDecimal(4, hd.getTongTienThanhToan());
            pstmt.setInt(5, hd.getMaPTTT());
            pstmt.setString(6, hd.getLoaiHD());
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    // ---------------------------------------------------------
    // layHoaDonTheoMa — 2 overloads
    // ---------------------------------------------------------

    public HoaDonDTO layHoaDonTheoMa(int maHD) throws Exception {
        String sql = "SELECT MAHD, MADON, MACA, NGAYXUATHD, THUEVAT, TONGTIENTHANHTOAN, MAPTTT, LOAIHD "
                + "FROM HOADON WHERE MAHD = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maHD);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapHoaDon(rs);
            }
        } catch (SQLException e) {
            handleException("layHoaDonTheoMa", e);
        }
        return null;
    }

    /** Dùng trong Distributed Transaction — dùng conn đã có sẵn. */
    public HoaDonDTO layHoaDonTheoMa(Connection conn, int maHD) throws Exception {
        String sql = "SELECT MAHD, MADON, MACA, NGAYXUATHD, THUEVAT, TONGTIENTHANHTOAN, MAPTTT, LOAIHD "
                + "FROM HOADON WHERE MAHD = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maHD);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapHoaDon(rs);
            }
        } catch (SQLException e) {
            handleException("layHoaDonTheoMa[tx]", e);
        }
        return null;
    }

    private HoaDonDTO mapHoaDon(ResultSet rs) throws SQLException {
        HoaDonDTO hd = new HoaDonDTO();
        hd.setMaHD(rs.getInt("MAHD"));
        int maDon = rs.getInt("MADON");
        if (!rs.wasNull()) hd.setMaDon(maDon);
        hd.setMaCa(rs.getInt("MACA"));
        if (rs.getTimestamp("NGAYXUATHD") != null)
            hd.setNgayXuatHd(rs.getTimestamp("NGAYXUATHD").toLocalDateTime());
        hd.setThueVAT(rs.getDouble("THUEVAT"));
        hd.setTongTienThanhToan(rs.getBigDecimal("TONGTIENTHANHTOAN"));
        hd.setMaPTTT(rs.getInt("MAPTTT"));
        hd.setLoaiHD(rs.getString("LOAIHD"));
        return hd;
    }

    // ---------------------------------------------------------
    // Các thao tác khác
    // ---------------------------------------------------------

    public void huyHoaDon(int maHD) throws Exception {
        String sql = "UPDATE HOADON SET TRANGTHAI = 'cancelled' WHERE MAHD = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maHD);
            if (pstmt.executeUpdate() == 0)
                throw new Exception("Không tìm thấy hóa đơn MAHD=" + maHD);
        } catch (SQLException e) {
            handleException("huyHoaDon", e);
        }
    }

    public boolean capNhatHoaDon(HoaDonDTO hd) throws Exception {
        String sql = "UPDATE HOADON SET MADON = ?, MACA = ?, THUEVAT = ?, TONGTIENTHANHTOAN = ?, MAPTTT = ?, LOAIHD = ? WHERE MAHD = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (hd.getMaDon() != null) pstmt.setInt(1, hd.getMaDon()); else pstmt.setNull(1, Types.NUMERIC);
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

    // ---------------------------------------------------------
    // thanhToanVaThangHang — 2 overloads
    // ---------------------------------------------------------

    public void thanhToanVaThangHang(int maHD, Integer maKH, double soTienThanhToan) throws Exception {
        try (Connection conn = moKetNoi()) {
            thanhToanVaThangHangWithConn(conn, maHD, maKH, soTienThanhToan);
        } catch (SQLException e) {
            handleException("thanhToanVaThangHang", e);
        }
    }

    /** Dùng trong Distributed Transaction — Service quản lý Connection/COMMIT. */
    public void thanhToanVaThangHang(Connection conn, int maHD, Integer maKH, double soTienThanhToan) throws Exception {
        try {
            thanhToanVaThangHangWithConn(conn, maHD, maKH, soTienThanhToan);
        } catch (SQLException e) {
            handleException("thanhToanVaThangHang[tx]", e);
        }
    }

    private void thanhToanVaThangHangWithConn(Connection conn, int maHD, Integer maKH, double soTienThanhToan) throws SQLException {
        String sql = "{CALL PROC_THANHTOANVATHANGHANG(?, ?, ?)}";
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, maHD);
            if (maKH != null) cstmt.setInt(2, maKH); else cstmt.setNull(2, Types.NUMERIC);
            cstmt.setDouble(3, soTienThanhToan);
            cstmt.execute();
        }
    }
}
