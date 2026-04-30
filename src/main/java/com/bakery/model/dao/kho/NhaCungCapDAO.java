package com.bakery.model.dao.kho;
import com.bakery.model.dao.BaseDAO;

import com.bakery.model.dto.kho.NhaCungCapDTO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO extends BaseDAO {

    public List<NhaCungCapDTO> layDanhSachNhaCungCap() throws Exception {
        List<NhaCungCapDTO> ds = new ArrayList<>();
        String sql = "SELECT MANCC, TENNCC, SDT, DIACHI, THOIDIEMXOA, MANX " +
                     "FROM NHACUNGCAP WHERE THOIDIEMXOA IS NULL ORDER BY MANCC DESC";

        try (Connection conn = moKetNoi();
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
            handleException("layDanhSachNhaCungCap", e);
        }
        return ds;
    }

    public int themNhaCungCap(NhaCungCapDTO ncc) throws Exception {
        String sql = "{CALL PROC_THEM_NHACUNGCAP(?, ?, ?, ?)}";

        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, ncc.getTenNCC());
            cstmt.setString(2, ncc.getSdt());
            cstmt.setString(3, ncc.getDiaChi());
            cstmt.registerOutParameter(4, Types.NUMERIC);

            cstmt.execute();
            return cstmt.getInt(4);
        } catch (SQLException e) {
            handleException("themNhaCungCap", e);
            throw e;
        }
    }

    public void suaNhaCungCap(NhaCungCapDTO ncc) throws Exception {
        String sql = "{CALL PROC_SUA_NHACUNGCAP(?, ?, ?, ?)}";

        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, ncc.getMaNCC());
            cstmt.setString(2, ncc.getTenNCC());
            cstmt.setString(3, ncc.getSdt());
            cstmt.setString(4, ncc.getDiaChi());

            cstmt.execute();
        } catch (SQLException e) {
            handleException("suaNhaCungCap", e);
            throw e;
        }
    }

    public void xoaNhaCungCap(int maNCC, int maNVCapNhat) throws Exception {
        String sql = "{CALL PROC_XOA_NHACUNGCAP(?, ?)}";

        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, maNCC);
            cstmt.setInt(2, maNVCapNhat);

            cstmt.execute();
        } catch (SQLException e) {
            handleException("xoaNhaCungCap", e);
            throw e;
        }
    }
}
