import re

head_dao = """package com.bakery.model.dao;

import com.bakery.model.dto.KhachHangDTO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO extends BaseDAO {

    public KhachHangDTO timKhachHangBangSDT(String sdt) throws Exception {
        String sql = "SELECT * FROM KHACHHANG WHERE SDT = ? AND THOIDIEMXOA IS NULL";

        try (Connection conn = moKetNoi();
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
            handleException("timKhachHangBangSDT", e);
        }
        return null;
    }

    public int themKhachHangMoi(KhachHangDTO kh) throws Exception {
        String sql = "INSERT INTO KHACHHANG (HOTEN, SDT, DIACHI) VALUES (?, ?, ?)";

        try (Connection conn = moKetNoi();
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
            handleException("themKhachHangMoi", e);
        }
        return -1;
    }

    public boolean capNhatKhachHang(KhachHangDTO kh) throws Exception {
        String sql = "UPDATE KHACHHANG SET HOTEN = ?, SDT = ?, DIACHI = ? WHERE MAKH = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kh.getHoTen());
            pstmt.setString(2, kh.getSdt());
            pstmt.setString(3, kh.getDiaChi());
            pstmt.setInt(4, kh.getMaKH());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("capNhatKhachHang", e);
        }
        return false;
    }

    public boolean capNhatDiemTichLuy(int maKH, int diemMoi) throws Exception {
        String sql = "UPDATE KHACHHANG SET DIEMTICHLUY = ? WHERE MaKH = ?";

        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, diemMoi);
            pstmt.setInt(2, maKH);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("capNhatDiemTichLuy", e);
        }
        return false;
    }

"""

