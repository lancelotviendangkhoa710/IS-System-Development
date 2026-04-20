package com.bakery.model.dao;

import com.bakery.model.dto.DanhMucSPDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DanhMucSPDAO {

    public List<DanhMucSPDTO> layTatCaDanhMucConHoatDong() {
        List<DanhMucSPDTO> list = new ArrayList<>();
        String sql = "SELECT MADM, TENDM, THOIDIEMXOA, MANX " +
                "FROM DANHMUCSP " +
                "WHERE THOIDIEMXOA IS NULL " +
                "ORDER BY MADM";

        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) {
                throw new SQLException("Khong the ket noi CSDL.");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        DanhMucSPDTO dto = new DanhMucSPDTO();
                        dto.setMaDM(rs.getInt("MADM"));
                        dto.setTenDM(rs.getString("TENDM"));

                        if (rs.getTimestamp("THOIDIEMXOA") != null) {
                            dto.setThoiDiemXoa(rs.getTimestamp("THOIDIEMXOA").toLocalDateTime());
                        }

                        int maNX = rs.getInt("MANX");
                        if (!rs.wasNull()) {
                            dto.setMaNX(maNX);
                        }

                        list.add(dto);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - layTatCaDanhMucConHoatDong: " + e.getMessage());
        }

        return list;
    }
}
