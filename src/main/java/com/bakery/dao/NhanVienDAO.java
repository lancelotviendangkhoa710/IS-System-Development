package com.bakery.dao;
import com.bakery.dto.NhanVienDTO;
import com.bakery.utils.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NhanVienDAO {

    public NhanVienDTO kiemTraDangNhap(String username, String password) {
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
                        nv.setNgaySinh(rs.getDate("NGAYSINH").toLocalDate());//nếu null gán ngày sinh là ngày hiện tại
                    }
                    nv.setSdt(rs.getString("SDT"));
                    nv.setTenDangNhap(rs.getString("TENDANGNHAP"));
                    nv.setMatKhau(rs.getString("MATKHAU"));
                    nv.setTrangThaiLamViec(rs.getInt("TRANGTHAILAMVIEC"));
                    return nv;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - kiemTraDangNhap: " + e.getMessage());
        }
        return null; // Trả về null nếu sai thông tin hoặc tài khoản bị khóa
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
}