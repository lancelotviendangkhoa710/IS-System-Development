package com.bakery.dao;

import com.bakery.dto.SanPhamDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    public List<SanPhamDTO> layDanhSachSanPhamDeBan(int maDanhMuc) {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT MASP, MADM, TENSP, GIACOBAN, HINHANH, CHOPHEPTUYCHINH, " +
                     "THOIGIANBAOQUAN, SOLUONGTON, PHIENBAN, THOIDIEMXOA, THOIGIANCHUANBI, MANX " +
                     "FROM SANPHAM WHERE MADM = ? AND THOIDIEMXOA IS NULL AND SOLUONGTON > 0";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maDanhMuc);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SanPhamDTO sp = new SanPhamDTO();
                    sp.setMaSP(rs.getInt("MASP"));
                    sp.setMaDM(rs.getInt("MADM"));
                    sp.setTenSP(rs.getString("TENSP"));
                    sp.setGiaCoBan(rs.getDouble("GIACOBAN"));
                    sp.setHinhAnh(rs.getString("HINHANH"));
                    sp.setChoPhepTuyChinh(rs.getInt("CHOPHEPTUYCHINH"));
                    sp.setThoiGianBaoQuan(rs.getInt("THOIGIANBAOQUAN"));
                    sp.setSoLuongTon(rs.getDouble("SOLUONGTON"));
                    sp.setPhienBan(rs.getInt("PHIENBAN"));
                    sp.setThoiGianChuanBi(rs.getInt("THOIGIANCHUANBI"));

                    if (rs.getTimestamp("THOIDIEMXOA") != null) {
                        sp.setThoiDiemXoa(rs.getTimestamp("THOIDIEMXOA").toLocalDateTime());
                    }

                    int maNX = rs.getInt("MANX");
                    if (!rs.wasNull()) {
                        sp.setMaNX(maNX);
                    }

                    list.add(sp);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
