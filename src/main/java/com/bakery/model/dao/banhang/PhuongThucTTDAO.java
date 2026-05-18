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

    /**
     * Tra cứu MAPTTT động theo từ khóa tên — tuân thủ rule CẤM hardcode ID.
     * Tìm kiếm LIKE không phân biệt hoa thường.
     *
     * @param tuKhoa từ khóa tên (VD: "Tiền mặt", "tien mat", "mặt")
     * @return MAPTTT nếu tìm thấy, -1 nếu không tìm thấy
     */
    public int layMaTheoTen(String tuKhoa) throws Exception {
        String sql = "SELECT MAPTTT FROM PHUONGTHUCTT "
                + "WHERE UPPER(TENPTTT) LIKE '%' || UPPER(?) || '%' "
                + "AND THOIDIEMXOA IS NULL "
                + "FETCH FIRST 1 ROW ONLY";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tuKhoa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("MAPTTT");
            }
        } catch (SQLException e) {
            handleException("layMaTheoTen", e);
        }
        return -1;
    }

}
