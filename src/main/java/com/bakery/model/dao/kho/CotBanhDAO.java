package com.bakery.model.dao.kho;
import com.bakery.model.dao.BaseDAO;

import com.bakery.model.dto.kho.CotBanhDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CotBanhDAO extends BaseDAO {

    public List<CotBanhDTO> layDanhSachPhuPhi() throws Exception {
        List<CotBanhDTO> list = new ArrayList<>();
        String sql = "SELECT MACOT, TENCOT, PHUPHI, THOIDIEMXOA, MANX FROM COTBANH WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                CotBanhDTO item = new CotBanhDTO();
                item.setMaCot(rs.getInt("MACOT"));
                item.setTenCot(rs.getString("TENCOT"));
                item.setPhuPhi(BigDecimal.valueOf(rs.getDouble("PHUPHI")));

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
