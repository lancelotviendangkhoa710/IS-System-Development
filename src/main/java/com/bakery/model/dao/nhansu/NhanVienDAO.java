package com.bakery.model.dao.nhansu;
import com.bakery.model.dao.BaseDAO;

import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.utils.PasswordUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class NhanVienDAO extends BaseDAO {
    public NhanVienDTO kiemTraDangNhap(String username, String password) throws Exception {
        NhanVienDTO nhanVien = timNhanVienTheoTenDangNhap(username);
        if (nhanVien == null || !PasswordUtils.matches(password, nhanVien.getMatKhau())) {
            throw new Exception("Ten dang nhap hoac mat khau khong chinh xac.");
        }
        if (nhanVien.getTrangThaiLamViec() != 1) {
            throw new Exception("Tai khoan da bi vo hieu hoa.");
        }
        return nhanVien;
    }

    public NhanVienDTO timNhanVienTheoTenDangNhap(String username) throws Exception {
        String sql = """
                SELECT NV.MANV, NV.HOTEN, NV.NGAYSINH, NV.SDT, NV.TRANGTHAILAMVIEC,
                       TK.MATAIKHOAN, TK.TENDANGNHAP, TK.MATKHAU, TK.TRANGTHAITK, TK.EMAIL
                FROM NHANVIEN NV
                JOIN TAIKHOAN TK ON NV.MANV = TK.MANV
                WHERE UPPER(TRIM(TK.TENDANGNHAP)) = UPPER(?)
                """;

        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    NhanVienDTO nv = mapNhanVien(rs);
                    loadNhanVienRoles(nv, conn);
                    return nv;
                }
                return null;
            }
        } catch (SQLException e) {
            handleException("timNhanVienTheoTenDangNhap", e);
            return null;
        }
    }

    public NhanVienDTO timNhanVienTheoMa(int maNV) throws Exception {
        String sql = """
                SELECT NV.MANV, NV.HOTEN, NV.NGAYSINH, NV.SDT, NV.TRANGTHAILAMVIEC,
                       TK.MATAIKHOAN, TK.TENDANGNHAP, TK.MATKHAU, TK.TRANGTHAITK, TK.EMAIL
                FROM NHANVIEN NV
                JOIN TAIKHOAN TK ON NV.MANV = TK.MANV
                WHERE NV.MANV = ?
                """;

        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maNV);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    NhanVienDTO nv = mapNhanVien(rs);
                    loadNhanVienRoles(nv, conn);
                    return nv;
                }
                return null;
            }
        } catch (SQLException e) {
            handleException("timNhanVienTheoMa", e);
            return null;
        }
    }

    private void loadNhanVienRoles(NhanVienDTO nv, Connection conn) throws SQLException {
        String sql = """
                SELECT TKVT.MAVAITRO, VT.TENVAITRO
                FROM TAIKHOAN_VAITRO TKVT
                JOIN VAITRO VT ON TKVT.MAVAITRO = VT.MAVAITRO
                WHERE TKVT.MATAIKHOAN = ? AND VT.THOIDIEMXOA IS NULL
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nv.getMaTaiKhoan());
            try (ResultSet rs = pstmt.executeQuery()) {
                java.util.List<Integer> ids = new java.util.ArrayList<>();
                java.util.List<String> names = new java.util.ArrayList<>();
                while (rs.next()) {
                    ids.add(rs.getInt("MAVAITRO"));
                    names.add(rs.getString("TENVAITRO"));
                }
                nv.setDanhSachMaVaiTro(ids);
                nv.setDanhSachTenVaiTro(names);
            }
        }
    }

    /** Đổi mật khẩu qua Procedure — tuân thủ D3 */
    public boolean doiMatKhau(int maNV, String matKhauMoiDaHash) throws Exception {
        String sql = "{CALL PROC_DOI_MATKHAU_TAIKHOAN(?, ?)}";
        try (Connection conn = moKetNoi();
                CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, maNV);
            cstmt.setString(2, matKhauMoiDaHash);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            handleException("doiMatKhau", e);
            return false;
        }
    }

    public int themNhanVien(NhanVienDTO nv) throws Exception {
        String sqlNV = "{CALL PROC_THEM_NHANVIEN(?, ?, ?, ?, ?, ?, ?)}";
        int generatedId = -1;

        try (Connection conn = moKetNoi()) {
            try (CallableStatement cstmt = conn.prepareCall(sqlNV)) {
                cstmt.setNString(1, nv.getHoTen());
                if (nv.getNgaySinh() != null) {
                    cstmt.setDate(2, java.sql.Date.valueOf(nv.getNgaySinh()));
                } else {
                    cstmt.setNull(2, java.sql.Types.DATE);
                }
                cstmt.setString(3, nv.getSdt());
                cstmt.setString(4, nv.getTenDangNhap());
                cstmt.setString(5, nv.getMatKhau());
                cstmt.setInt(6, nv.getTrangThaiLamViec());
                cstmt.registerOutParameter(7, java.sql.Types.NUMERIC);

                cstmt.execute();
                generatedId = cstmt.getInt(7);
            }

            // Gán các vai trò từ danh sách đa vai trò
            if (generatedId > 0 && nv.getDanhSachMaVaiTro() != null && !nv.getDanhSachMaVaiTro().isEmpty()) {
                String sqlVT = "{CALL PROC_GAN_VAITRO_NHANVIEN(?, ?)}";
                try (CallableStatement cstmt = conn.prepareCall(sqlVT)) {
                    for (Integer maVT : nv.getDanhSachMaVaiTro()) {
                        cstmt.setInt(1, generatedId);
                        cstmt.setInt(2, maVT);
                        cstmt.addBatch();
                    }
                    cstmt.executeBatch();
                }
            }
        } catch (SQLException e) {
            handleException("themNhanVien", e);
            return -1;
        }
        return generatedId;
    }

    public boolean suaNhanVien(NhanVienDTO nv) throws Exception {
        String sqlNV = "{CALL PROC_SUA_NHANVIEN(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = moKetNoi()) {
            try (CallableStatement cstmt = conn.prepareCall(sqlNV)) {
                cstmt.setInt(1, nv.getMaNV());
                cstmt.setNString(2, nv.getHoTen());
                if (nv.getNgaySinh() != null) {
                    cstmt.setDate(3, java.sql.Date.valueOf(nv.getNgaySinh()));
                } else {
                    cstmt.setNull(3, java.sql.Types.DATE);
                }
                cstmt.setString(4, nv.getSdt());
                cstmt.setString(5, nv.getTenDangNhap());
                cstmt.setString(6, nv.getMatKhau());
                cstmt.setInt(7, nv.getTrangThaiLamViec());
                cstmt.execute();
            }

            // Cập nhật lại danh sách vai trò (Xóa cũ, thêm mới) theo MATAIKHOAN
            if (nv.getDanhSachMaVaiTro() != null) {
                String sqlDel = "DELETE FROM TAIKHOAN_VAITRO WHERE MATAIKHOAN = (SELECT MATAIKHOAN FROM TAIKHOAN WHERE MANV = ? AND ROWNUM = 1)";
                try (PreparedStatement delStmt = conn.prepareStatement(sqlDel)) {
                    delStmt.setInt(1, nv.getMaNV());
                    delStmt.executeUpdate();
                }

                String sqlIns = "{CALL PROC_GAN_VAITRO_NHANVIEN(?, ?)}";
                try (CallableStatement insStmt = conn.prepareCall(sqlIns)) {
                    for (Integer maVT : nv.getDanhSachMaVaiTro()) {
                        insStmt.setInt(1, nv.getMaNV());
                        insStmt.setInt(2, maVT);
                        insStmt.addBatch();
                    }
                    insStmt.executeBatch();
                }
            }
            return true;
        } catch (SQLException e) {
            handleException("suaNhanVien", e);
            return false;
        }
    }

    public boolean xoaNhanVien(int maNV) throws Exception {
        String sql = "{CALL PROC_XOA_NHANVIEN(?)}";
        try (Connection conn = moKetNoi();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, maNV);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            handleException("xoaNhanVien", e);
            return false;
        }
    }

    public java.util.List<NhanVienDTO> layTatCaNhanVien() throws Exception {
        String sql = """
                SELECT NV.MANV, NV.HOTEN, NV.NGAYSINH, NV.SDT, NV.TRANGTHAILAMVIEC,
                       TK.MATAIKHOAN, TK.TENDANGNHAP, TK.MATKHAU, TK.TRANGTHAITK, TK.EMAIL
                FROM NHANVIEN NV
                JOIN TAIKHOAN TK ON NV.MANV = TK.MANV
                ORDER BY NV.MANV DESC
                """;

        java.util.List<NhanVienDTO> list = new java.util.ArrayList<>();
        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                NhanVienDTO nv = mapNhanVien(rs);
                loadNhanVienRoles(nv, conn);
                list.add(nv);
            }
            return list;
        } catch (SQLException e) {
            handleException("layTatCaNhanVien", e);
            return new java.util.ArrayList<>();
        }
    }

    private NhanVienDTO mapNhanVien(ResultSet rs) throws SQLException {
        NhanVienDTO nv = new NhanVienDTO();
        nv.setMaNV(rs.getInt("MANV"));
        nv.setHoTen(rs.getString("HOTEN"));
        if (rs.getDate("NGAYSINH") != null) {
            nv.setNgaySinh(rs.getDate("NGAYSINH").toLocalDate());
        }
        nv.setSdt(rs.getString("SDT"));
        nv.setTrangThaiLamViec(rs.getInt("TRANGTHAILAMVIEC"));
        nv.setMaTaiKhoan(rs.getInt("MATAIKHOAN"));
        nv.setTenDangNhap(rs.getString("TENDANGNHAP"));
        nv.setMatKhau(rs.getString("MATKHAU"));
        nv.setTrangThaiTK(rs.getInt("TRANGTHAITK"));
        nv.setEmail(rs.getString("EMAIL"));
        return nv;
    }

    /**
     * Cập nhật thông tin cá nhân cơ bản của nhân viên đang đăng nhập.
     * Không tác động vai trò và trạng thái hệ thống.
     */
    public boolean capNhatThongTinCaNhan(int maNV, String hoTen, String sdt) throws Exception {
        String sql = "{CALL PROC_SUA_NHANVIEN(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = moKetNoi();
                CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, maNV);
            cstmt.setNString(2, hoTen);
            cstmt.setNull(3, java.sql.Types.DATE);
            cstmt.setString(4, sdt);
            cstmt.setNull(5, java.sql.Types.VARCHAR);
            cstmt.setNull(6, java.sql.Types.VARCHAR);
            cstmt.setNull(7, java.sql.Types.NUMERIC);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            handleException("capNhatThongTinCaNhan", e);
            return false;
        }
    }

    public void capNhatVaiTroNhanVien(int maNV, java.util.List<Integer> dsMaVT) throws Exception {
        try (Connection conn = moKetNoi()) {
            // 1. Xóa vai trò cũ theo MATAIKHOAN
            String sqlDel = "DELETE FROM TAIKHOAN_VAITRO WHERE MATAIKHOAN = (SELECT MATAIKHOAN FROM TAIKHOAN WHERE MANV = ? AND ROWNUM = 1)";
            try (PreparedStatement delStmt = conn.prepareStatement(sqlDel)) {
                delStmt.setInt(1, maNV);
                delStmt.executeUpdate();
            }

            // 2. Thêm vai trò mới
            if (dsMaVT != null && !dsMaVT.isEmpty()) {
                String sqlIns = "{CALL PROC_GAN_VAITRO_NHANVIEN(?, ?)}";
                try (CallableStatement insStmt = conn.prepareCall(sqlIns)) {
                    for (Integer maVT : dsMaVT) {
                        insStmt.setInt(1, maNV);
                        insStmt.setInt(2, maVT);
                        insStmt.addBatch();
                    }
                    insStmt.executeBatch();
                }
            }
        } catch (SQLException e) {
            handleException("capNhatVaiTroNhanVien", e);
            throw new Exception("Không thể cập nhật vai trò nhân viên.");
        }
    }

    /** Lấy MATAIKHOAN từ bảng TAIKHOAN theo MANV — dùng cho AccountTokenDAO. */
    public int layMaTaiKhoan(int maNV) throws Exception {
        String sql = "SELECT MATAIKHOAN FROM TAIKHOAN WHERE MANV = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maNV);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("MATAIKHOAN");
                throw new Exception("Không tìm thấy tài khoản cho nhân viên mã: " + maNV);
            }
        } catch (SQLException e) {
            handleException("layMaTaiKhoan", e);
            return -1;
        }
    }

    /** Cho thôi việc (soft-delete): TRANGTHAILAMVIEC=0 + TRANGTHAITK=0 + ghi lịch sử */
    public boolean thoiViec(int maNV, int maNvThaoTac) throws Exception {
        String sql = "{CALL PROC_THOIVIEC_NHANVIEN(?, ?)}";
        try (Connection conn = moKetNoi();
                CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, maNV);
            cstmt.setInt(2, maNvThaoTac);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            handleException("thoiViec", e);
            return false;
        }
    }

    /**
     * Khôi phục nhân viên đã thôi việc — đảo ngược PROC_THOIVIEC_NHANVIEN.
     * Dùng PROC_SUA_NHANVIEN để set TRANGTHAILAMVIEC=1, sau đó mở lại tài khoản + ghi audit.
     */
    public boolean khoiPhucNhanVien(int maNV, int maNvThaoTac) throws Exception {
        String sqlNV = "{CALL PROC_SUA_NHANVIEN(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = moKetNoi()) {
            // 1. Set TRANGTHAILAMVIEC = 1 qua PROC_SUA_NHANVIEN (pass NULL giữ nguyên các field khác)
            try (CallableStatement cstmt = conn.prepareCall(sqlNV)) {
                cstmt.setInt(1, maNV);
                cstmt.setNull(2, java.sql.Types.NVARCHAR);   // HOTEN — giữ nguyên
                cstmt.setNull(3, java.sql.Types.DATE);        // NGAYSINH — giữ nguyên
                cstmt.setNull(4, java.sql.Types.VARCHAR);     // SDT — giữ nguyên
                cstmt.setNull(5, java.sql.Types.VARCHAR);     // TENDANGNHAP — giữ nguyên
                cstmt.setNull(6, java.sql.Types.VARCHAR);     // MATKHAU — giữ nguyên
                cstmt.setInt(7, 1);                           // TRANGTHAILAMVIEC = 1
                cstmt.execute();
            }

            // 2. Mở lại tài khoản đăng nhập
            String sqlTK = "UPDATE TAIKHOAN SET TRANGTHAITK = 1 WHERE MANV = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlTK)) {
                pstmt.setInt(1, maNV);
                pstmt.executeUpdate();
            }

            // 3. Ghi audit log
            if (maNvThaoTac > 0) {
                String sqlLog = "INSERT INTO HOATDONGNHANVIEN (MANV, NHOM, HANHDONG, ENTITY_ID)"
                        + " VALUES (?, 'NHAN_SU', 'Khoi phuc NV #' || TO_CHAR(?), ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sqlLog)) {
                    pstmt.setInt(1, maNvThaoTac);
                    pstmt.setInt(2, maNV);
                    pstmt.setInt(3, maNV);
                    pstmt.executeUpdate();
                }
            }

            return true;
        } catch (SQLException e) {
            handleException("khoiPhucNhanVien", e);
            return false;
        }
    }

    /**
     * Lấy maTaiKhoan và email theo tên đăng nhập — dùng cho OTP reset mật khẩu.
     * @return mảng [maTaiKhoan, email] hoặc null nếu không tìm thấy / email chưa thiết lập
     */
    public String[] layEmailVaMaTaiKhoanTheoUsername(String tenDangNhap) throws Exception {
        String sql = """
                SELECT TK.MATAIKHOAN, TK.EMAIL
                FROM TAIKHOAN TK
                WHERE UPPER(TRIM(TK.TENDANGNHAP)) = UPPER(?)
                  AND TK.TRANGTHAITK = 1
                """;
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tenDangNhap);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String maTK = String.valueOf(rs.getInt("MATAIKHOAN"));
                    String email = rs.getString("EMAIL");
                    return new String[]{maTK, email};
                }
                return null;
            }
        } catch (SQLException e) {
            handleException("layEmailVaMaTaiKhoanTheoUsername", e);
            return null;
        }
    }

    /** Cập nhật địa chỉ email trong TAIKHOAN theo MANV. */
    public void capNhatEmail(int maNV, String email) throws Exception {
        String sql = "UPDATE TAIKHOAN SET EMAIL = ? WHERE MANV = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, maNV);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleException("capNhatEmail", e);
        }
    }
}
