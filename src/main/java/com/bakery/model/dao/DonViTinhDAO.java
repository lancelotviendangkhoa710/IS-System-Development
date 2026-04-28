package com.bakery.model.dao;

import com.bakery.model.dto.DonViTinhDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO truy xuất bảng DONVITINH.
 * Chỉ cần đọc danh sách để nạp vào ComboBox ở màn hình Nguyên liệu.
 */
public class DonViTinhDAO {

    /**
     * Lấy tất cả đơn vị tính còn hoạt động (chưa bị xóa mềm).
     *
     * @return danh sách DonViTinhDTO, rỗng nếu không có dữ liệu
     */
    public List<DonViTinhDTO> layTatCaDonViTinh() {
        List<DonViTinhDTO> list = new ArrayList<>();
        String sql = "SELECT MADVT, TENDVT, THOIDIEMXOA, MANX " +
                     "FROM DONVITINH " +
                     "WHERE THOIDIEMXOA IS NULL " +
                     "ORDER BY MADVT";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                DonViTinhDTO dto = new DonViTinhDTO();
                dto.setMaDVT(rs.getInt("MADVT"));
                dto.setTenDVT(rs.getString("TENDVT"));

                if (rs.getTimestamp("THOIDIEMXOA") != null) {
                    dto.setThoiDiemXoa(rs.getTimestamp("THOIDIEMXOA").toLocalDateTime());
                }

                int maNX = rs.getInt("MANX");
                if (!rs.wasNull()) {
                    dto.setMaNX(maNX);
                }

                list.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - layTatCaDonViTinh: " + e.getMessage());
        }

        return list;
    }
}
