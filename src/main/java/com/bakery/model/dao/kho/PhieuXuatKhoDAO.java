package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.kho.PhieuXuatKhoDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho PHIEUXUATKHO.
 * Gọi PROC_XUATHUYBANH để xuất hủy thành phẩm.
 */
public class PhieuXuatKhoDAO extends BaseDAO {

    /** Lấy 50 phiếu xuất kho gần nhất. */
    public List<PhieuXuatKhoDTO> layDanhSachPhieuXuat() throws Exception {
        List<PhieuXuatKhoDTO> list = new ArrayList<>();
        String sql = "SELECT PX.MAPX, PX.NGAYXUAT, PX.LYDOXUAT, NV.HOTEN AS TENNV " +
                "FROM PHIEUXUATKHO PX " +
                "LEFT JOIN NHANVIEN NV ON PX.MANV = NV.MANV " +
                "ORDER BY PX.MAPX DESC " +
                "FETCH FIRST 50 ROWS ONLY";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PhieuXuatKhoDTO dto = new PhieuXuatKhoDTO();
                dto.setMaPX(rs.getInt("MAPX"));
                dto.setNgayXuat(rs.getTimestamp("NGAYXUAT") != null
                        ? rs.getTimestamp("NGAYXUAT").toLocalDateTime() : null);
                dto.setLyDoXuat(rs.getString("LYDOXUAT"));
                dto.setTenNhanVien(rs.getString("TENNV"));
                list.add(dto);
            }
        } catch (SQLException e) {
            handleException("layDanhSachPhieuXuat", e);
        }
        return list;
    }

    /**
     * Xuất hủy bánh bảo quản hỏng qua PROC_XUATHUYBANH.
     * LYDOXUAT được hardcode = 'San pham hong' trong Procedure.
     *
     * @param maSP       mã sản phẩm cần hủy
     * @param soLuongHuy số lượng hủy
     * @param maNV       mã nhân viên thực hiện
     */
    public void xuatHuyBanh(int maSP, double soLuongHuy, int maNV) throws Exception {
        String sql = "{CALL PROC_XUATHUYBANH(?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maSP);
            cs.setDouble(2, soLuongHuy);
            cs.setInt(3, maNV);
            cs.execute();
        } catch (SQLException e) {
            handleException("xuatHuyBanh", e);
            throw e;
        }
    }

    /**
     * Xuất kho sản xuất qua PROC_XUATKHOSANXUAT.
     * Procedure tự: kiểm tra đủ NL (Pessimistic Lock) → tạo phiếu → xuất FIFO theo lô.
     * Trigger TRG_XUATSLNGUYENLIEU sẽ tự trừ tồn kho.
     *
     * @param maSP            mã sản phẩm cần làm
     * @param soLuongSanXuat  số lượng bánh cần làm
     * @param maNV            mã nhân viên (thợ bếp) thực hiện
     */
    public void xuatKhoSanXuat(int maSP, double soLuongSanXuat, int maNV) throws Exception {
        String sql = "{CALL PROC_XUATKHOSANXUAT(?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maSP);
            cs.setDouble(2, soLuongSanXuat);
            cs.setInt(3, maNV);
            cs.execute();
        } catch (SQLException e) {
            handleException("xuatKhoSanXuat", e);
            throw e;
        }
    }

    /**
     * Xuất hủy nguyên liệu hỏng qua PROC_XUATNGUYENLIEUHO NG.
     * Procedure tự: kiểm tra tồn kho (Pessimistic Lock) → tạo phiếu xuất
     * → rút lô FIFO → trigger TRG_XUATSLNGUYENLIEU trừ tồn tự động.
     *
     * @param maNL       mã nguyên liệu cần hủy
     * @param soLuongHuy số lượng hủy
     * @param maNV       mã nhân viên thực hiện
     */
    public void xuatHuyNguyenLieu(int maNL, double soLuongHuy, int maNV) throws Exception {
        String sql = "{CALL PROC_XUATNGUYENLIEUHO NG(?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maNL);
            cs.setDouble(2, soLuongHuy);
            cs.setInt(3, maNV);
            cs.execute();
        } catch (SQLException e) {
            handleException("xuatHuyNguyenLieu", e);
            throw e;
        }
    }
}
