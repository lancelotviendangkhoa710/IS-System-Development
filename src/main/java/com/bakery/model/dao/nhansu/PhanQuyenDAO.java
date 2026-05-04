package com.bakery.model.dao.nhansu;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.nhansu.ChucNangDTO;
import com.bakery.model.enums.SystemModule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PhanQuyenDAO extends BaseDAO {
    private static final String ROLE_QUAN_LY = "Quản lý";
    private static final String ROLE_THU_NGAN = "Thu ngân";
    private static final String ROLE_THO_BEP = "Thợ bếp";
    private static final String ROLE_THU_KHO = "Thủ kho";

    private static final String[][] DEFAULT_CHUC_NANG = {
            {"Bán hàng POS", "Lập hóa đơn bán lẻ tại quầy.", "POS"},
            {"Theo dõi đơn hàng", "Tra cứu và cập nhật tiến độ đơn bánh.", "POS"},
            {"Khách hàng thành viên", "Quản lý khách hàng và lịch sử mua hàng.", "CRM"},
            {"Kho và nguyên liệu", "Quản lý tồn kho, nguyên liệu và xuất nhập.", "INVENTORY"},
            {"Nhà cung cấp", "Quản lý đối tác cung ứng nguyên liệu.", "INVENTORY"},
            {"Nhân sự và phân quyền", "Quản lý nhân viên và vai trò truy cập.", "HR"},
            {"Báo cáo kinh doanh", "Theo dõi doanh thu và số liệu vận hành.", "REPORTS"},
            {"Điều phối bếp", "Theo dõi sản xuất và ưu tiên đơn trong bếp.", "KDS"}
    };

    private void damBaoChucNangMacDinh() throws Exception {
        String sql = """
                INSERT INTO CHUCNANG (TENCHUCNANG, MOTA, MODULE)
                SELECT ?, ?, ?
                FROM DUAL
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM CHUCNANG
                    WHERE UPPER(TRIM(TENCHUCNANG)) = UPPER(TRIM(?))
                )
                """;

        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String[] chucNang : DEFAULT_CHUC_NANG) {
                pstmt.setString(1, chucNang[0]);
                pstmt.setString(2, chucNang[1]);
                pstmt.setString(3, chucNang[2]);
                pstmt.setString(4, chucNang[0]);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            handleException("damBaoChucNangMacDinh", e);
            throw new Exception("Khong the khoi tao cac chuc nang mac dinh.");
        }
    }

    private void capQuyenMacDinhTheoVaiTro() throws Exception {
        damBaoChucNangMacDinh();

        String sqlVaiTro = """
                SELECT MAVAITRO, TENVAITRO
                FROM VAITRO
                WHERE THOIDIEMXOA IS NULL
                  AND TENVAITRO IN (?, ?, ?, ?)
                """;
        String sqlCapQuyen = """
                INSERT INTO VAITRO_CHUCNANG (MAVAITRO, MACHUCNANG)
                SELECT ?, C.MACHUCNANG
                FROM CHUCNANG C
                WHERE C.TENCHUCNANG = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM VAITRO_CHUCNANG VC
                      WHERE VC.MAVAITRO = ?
                        AND VC.MACHUCNANG = C.MACHUCNANG
                  )
                """;

        try (Connection conn = moKetNoi();
             PreparedStatement roleStmt = conn.prepareStatement(sqlVaiTro);
             PreparedStatement grantStmt = conn.prepareStatement(sqlCapQuyen)) {

            roleStmt.setString(1, ROLE_QUAN_LY);
            roleStmt.setString(2, ROLE_THU_NGAN);
            roleStmt.setString(3, ROLE_THO_BEP);
            roleStmt.setString(4, ROLE_THU_KHO);

            try (ResultSet rs = roleStmt.executeQuery()) {
                while (rs.next()) {
                    int maVaiTro = rs.getInt("MAVAITRO");
                    String tenVaiTro = rs.getString("TENVAITRO");

                    for (String tenChucNang : layDanhSachChucNangMacDinh(tenVaiTro)) {
                        grantStmt.setInt(1, maVaiTro);
                        grantStmt.setString(2, tenChucNang);
                        grantStmt.setInt(3, maVaiTro);
                        grantStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            handleException("capQuyenMacDinhTheoVaiTro", e);
            throw new Exception("Khong the dong bo phan quyen mac dinh theo vai tro.");
        }
    }

    public RolePermissionInfo layThongTinPhanQuyenTheoVaiTro(int maVaiTro) throws Exception {
        capQuyenMacDinhTheoVaiTro();

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
            handleException("layThongTinPhanQuyenTheoVaiTro", e);
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

    private List<String> layDanhSachChucNangMacDinh(String tenVaiTro) {
        return switch (tenVaiTro) {
            case ROLE_QUAN_LY -> List.of(
                    "Bán hàng POS",
                    "Theo dõi đơn hàng",
                    "Khách hàng thành viên",
                    "Kho và nguyên liệu",
                    "Nhà cung cấp",
                    "Nhân sự và phân quyền",
                    "Báo cáo kinh doanh",
                    "Điều phối bếp");
            case ROLE_THU_NGAN -> List.of(
                    "Bán hàng POS",
                    "Theo dõi đơn hàng",
                    "Khách hàng thành viên",
                    "Báo cáo kinh doanh");
            case ROLE_THO_BEP -> List.of(
                    "Điều phối bếp",
                    "Theo dõi đơn hàng",
                    "Kho và nguyên liệu");
            case ROLE_THU_KHO -> List.of(
                    "Kho và nguyên liệu",
                    "Nhà cung cấp");
            default -> List.of();
        };
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