devhuy_methods = """
    private static final String CUSTOMER_SELECT_SQL = "SELECT KH.*, HTV.TENHANG AS TENHANG, NV.HOTEN AS TENNX FROM KHACHHANG KH LEFT JOIN HANGTHANHVIEN HTV ON KH.MAHANG = HTV.MAHANG LEFT JOIN NHANVIEN NV ON KH.MANX = NV.MANV";

    public KhachHangDTO findActiveCustomerByPhone(String phone) throws Exception {
        String sql = CUSTOMER_SELECT_SQL + " WHERE KH.SDT = ? AND KH.THOIDIEMXOA IS NULL";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            handleException("findActiveCustomerByPhone", e);
        }
        return null;
    }

    public KhachHangDTO findActiveCustomerById(int customerId) throws Exception {
        String sql = CUSTOMER_SELECT_SQL + " WHERE KH.MAKH = ? AND KH.THOIDIEMXOA IS NULL";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            handleException("findActiveCustomerById", e);
        }
        return null;
    }

    public KhachHangDTO findActiveCustomerByAddress(String address) throws Exception {
        String sql = CUSTOMER_SELECT_SQL + " WHERE KH.DIACHI = ? AND KH.THOIDIEMXOA IS NULL";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, address);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            handleException("findActiveCustomerByAddress", e);
        }
        return null;
    }

    public KhachHangDTO findDeletedCustomerByPhone(String phone) throws Exception {
        String sql = CUSTOMER_SELECT_SQL + " WHERE KH.SDT = ? AND KH.THOIDIEMXOA IS NOT NULL";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            handleException("findDeletedCustomerByPhone", e);
        }
        return null;
    }

    public List<KhachHangDTO> getAllActiveCustomers() throws Exception {
        String sql = CUSTOMER_SELECT_SQL + " WHERE KH.THOIDIEMXOA IS NULL ORDER BY KH.MAKH DESC";
        List<KhachHangDTO> customers = new ArrayList<>();
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                customers.add(mapRowToCustomer(rs));
            }
        } catch (SQLException e) {
            handleException("getAllActiveCustomers", e);
        }
        return customers;
    }

    public List<KhachHangDTO> searchActiveCustomers(String keyword) throws Exception {
        String sql = CUSTOMER_SELECT_SQL + " WHERE KH.THOIDIEMXOA IS NULL AND (LOWER(KH.HOTEN) LIKE ? OR KH.SDT LIKE ? OR TO_CHAR(KH.MAKH) LIKE ? OR LOWER(KH.DIACHI) LIKE ?) ORDER BY KH.MAKH DESC";
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        String searchPattern = "%" + normalizedKeyword + "%";
        List<KhachHangDTO> customers = new ArrayList<>();

        try (Connection conn = moKetNoi();
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
            handleException("searchActiveCustomers", e);
        }
        return customers;
    }

    public int createCustomer(KhachHangDTO customer) throws Exception {
        String sql = "{ CALL PROC_THEM_KHACHHANG(?, ?, ?, ?, ?, ?) }";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, customer.getHoTen());
            cstmt.setString(2, customer.getSdt());
            cstmt.setString(3, customer.getDiaChi());
            cstmt.setInt(4, customer.getDiemTichLuy() != null ? customer.getDiemTichLuy() : 0);
            if (customer.getMaHang() != null && customer.getMaHang() > 0) {
                cstmt.setInt(5, customer.getMaHang());
            } else {
                cstmt.setNull(5, Types.INTEGER);
            }
            cstmt.registerOutParameter(6, Types.INTEGER);
            cstmt.execute();
            int customerId = cstmt.getInt(6);
            if (customerId <= 0) {
                throw new Exception("Khong nhan duoc ma khach hang moi");
            }
            return customerId;
        } catch (SQLException e) {
            handleException("createCustomer", e);
        }
        return -1;
    }

    public void updateCustomer(KhachHangDTO customer) throws Exception {
        String sql = "{ CALL PROC_SUA_KHACHHANG(?, ?, ?, ?, ?, ?) }";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, customer.getMaKH());
            cstmt.setString(2, customer.getHoTen() != null ? customer.getHoTen() : "");
            cstmt.setString(3, customer.getSdt() != null ? customer.getSdt() : "");
            cstmt.setString(4, customer.getDiaChi() != null ? customer.getDiaChi() : "");
            if (customer.getDiemTichLuy() != null && customer.getDiemTichLuy() >= 0) {
                cstmt.setInt(5, customer.getDiemTichLuy());
            } else {
                cstmt.setNull(5, Types.INTEGER);
            }
            if (customer.getMaHang() != null && customer.getMaHang() > 0) {
                cstmt.setInt(6, customer.getMaHang());
            } else {
                cstmt.setNull(6, Types.INTEGER);
            }
            cstmt.execute();
        } catch (SQLException e) {
            handleException("updateCustomer", e);
        }
    }

    public void softDeleteCustomer(int customerId, int deletedByEmployeeId) throws Exception {
        String sql = "{ CALL PROC_XOA_KHACHHANG(?, ?) }";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, customerId);
            cstmt.setInt(2, deletedByEmployeeId);
            cstmt.execute();
        } catch (SQLException e) {
            handleException("softDeleteCustomer", e);
        }
    }

    public void restoreCustomer(int customerId) throws Exception {
        String sql = "UPDATE KHACHHANG SET THOIDIEMXOA = NULL, MANX = NULL WHERE MAKH = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new Exception("Khong tim thay khach hang de khoi phuc");
            }
        } catch (SQLException e) {
            handleException("restoreCustomer", e);
        }
    }

    public void updateCustomerPoints(int customerId, int newPoints) throws Exception {
        String sql = "UPDATE KHACHHANG SET DIEMTICHLUY = ? WHERE MAKH = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newPoints);
            pstmt.setInt(2, customerId);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new Exception("Khong tim thay khach hang de cap nhat diem");
            }
        } catch (SQLException e) {
            handleException("updateCustomerPoints", e);
        }
    }

    public void syncAllCustomerTiers() throws Exception {
        String sql = "UPDATE KHACHHANG KH SET MAHANG = (SELECT MAHANG FROM HANGTHANHVIEN HTV WHERE HTV.DIEMTOITHIEU <= KH.DIEMTICHLUY AND HTV.THOIDIEMXOA IS NULL ORDER BY HTV.DIEMTOITHIEU DESC FETCH FIRST 1 ROW ONLY)";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleException("syncAllCustomerTiers", e);
        }
    }

    public int countActiveCustomers() throws Exception {
        String sql = "SELECT COUNT(*) FROM KHACHHANG WHERE THOIDIEMXOA IS NULL";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            handleException("countActiveCustomers", e);
        }
        return 0;
    }

    public int countNewCustomersInMonth(int year, int month) throws Exception {
        String sql = "SELECT COUNT(*) FROM KHACHHANG WHERE EXTRACT(YEAR FROM NGAYDANGKY) = ? AND EXTRACT(MONTH FROM NGAYDANGKY) = ? AND THOIDIEMXOA IS NULL";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            handleException("countNewCustomersInMonth", e);
        }
        return 0;
    }

    public List<KhachHangDTO> getAllDeletedCustomers() throws Exception {
        String sql = CUSTOMER_SELECT_SQL + " WHERE KH.THOIDIEMXOA IS NOT NULL ORDER BY KH.THOIDIEMXOA DESC";
        List<KhachHangDTO> customers = new ArrayList<>();
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                customers.add(mapRowToCustomer(rs));
            }
        } catch (SQLException e) {
            handleException("getAllDeletedCustomers", e);
        }
        return customers;
    }

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
        customer.setTenHang(rs.getString("TENHANG"));
        if (rs.getTimestamp("THOIDIEMXOA") != null) {
            customer.setThoiDiemXoa(rs.getTimestamp("THOIDIEMXOA").toLocalDateTime());
        }
        customer.setMaNX(rs.getInt("MANX"));
        customer.setTenNguoiXoa(rs.getString("TENNX"));
        return customer;
    }
}
"""

with open("d:/Clone/src/main/java/com/bakery/model/dao/KhachHangDAO.java", "w", encoding="utf-8") as f:
    f.write(head_dao + devhuy_methods)

