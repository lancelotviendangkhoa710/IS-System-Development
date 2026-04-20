package com.bakery.model.dao;

import com.bakery.model.dto.KieuTrangTriDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KieuTrangTriDAO {

    public List<KieuTrangTriDTO> layDanhSachPhuPhi() {
        List<KieuTrangTriDTO> list = new ArrayList<>();
        String sql = "SELECT MATRANGTRI, TENTRANGTRI, PHUPHI, THOIDIEMXOA, MANX FROM KIEUTRANGTRI WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                KieuTrangTriDTO item = new KieuTrangTriDTO();
                item.setMaTrangTri(rs.getInt("MATRANGTRI"));
                item.setTenTrangTri(rs.getString("TENTRANGTRI"));
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
