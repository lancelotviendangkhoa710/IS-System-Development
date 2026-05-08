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
                       TK.TENDANGNHAP, TK.MATKHAU, TK.TRANGTHAITK
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
                       TK.TENDANGNHAP, TK.MATKHAU, TK.TRANGTHAITK
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
                SELECT NVVT.MAVAITRO, VT.TENVAITRO
                FROM NHANVIEN_VAITRO NVVT
                JOIN VAITRO VT ON NVVT.MAVAITRO = VT.MAVAITRO
                WHERE NVVT.MANV = ? AND VT.THOIDIEMXOA IS NULL
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nv.getMaNV());
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
        // 1. Gọi Procedure tạo nhân viên và tài khoản đăng nhập
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

            // 2. Gán các vai trò từ danh sách đa vai trò
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
        // 1. Cập nhật thông tin cơ bản và tài khoản đăng nhập
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

            // 2. Cập nhật lại danh sách vai trò (Xóa cũ, thêm mới)
            if (nv.getDanhSachMaVaiTro() != null) {
                String sqlDel = "DELETE FROM NHANVIEN_VAITRO WHERE MANV = ?";
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
                       TK.TENDANGNHAP, TK.MATKHAU, TK.TRANGTHAITK
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
        nv.setTenDangNhap(rs.getString("TENDANGNHAP"));
        nv.setMatKhau(rs.getString("MATKHAU"));
        nv.setTrangThaiTK(rs.getInt("TRANGTHAITK"));
        return nv;
    }

    public void capNhatVaiTroNhanVien(int maNV, java.util.List<Integer> dsMaVT) throws Exception {
        try (Connection conn = moKetNoi()) {
            // 1. Xóa vai trò cũ
            String sqlDel = "DELETE FROM NHANVIEN_VAITRO WHERE MANV = ?";
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

    /** Cho thôi việc (soft-delete): TRANGTHAILAMVIEC=0 + TRANGTHAITK=0 */
    public boolean thoiViec(int maNV) throws Exception {
        String sql = "{CALL PROC_THOIVIEC_NHANVIEN(?)}";
        try (Connection conn = moKetNoi();
                CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, maNV);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            handleException("thoiViec", e);
            return false;
        }
    }
}
