package com.bakery.model.dao;

import com.bakery.model.dto.KhachHangDTO;
import com.bakery.utils.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KhachHangDAO {

    public KhachHangDTO timKhachHangBangSDT(String sdt) {
        String sql = "SELECT * FROM KHACHHANG WHERE SDT = ? AND THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sdt);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    KhachHangDTO kh = new KhachHangDTO();
                    kh.setMaKH(rs.getInt("MaKH"));
                    kh.setHoTen(rs.getString("HOTEN"));
                    kh.setSdt(rs.getString("SDT"));
                    kh.setDiaChi(rs.getString("DIACHI"));
                    if (rs.getDate("NGAYDANGKY") != null) {
                        kh.setNgayDangKy(rs.getDate("NGAYDANGKY").toLocalDate());
                    }
                    kh.setDiemTichLuy(rs.getInt("DIEMTICHLUY"));
                    kh.setMaHang(rs.getInt("MAHANG"));
                    return kh;
                }
            }
        } catch (SQLException e) {
            System.err.println("L?i DAO - timKhachHangBangSDT: " + e.getMessage());
        }
        return null;
    }

    public int themKhachHangMoi(KhachHangDTO kh) {
        String sql = "INSERT INTO KHACHHANG (HOTEN, SDT, DIACHI) VALUES (?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, new String[] { "MAKH" })) {

            pstmt.setString(1, kh.getHoTen());
            pstmt.setString(2, kh.getSdt());
            pstmt.setString(3, kh.getDiaChi());

            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("L?i DAO - themKhachHangMoi: " + e.getMessage());
        }
        return -1;
    }

    public boolean capNhatKhachHang(KhachHangDTO kh) {
        String sql = "UPDATE KHACHHANG SET HOTEN = ?, SDT = ?, DIACHI = ? WHERE MAKH = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kh.getHoTen());
            pstmt.setString(2, kh.getSdt());
            pstmt.setString(3, kh.getDiaChi());
            pstmt.setInt(4, kh.getMaKH());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("L?i DAO - capNhatKhachHang: " + e.getMessage());
        }
        return false;
    }

    public boolean capNhatDiemTichLuy(int maKH, int diemMoi) {
        String sql = "UPDATE KHACHHANG SET DIEMTICHLUY = ? WHERE MaKH = ?";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, diemMoi);
            pstmt.setInt(2, maKH);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("L?i DAO - capNhatDiemTichLuy: " + e.getMessage());
        }
        return false;
    }
}