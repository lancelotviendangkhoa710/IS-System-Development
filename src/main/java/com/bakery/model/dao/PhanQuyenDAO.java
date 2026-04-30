package com.bakery.model.dao;

import com.bakery.model.dto.ChucNangDTO;
import com.bakery.model.enums.SystemModule;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PhanQuyenDAO {
    public RolePermissionInfo layThongTinPhanQuyenTheoVaiTro(int maVaiTro) throws Exception {
        String sql = """
                SELECT V.TENVAITRO,
                       CASE WHEN V.THOIDIEMXOA IS NULL THEN 1 ELSE 0 END AS VAITRO_HOATDONG,
                       C.MACHUCNANG,
                       C.TENCHUCNANG,
                       C.MOTA,
                       C.MODULE
                FROM VAITRO V
                LEFT JOIN VAITRO_CHUCNANG VC ON V.MAVAITRO = VC.MAVAITRO
                LEFT JOIN CHUCNANG C ON VC.MACHUCNANG = C.MACHUCNANG
                WHERE V.MAVAITRO = ?
                ORDER BY C.MACHUCNANG
                """;

        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maVaiTro);

            try (ResultSet rs = pstmt.executeQuery()) {
                String tenVaiTro = null;
                boolean vaiTroHoatDong = false;
                List<ChucNangDTO> danhSach = new ArrayList<>();
                Set<String> permissionKeys = new LinkedHashSet<>();

                while (rs.next()) {
                    if (tenVaiTro == null) {
                        tenVaiTro = rs.getString("TENVAITRO");
                        vaiTroHoatDong = rs.getInt("VAITRO_HOATDONG") == 1;
                    }

                    int maChucNang = rs.getInt("MACHUCNANG");
                    if (rs.wasNull()) {
                        continue;
                    }

                    ChucNangDTO cn = new ChucNangDTO();
                    cn.setMaChucNang(maChucNang);
                    cn.setTenChucNang(rs.getString("TENCHUCNANG"));
                    cn.setMoTa(rs.getString("MOTA"));
                    String moduleStr = rs.getString("MODULE");
                    if (moduleStr != null) {
                        cn.setModule(SystemModule.fromValue(moduleStr));
                    }
                    danhSach.add(cn);
                    permissionKeys.add(chuanHoaPermissionKey(cn.getTenChucNang()));
                }

                if (tenVaiTro == null) {
                    return null;
                }
                return new RolePermissionInfo(tenVaiTro, vaiTroHoatDong, danhSach, permissionKeys);
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - layThongTinPhanQuyenTheoVaiTro: " + e.getMessage());
            throw new Exception("Khong the tai phan quyen tu CSDL.");
        }
    }

    public List<ChucNangDTO> layDanhSachChucNangTheoVaiTro(int maVaiTro) {
        try {
            RolePermissionInfo info = layThongTinPhanQuyenTheoVaiTro(maVaiTro);
            return info == null ? new ArrayList<>() : info.getDanhSachChucNang();
        } catch (Exception e) {
            throw new IllegalStateException("Khong the tai phan quyen tu CSDL.", e);
        }
    }

    public static String chuanHoaPermissionKey(String tenChucNang) {
        if (tenChucNang == null) {
            return "";
        }
        return tenChucNang.trim().replaceAll("\\s+", "_").toUpperCase();
    }

    private Connection moKetNoi() throws Exception {
        Connection connection = DBConnect.getConnection();
        if (connection == null) {
            throw new Exception("Khong the ket noi CSDL.");
        }
        return connection;
    }

    public static final class RolePermissionInfo {
        private final String tenVaiTro;
        private final boolean vaiTroHoatDong;
        private final List<ChucNangDTO> danhSachChucNang;
        private final Set<String> permissionKeys;

        public RolePermissionInfo(
                String tenVaiTro,
                boolean vaiTroHoatDong,
                List<ChucNangDTO> danhSachChucNang,
                Set<String> permissionKeys
        ) {
            this.tenVaiTro = tenVaiTro;
            this.vaiTroHoatDong = vaiTroHoatDong;
            this.danhSachChucNang = List.copyOf(danhSachChucNang);
            this.permissionKeys = Set.copyOf(permissionKeys);
        }

        public String getTenVaiTro() {
            return tenVaiTro;
        }

        public boolean isVaiTroHoatDong() {
            return vaiTroHoatDong;
        }

        public List<ChucNangDTO> getDanhSachChucNang() {
            return danhSachChucNang;
        }

        public Set<String> getPermissionKeys() {
            return permissionKeys;
        }
    }
}