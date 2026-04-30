package com.bakery.model.dao.nhansu;

import com.bakery.model.dto.nhansu.VaiTroDTO;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class VaiTroDAO extends BaseDAO {
    private static final List<String> DEFAULT_ROLE_NAMES = List.of("Thu ngan", "Quan ly", "Tho bep");

    public List<VaiTroDTO> layDanhSachVaiTroDangHoatDong() throws Exception {
        damBaoVaiTroMacDinh();

        String sql = """
                SELECT MAVAITRO, TENVAITRO, MOTA
                FROM VAITRO
                WHERE THOIDIEMXOA IS NULL
                ORDER BY MAVAITRO
                """;

        List<VaiTroDTO> danhSachVaiTro = new ArrayList<>();

        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                VaiTroDTO vaiTro = new VaiTroDTO();
                vaiTro.setMaVaiTro(rs.getInt("MAVAITRO"));
                vaiTro.setTenVaiTro(rs.getString("TENVAITRO"));
                vaiTro.setMoTa(rs.getString("MOTA"));
                danhSachVaiTro.add(vaiTro);
            }

            return danhSachVaiTro;
        } catch (SQLException e) {
            handleException("layDanhSachVaiTroDangHoatDong", e);
            throw new Exception("Loi he thong khi tai danh sach vai tro!");
        }
    }

    private void damBaoVaiTroMacDinh() throws Exception {
        String sqlKiemTra = """
                SELECT COUNT(*)
                FROM VAITRO
                WHERE THOIDIEMXOA IS NULL AND UPPER(TRIM(TENVAITRO)) = UPPER(?)
                """;
        String sqlThemVaiTro = "{CALL PROC_THEM_VAITRO(?, ?, ?)}";

        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sqlKiemTra);
             CallableStatement cstmt = conn.prepareCall(sqlThemVaiTro)) {

            for (String tenVaiTro : DEFAULT_ROLE_NAMES) {
                pstmt.setString(1, tenVaiTro);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        continue;
                    }
                }

                cstmt.setNString(1, tenVaiTro);
                cstmt.setNString(2, "Vai tro mac dinh cho man hinh dang ky.");
                cstmt.registerOutParameter(3, Types.NUMERIC);
                cstmt.execute();
            }
        } catch (SQLException e) {
            handleException("damBaoVaiTroMacDinh", e);
            throw new Exception("Khong the khoi tao cac vai tro mac dinh.");
        }
    }
}
