package com.bakery.models.dao;

import com.bakery.models.dto.NhanVienDTO;
import com.bakery.utils.DBConnect;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class NhanVienDAO {

    public NhanVienDTO kiemTraDangNhap(String username, String password) throws Exception {
        String sql = "SELECT * FROM NHANVIEN WHERE TENDANGNHAP = ? AND MATKHAU = ? AND TRANGTHAILAMVIEC = 1";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
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
                } else {
                    throw new Exception("Tên đăng nhập hoặc mật khẩu không chính xác!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - kiemTraDangNhap: " + e.getMessage());
            throw new Exception("Lỗi hệ thống khi đăng nhập!");
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
            System.err.println("Lỗi DAO - doiMatKhau: " + e.getMessage());
        }
        return false;
    }

    public int themNhanVien(NhanVienDTO nv) throws Exception {
        String sql = "{CALL PROC_THEM_NHANVIEN(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnect.getConnection();
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
            System.err.println("Lỗi DAO - themNhanVien: " + e.getMessage());
            throw new Exception("Lỗi khi thêm Nhân viên: " + e.getMessage());
        }
    }

    public boolean suaNhanVien(NhanVienDTO nv) throws Exception {
        String sql = "{CALL PROC_SUA_NHANVIEN(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnect.getConnection();
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
            System.err.println("Lỗi DAO - suaNhanVien: " + e.getMessage());
            throw new Exception("Lỗi khi cập nhật Nhân viên: " + e.getMessage());
        }
    }

    public boolean xoaNhanVien(int maNV) throws Exception {
        String sql = "{CALL PROC_XOA_NHANVIEN(?)}";
        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
             
            cstmt.setInt(1, maNV);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - xoaNhanVien: " + e.getMessage());
            throw new Exception("Lỗi khi xóa Nhân viên: " + e.getMessage());
        }
    }
}