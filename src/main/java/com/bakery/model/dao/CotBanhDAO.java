package com.bakery.model.dao;

import com.bakery.model.dto.CotBanhDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CotBanhDAO {

    public List<CotBanhDTO> layDanhSachPhuPhi() {
        List<CotBanhDTO> list = new ArrayList<>();
        String sql = "SELECT MACOT, TENCOT, PHUPHI, THOIDIEMXOA, MANX FROM COTBANH WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                CotBanhDTO item = new CotBanhDTO();
                item.setMaCot(rs.getInt("MACOT"));
                item.setTenCot(rs.getString("TENCOT"));
                item.setPhuPhi(rs.getDouble("PHUPHI"));

                int maNX = rs.getInt("MANX");
                if (!rs.wasNull()) {
                    item.setMaNX(maNX);
                }

                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
