package com.bakery.model.dao.hethong;
import com.bakery.model.dao.BaseDAO;

import com.bakery.model.dto.hethong.PhieuThuChiDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuThuChiDAO extends BaseDAO {

    /**
     * Tạo phiếu thu chi mới.
     * MAHD và MAPN đều có thể null → dùng setNull(n, Types.INTEGER).
     */
    public void taoPhieuThuChi(PhieuThuChiDTO ptc) throws Exception {
        try (Connection conn = moKetNoi()) {
            taoPhieuThuChiWithConn(conn, ptc);
        } catch (SQLException e) {
            handleException("taoPhieuThuChi", e);
        }
    }

    /**
     * Overload dùng trong Distributed Transaction.
     * Không đóng Connection; không COMMIT.
     */
    public void taoPhieuThuChi(Connection conn, PhieuThuChiDTO ptc) throws Exception {
        try {
            taoPhieuThuChiWithConn(conn, ptc);
        } catch (SQLException e) {
            handleException("taoPhieuThuChi[tx]", e);
        }
    }

    private void taoPhieuThuChiWithConn(Connection conn, PhieuThuChiDTO ptc) throws SQLException {
        String sql = "INSERT INTO PHIEUTHUCHI "
                + "(MALOAITHUCHI, SOTIEN, MANV, MAHD, MAPN, MACA, GHICHU, TRANGTHAI) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 'active')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ptc.getMaLoaiThuChi());
            ps.setBigDecimal(2, ptc.getSoTien());
            ps.setInt(3, ptc.getMaNV());
            if (ptc.getMaHD() != null) ps.setInt(4, ptc.getMaHD()); else ps.setNull(4, Types.INTEGER);
            if (ptc.getMaPN() != null) ps.setInt(5, ptc.getMaPN()); else ps.setNull(5, Types.INTEGER);
            ps.setInt(6, ptc.getMaCa());
            ps.setString(7, ptc.getGhiChu());
            ps.executeUpdate();
        }
    }

    /**
     * Overload dùng trong Distributed Transaction.
     */
    public int layMaLoaiTheoTen(Connection conn, String tenLoai) throws Exception {
        String sql = "SELECT MALOAITHUCHI FROM LOAITHUCHI WHERE UPPER(TENLOAITHUCHI) = UPPER(?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenLoai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            handleException("layMaLoaiTheoTen[tx]", e);
        }
        return -1;
    }

    /**
     * Tìm mã loại thu chi theo tên — không cần Connection bên ngoài.
     * Khôi phục: ThanhToanService dùng để tra MALOAITHUCHI động thay vì hardcode.
     */
    public int layMaLoaiTheoTen(String tenLoai) throws Exception {
        String sql = "SELECT MALOAITHUCHI FROM LOAITHUCHI WHERE UPPER(TENLOAITHUCHI) = UPPER(?)";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenLoai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            handleException("layMaLoaiTheoTen", e);
        }
        return -1;
    }

    public List<PhieuThuChiDTO> layTheoMaCa(int maCa) throws Exception {
        List<PhieuThuChiDTO> ds = new ArrayList<>();
        String sql = "SELECT p.MAPHIEUTC, p.NGAYTAO, p.MALOAITHUCHI, p.SOTIEN, "
                + "p.MANV, p.MAHD, p.MAPN, p.MACA, p.GHICHU, p.TRANGTHAI, "
                + "l.TENLOAITHUCHI, l.PHANLOAI, nv.HOTEN "
                + "FROM PHIEUTHUCHI p "
                + "JOIN LOAITHUCHI l ON p.MALOAITHUCHI = l.MALOAITHUCHI "
                + "LEFT JOIN NHANVIEN nv ON p.MANV = nv.MANV "
                + "WHERE p.MACA = ? "
                + "ORDER BY p.NGAYTAO DESC";

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maCa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhieuThuChiDTO p = new PhieuThuChiDTO();
                    p.setMaPhieuTC(rs.getInt("MAPHIEUTC"));
                    Timestamp ts = rs.getTimestamp("NGAYTAO");
                    if (ts != null) p.setNgayTao(ts.toLocalDateTime());
                    p.setMaLoaiThuChi(rs.getInt("MALOAITHUCHI"));
                    p.setSoTien(rs.getBigDecimal("SOTIEN"));
                    p.setMaNV(rs.getInt("MANV"));
                    int maHD = rs.getInt("MAHD");
                    if (!rs.wasNull()) p.setMaHD(maHD);
                    int maPN = rs.getInt("MAPN");
                    if (!rs.wasNull()) p.setMaPN(maPN);
                    p.setMaCa(rs.getInt("MACA"));
                    p.setGhiChu(rs.getString("GHICHU"));
                    String tt = rs.getString("TRANGTHAI");
                    p.setTrangThai(tt != null ? tt : "active");
                    p.setTenLoaiThuChi(rs.getString("TENLOAITHUCHI"));
                    p.setTenNhanVien(rs.getString("HOTEN"));
                    p.setPhanLoai(rs.getString("PHANLOAI"));
                    ds.add(p);
                }
            }
        } catch (SQLException e) {
            handleException("layTheoMaCa", e);
        }
        return ds;
    }

    /** Lấy toàn bộ phiếu thu chi không giới hạn ca — dùng cho view "Tất cả ca". */
    public List<PhieuThuChiDTO> layTatCa() throws Exception {
        List<PhieuThuChiDTO> ds = new ArrayList<>();
        String sql = "SELECT p.MAPHIEUTC, p.NGAYTAO, p.MALOAITHUCHI, p.SOTIEN, "
                + "p.MANV, p.MAHD, p.MAPN, p.MACA, p.GHICHU, p.TRANGTHAI, "
                + "l.TENLOAITHUCHI, l.PHANLOAI, nv.HOTEN "
                + "FROM PHIEUTHUCHI p "
                + "JOIN LOAITHUCHI l ON p.MALOAITHUCHI = l.MALOAITHUCHI "
                + "LEFT JOIN NHANVIEN nv ON p.MANV = nv.MANV "
                + "ORDER BY p.NGAYTAO DESC";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhieuThuChiDTO p = new PhieuThuChiDTO();
                p.setMaPhieuTC(rs.getInt("MAPHIEUTC"));
                Timestamp ts = rs.getTimestamp("NGAYTAO");
                if (ts != null) p.setNgayTao(ts.toLocalDateTime());
                p.setMaLoaiThuChi(rs.getInt("MALOAITHUCHI"));
                p.setSoTien(rs.getBigDecimal("SOTIEN"));
                p.setMaNV(rs.getInt("MANV"));
                int maHD = rs.getInt("MAHD"); if (!rs.wasNull()) p.setMaHD(maHD);
                int maPN = rs.getInt("MAPN"); if (!rs.wasNull()) p.setMaPN(maPN);
                p.setMaCa(rs.getInt("MACA"));
                p.setGhiChu(rs.getString("GHICHU"));
                String tt = rs.getString("TRANGTHAI");
                p.setTrangThai(tt != null ? tt : "active");
                p.setTenLoaiThuChi(rs.getString("TENLOAITHUCHI"));
                p.setTenNhanVien(rs.getString("HOTEN"));
                p.setPhanLoai(rs.getString("PHANLOAI"));
                ds.add(p);
            }
        } catch (SQLException e) {
            handleException("layTatCa", e);
        }
        return ds;
    }

    public void huyPhieu(int maPhieuTC, String lyDo) throws Exception {
        String sql = "UPDATE PHIEUTHUCHI SET TRANGTHAI = 'cancelled', "
                + "GHICHU = NVL(GHICHU, '') || ' [Lý do huỷ: ' || ? || ']' "
                + "WHERE MAPHIEUTC = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lyDo);
            ps.setInt(2, maPhieuTC);
            ps.executeUpdate();
        } catch (SQLException e) {
            handleException("huyPhieu", e);
        }
    }

    /**
     * Task 2.3: Tính tổng thu/chi trực tiếp tại DB — tránh load toàn bộ phiếu lên Java.
     * Filter CANCELLED nhất quán với DB (UPPER) thay vì so sánh string Java.
     *
     * @param maCa mã ca cần tổng hợp (0 = toàn bộ ca)
     * @return BigDecimal[2] = {tongThu, tongChi}
     */
    public java.math.BigDecimal[] tinhTongThuChi(int maCa) throws Exception {
        String sql = "SELECT " +
            "  NVL(SUM(CASE WHEN l.PHANLOAI = 'Thu' " +
            "    AND UPPER(NVL(p.TRANGTHAI, 'active')) != 'CANCELLED' THEN p.SOTIEN END), 0) AS TONG_THU, " +
            "  NVL(SUM(CASE WHEN l.PHANLOAI = 'Chi' " +
            "    AND UPPER(NVL(p.TRANGTHAI, 'active')) != 'CANCELLED' THEN p.SOTIEN END), 0) AS TONG_CHI " +
            "FROM PHIEUTHUCHI p " +
            "JOIN LOAITHUCHI l ON p.MALOAITHUCHI = l.MALOAITHUCHI " +
            (maCa > 0 ? "WHERE p.MACA = ?" : "");
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (maCa > 0) ps.setInt(1, maCa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new java.math.BigDecimal[]{
                        rs.getBigDecimal("TONG_THU"),
                        rs.getBigDecimal("TONG_CHI")
                    };
                }
            }
        } catch (SQLException e) {
            handleException("tinhTongThuChi", e);
        }
        return new java.math.BigDecimal[]{java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO};
    }

}

