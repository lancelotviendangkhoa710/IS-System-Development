package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.kho.CongThucDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO truy xuất bảng CONGTHUC (Bill of Materials). */
public class CongThucDAO extends BaseDAO {

    /**
     * Lấy danh sách công thức của một sản phẩm, JOIN với NGUYENLIEU để lấy tên và giá.
     */
    public List<CongThucDTO> layCongThucTheoSP(int maSP) throws Exception {
        List<CongThucDTO> list = new ArrayList<>();
        String sql = "SELECT CT.MASP, CT.MANL, CT.SOLUONGTIEUHAO, " +
                     "NL.TENNL, NVL(NL.GIAVONTRUNGBINH, 0) AS DONGIA " +
                     "FROM CONGTHUC CT " +
                     "JOIN NGUYENLIEU NL ON CT.MANL = NL.MANL " +
                     "WHERE CT.MASP = ? " +
                     "ORDER BY NL.TENNL";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maSP);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CongThucDTO dto = new CongThucDTO();
                    dto.setMaSP(rs.getInt("MASP"));
                    dto.setMaNL(rs.getInt("MANL"));
                    dto.setSoLuongTieuHao(rs.getDouble("SOLUONGTIEUHAO"));
                    dto.setTenNguyenLieu(rs.getString("TENNL"));
                    dto.setDonGia(rs.getDouble("DONGIA"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            handleException("layCongThucTheoSP", e);
        }
        return list;
    }

    /**
     * Upsert (thêm hoặc sửa) một dòng nguyên liệu trong công thức.
     * Gọi PROC_UPSERT_CONGTHUC.
     */
    public void upsertCongThuc(int maSP, int maNL, double soLuongTieuHao) throws Exception {
        String sql = "{CALL PROC_UPSERT_CONGTHUC(?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, maSP);
            cstmt.setInt(2, maNL);
            cstmt.setDouble(3, soLuongTieuHao);
            cstmt.execute();
        } catch (SQLException e) {
            handleException("upsertCongThuc", e);
        }
    }

    /**
     * Xóa một dòng nguyên liệu khỏi công thức sản phẩm.
     * Gọi PROC_XOA_CONGTHUC.
     */
    public void xoaCongThuc(int maSP, int maNL) throws Exception {
        String sql = "{CALL PROC_XOA_CONGTHUC(?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, maSP);
            cstmt.setInt(2, maNL);
            cstmt.execute();
        } catch (SQLException e) {
            handleException("xoaCongThuc", e);
        }
    }

    /**
     * Tính tổng giá vốn của một sản phẩm từ công thức nguyên liệu.
     * Gọi FUNC_TONGGIAVON(P_MASP) đã có sẵn trên DB.
     *
     * @return tổng giá vốn (VND), hoặc 0 nếu chưa có công thức / lỗi
     */
    public double tinhTongGiaVon(int maSP) throws Exception {
        String sql = "{ ? = CALL FUNC_TONGGIAVON(?) }";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.NUMERIC);
            cstmt.setInt(2, maSP);
            cstmt.execute();
            double result = cstmt.getDouble(1);
            return cstmt.wasNull() ? 0.0 : result;
        } catch (SQLException e) {
            handleException("tinhTongGiaVon", e);
        }
        return 0.0;
    }

    /**
     * Kiểm tra sản phẩm có công thức nguyên liệu chưa.
     */
    public boolean coCongThuc(int maSP) throws Exception {
        String sql = "SELECT COUNT(*) FROM CONGTHUC WHERE MASP = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maSP);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            handleException("coCongThuc", e);
        }
        return false;
    }

    /**
     * Số bánh tối đa có thể làm được với tồn kho hiện tại.
     * Gọi FUNC_SOLUONGKHADUNG — DB tìm nguyên liệu thắt cổ chai (MIN logic).
     */
    public double tinhSoLuongKhaDung(int maSP) throws Exception {
        String sql = "{ ? = CALL FUNC_SOLUONGKHADUNG(?) }";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.NUMERIC);
            cstmt.setInt(2, maSP);
            cstmt.execute();
            double result = cstmt.getDouble(1);
            return cstmt.wasNull() ? 0.0 : result;
        } catch (SQLException e) {
            handleException("tinhSoLuongKhaDung", e);
        }
        return 0.0;
    }
}
