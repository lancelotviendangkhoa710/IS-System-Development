package com.bakery.model.dao.nhansu;

import com.bakery.model.dao.BaseDAO;
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
    private static final List<String> DEFAULT_ROLE_NAMES = List.of("Quản lý", "Thu ngân", "Thợ bếp", "Thủ kho");

    public List<VaiTroDTO> layDanhSachVaiTroDangHoatDong() throws Exception {
        damBaoVaiTroMacDinh();

        String sql = """
                SELECT MAVAITRO, TENVAITRO, MOTA
                FROM VAITRO
                WHERE THOIDIEMXOA IS NULL
                  AND TENVAITRO IN (?, ?, ?, ?)
                ORDER BY MAVAITRO
                """;

        List<VaiTroDTO> danhSachVaiTro = new ArrayList<>();

        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < DEFAULT_ROLE_NAMES.size(); i++) {
                pstmt.setString(i + 1, DEFAULT_ROLE_NAMES.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    VaiTroDTO vaiTro = new VaiTroDTO();
                    vaiTro.setMaVaiTro(rs.getInt("MAVAITRO"));
                    vaiTro.setTenVaiTro(rs.getString("TENVAITRO"));
                    vaiTro.setMoTa(rs.getString("MOTA"));
                    danhSachVaiTro.add(vaiTro);
                }
            }

            return danhSachVaiTro;
        } catch (SQLException e) {
            handleException("layDanhSachVaiTroDangHoatDong", e);
            throw new Exception("Lỗi hệ thống khi tải danh sách vai trò!");
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
                cstmt.setNString(2, "Vai trò mặc định phân quyền theo module.");
                cstmt.registerOutParameter(3, Types.NUMERIC);
                cstmt.execute();
            }
        } catch (SQLException e) {
            handleException("damBaoVaiTroMacDinh", e);
            throw new Exception("Không thể khởi tạo các vai trò mặc định.");
        }
    }
}
