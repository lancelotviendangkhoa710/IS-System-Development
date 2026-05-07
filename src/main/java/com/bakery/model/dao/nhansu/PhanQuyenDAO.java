package com.bakery.model.dao.nhansu;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.nhansu.ChucNangDTO;
import com.bakery.model.enums.SystemModule;

import java.sql.CallableStatement;
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
            { "Bán hàng POS", "Lập hóa đơn bán lẻ tại quầy.", "POS" },
            { "Theo dõi đơn hàng", "Tra cứu và cập nhật tiến độ đơn bánh.", "POS" },
            { "Khách hàng thành viên", "Quản lý khách hàng và lịch sử mua hàng.", "CRM" },
            { "Kho và nguyên liệu", "Quản lý tồn kho, nguyên liệu và xuất nhập.", "INVENTORY" },
            { "Nhà cung cấp", "Quản lý đối tác cung ứng nguyên liệu.", "INVENTORY" },
            { "Nhân sự và phân quyền", "Quản lý nhân viên và vai trò truy cập.", "HR" },
            { "Báo cáo kinh doanh", "Theo dõi doanh thu và số liệu vận hành.", "REPORTS" },
            { "Điều phối bếp", "Theo dõi sản xuất và ưu tiên đơn trong bếp.", "KDS" }
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
                INSERT INTO VAITRO_CHUCNANG (MAVAITRO, MACHUCNANG, CAN_VIEW, CAN_ADD, CAN_EDIT, CAN_DELETE, CAN_DOWNLOAD)
                SELECT ?, C.MACHUCNANG, 1, 1, 1, 0, 0
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
        // damBaoChucNangMacDinh(); // Bỏ qua phần này để tập trung vào query

        String sql = """
                SELECT V.TENVAITRO,
                       CASE WHEN V.THOIDIEMXOA IS NULL THEN 1 ELSE 0 END AS VAITRO_HOATDONG,
                       C.MACHUCNANG,
                       C.TENCHUCNANG,
                       C.MOTA,
                       C.MODULE,
                       VC.CAN_VIEW, VC.CAN_ADD, VC.CAN_EDIT, VC.CAN_DELETE, VC.CAN_DOWNLOAD
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

                    // Map chi tiết quyền
                    cn.setCanView(rs.getInt("CAN_VIEW") == 1);
                    cn.setCanAdd(rs.getInt("CAN_ADD") == 1);
                    cn.setCanEdit(rs.getInt("CAN_EDIT") == 1);
                    cn.setCanDelete(rs.getInt("CAN_DELETE") == 1);
                    cn.setCanDownload(rs.getInt("CAN_DOWNLOAD") == 1);

                    danhSach.add(cn);

                    // Chỉ thêm key nếu có ít nhất một quyền hoạt động
                    if (cn.isCanView() || cn.isCanAdd() || cn.isCanEdit() || cn.isCanDelete() || cn.isCanDownload()) {
                        permissionKeys.add(chuanHoaPermissionKey(cn.getTenChucNang()));
                    }
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

    /**
     * Hợp nhất quyền từ nhiều vai trò (OR logic).
     */
    public RolePermissionInfo layPhanQuyenHopNhat(List<Integer> dsMaVaiTro) throws Exception {
        if (dsMaVaiTro == null || dsMaVaiTro.isEmpty())
            return null;

        List<ChucNangDTO> allFunctions = new ArrayList<>();
        Set<String> allKeys = new LinkedHashSet<>();
        List<String> roleNames = new ArrayList<>();

        for (int maVT : dsMaVaiTro) {
            RolePermissionInfo info = layThongTinPhanQuyenTheoVaiTro(maVT);
            if (info != null && info.isVaiTroHoatDong()) {
                roleNames.add(info.getTenVaiTro());
                mergePermissions(allFunctions, info.getDanhSachChucNang());
                allKeys.addAll(info.getPermissionKeys());
            }
        }

        return new RolePermissionInfo(
                String.join(" + ", roleNames),
                true,
                allFunctions,
                allKeys);
    }

    private void mergePermissions(List<ChucNangDTO> target, List<ChucNangDTO> source) {
        for (ChucNangDTO s : source) {
            ChucNangDTO t = target.stream()
                    .filter(x -> x.getMaChucNang() == s.getMaChucNang())
                    .findFirst()
                    .orElse(null);

            if (t == null) {
                // Clone DTO mới để tránh side effect
                ChucNangDTO newItem = new ChucNangDTO(s.getMaChucNang(), s.getTenChucNang(), s.getMoTa(),
                        s.getModule());
                newItem.setCanView(s.isCanView());
                newItem.setCanAdd(s.isCanAdd());
                newItem.setCanEdit(s.isCanEdit());
                newItem.setCanDelete(s.isCanDelete());
                newItem.setCanDownload(s.isCanDownload());
                target.add(newItem);
            } else {
                // OR logic cho các flags
                t.setCanView(t.isCanView() || s.isCanView());
                t.setCanAdd(t.isCanAdd() || s.isCanAdd());
                t.setCanEdit(t.isCanEdit() || s.isCanEdit());
                t.setCanDelete(t.isCanDelete() || s.isCanDelete());
                t.setCanDownload(t.isCanDownload() || s.isCanDownload());
            }
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
                Set<String> permissionKeys) {
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

    public List<ChucNangDTO> layToanBoChucNang() throws Exception {
        damBaoChucNangMacDinh();
        String sql = "SELECT MACHUCNANG, TENCHUCNANG, MOTA, MODULE FROM CHUCNANG ORDER BY MACHUCNANG";
        List<ChucNangDTO> list = new ArrayList<>();
        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ChucNangDTO cn = new ChucNangDTO();
                cn.setMaChucNang(rs.getInt("MACHUCNANG"));
                cn.setTenChucNang(rs.getString("TENCHUCNANG"));
                cn.setMoTa(rs.getString("MOTA"));
                cn.setModule(SystemModule.fromValue(rs.getString("MODULE")));
                list.add(cn);
            }
        }
        return list;
    }

    public void capNhatQuyenChiTiet(int maVaiTro, int maChucNang,
            boolean v, boolean a, boolean e, boolean d, boolean dl) throws Exception {
        String sql = """
                MERGE INTO VAITRO_CHUCNANG target
                USING (SELECT ? as MAVAITRO, ? as MACHUCNANG FROM DUAL) source
                ON (target.MAVAITRO = source.MAVAITRO AND target.MACHUCNANG = source.MACHUCNANG)
                WHEN MATCHED THEN
                    UPDATE SET CAN_VIEW = ?, CAN_ADD = ?, CAN_EDIT = ?, CAN_DELETE = ?, CAN_DOWNLOAD = ?
                WHEN NOT MATCHED THEN
                    INSERT (MAVAITRO, MACHUCNANG, CAN_VIEW, CAN_ADD, CAN_EDIT, CAN_DELETE, CAN_DOWNLOAD)
                    VALUES (source.MAVAITRO, source.MACHUCNANG, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int valV = v ? 1 : 0;
            int valA = a ? 1 : 0;
            int valE = e ? 1 : 0;
            int valD = d ? 1 : 0;
            int valDL = dl ? 1 : 0;

            pstmt.setInt(1, maVaiTro);
            pstmt.setInt(2, maChucNang);
            pstmt.setInt(3, valV);
            pstmt.setInt(4, valA);
            pstmt.setInt(5, valE);
            pstmt.setInt(6, valD);
            pstmt.setInt(7, valDL);
            pstmt.setInt(8, valV);
            pstmt.setInt(9, valA);
            pstmt.setInt(10, valE);
            pstmt.setInt(11, valD);
            pstmt.setInt(12, valDL);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            handleException("capNhatQuyenChiTiet", ex);
            throw new Exception("Khong thể cập nhật quyền chi tiết.");
        }
    }
}
