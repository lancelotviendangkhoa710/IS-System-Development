package com.bakery.model.dao;

import com.bakery.model.dto.NhaCungCapDTO;
import com.bakery.utils.DBConnect;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {

    public List<NhaCungCapDTO> layDanhSachNhaCungCap() {
        List<NhaCungCapDTO> ds = new ArrayList<>();
        String sql = "SELECT MANCC, TENNCC, SDT, DIACHI, THOIDIEMXOA, MANX " +
                     "FROM NHACUNGCAP WHERE THOIDIEMXOA IS NULL ORDER BY MANCC DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                NhaCungCapDTO ncc = new NhaCungCapDTO();
                ncc.setMaNCC(rs.getInt("MANCC"));
                ncc.setTenNCC(rs.getString("TENNCC"));
                ncc.setSdt(rs.getString("SDT"));
                ncc.setDiaChi(rs.getString("DIACHI"));
                
                if (rs.getTimestamp("THOIDIEMXOA") != null) {
                    ncc.setThoiDiemXoa(rs.getTimestamp("THOIDIEMXOA").toLocalDateTime());
                }
                
                int maNX = rs.getInt("MANX");
                if (!rs.wasNull()) {
                    ncc.setMaNX(maNX);
                }
                
                ds.add(ncc);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - layDanhSachNhaCungCap: " + e.getMessage());
        }
        return ds;
    }

    public int themNhaCungCap(NhaCungCapDTO ncc) throws SQLException {
        String sql = "{CALL PROC_THEM_NHACUNGCAP(?, ?, ?, ?)}";

        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, ncc.getTenNCC());
            cstmt.setString(2, ncc.getSdt());
            cstmt.setString(3, ncc.getDiaChi());
            cstmt.registerOutParameter(4, Types.NUMERIC);

            cstmt.execute();
            return cstmt.getInt(4);
        }
    }

    public void suaNhaCungCap(NhaCungCapDTO ncc) throws SQLException {
        String sql = "{CALL PROC_SUA_NHACUNGCAP(?, ?, ?, ?)}";

        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, ncc.getMaNCC());
            cstmt.setString(2, ncc.getTenNCC());
            cstmt.setString(3, ncc.getSdt());
            cstmt.setString(4, ncc.getDiaChi());

            cstmt.execute();
        }
    }

    public void xoaNhaCungCap(int maNCC, int maNVCapNhat) throws SQLException {
        String sql = "{CALL PROC_XOA_NHACUNGCAP(?, ?)}";

        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, maNCC);
            cstmt.setInt(2, maNVCapNhat);

            cstmt.execute();
        }
    }
}
