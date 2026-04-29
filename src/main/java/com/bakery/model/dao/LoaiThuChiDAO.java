package com.bakery.model.dao;

import com.bakery.model.dto.LoaiThuChiDTO;
import com.bakery.utils.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiThuChiDAO {

    /**
     * Lấy danh sách loại thu chi còn hoạt động (chưa bị xóa mềm).
     * Không bao giờ trả null — trả list rỗng nếu không có dữ liệu.
     */
    public List<LoaiThuChiDTO> layDanhSach() {
        List<LoaiThuChiDTO> ds = new ArrayList<>();
        String sql = "SELECT MALOAITHUCHI, TENLOAITHUCHI, PHANLOAI, THOIDIEMXOA, MANX "
                + "FROM LOAITHUCHI "
                + "WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LoaiThuChiDTO ltc = new LoaiThuChiDTO();
                    ltc.setMaLoaiThuChi(rs.getInt("MALOAITHUCHI"));
                    ltc.setTenLoaiThuChi(rs.getString("TENLOAITHUCHI"));
                    ltc.setPhanLoai(rs.getString("PHANLOAI"));

                    int maNX = rs.getInt("MANX");
                    if (!rs.wasNull()) {
                        ltc.setMaNX(maNX);
                    }

                    ds.add(ltc);
                }
            }

        } catch (SQLException e) {
            System.err.println("[LoaiThuChiDAO] Lỗi: " + e.getMessage());
            throw new RuntimeException("Lỗi hệ thống khi lấy danh sách loại thu chi: " + e.getMessage(), e);
        }
        return ds;
    }

    /** Lấy tất cả danh mục kể cả đã khoá (THOIDIEMXOA != NULL). */
    public List<LoaiThuChiDTO> layTatCa() {
        List<LoaiThuChiDTO> ds = new ArrayList<>();
        String sql = "SELECT MALOAITHUCHI, TENLOAITHUCHI, PHANLOAI, THOIDIEMXOA, MANX "
                + "FROM LOAITHUCHI ORDER BY MALOAITHUCHI";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoaiThuChiDTO ltc = new LoaiThuChiDTO();
                ltc.setMaLoaiThuChi(rs.getInt("MALOAITHUCHI"));
                ltc.setTenLoaiThuChi(rs.getString("TENLOAITHUCHI"));
                ltc.setPhanLoai(rs.getString("PHANLOAI"));
                Timestamp ts = rs.getTimestamp("THOIDIEMXOA");
                if (ts != null) ltc.setThoiDiemXoa(ts.toLocalDateTime());
                int maNX = rs.getInt("MANX");
                if (!rs.wasNull()) ltc.setMaNX(maNX);
                ds.add(ltc);
            }
        } catch (SQLException e) {
            System.err.println("[LoaiThuChiDAO] Lỗi: " + e.getMessage());
            throw new RuntimeException("Lỗi hệ thống khi lấy danh sách loại: " + e.getMessage(), e);
        }
        return ds;
    }

    public void moKhoa(int maLoaiThuChi) {
        String sql = "UPDATE LOAITHUCHI SET THOIDIEMXOA = NULL WHERE MALOAITHUCHI = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maLoaiThuChi);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[LoaiThuChiDAO] Lỗi: " + e.getMessage());
            throw new RuntimeException("Lỗi mở khoá danh mục: " + e.getMessage(), e);
        }
    }

    public void them(String tenLoaiThuChi, String phanLoai) {
        String sql = "INSERT INTO LOAITHUCHI (TENLOAITHUCHI, PHANLOAI) VALUES (?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenLoaiThuChi);
            ps.setString(2, phanLoai);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[LoaiThuChiDAO] Lỗi: " + e.getMessage());
            throw new RuntimeException("Lỗi thêm loại thu chi: " + e.getMessage(), e);
        }
    }

    public void sua(int maLoaiThuChi, String tenLoaiThuChi, String phanLoai) {
        String sql = "UPDATE LOAITHUCHI SET TENLOAITHUCHI = ?, PHANLOAI = ? WHERE MALOAITHUCHI = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenLoaiThuChi);
            ps.setString(2, phanLoai);
            ps.setInt(3, maLoaiThuChi);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[LoaiThuChiDAO] Lỗi: " + e.getMessage());
            throw new RuntimeException("Lỗi cập nhật loại thu chi: " + e.getMessage(), e);
        }
    }

    public void xoa(int maLoaiThuChi) {
        String sql = "UPDATE LOAITHUCHI SET THOIDIEMXOA = SYSDATE WHERE MALOAITHUCHI = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maLoaiThuChi);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[LoaiThuChiDAO] Lỗi: " + e.getMessage());
            throw new RuntimeException("Lỗi xoá loại thu chi: " + e.getMessage(), e);
        }
    }

}
