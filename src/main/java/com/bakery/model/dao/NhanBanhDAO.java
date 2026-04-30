package com.bakery.model.dao;

import com.bakery.model.dto.NhanBanhDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhanBanhDAO extends BaseDAO {

    public List<NhanBanhDTO> layDanhSachPhuPhi() throws Exception {
        List<NhanBanhDTO> list = new ArrayList<>();
        String sql = "SELECT MANHAN, TENNHAN, PHUPHI, THOIDIEMXOA, MANX FROM NHANBANH WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                NhanBanhDTO item = new NhanBanhDTO();
                item.setMaNhan(rs.getInt("MANHAN"));
                item.setTenNhan(rs.getString("TENNHAN"));
                item.setPhuPhi(rs.getBigDecimal("PHUPHI"));

                int maNX = rs.getInt("MANX");
                if (!rs.wasNull()) {
                    item.setMaNX(maNX);
                }

                list.add(item);
            }
        } catch (SQLException e) {
            handleException("layDanhSachPhuPhi", e);
        }
        return list;
    }
}
