package com.bakery.model.dao;

import com.bakery.model.dto.NhanVienDTO;
import com.bakery.utils.DBConnect;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

public class NhanVienDAO {

    public NhanVienDTO kiemTraDangNhap(String username, String password) throws Exception {
        String sql = "SELECT NV.*, VT.TENVAITRO AS TENVAITRO " +
                "FROM NHANVIEN NV " +
                "LEFT JOIN VAITRO VT ON VT.MAVAITRO = NV.MAVAITRO " +
                "WHERE NV.TENDANGNHAP = ? AND NV.MATKHAU = ? AND NV.TRANGTHAILAMVIEC = 1";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    NhanVienDTO nv = new NhanVienDTO();
                    nv.setMaNV(rs.getInt("MANV"));
                    nv.setMaVaiTro(rs.getInt("MAVAITRO"));
                    nv.setTenVaiTro(rs.getString("TENVAITRO"));
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

    public int themNhanVien(NhanVienDTO nv) throws SQLException {
        String sql = "{CALL PROC_THEM_NHANVIEN(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBConnect.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, nv.getMaVaiTro());
            cstmt.setString(2, nv.getHoTen());
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
        }
    }

    public Map<Integer, String> layDanhSachVaiTro() {
        Map<Integer, String> map = new LinkedHashMap<>();
        String sql = "SELECT MAVAITRO, TENVAITRO FROM VAITRO WHERE THOIDIEMXOA IS NULL ORDER BY MAVAITRO";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("MAVAITRO"), rs.getString("TENVAITRO"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - layDanhSachVaiTro: " + e.getMessage());
        }
        return map;
    }
}
