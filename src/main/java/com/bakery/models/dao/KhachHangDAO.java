package com.bakery.models.dao;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.utils.DBConnect;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class KhachHangDAO {

    // ====== TÌM KIẾM ======

    public KhachHangDTO timKhachHangBangSDT(String sdt) {
        String sql = "SELECT * FROM KHACHHANG WHERE SDT = ? AND THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sdt);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKhachHang(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - timKhachHangBangSDT: " + e.getMessage());
        }
        return null;
    }

    public KhachHangDTO timKhachHangBangMaKH(int maKH) {
        String sql = "SELECT * FROM KHACHHANG WHERE MAKH = ? AND THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maKH);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKhachHang(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - timKhachHangBangMaKH: " + e.getMessage());
        }
        return null;
    }

    public KhachHangDTO timKhachHangXoa(String sdt) {
        String sql = "SELECT * FROM KHACHHANG WHERE SDT = ? AND THOIDIEMXOA IS NOT NULL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sdt);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToKhachHang(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - timKhachHangXoa: " + e.getMessage());
        }
        return null;
    }

    // ====== THÊM ======

    public int themKhachHangMoi(KhachHangDTO kh) throws SQLException {
        String sql = "{ CALL PROC_THEM_KHACHHANG(?, ?, ?, ?, ?, ?) }";

        try (Connection conn = DBConnect.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, kh.getHoTen());
            cstmt.setString(2, kh.getSdt());
            cstmt.setString(3, kh.getDiaChi());
            cstmt.setInt(4, kh.getDiemTichLuy());
            if (kh.getMaHang() > 0) {
                cstmt.setInt(5, kh.getMaHang());
            } else {
                cstmt.setNull(5, Types.INTEGER);
            }
            cstmt.registerOutParameter(6, Types.INTEGER);

            cstmt.execute();
            int maKH = cstmt.getInt(6);

            if (maKH > 0) {
                return maKH;
            } else {
                throw new SQLException("Thêm khách hàng thất bại - không nhận được mã khách");
            }

        } catch (SQLException e) {
            String errorMsg = mapProcedureErrorToMessage(e);
            System.err.println("Lỗi DAO - themKhachHangMoi: " + errorMsg);
            throw new SQLException(errorMsg, e);
        }
    }

    // ====== SỬA ======

    public void suaKhachHang(KhachHangDTO kh) throws SQLException {
        String sql = "{ CALL PROC_SUA_KHACHHANG(?, ?, ?, ?, ?, ?) }";

        try (Connection conn = DBConnect.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, kh.getMaKH());
            cstmt.setString(2, kh.getHoTen() != null ? kh.getHoTen() : "");
            cstmt.setString(3, kh.getSdt() != null ? kh.getSdt() : "");
            cstmt.setString(4, kh.getDiaChi() != null ? kh.getDiaChi() : "");
            if (kh.getDiemTichLuy() >= 0) {
                cstmt.setInt(5, kh.getDiemTichLuy());
            } else {
                cstmt.setNull(5, Types.INTEGER);
            }
            if (kh.getMaHang() > 0) {
                cstmt.setInt(6, kh.getMaHang());
            } else {
                cstmt.setNull(6, Types.INTEGER);
            }

            cstmt.execute();

        } catch (SQLException e) {
            String errorMsg = mapProcedureErrorToMessage(e);
            System.err.println("Lỗi DAO - suaKhachHang: " + errorMsg);
            throw new SQLException(errorMsg, e);
        }
    }

    // ====== XÓA ======

    public void xoaKhachHang(int maKH, int manvXoa) throws SQLException {
        String sql = "{ CALL PROC_XOA_KHACHHANG(?, ?) }";

        try (Connection conn = DBConnect.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, maKH);
            cstmt.setInt(2, manvXoa);

            cstmt.execute();

        } catch (SQLException e) {
            String errorMsg = mapProcedureErrorToMessage(e);
            System.err.println("Lỗi DAO - xoaKhachHang: " + errorMsg);
            throw new SQLException(errorMsg, e);
        }
    }

    public void khoiPhucKhachHang(int maKH) throws SQLException {
        String sql = "UPDATE KHACHHANG SET THOIDIEMXOA = NULL, MANX = NULL WHERE MAKH = ?";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maKH);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("Không tìm thấy khách hàng để khôi phục");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi DAO - khoiPhucKhachHang: " + e.getMessage());
            throw e;
        }
    }

    // ====== CẬP NHẬT ======

    public void capNhatDiemTichLuy(int maKH, int diemMoi) throws SQLException {
        String sql = "UPDATE KHACHHANG SET DIEMTICHLUY = ? WHERE MAKH = ?";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, diemMoi);
            pstmt.setInt(2, maKH);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Không tìm thấy khách hàng để cập nhật điểm");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi DAO - capNhatDiemTichLuy: " + e.getMessage());
            throw e;
        }
    }

    // ====== HỖ TRỢ ======

    private KhachHangDTO mapResultSetToKhachHang(ResultSet rs) throws SQLException {
        KhachHangDTO kh = new KhachHangDTO();
        kh.setMaKH(rs.getInt("MAKH"));
        kh.setHoTen(rs.getString("HOTEN"));
        kh.setSdt(rs.getString("SDT"));
        kh.setDiaChi(rs.getString("DIACHI"));
        if (rs.getDate("NGAYDANGKY") != null) {
            kh.setNgayDangKy(rs.getDate("NGAYDANGKY").toLocalDate());
        }
        kh.setDiemTichLuy(rs.getInt("DIEMTICHLUY"));
        kh.setMaHang(rs.getInt("MAHANG"));
        if (rs.getTimestamp("THOIDIEMXOA") != null) {
            kh.setThoiDiemXoa(rs.getTimestamp("THOIDIEMXOA").toLocalDateTime());
        }
        kh.setMaNX(rs.getInt("MANX"));
        return kh;
    }

    private String mapProcedureErrorToMessage(SQLException e) {
        int errorCode = e.getErrorCode();
        
        switch (errorCode) {
            case -20100:
                return "Lỗi hệ thống khi thêm Khách hàng";
            case -20101:
                return "Khách hàng không tồn tại để cập nhật";
            case -20102:
                return "Lỗi hệ thống khi cập nhật Khách hàng";
            case -20103:
                return "Khách hàng không tồn tại để xóa";
            case -20104:
                return "Lỗi hệ thống khi xóa Khách hàng";
            default:
                return "Lỗi cơ sở dữ liệu: " + e.getMessage();
        }
    }
}