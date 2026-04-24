package com.bakery.models.dao;

import com.bakery.models.dto.HangThanhVienDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HangThanhVienDAO {

    // Lay tat ca hang thanh vien.
    public List<HangThanhVienDTO> getAllTiers() {
        List<HangThanhVienDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM HANGTHANHVIEN";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                HangThanhVienDTO hang = new HangThanhVienDTO();
                hang.setMaHang(rs.getInt("MAHANG"));
                hang.setTenHang(rs.getString("TENHANG"));
                hang.setDiemToiThieu(rs.getInt("DIEMTOITHIEU"));
                hang.setPhanTramGiamGia(rs.getDouble("PHANTRAMGIAMGIA"));
                list.add(hang);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lay thong tin hang thanh vien theo ma.
    public HangThanhVienDTO getTierById(int tierId) {
        String sql = "SELECT * FROM HANGTHANHVIEN WHERE MAHANG = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tierId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HangThanhVienDTO hang = new HangThanhVienDTO();
                    hang.setMaHang(rs.getInt("MAHANG"));
                    hang.setTenHang(rs.getString("TENHANG"));
                    hang.setDiemToiThieu(rs.getInt("DIEMTOITHIEU"));
                    hang.setPhanTramGiamGia(rs.getDouble("PHANTRAMGIAMGIA"));
                    return hang;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== Compatibility wrappers (CRM migration) =====
    @Deprecated
    public List<HangThanhVienDTO> getAll() {
        return getAllTiers();
    }

    @Deprecated
    public HangThanhVienDTO getById(int maHang) {
        return getTierById(maHang);
    }
}
