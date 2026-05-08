package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/** DAO truy xuất bảng CONGTHUC (Bill of Materials). */
public class CongThucDAO extends BaseDAO {

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
     * Dùng để quyết định có nên hiển thị giá vốn BOM trên UI hay không.
     */
    public boolean coCongThuc(int maSP) throws Exception {
        String sql = "SELECT COUNT(*) FROM CONGTHUC WHERE MASP = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maSP);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
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
