package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.kho.KieuTrangTriDTO;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KieuTrangTriDAO extends BaseDAO {

    public List<KieuTrangTriDTO> layDanhSachPhuPhi() throws Exception {
        List<KieuTrangTriDTO> list = new ArrayList<>();
        String sql = "SELECT MATRANGTRI, TENTRANGTRI, PHUPHI, THOIDIEMXOA, MANX FROM KIEUTRANGTRI WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                KieuTrangTriDTO item = new KieuTrangTriDTO();
                item.setMaTrangTri(rs.getInt("MATRANGTRI"));
                item.setTenTrangTri(rs.getString("TENTRANGTRI"));
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

    public boolean them(KieuTrangTriDTO item) throws Exception {
        String sql = "INSERT INTO KIEUTRANGTRI (TENTRANGTRI, PHUPHI) VALUES (?, ?)";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getTenTrangTri());
            pstmt.setBigDecimal(2, item.getPhuPhi());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("them", e);
            return false;
        }
    }

    public boolean sua(KieuTrangTriDTO item) throws Exception {
        String sql = "UPDATE KIEUTRANGTRI SET TENTRANGTRI = ?, PHUPHI = ? WHERE MATRANGTRI = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getTenTrangTri());
            pstmt.setBigDecimal(2, item.getPhuPhi());
            pstmt.setInt(3, item.getMaTrangTri());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("sua", e);
            return false;
        }
    }

    public boolean xoa(int maTrangTri, int maNV) throws Exception {
        String sql = "UPDATE KIEUTRANGTRI SET THOIDIEMXOA = CURRENT_TIMESTAMP, MANX = ? WHERE MATRANGTRI = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maNV);
            pstmt.setInt(2, maTrangTri);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("xoa", e);
            return false;
        }
    }
}
