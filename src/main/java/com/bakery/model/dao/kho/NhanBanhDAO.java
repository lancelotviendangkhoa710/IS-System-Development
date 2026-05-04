package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.kho.NhanBanhDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhanBanhDAO extends BaseDAO {

    public List<NhanBanhDTO> layDanhSachPhuPhi() throws Exception {
        List<NhanBanhDTO> list = new ArrayList<>();
        String sql = "SELECT MANHAN, TENNHAN, PHUPHI, THOIDIEMXOA, MANX FROM NHANBANH WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                NhanBanhDTO item = new NhanBanhDTO();
                item.setMaNhan(rs.getInt("MANHAN"));
                item.setTenNhan(rs.getString("TENNHAN"));
                item.setPhuPhi(rs.getBigDecimal("PHUPHI"));

                int maNX = rs.getInt("MANX");
                if (!rs.wasNull()) {
                    item.setMaNX(maNX);
                }

                list.add(item);
            }
        } catch (SQLException e) {
            handleException("layDanhSachPhuPhi", e);
        }
        return list;
    }

    public boolean them(NhanBanhDTO item) throws Exception {
        String sql = "INSERT INTO NHANBANH (TENNHAN, PHUPHI) VALUES (?, ?)";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getTenNhan());
            pstmt.setBigDecimal(2, item.getPhuPhi());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("them", e);
            return false;
        }
    }

    public boolean sua(NhanBanhDTO item) throws Exception {
        String sql = "UPDATE NHANBANH SET TENNHAN = ?, PHUPHI = ? WHERE MANHAN = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getTenNhan());
            pstmt.setBigDecimal(2, item.getPhuPhi());
            pstmt.setInt(3, item.getMaNhan());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("sua", e);
            return false;
        }
    }

    public boolean xoa(int maNhan, int maNV) throws Exception {
        String sql = "UPDATE NHANBANH SET THOIDIEMXOA = CURRENT_TIMESTAMP, MANX = ? WHERE MANHAN = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maNV);
            pstmt.setInt(2, maNhan);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("xoa", e);
            return false;
        }
    }
}
