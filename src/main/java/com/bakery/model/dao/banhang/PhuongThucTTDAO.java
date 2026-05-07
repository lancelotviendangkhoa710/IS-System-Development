package com.bakery.model.dao.banhang;

import com.bakery.model.dao.BaseDAO;

import com.bakery.model.dto.banhang.PhuongThucTTDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhuongThucTTDAO extends BaseDAO {

    public List<PhuongThucTTDTO> layDanhSach() throws Exception {
        List<PhuongThucTTDTO> ds = new ArrayList<>();
        String sql = "SELECT MAPTTT, TENPTTT, THOIDIEMXOA, MANX "
                + "FROM PHUONGTHUCTT "
                + "WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = moKetNoi();
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
            handleException("layDanhSach", e);
        }
        return ds;
    }

}
