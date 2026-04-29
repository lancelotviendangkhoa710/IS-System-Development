package com.bakery.model.dao;

import com.bakery.model.dto.NguyenLieuDTO;
import com.bakery.utils.DBConnect;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO truy xuất bảng NGUYENLIEU.
 * Tương tác với DB qua PreparedStatement (SELECT) và Stored Procedure
 * (CUD).
 */
public class NguyenLieuDAO {

    /**
     * Lấy tất cả nguyên liệu còn hoạt động (chưa bị xóa mềm).
     */
    public List<NguyenLieuDTO> layTatCaNguyenLieu() {
        List<NguyenLieuDTO> list = new ArrayList<>();
        String sql = "SELECT MANL, TENNL, XUATXU, MADVT, GIAVONTRUNGBINH, " +
                "MUCTONANTOAN, SOLUONGTONTONG, DATCHUANVSATTP, PHIENBAN, " +
                "THOIDIEMXOA, MANX " +
                "FROM NGUYENLIEU " +
                "WHERE THOIDIEMXOA IS NULL " +
                "ORDER BY MANL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapNguyenLieu(rs));
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - layTatCaNguyenLieu: " + e.getMessage());
        }

        return list;
    }

    /**
     * Tìm kiếm nguyên liệu theo tên (LIKE, không phân biệt hoa
     * thường).
     */
    public List<NguyenLieuDTO> timKiemNguyenLieu(String keyword) {
        List<NguyenLieuDTO> list = new ArrayList<>();
        String sql = "SELECT MANL, TENNL, XUATXU, MADVT, GIAVONTRUNGBINH, " +
                "MUCTONANTOAN, SOLUONGTONTONG, DATCHUANVSATTP, PHIENBAN, " +
                "THOIDIEMXOA, MANX " +
                "FROM NGUYENLIEU " +
                "WHERE THOIDIEMXOA IS NULL " +
                "AND LOWER(TENNL) LIKE ? " +
                "ORDER BY MANL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapNguyenLieu(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - timKiemNguyenLieu: " + e.getMessage());
        }

        return list;
    }

    /**
     * Thêm mới nguyên liệu qua PROC_THEM_NGUYENLIEU.
     * Tham số: P_TENNL, P_XUATXU, P_MUCTONANTOAN, P_MADVT, P_MANV, P_MANL_OUT
     *
     * @return mã nguyên liệu vừa tạo (>= 1), hoặc -1 nếu lỗi
     */
    public int themNguyenLieu(NguyenLieuDTO dto, int maNv) {
        String sql = "{CALL PROC_THEM_NGUYENLIEU(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnect.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, dto.getTenNL());
            cstmt.setString(2, dto.getXuatXu());
            cstmt.setDouble(3, dto.getMucTonAnToan());
            cstmt.setInt(4, dto.getMaDVT());
            cstmt.setInt(5, maNv);
            cstmt.registerOutParameter(6, Types.NUMERIC);

            cstmt.execute();
            return cstmt.getInt(6);
        } catch (SQLException e) {
            System.err.println("Loi DAO - themNguyenLieu: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Cập nhật nguyên liệu qua PROC_SUA_NGUYENLIEU.
     * Tham số: P_MANL, P_TENNL, P_XUATXU, P_MADVT, P_MUCTONANTOAN
     */
    public boolean capNhatNguyenLieu(NguyenLieuDTO dto) {
        String sql = "{CALL PROC_SUA_NGUYENLIEU(?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnect.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, dto.getMaNL());
            cstmt.setString(2, dto.getTenNL());
            cstmt.setString(3, dto.getXuatXu());
            cstmt.setInt(4, dto.getMaDVT());
            cstmt.setDouble(5, dto.getMucTonAnToan());

            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Loi DAO - capNhatNguyenLieu: " + e.getMessage());
        }
        return false;
    }

    /**
     * Xóa nguyên liệu qua PROC_XOA_NGUYENLIEU.
     * Procedure tự phân loại: xóa mềm nếu đã có lịch sử, xóa
     * cứng nếu chưa.
     */
    public boolean xoaNguyenLieu(int maNL, int maNv) {
        String sql = "{CALL PROC_XOA_NGUYENLIEU(?, ?)}";
        try (Connection conn = DBConnect.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, maNL);
            cstmt.setInt(2, maNv);

            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Loi DAO - xoaNguyenLieu: " + e.getMessage());
        }
        return false;
    }

    private NguyenLieuDTO mapNguyenLieu(ResultSet rs) throws SQLException {
        NguyenLieuDTO dto = new NguyenLieuDTO();
        dto.setMaNL(rs.getInt("MANL"));
        dto.setTenNL(rs.getString("TENNL"));
        dto.setXuatXu(rs.getString("XUATXU"));
        dto.setMaDVT(rs.getInt("MADVT"));
        dto.setGiaVonTrungBinh(rs.getBigDecimal("GIAVONTRUNGBINH"));
        dto.setMucTonAnToan(rs.getDouble("MUCTONANTOAN"));
        dto.setSoLuongTonTong(rs.getDouble("SOLUONGTONTONG"));
        dto.setDatChuanVSATTP(rs.getInt("DATCHUANVSATTP"));
        dto.setPhienBan(rs.getInt("PHIENBAN"));

        if (rs.getTimestamp("THOIDIEMXOA") != null) {
            dto.setThoiDiemXoa(rs.getTimestamp("THOIDIEMXOA").toLocalDateTime());
        }

        int maNX = rs.getInt("MANX");
        if (!rs.wasNull()) {
            dto.setMaNX(maNX);
        }

        return dto;
    }
}
