package com.bakery.model.dao;

import com.bakery.model.dto.DanhMucSPDTO;
import com.bakery.utils.DBConnect;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DanhMucSPDAO {

    public List<DanhMucSPDTO> layTatCaDanhMucConHoatDong() {
        List<DanhMucSPDTO> list = new ArrayList<>();
        String sql = "SELECT MADM, TENDM, THOIDIEMXOA, MANX " +
                "FROM DANHMUCSP " +
                "WHERE THOIDIEMXOA IS NULL " +
                "ORDER BY MADM";

        try (Connection conn = DBConnect.getConnection()) {
            if (conn == null) {
                throw new SQLException("Khong the ket noi CSDL.");
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        DanhMucSPDTO dto = new DanhMucSPDTO();
                        dto.setMaDM(rs.getInt("MADM"));
                        dto.setTenDM(rs.getString("TENDM"));

                        if (rs.getTimestamp("THOIDIEMXOA") != null) {
                            dto.setThoiDiemXoa(rs.getTimestamp("THOIDIEMXOA").toLocalDateTime());
                        }

                        int maNX = rs.getInt("MANX");
                        if (!rs.wasNull()) {
                            dto.setMaNX(maNX);
                        }

                        list.add(dto);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - layTatCaDanhMucConHoatDong: " + e.getMessage());
        }

        return list;
    }

    public int themDanhMuc(DanhMucSPDTO dto) {
        String sql = "{CALL PROC_THEM_DANHMUCSP(?, ?)}";
        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, dto.getTenDM());
            cstmt.registerOutParameter(2, Types.NUMERIC);

            cstmt.execute();
            return cstmt.getInt(2);
        } catch (SQLException e) {
            System.err.println("Loi DAO - themDanhMuc: " + e.getMessage());
        }
        return -1;
    }

    public boolean capNhatDanhMuc(DanhMucSPDTO dto) {
        String sql = "{CALL PROC_SUA_DANHMUCSP(?, ?)}";
        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, dto.getMaDM());
            cstmt.setString(2, dto.getTenDM());

            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Loi DAO - capNhatDanhMuc: " + e.getMessage());
        }
        return false;
    }

    public boolean xoaDanhMuc(int maDM, int maNvXoa) {
        String sql = "{CALL PROC_XOA_DANHMUCSP(?, ?)}";
        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, maDM);
            cstmt.setInt(2, maNvXoa);

            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Loi DAO - xoaDanhMuc: " + e.getMessage());
        }
        return false;
    }

    public List<DanhMucSPDTO> timKiemDanhMuc(String keyword) {
        List<DanhMucSPDTO> list = new ArrayList<>();
        String sql = "SELECT MADM, TENDM, THOIDIEMXOA, MANX " +
                "FROM DANHMUCSP " +
                "WHERE THOIDIEMXOA IS NULL " +
                "AND LOWER(TENDM) LIKE ? " +
                "ORDER BY MADM";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DanhMucSPDTO dto = new DanhMucSPDTO();
                    dto.setMaDM(rs.getInt("MADM"));
                    dto.setTenDM(rs.getString("TENDM"));
                    
                    int maNX = rs.getInt("MANX");
                    if (!rs.wasNull()) {
                        dto.setMaNX(maNX);
                    }
                    
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - timKiemDanhMuc: " + e.getMessage());
        }
        return list;
    }
}
