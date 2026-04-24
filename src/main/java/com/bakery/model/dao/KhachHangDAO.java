package com.bakery.model.dao;

import com.bakery.model.dto.KhachHangDTO;
import com.bakery.utils.DBConnect;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

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
            System.err.println("Lỗi DAO - timKhachHangBangSDT: " + e.getMessage());
        }
        return null;
    }

    public int themKhachHangMoi(KhachHangDTO kh) {
        String sql = "{CALL PROC_THEMKHACHHANG(?, ?, ?, ?)}";

        try (Connection conn = DBConnect.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, kh.getHoTen());
            cstmt.setString(2, kh.getSdt());
            cstmt.setString(3, kh.getDiaChi());
            cstmt.registerOutParameter(4, Types.NUMERIC);

            cstmt.execute();
            return cstmt.getInt(4);
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - themKhachHangMoi: " + e.getMessage());
        }
        return -1;
    }

    public boolean capNhatKhachHang(KhachHangDTO kh) {
        String sql = "{CALL PROC_CAPNHATKHACHHANG(?, ?, ?, ?)}";

        try (Connection conn = DBConnect.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, kh.getMaKH());
            cstmt.setString(2, kh.getHoTen());
            cstmt.setString(3, kh.getSdt());
            cstmt.setString(4, kh.getDiaChi());

            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - capNhatKhachHang: " + e.getMessage());
        }
        return false;
    }

    public List<KhachHangDTO> layDanhSachKhachHang() {
        List<KhachHangDTO> ds = new ArrayList<>();
        String sql = "SELECT MAKH, HOTEN, SDT, DIACHI, NGAYDANGKY, DIEMTICHLUY, MAHANG " +
                     "FROM KHACHHANG WHERE THOIDIEMXOA IS NULL ORDER BY MAKH DESC";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    KhachHangDTO kh = new KhachHangDTO();
                    kh.setMaKH(rs.getInt("MAKH"));
                    kh.setHoTen(rs.getString("HOTEN"));
                    kh.setSdt(rs.getString("SDT"));
                    kh.setDiaChi(rs.getString("DIACHI"));
                    if (rs.getDate("NGAYDANGKY") != null) {
                        kh.setNgayDangKy(rs.getDate("NGAYDANGKY").toLocalDate());
                    }
                    kh.setDiemTichLuy(rs.getInt("DIEMTICHLUY"));
                    int maHang = rs.getInt("MAHANG");
                    if (!rs.wasNull()) {
                        kh.setMaHang(maHang);
                    }
                    ds.add(kh);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - layDanhSachKhachHang: " + e.getMessage());
        }
        return ds;
    }

    public boolean capNhatDiemTichLuy(int maKH, int diemMoi) {
        String sql = "UPDATE KHACHHANG SET DIEMTICHLUY = ? WHERE MaKH = ?";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, diemMoi);
            pstmt.setInt(2, maKH);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - capNhatDiemTichLuy: " + e.getMessage());
        }
        return false;
    }
}