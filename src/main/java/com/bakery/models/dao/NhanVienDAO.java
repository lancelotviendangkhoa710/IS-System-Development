package com.bakery.models.dao;

import com.bakery.models.dto.NhanVienDTO;
import com.bakery.utils.DBConnect;
import com.bakery.utils.PasswordUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class NhanVienDAO {
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
                SELECT MANV, MAVAITRO, HOTEN, NGAYSINH, SDT, TENDANGNHAP, MATKHAU, TRANGTHAILAMVIEC
                FROM NHANVIEN
                WHERE UPPER(TRIM(TENDANGNHAP)) = UPPER(?)
                """;

        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapNhanVien(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - timNhanVienTheoTenDangNhap: " + e.getMessage());
            throw new Exception("Khong the truy van tai khoan. Vui long kiem tra ket noi CSDL.");
        }
    }

    public NhanVienDTO timNhanVienTheoMa(int maNV) throws Exception {
        String sql = """
                SELECT MANV, MAVAITRO, HOTEN, NGAYSINH, SDT, TENDANGNHAP, MATKHAU, TRANGTHAILAMVIEC
                FROM NHANVIEN
                WHERE MANV = ?
                """;

        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maNV);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapNhanVien(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - timNhanVienTheoMa: " + e.getMessage());
            throw new Exception("Khong the tai thong tin nhan vien tu CSDL.");
        }
    }

    public boolean doiMatKhau(int maNV, String matKhauMoiDaHash) throws Exception {
        String sql = "UPDATE NHANVIEN SET MATKHAU = ? WHERE MANV = ?";

        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, matKhauMoiDaHash);
            pstmt.setInt(2, maNV);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi DAO - doiMatKhau: " + e.getMessage());
            throw new Exception("Khong the cap nhat mat khau do loi CSDL.");
        }
    }

    public int themNhanVien(NhanVienDTO nv) throws Exception {
        String sql = "{CALL PROC_THEM_NHANVIEN(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, nv.getMaVaiTro());
            cstmt.setNString(2, nv.getHoTen());
            if (nv.getNgaySinh() != null) {
                cstmt.setDate(3, java.sql.Date.valueOf(nv.getNgaySinh()));
            } else {
                cstmt.setNull(3, Types.DATE);
            }
            cstmt.setString(4, nv.getSdt());
            cstmt.setString(5, nv.getTenDangNhap());
            cstmt.setString(6, nv.getMatKhau());
            cstmt.registerOutParameter(7, Types.NUMERIC);

            cstmt.execute();
            return cstmt.getInt(7);
        } catch (SQLException e) {
            System.err.println("Loi DAO - themNhanVien: " + e.getMessage());
            throw new Exception("Loi khi them nhan vien: " + e.getMessage());
        }
    }

    public boolean suaNhanVien(NhanVienDTO nv) throws Exception {
        String sql = "{CALL PROC_SUA_NHANVIEN(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, nv.getMaNV());
            cstmt.setInt(2, nv.getMaVaiTro());
            cstmt.setNString(3, nv.getHoTen());
            if (nv.getNgaySinh() != null) {
                cstmt.setDate(4, java.sql.Date.valueOf(nv.getNgaySinh()));
            } else {
                cstmt.setNull(4, Types.DATE);
            }
            cstmt.setString(5, nv.getSdt());
            cstmt.setString(6, nv.getTenDangNhap());
            cstmt.setString(7, nv.getMatKhau());
            cstmt.setInt(8, nv.getTrangThaiLamViec());

            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Loi DAO - suaNhanVien: " + e.getMessage());
            throw new Exception("Loi khi cap nhat nhan vien: " + e.getMessage());
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
            System.err.println("Loi DAO - xoaNhanVien: " + e.getMessage());
            throw new Exception("Loi khi xoa nhan vien: " + e.getMessage());
        }
    }

    private Connection moKetNoi() throws Exception {
        Connection connection = DBConnect.getConnection();
        if (connection == null) {
            throw new Exception("Khong the ket noi CSDL.");
        }
        return connection;
    }

    private NhanVienDTO mapNhanVien(ResultSet rs) throws SQLException {
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
}
