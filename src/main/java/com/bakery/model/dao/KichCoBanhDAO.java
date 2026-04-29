package com.bakery.model.dao;

import com.bakery.model.dto.KichCoBanhDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KichCoBanhDAO {

    public List<KichCoBanhDTO> layDanhSachPhuPhi() {
        List<KichCoBanhDTO> list = new ArrayList<>();
        String sql = "SELECT MAKC, TENKC, PHUPHI, THOIDIEMXOA, MANX FROM KICHCOBANH WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                KichCoBanhDTO item = new KichCoBanhDTO();
                item.setMaKC(rs.getInt("MAKC"));
                item.setTenKC(rs.getString("TENKC"));
                item.setPhuPhi(rs.getBigDecimal("PHUPHI"));

                int maNX = rs.getInt("MANX");
                if (!rs.wasNull()) {
                    item.setMaNX(maNX);
                }

                list.add(item);
            }
        } catch (SQLException e) {
            System.err.println("[KichCoBanhDAO] Lỗi: " + e.getMessage());
        }
        return list;
    }
}
