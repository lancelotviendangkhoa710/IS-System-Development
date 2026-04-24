package com.bakery.models.dao;

import com.bakery.models.dto.KhachHangDTO;
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

    // Tim khach hang dang hoat dong theo so dien thoai.
    public KhachHangDTO findActiveCustomerByPhone(String phone) {
        String sql = "SELECT * FROM KHACHHANG WHERE SDT = ? AND THOIDIEMXOA IS NULL";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO error - findActiveCustomerByPhone: " + e.getMessage());
        }
        return null;
    }

    // Tim khach hang dang hoat dong theo ma khach hang.
    public KhachHangDTO findActiveCustomerById(int customerId) {
        String sql = "SELECT * FROM KHACHHANG WHERE MAKH = ? AND THOIDIEMXOA IS NULL";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO error - findActiveCustomerById: " + e.getMessage());
        }
        return null;
    }

    // Tim khach hang dang hoat dong theo dia chi.
    public KhachHangDTO findActiveCustomerByAddress(String address) {
        String sql = "SELECT * FROM KHACHHANG WHERE DIACHI = ? AND THOIDIEMXOA IS NULL";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, address);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO error - findActiveCustomerByAddress: " + e.getMessage());
        }
        return null;
    }

    // Tim khach hang da bi xoa theo so dien thoai.
    public KhachHangDTO findDeletedCustomerByPhone(String phone) {
        String sql = "SELECT * FROM KHACHHANG WHERE SDT = ? AND THOIDIEMXOA IS NOT NULL";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO error - findDeletedCustomerByPhone: " + e.getMessage());
        }
        return null;
    }

    // Lay danh sach khach hang dang hoat dong.
    public List<KhachHangDTO> getAllActiveCustomers() throws SQLException {
        String sql = "SELECT * FROM KHACHHANG WHERE THOIDIEMXOA IS NULL ORDER BY MAKH DESC";
        List<KhachHangDTO> customers = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                customers.add(mapRowToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("DAO error - getAllActiveCustomers: " + e.getMessage());
            throw e;
        }
        return customers;
    }

    // Tim kiem khach hang dang hoat dong theo tu khoa.
    public List<KhachHangDTO> searchActiveCustomers(String keyword) throws SQLException {
        String sql = """
                SELECT *
                FROM KHACHHANG
                WHERE THOIDIEMXOA IS NULL
                  AND (
                        LOWER(HOTEN) LIKE ?
                        OR SDT LIKE ?
                        OR TO_CHAR(MAKH) LIKE ?
                        OR LOWER(DIACHI) LIKE ?
                  )
                ORDER BY MAKH DESC
                """;
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        String searchPattern = "%" + normalizedKeyword + "%";
        List<KhachHangDTO> customers = new ArrayList<>();

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapRowToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO error - searchActiveCustomers: " + e.getMessage());
            throw e;
        }
        return customers;
    }

    // Tao moi khach hang qua procedure.
    public int createCustomer(KhachHangDTO customer) throws SQLException {
        String sql = "{ CALL PROC_THEM_KHACHHANG(?, ?, ?, ?, ?, ?) }";
        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, customer.getHoTen());
            cstmt.setString(2, customer.getSdt());
            cstmt.setString(3, customer.getDiaChi());
            cstmt.setInt(4, customer.getDiemTichLuy());
            if (customer.getMaHang() > 0) {
                cstmt.setInt(5, customer.getMaHang());
            } else {
                cstmt.setNull(5, Types.INTEGER);
            }
            cstmt.registerOutParameter(6, Types.INTEGER);
            cstmt.execute();
            int customerId = cstmt.getInt(6);
            if (customerId <= 0) {
                throw new SQLException("Khong nhan duoc ma khach hang moi");
            }
            return customerId;
        } catch (SQLException e) {
            String errorMsg = mapProcedureErrorToMessage(e);
            System.err.println("DAO error - createCustomer: " + errorMsg);
            throw new SQLException(errorMsg, e);
        }
    }

    // Cap nhat thong tin khach hang qua procedure.
    public void updateCustomer(KhachHangDTO customer) throws SQLException {
        String sql = "{ CALL PROC_SUA_KHACHHANG(?, ?, ?, ?, ?, ?) }";
        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, customer.getMaKH());
            cstmt.setString(2, customer.getHoTen() != null ? customer.getHoTen() : "");
            cstmt.setString(3, customer.getSdt() != null ? customer.getSdt() : "");
            cstmt.setString(4, customer.getDiaChi() != null ? customer.getDiaChi() : "");
            if (customer.getDiemTichLuy() >= 0) {
                cstmt.setInt(5, customer.getDiemTichLuy());
            } else {
                cstmt.setNull(5, Types.INTEGER);
            }
            if (customer.getMaHang() > 0) {
                cstmt.setInt(6, customer.getMaHang());
            } else {
                cstmt.setNull(6, Types.INTEGER);
            }
            cstmt.execute();
        } catch (SQLException e) {
            String errorMsg = mapProcedureErrorToMessage(e);
            System.err.println("DAO error - updateCustomer: " + errorMsg);
            throw new SQLException(errorMsg, e);
        }
    }

    // Xoa mem khach hang qua procedure.
    public void softDeleteCustomer(int customerId, int deletedByEmployeeId) throws SQLException {
        String sql = "{ CALL PROC_XOA_KHACHHANG(?, ?) }";
        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, customerId);
            cstmt.setInt(2, deletedByEmployeeId);
            cstmt.execute();
        } catch (SQLException e) {
            String errorMsg = mapProcedureErrorToMessage(e);
            System.err.println("DAO error - softDeleteCustomer: " + errorMsg);
            throw new SQLException(errorMsg, e);
        }
    }

    // Khoi phuc khach hang da xoa.
    public void restoreCustomer(int customerId) throws SQLException {
        String sql = "UPDATE KHACHHANG SET THOIDIEMXOA = NULL, MANX = NULL WHERE MAKH = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Khong tim thay khach hang de khoi phuc");
            }
        } catch (SQLException e) {
            System.err.println("DAO error - restoreCustomer: " + e.getMessage());
            throw e;
        }
    }

    // Cap nhat diem tich luy cho khach hang.
    public void updateCustomerPoints(int customerId, int newPoints) throws SQLException {
        String sql = "UPDATE KHACHHANG SET DIEMTICHLUY = ? WHERE MAKH = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newPoints);
            pstmt.setInt(2, customerId);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Khong tim thay khach hang de cap nhat diem");
            }
        } catch (SQLException e) {
            System.err.println("DAO error - updateCustomerPoints: " + e.getMessage());
            throw e;
        }
    }

    // Dem tong so khach hang dang hoat dong.
    public int countActiveCustomers() {
        String sql = "SELECT COUNT(*) FROM KHACHHANG WHERE THOIDIEMXOA IS NULL";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("DAO error - countActiveCustomers: " + e.getMessage());
        }
        return 0;
    }

    // Dem so khach hang moi trong thang.
    public int countNewCustomersInMonth(int year, int month) {
        String sql = "SELECT COUNT(*) FROM KHACHHANG WHERE EXTRACT(YEAR FROM NGAYDANGKY) = ? AND EXTRACT(MONTH FROM NGAYDANGKY) = ? AND THOIDIEMXOA IS NULL";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO error - countNewCustomersInMonth: " + e.getMessage());
        }
        return 0;
    }

    // Lay danh sach khach hang da xoa.
    public List<KhachHangDTO> getAllDeletedCustomers() {
        String sql = "SELECT * FROM KHACHHANG WHERE THOIDIEMXOA IS NOT NULL ORDER BY THOIDIEMXOA DESC";
        List<KhachHangDTO> customers = new ArrayList<>();
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                customers.add(mapRowToCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("DAO error - getAllDeletedCustomers: " + e.getMessage());
        }
        return customers;
    }

    // Anh xa ResultSet thanh DTO.
    private KhachHangDTO mapRowToCustomer(ResultSet rs) throws SQLException {
        KhachHangDTO customer = new KhachHangDTO();
        customer.setMaKH(rs.getInt("MAKH"));
        customer.setHoTen(rs.getString("HOTEN"));
        customer.setSdt(rs.getString("SDT"));
        customer.setDiaChi(rs.getString("DIACHI"));
        if (rs.getDate("NGAYDANGKY") != null) {
            customer.setNgayDangKy(rs.getDate("NGAYDANGKY").toLocalDate());
        }
        customer.setDiemTichLuy(rs.getInt("DIEMTICHLUY"));
        customer.setMaHang(rs.getInt("MAHANG"));
        if (rs.getTimestamp("THOIDIEMXOA") != null) {
            customer.setThoiDiemXoa(rs.getTimestamp("THOIDIEMXOA").toLocalDateTime());
        }
        customer.setMaNX(rs.getInt("MANX"));
        return customer;
    }

    // Chuyen ma loi DB thanh thong diep de Service hien thi.
    private String mapProcedureErrorToMessage(SQLException e) {
        int errorCode = e.getErrorCode();
        return switch (errorCode) {
            case -20100 -> "Loi he thong khi them khach hang";
            case -20101 -> "Khach hang khong ton tai de cap nhat";
            case -20102 -> "Loi he thong khi cap nhat khach hang";
            case -20103 -> "Khach hang khong ton tai de xoa";
            case -20104 -> "Loi he thong khi xoa khach hang";
            default -> "Loi co so du lieu: " + e.getMessage();
        };
    }

    // ===== Compatibility wrappers (CRM migration) =====
    @Deprecated
    public KhachHangDTO timKhachHangBangSDT(String sdt) { return findActiveCustomerByPhone(sdt); }
    @Deprecated
    public KhachHangDTO timKhachHangBangMaKH(int maKH) { return findActiveCustomerById(maKH); }
    @Deprecated
    public KhachHangDTO timKhachHangBangDiaChi(String diaChi) { return findActiveCustomerByAddress(diaChi); }
    @Deprecated
    public KhachHangDTO timKhachHangXoa(String sdt) { return findDeletedCustomerByPhone(sdt); }
    @Deprecated
    public List<KhachHangDTO> layDanhSachKhachHangHoatDong() throws SQLException { return getAllActiveCustomers(); }
    @Deprecated
    public List<KhachHangDTO> timKhachHangTheoTuKhoa(String tuKhoa) throws SQLException { return searchActiveCustomers(tuKhoa); }
    @Deprecated
    public int themKhachHangMoi(KhachHangDTO kh) throws SQLException { return createCustomer(kh); }
    @Deprecated
    public void suaKhachHang(KhachHangDTO kh) throws SQLException { updateCustomer(kh); }
    @Deprecated
    public void xoaKhachHang(int maKH, int manvXoa) throws SQLException { softDeleteCustomer(maKH, manvXoa); }
    @Deprecated
    public void khoiPhucKhachHang(int maKH) throws SQLException { restoreCustomer(maKH); }
    @Deprecated
    public void capNhatDiemTichLuy(int maKH, int diemMoi) throws SQLException { updateCustomerPoints(maKH, diemMoi); }
    @Deprecated
    public List<KhachHangDTO> getAllActive() {
        try {
            return getAllActiveCustomers();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }
    @Deprecated
    public int countActive() { return countActiveCustomers(); }
    @Deprecated
    public int countNewThisMonth(int year, int month) { return countNewCustomersInMonth(year, month); }
    @Deprecated
    public List<KhachHangDTO> getAllDeleted() { return getAllDeletedCustomers(); }
    @Deprecated
    public boolean softDelete(int maKH, int manvXoa) {
        try { softDeleteCustomer(maKH, manvXoa); return true; }
        catch (SQLException e) { return false; }
    }
    @Deprecated
    public boolean restore(int maKH) {
        try { restoreCustomer(maKH); return true; }
        catch (SQLException e) { return false; }
    }
    @Deprecated
    public boolean update(KhachHangDTO customer) {
        try { updateCustomer(customer); return true; }
        catch (SQLException e) { return false; }
    }
    @Deprecated
    public boolean insert(KhachHangDTO kh) {
        try { createCustomer(kh); return true; }
        catch (SQLException e) { return false; }
    }
}

