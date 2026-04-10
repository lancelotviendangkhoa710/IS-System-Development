package com.bakery.dao;

import com.bakery.dto.NhanVienDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NhanVienDAO {
    private static final String DEFAULT_ROLE_NAME = "NV";

    public NhanVienDTO kiemTraDangNhap(String username, String password) throws Exception {
        return dangNhap(username, password);
    }

    public NhanVienDTO dangNhap(String username, String password) throws Exception {
        String sql = "SELECT * FROM NHANVIEN WHERE TENDANGNHAP = ? AND MATKHAU = ? AND TRANGTHAILAMVIEC = 1";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapNhanVien(rs);
                }
                throw new Exception("Ten dang nhap hoac mat khau khong chinh xac.");
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - dangNhap: " + e.getMessage());
            throw new Exception("Loi he thong khi dang nhap.");
        }
    }

    public NhanVienDTO taoTaiKhoanNhanVien(String hoTen, String sdt, String tenDangNhap, String matKhau) throws Exception {
        String normalizedName = trimToNull(hoTen);
        String normalizedPhone = trimToNull(sdt);
        String normalizedUsername = trimToNull(tenDangNhap);
        String normalizedPassword = trimToNull(matKhau);

        if (normalizedName == null || normalizedPhone == null || normalizedUsername == null || normalizedPassword == null) {
            throw new Exception("Thong tin dang ky khong du.");
        }

        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) {
                throw new Exception("Khong ket noi duoc CSDL.");
            }

            conn.setAutoCommit(false);
            try {
                if (existsByUsername(conn, normalizedUsername)) {
                    throw new Exception("Ten dang nhap da ton tai.");
                }
                if (existsByPhone(conn, normalizedPhone)) {
                    throw new Exception("So dien thoai da ton tai.");
                }

                int maVaiTro = getOrCreateDefaultRole(conn);
                int maNhanVien = insertNhanVien(conn, maVaiTro, normalizedName, normalizedPhone, normalizedUsername, normalizedPassword);
                conn.commit();
                return findById(conn, maNhanVien);
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - taoTaiKhoanNhanVien: " + e.getMessage());
            throw new Exception("Loi he thong khi tao tai khoan.");
        }
    }

    public boolean doiMatKhau(int maNV, String matKhauMoi) {
        String sql = "UPDATE NHANVIEN SET MATKHAU = ? WHERE MANV = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matKhauMoi);
            pstmt.setInt(2, maNV);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi DAO - doiMatKhau: " + e.getMessage());
        }
        return false;
    }

    public boolean doiMatKhau(int maNV, String matKhauCu, String matKhauMoi) throws Exception {
        String checkSql = "SELECT 1 FROM NHANVIEN WHERE MANV = ? AND MATKHAU = ? AND TRANGTHAILAMVIEC = 1";
        String updateSql = "UPDATE NHANVIEN SET MATKHAU = ? WHERE MANV = ?";

        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) {
                throw new Exception("Khong ket noi duoc CSDL.");
            }

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, maNV);
                checkStmt.setString(2, matKhauCu);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new Exception("Mat khau hien tai khong dung.");
                    }
                }
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, matKhauMoi);
                updateStmt.setInt(2, maNV);
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - doiMatKhau(co kiem tra): " + e.getMessage());
            throw new Exception("Loi he thong khi doi mat khau.");
        }
    }

    private static NhanVienDTO mapNhanVien(ResultSet rs) throws SQLException {
        NhanVienDTO nv = new NhanVienDTO();
        nv.setMaNV(rs.getInt("MANV"));
        nv.setMaVaiTro(rs.getInt("MAVAITRO"));
        nv.setHoTen(rs.getString("HOTEN"));
        if (rs.getDate("NGAYSINH") != null) {
            nv.setNgaySinh(rs.getDate("NGAYSINH").toLocalDate());
        }
        nv.setSdt(rs.getString("SDT"));
        nv.setTenDangNhap(rs.getString("TENDANGNHAP"));
        nv.setMatKhau(rs.getString("MATKHAU"));
        nv.setTrangThaiLamViec(rs.getInt("TRANGTHAILAMVIEC"));
        return nv;
    }

    private static boolean existsByUsername(Connection conn, String username) throws SQLException {
        String sql = "SELECT 1 FROM NHANVIEN WHERE TENDANGNHAP = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean existsByPhone(Connection conn, String phone) throws SQLException {
        String sql = "SELECT 1 FROM NHANVIEN WHERE SDT = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static int getOrCreateDefaultRole(Connection conn) throws SQLException {
        String selectSql = "SELECT MAVAITRO FROM VAITRO WHERE TENVAITRO = ? FETCH FIRST 1 ROW ONLY";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, DEFAULT_ROLE_NAME);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        String insertSql = "INSERT INTO VAITRO (TENVAITRO, MOTA) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, DEFAULT_ROLE_NAME);
            insertStmt.setString(2, "Auto-generated default role for employee accounts");
            insertStmt.executeUpdate();
            try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Khong tao duoc vai tro mac dinh.");
    }

    private static int insertNhanVien(
            Connection conn,
            int maVaiTro,
            String hoTen,
            String sdt,
            String tenDangNhap,
            String matKhau
    ) throws SQLException {
        String sql = "INSERT INTO NHANVIEN (MAVAITRO, HOTEN, SDT, TENDANGNHAP, MATKHAU, TRANGTHAILAMVIEC) VALUES (?, ?, ?, ?, ?, 1)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, maVaiTro);
            pstmt.setString(2, hoTen);
            pstmt.setString(3, sdt);
            pstmt.setString(4, tenDangNhap);
            pstmt.setString(5, matKhau);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Khong tao duoc nhan vien moi.");
    }

    private static NhanVienDTO findById(Connection conn, int maNhanVien) throws SQLException {
        String sql = "SELECT * FROM NHANVIEN WHERE MANV = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maNhanVien);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapNhanVien(rs);
                }
            }
        }
        throw new SQLException("Khong tim thay nhan vien vua tao.");
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
