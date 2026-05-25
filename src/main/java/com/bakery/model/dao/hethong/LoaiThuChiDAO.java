package com.bakery.model.dao.hethong;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.hethong.LoaiThuChiDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiThuChiDAO extends BaseDAO {

    /**
     * Lấy danh sách loại thu chi còn hoạt động (chưa bị xóa mềm).
     * Không bao giờ trả null — trả list rỗng nếu không có dữ liệu.
     */
    public List<LoaiThuChiDTO> layDanhSach() throws Exception {
        List<LoaiThuChiDTO> ds = new ArrayList<>();
        String sql = "SELECT MALOAITHUCHI, TENLOAITHUCHI, PHANLOAI, THOIDIEMXOA, MANX "
                + "FROM LOAITHUCHI "
                + "WHERE THOIDIEMXOA IS NULL "
                + "ORDER BY PHANLOAI, TENLOAITHUCHI";

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoaiThuChiDTO ltc = new LoaiThuChiDTO();
                ltc.setMaLoaiThuChi(rs.getInt("MALOAITHUCHI"));
                ltc.setTenLoaiThuChi(rs.getString("TENLOAITHUCHI"));
                ltc.setPhanLoai(rs.getString("PHANLOAI"));
                int maNX = rs.getInt("MANX");
                if (!rs.wasNull()) ltc.setMaNX(maNX);
                ds.add(ltc);
            }
        } catch (SQLException e) {
            handleException("layDanhSach", e);
        }
        return ds;
    }

    /** Lấy tất cả danh mục kể cả đã khoá (THOIDIEMXOA != NULL). */
    public List<LoaiThuChiDTO> layTatCa() throws Exception {
        List<LoaiThuChiDTO> ds = new ArrayList<>();
        String sql = "SELECT MALOAITHUCHI, TENLOAITHUCHI, PHANLOAI, THOIDIEMXOA, MANX "
                + "FROM LOAITHUCHI ORDER BY PHANLOAI, TENLOAITHUCHI";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoaiThuChiDTO ltc = new LoaiThuChiDTO();
                ltc.setMaLoaiThuChi(rs.getInt("MALOAITHUCHI"));
                ltc.setTenLoaiThuChi(rs.getString("TENLOAITHUCHI"));
                ltc.setPhanLoai(rs.getString("PHANLOAI"));
                Timestamp ts = rs.getTimestamp("THOIDIEMXOA");
                if (ts != null) ltc.setThoiDiemXoa(ts.toLocalDateTime());
                int maNX = rs.getInt("MANX");
                if (!rs.wasNull()) ltc.setMaNX(maNX);
                ds.add(ltc);
            }
        } catch (SQLException e) {
            handleException("layTatCa", e);
        }
        return ds;
    }

    /**
     * Mở khoá loại thu chi (chưa có Stored Procedure — dùng SQL trực tiếp).
     * Cần User xác nhận trước khi tạo PROC_MOKHOA_LOAITHUCHI.
     */
    public void moKhoa(int maLoaiThuChi) throws Exception {
        String sql = "UPDATE LOAITHUCHI SET THOIDIEMXOA = NULL, MANX = NULL WHERE MALOAITHUCHI = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maLoaiThuChi);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            handleException("moKhoa", e);
        }
    }

    /** Thêm loại thu chi mới qua PROC_THEM_LOAITHUCHI. */
    public void them(String tenLoaiThuChi, String phanLoai) throws Exception {
        String sql = "{CALL PROC_THEM_LOAITHUCHI(?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, tenLoaiThuChi);
            cs.setString(2, phanLoai);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();
        } catch (SQLException e) {
            handleException("them", e);
            throw e;
        }
    }

    /** Sửa loại thu chi qua PROC_SUA_LOAITHUCHI. */
    public void sua(int maLoaiThuChi, String tenLoaiThuChi, String phanLoai) throws Exception {
        String sql = "{CALL PROC_SUA_LOAITHUCHI(?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maLoaiThuChi);
            cs.setString(2, tenLoaiThuChi);
            cs.setString(3, phanLoai);
            cs.execute();
        } catch (SQLException e) {
            handleException("sua", e);
            throw e;
        }
    }

    /**
     * Xóa mềm loại thu chi qua PROC_XOA_LOAITHUCHI.
     *
     * @param maLoaiThuChi mã loại cần xóa
     * @param maNguoiXoa   mã nhân viên thực hiện (ghi vào cột MANX)
     */
    public void xoa(int maLoaiThuChi, int maNguoiXoa) throws Exception {
        String sql = "{CALL PROC_XOA_LOAITHUCHI(?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maLoaiThuChi);
            cs.setInt(2, maNguoiXoa);
            cs.execute();
        } catch (SQLException e) {
            handleException("xoa", e);
            throw e;
        }
    }
}
