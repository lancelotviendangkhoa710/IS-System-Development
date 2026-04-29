package com.bakery.model.dao;

import com.bakery.utils.DBConnect;

import java.math.BigDecimal;
import java.sql.*;

public class DoiSoatDAO {

    /**
     * Gọi FUNC_TinhTienMatLyTuong để tính tiền mặt lý tưởng trong két cuối ca.
     * Kết quả là con số BÍ MẬT — tầng Service giữ nội bộ, KHÔNG trả lên View.
     *
     * @return tổng tiền mặt lý tưởng theo hệ thống
     */
    /**
     * Lấy tiền khai báo đầu ca từ bảng DOISOAT.
     */
    public BigDecimal layTienKhaiBaoDauCa(int maCa) {
        String sql = "SELECT NVL(TIENKHAIBAODAUCA, 0) FROM DOISOAT WHERE MACA = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maCa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy tiền khai báo đầu ca: " + e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal tinhTienMatLyTuong(int maCa, BigDecimal tienKhaiBaoDauCa) {
        String sql = "SELECT FUNC_TINHTIENMATLYTUONG(?, ?) FROM DUAL";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maCa);
            ps.setBigDecimal(2, tienKhaiBaoDauCa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }

        } catch (SQLException e) {
            if (e.getErrorCode() >= -20599 && e.getErrorCode() <= -20001) {
                String msg = e.getMessage().replaceAll("ORA-\\d+: ", "").trim();
                throw new RuntimeException(msg, e);
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi hệ thống khi tính tiền mặt lý tưởng: " + e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Gọi PROC_DongCaDoiSoat để ghi kết quả đối soát và đóng ca làm việc.
     * lyDoChenhLech truyền null nếu tiền khớp (chênh lệch = 0).
     */
    public void dongCaDoiSoat(int maCa, BigDecimal tienThucTeDem,
                               BigDecimal chenhLech, String lyDoChenhLech) {
        String sql = "{CALL PROC_DONGCADOISOAT(?, ?, ?, ?)}";

        try (Connection conn = DBConnect.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, maCa);
            cs.setBigDecimal(2, tienThucTeDem);
            cs.setBigDecimal(3, chenhLech);

            if (lyDoChenhLech != null) {
                cs.setString(4, lyDoChenhLech);
            } else {
                cs.setNull(4, Types.NVARCHAR);
            }

            cs.execute();

        } catch (SQLException e) {
            if (e.getErrorCode() >= -20599 && e.getErrorCode() <= -20001) {
                String msg = e.getMessage().replaceAll("ORA-\\d+: ", "").trim();
                throw new RuntimeException(msg, e);
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi hệ thống khi đóng ca đối soát: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        DoiSoatDAO dao = new DoiSoatDAO();

        System.out.println("=== Test DoiSoatDAO ===\n");

        // Test tinhTienMatLyTuong với maCa giả (maCa = 1, tiền khai báo = 500.000đ)
        int maCaTest = 1;
        BigDecimal tienKhaiDao = new BigDecimal("500000");

        System.out.println("Gọi FUNC_TinhTienMatLyTuong với maCa=" + maCaTest
                + ", tienKhaiBaoDauCa=" + tienKhaiDao);
        try {
            BigDecimal tienLyTuong = dao.tinhTienMatLyTuong(maCaTest, tienKhaiDao);
            System.out.println("Tiền mặt lý tưởng (bí mật): " + tienLyTuong);
        } catch (RuntimeException e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}
