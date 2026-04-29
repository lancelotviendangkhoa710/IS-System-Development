package com.bakery.model.dao;

import com.bakery.model.dto.PhuongThucTTDTO;
import com.bakery.utils.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhuongThucTTDAO {

    /**
     * Lấy danh sách phương thức thanh toán còn hoạt động (chưa bị xóa mềm).
     * Không bao giờ trả null — trả list rỗng nếu không có dữ liệu.
     */
    public List<PhuongThucTTDTO> layDanhSach() {
        List<PhuongThucTTDTO> ds = new ArrayList<>();
        String sql = "SELECT MAPTTT, TENPTTT, THOIDIEMXOA, MANX "
                + "FROM PHUONGTHUCTT "
                + "WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhuongThucTTDTO pttt = new PhuongThucTTDTO();
                    pttt.setMaPTTT(rs.getInt("MAPTTT"));
                    pttt.setTenPTTT(rs.getString("TENPTTT"));

                    int maNX = rs.getInt("MANX");
                    if (!rs.wasNull()) {
                        pttt.setMaNX(maNX);
                    }

                    ds.add(pttt);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi hệ thống khi lấy danh sách phương thức thanh toán: " + e.getMessage(), e);
        }
        return ds;
    }

    public static void main(String[] args) {
        PhuongThucTTDAO dao = new PhuongThucTTDAO();

        System.out.println("=== Test PhuongThucTTDAO ===\n");

        try {
            List<PhuongThucTTDTO> ds = dao.layDanhSach();
            System.out.println("Tổng số phương thức thanh toán: " + ds.size());
            for (PhuongThucTTDTO pttt : ds) {
                System.out.printf("  [%d] %s%n",
                        pttt.getMaPTTT(),
                        pttt.getTenPTTT());
            }
        } catch (RuntimeException e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}
