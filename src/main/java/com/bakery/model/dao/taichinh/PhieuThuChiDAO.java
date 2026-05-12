package com.bakery.model.dao.taichinh;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.taichinh.PhieuThuChiDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho PHIEUTHUCHI.
 * Tạo phiếu qua PROC_TAOPHIEUTHUCHI.
 * Hủy phiếu qua UPDATE trực tiếp (soft-delete, không có procedure riêng).
 */
public class PhieuThuChiDAO extends BaseDAO {

    private static final String SQL_SELECT_BASE =
            "SELECT P.MAPHIEUTC, P.NGAYTAO, P.MALOAITHUCHI, L.TENLOAITHUCHI, L.PHANLOAI, " +
            "       P.SOTIEN, P.MANV, NV.HOTEN AS TENNV, P.MAHD, P.MAPN, P.MACA, P.GHICHU, P.TRANGTHAI " +
            "FROM PHIEUTHUCHI P " +
            "JOIN LOAITHUCHI L ON P.MALOAITHUCHI = L.MALOAITHUCHI " +
            "JOIN NHANVIEN NV ON P.MANV = NV.MANV ";

    /**
     * Lấy danh sách phiếu thu chi trong ca hiện tại (50 gần nhất).
     *
     * @param maCa mã ca làm việc hiện tại
     */
    public List<PhieuThuChiDTO> layDanhSachTheoCa(int maCa) throws Exception {
        List<PhieuThuChiDTO> list = new ArrayList<>();
        String sql = SQL_SELECT_BASE +
                     "WHERE P.MACA = ? " +
                     "ORDER BY P.MAPHIEUTC DESC " +
                     "FETCH FIRST 50 ROWS ONLY";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maCa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            handleException("layDanhSachTheoCa", e);
        }
        return list;
    }

    /**
     * Lấy toàn bộ phiếu thu chi (tối đa 100 bản ghi gần nhất, dùng cho báo cáo).
     */
    public List<PhieuThuChiDTO> layTatCaPhieu() throws Exception {
        List<PhieuThuChiDTO> list = new ArrayList<>();
        String sql = SQL_SELECT_BASE +
                     "ORDER BY P.MAPHIEUTC DESC " +
                     "FETCH FIRST 100 ROWS ONLY";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            handleException("layTatCaPhieu", e);
        }
        return list;
    }

    /**
     * Tạo phiếu thu chi thủ công qua PROC_TAOPHIEUTHUCHI.
     *
     * @param maLoaiThuChi phân loại hạng mục
     * @param soTien       số tiền (>0)
     * @param maNV         nhân viên lập phiếu
     * @param maCa         ca hiện tại
     * @param ghiChu       ghi chú tùy chọn
     * @return mã phiếu mới
     */
    public int taoPhieuThuChi(int maLoaiThuChi, double soTien, int maNV,
                               int maCa, String ghiChu) throws Exception {
        String sql = "{CALL PROC_TAOPHIEUTHUCHI(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maLoaiThuChi);
            cs.setDouble(2, soTien);
            cs.setInt(3, maNV);
            cs.setNull(4, Types.INTEGER);  // P_MAHD  — NULL (phiếu thủ công)
            cs.setNull(5, Types.INTEGER);  // P_MAPN  — NULL (phiếu thủ công)
            cs.setInt(6, maCa);
            if (ghiChu != null && !ghiChu.isBlank()) {
                cs.setString(7, ghiChu);
            } else {
                cs.setNull(7, Types.NVARCHAR);
            }
            cs.registerOutParameter(8, Types.INTEGER);
            cs.execute();
            return cs.getInt(8);
        } catch (SQLException e) {
            handleException("taoPhieuThuChi", e);
            throw e;
        }
    }

    /**
     * Hủy phiếu thu chi (soft-delete, đổi TRANGTHAI = 'cancelled').
     *
     * @param maPhieuTC mã phiếu cần hủy
     */
    public void huyPhieuThuChi(int maPhieuTC) throws Exception {
        String sql = "UPDATE PHIEUTHUCHI SET TRANGTHAI = 'cancelled' WHERE MAPHIEUTC = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maPhieuTC);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new Exception("Không tìm thấy phiếu thu chi #" + maPhieuTC + " để hủy.");
            }
            conn.commit();
        } catch (SQLException e) {
            handleException("huyPhieuThuChi", e);
            throw e;
        }
    }

    // ── Private helper ───────────────────────────────────────────────────

    private PhieuThuChiDTO mapRow(ResultSet rs) throws SQLException {
        PhieuThuChiDTO dto = new PhieuThuChiDTO();
        dto.setMaPhieuTC(rs.getInt("MAPHIEUTC"));
        dto.setNgayTao(rs.getTimestamp("NGAYTAO") != null
                ? rs.getTimestamp("NGAYTAO").toLocalDateTime() : null);
        dto.setMaLoaiThuChi(rs.getInt("MALOAITHUCHI"));
        dto.setTenLoaiThuChi(rs.getString("TENLOAITHUCHI"));
        dto.setPhanLoai(rs.getString("PHANLOAI"));
        dto.setSoTien(rs.getDouble("SOTIEN"));
        if (rs.wasNull()) dto.setSoTien(0);
        dto.setMaNV(rs.getInt("MANV"));
        dto.setTenNhanVien(rs.getString("TENNV"));
        int maHD = rs.getInt("MAHD");
        dto.setMaHD(rs.wasNull() ? null : maHD);
        int maPieuNhap = rs.getInt("MAPN");
        dto.setMaPhieuNhap(rs.wasNull() ? null : maPieuNhap);
        dto.setMaCa(rs.getInt("MACA"));
        dto.setGhiChu(rs.getString("GHICHU"));
        dto.setTrangThai(rs.getString("TRANGTHAI"));
        return dto;
    }
}
