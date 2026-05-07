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
     * Tìm mã loại thu chi theo tên (ví dụ: 'Bán hàng', 'Nhập hàng').
     */
    public int layMaLoaiTheoTen(String tenLoai) throws Exception {
        String sql = "SELECT MALOAITHUCHI FROM LOAITHUCHI WHERE UPPER(TENLOAITHUCHI) = UPPER(?)";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenLoai);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            handleException("layMaLoaiTheoTen", e);
        }
        return -1;
    }

}
