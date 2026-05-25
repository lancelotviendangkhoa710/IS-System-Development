package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.kho.CTPhieuXuatDTO;
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
                        ? rs.getTimestamp("NGAYXUAT").toLocalDateTime()
                        : null);
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
     * Procedure tự: kiểm tra đủ NL (Pessimistic Lock) → tạo phiếu → xuất FIFO theo
     * lô.
     * Trigger TRG_XUATSLNGUYENLIEU sẽ tự trừ tồn kho.
     *
     * @param maSP           mã sản phẩm cần làm
     * @param soLuongSanXuat số lượng bánh cần làm
     * @param maNV           mã nhân viên (thợ bếp) thực hiện
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
     * Xuất kho sản xuất cho nhiều sản phẩm cùng lúc qua PROC_XUATKHOMULTISANXUAT (Xuất mẻ bánh).
     *
     * @param jsonDatalist chuỗi JSON dạng [{"maSP":1,"soLuong":5}]
     * @param maNV         mã nhân viên thực hiện
     */
    public void xuatKhoMultiSanXuat(String jsonDatalist, int maNV) throws Exception {
        String sql = "{CALL PROC_XUATKHOMULTISANXUAT(?, ?)}";
        try (Connection conn = moKetNoi();
                CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, jsonDatalist);
            cs.setInt(2, maNV);
            cs.execute();
        } catch (SQLException e) {
            handleException("xuatKhoMultiSanXuat", e);
            throw e;
        }
    }

    /**
     * Xuất hủy nguyên liệu hỏng qua PROC_XUATNGUYENLIEUHONG.
     * Procedure tự: kiểm tra tồn kho (Pessimistic Lock) → tạo phiếu xuất
     * → rút lô FIFO → trigger TRG_XUATSLNGUYENLIEU trừ tồn tự động.
     *
     * @param maNL       mã nguyên liệu cần hủy
     * @param soLuongHuy số lượng hủy
     * @param maNV       mã nhân viên thực hiện
     */
    public void xuatHuyNguyenLieu(int maNL, double soLuongHuy, int maNV) throws Exception {
        String sql = "{CALL PROC_XUATNGUYENLIEUHONG(?, ?, ?)}";
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

    /**
     * Xuất hủy bánh bị sai sót trong sản xuất qua PROC_XUATSAISOTBANH.
     * LYDOXUAT hardcode = 'Sai sot trong qua trinh lam banh' trong Procedure.
     */
    public void xuatSaiSotBanh(int maSP, double soLuongHuy, int maNV) throws Exception {
        String sql = "{CALL PROC_XUATSAISOTBANH(?, ?, ?)}";
        try (Connection conn = moKetNoi();
                CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, maSP);
            cs.setDouble(2, soLuongHuy);
            cs.setInt(3, maNV);
            cs.execute();
        } catch (SQLException e) {
            handleException("xuatSaiSotBanh", e);
            throw e;
        }
    }

    /**
     * Lấy toàn bộ chi tiết phiếu xuất theo maPX.
     * Gộp 2 nguồn: CTPHIEUXUAT_NL (nguyên liệu) + CTPHIEUXUAT_TP (thành phẩm).
     */
    public List<CTPhieuXuatDTO> layChiTietPhieuXuat(int maPX) throws Exception {
        List<CTPhieuXuatDTO> list = new ArrayList<>();

        // ── Nguyên liệu xuất ──────────────────────────────────────────────────
        String sqlNL =
            "SELECT NL.TENNL, DVT.TENDVT, CX.SOLUONG " +
            "FROM CTPHIEUXUAT_NL CX " +
            "JOIN CTPHIEUNHAP   CTN ON CTN.MALO  = CX.MALO " +
            "JOIN NGUYENLIEU    NL  ON NL.MANL   = CTN.MANL " +
            "JOIN DONVITINH     DVT ON DVT.MADVT = NL.MADVT " +
            "WHERE CX.MAPX = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sqlNL)) {
            ps.setInt(1, maPX);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CTPhieuXuatDTO dto = new CTPhieuXuatDTO();
                    dto.setLoai("NL");
                    dto.setTenHang(rs.getString("TENNL"));
                    dto.setDonViTinh(rs.getString("TENDVT"));
                    dto.setSoLuong(rs.getDouble("SOLUONG"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            handleException("layChiTietPhieuXuat[NL]", e);
        }

        // ── Thành phẩm (bánh) xuất ────────────────────────────────────────────
        String sqlTP =
            "SELECT SP.TENSP, CT.SOLUONG, CT.DONGIAVON " +
            "FROM CTPHIEUXUAT_TP CT " +
            "JOIN SANPHAM SP ON SP.MASP = CT.MASP " +
            "WHERE CT.MAPX = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sqlTP)) {
            ps.setInt(1, maPX);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CTPhieuXuatDTO dto = new CTPhieuXuatDTO();
                    dto.setLoai("TP");
                    dto.setTenHang(rs.getString("TENSP"));
                    dto.setDonViTinh("cái");
                    dto.setSoLuong(rs.getDouble("SOLUONG"));
                    java.math.BigDecimal giaVon = rs.getBigDecimal("DONGIAVON");
                    dto.setDonGiaVon(giaVon);
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            handleException("layChiTietPhieuXuat[TP]", e);
        }

        return list;
    }
}
