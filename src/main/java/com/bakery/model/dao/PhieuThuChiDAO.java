package com.bakery.model.dao;

import com.bakery.model.dto.PhieuThuChiDTO;
import com.bakery.utils.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuThuChiDAO {

    /**
     * Tạo phiếu thu chi mới.
     * MAHD và MAPN đều có thể null → dùng setNull(n, Types.INTEGER).
     */
    public void taoPhieuThuChi(PhieuThuChiDTO ptc) {
        String sql = "INSERT INTO PHIEUTHUCHI "
                + "(MALOAITHUCHI, SOTIEN, MANV, MAHD, MAPN, MACA, GHICHU, TRANGTHAI) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 'active')";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ptc.getMaLoaiThuChi());
            ps.setBigDecimal(2, ptc.getSoTien());
            ps.setInt(3, ptc.getMaNV());

            if (ptc.getMaHD() != null) {
                ps.setInt(4, ptc.getMaHD());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (ptc.getMaPN() != null) {
                ps.setInt(5, ptc.getMaPN());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setInt(6, ptc.getMaCa());
            ps.setString(7, ptc.getGhiChu());
            ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() >= -20599 && e.getErrorCode() <= -20001) {
                String msg = e.getMessage().replaceAll("ORA-\\d+: ", "").trim();
                throw new RuntimeException(msg, e);
            }
            System.err.println("[PhieuThuChiDAO] Lỗi: " + e.getMessage());
            throw new RuntimeException("Lỗi hệ thống khi tạo phiếu thu chi: " + e.getMessage(), e);
        }
    }

    public List<PhieuThuChiDTO> layTheoMaCa(int maCa) {
        List<PhieuThuChiDTO> ds = new ArrayList<>();
        String sql = "SELECT p.MAPHIEUTC, p.NGAYTAO, p.MALOAITHUCHI, p.SOTIEN, "
                + "p.MANV, p.MAHD, p.MAPN, p.MACA, p.GHICHU, p.TRANGTHAI, "
                + "l.TENLOAITHUCHI, l.PHANLOAI, nv.HOTEN "
                + "FROM PHIEUTHUCHI p "
                + "JOIN LOAITHUCHI l ON p.MALOAITHUCHI = l.MALOAITHUCHI "
                + "LEFT JOIN NHANVIEN nv ON p.MANV = nv.MANV "
                + "WHERE p.MACA = ? "
                + "ORDER BY p.NGAYTAO DESC";

        try (Connection conn = DBConnect.getConnection();
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
            System.err.println("[PhieuThuChiDAO] Lỗi: " + e.getMessage());
            throw new RuntimeException("Lỗi hệ thống khi lấy giao dịch: " + e.getMessage(), e);
        }
        return ds;
    }

    public void huyPhieu(int maPhieuTC, String lyDo) {
        String sql = "UPDATE PHIEUTHUCHI SET TRANGTHAI = 'cancelled', "
                + "GHICHU = NVL(GHICHU, '') || ' [Lý do huỷ: ' || ? || ']' "
                + "WHERE MAPHIEUTC = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lyDo);
            ps.setInt(2, maPhieuTC);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[PhieuThuChiDAO] Lỗi: " + e.getMessage());
            throw new RuntimeException("Lỗi huỷ phiếu: " + e.getMessage(), e);
        }
    }

}
