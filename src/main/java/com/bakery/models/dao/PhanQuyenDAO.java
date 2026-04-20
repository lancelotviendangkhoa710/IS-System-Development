package com.bakery.models.dao;

import com.bakery.models.dto.ChucNangDTO;
import com.bakery.utils.DBConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhanQuyenDAO {

    public List<ChucNangDTO> layDanhSachChucNangTheoVaiTro(int maVaiTro) {
        List<ChucNangDTO> danhSach = new ArrayList<>();
        String sql = "SELECT C.MACHUCNANG, C.TENCHUCNANG, C.MOTA " +
                "FROM CHUCNANG C " +
                "JOIN VAITRO_CHUCNANG VC ON C.MACHUCNANG = VC.MACHUCNANG " +
                "WHERE VC.MAVAITRO = ?";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maVaiTro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ChucNangDTO cn = new ChucNangDTO();
                    cn.setMaChucNang(rs.getInt("MACHUCNANG"));
                    cn.setTenChucNang(rs.getString("TENCHUCNANG"));
                    cn.setMoTa(rs.getString("MOTA"));
                    danhSach.add(cn);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - layDanhSachChucNangTheoVaiTro: " + e.getMessage());
        }
        return danhSach;
    }
}