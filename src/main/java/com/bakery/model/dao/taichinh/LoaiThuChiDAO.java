package com.bakery.model.dao.taichinh;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.taichinh.LoaiThuChiDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho LOAITHUCHI.
 * Gọi: PROC_THEM_LOAITHUCHI / PROC_SUA_LOAITHUCHI / PROC_XOA_LOAITHUCHI.
 */
public class LoaiThuChiDAO extends BaseDAO {

    /** Lấy tất cả loại thu chi còn hoạt động (THOIDIEMXOA IS NULL). */
    public List<LoaiThuChiDTO> layDanhSachLoaiThuChi() throws Exception {
        List<LoaiThuChiDTO> list = new ArrayList<>();
        String sql = "SELECT MALOAITHUCHI, TENLOAITHUCHI, PHANLOAI " +
                     "FROM LOAITHUCHI " +
                     "WHERE THOIDIEMXOA IS NULL " +
                     "ORDER BY PHANLOAI, TENLOAITHUCHI";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoaiThuChiDTO dto = new LoaiThuChiDTO();
                dto.setMaLoaiThuChi(rs.getInt("MALOAITHUCHI"));
                dto.setTenLoaiThuChi(rs.getString("TENLOAITHUCHI"));
                dto.setPhanLoai(rs.getString("PHANLOAI"));
                list.add(dto);
            }
        } catch (SQLException e) {
            handleException("layDanhSachLoaiThuChi", e);
        }
        return list;
    }

    /**
     * Thêm loại thu chi mới qua PROC_THEM_LOAITHUCHI.
     *
     * @param tenLoaiThuChi tên hạng mục
     * @param phanLoai      'Thu' hoặc 'Chi'
     * @return mã loại thu chi mới được tạo
     */
    public int themLoaiThuChi(String tenLoaiThuChi, String phanLoai) throws Exception {
        String sql = "{CALL PROC_THEM_LOAITHUCHI(?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, tenLoaiThuChi);
            cs.setString(2, phanLoai);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();
            return cs.getInt(3);
        } catch (SQLException e) {
            handleException("themLoaiThuChi", e);
            throw e;
        }
    }

    /**
     * Sửa tên / phân loại qua PROC_SUA_LOAITHUCHI.
     *
     * @param maLoaiThuChi mã loại cần sửa
     * @param tenMoi       tên mới
     * @param phanLoaiMoi  phân loại mới ('Thu' | 'Chi')
     */
    public void suaLoaiThuChi(int maLoaiThuChi, String tenMoi, String phanLoaiMoi) throws Exception {
        String sql = "{CALL PROC_SUA_LOAITHUCHI(?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maLoaiThuChi);
            cs.setString(2, tenMoi);
            cs.setString(3, phanLoaiMoi);
            cs.execute();
        } catch (SQLException e) {
            handleException("suaLoaiThuChi", e);
            throw e;
        }
    }

    /**
     * Soft-delete loại thu chi qua PROC_XOA_LOAITHUCHI.
     *
     * @param maLoaiThuChi mã loại cần xóa
     * @param maNguoiXoa   mã nhân viên thực hiện
     */
    public void xoaLoaiThuChi(int maLoaiThuChi, int maNguoiXoa) throws Exception {
        String sql = "{CALL PROC_XOA_LOAITHUCHI(?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maLoaiThuChi);
            cs.setInt(2, maNguoiXoa);
            cs.execute();
        } catch (SQLException e) {
            handleException("xoaLoaiThuChi", e);
            throw e;
        }
    }
}
