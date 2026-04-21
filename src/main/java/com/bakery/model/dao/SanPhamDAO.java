package com.bakery.model.dao;

import com.bakery.model.dto.SanPhamDTO;
import com.bakery.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    public List<SanPhamDTO> layTatCaSanPhamDeBan() {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT MASP, MADM, TENSP, GIACOBAN, HINHANH, CHOPHEPTUYCHINH, " +
                "THOIGIANBAOQUAN, SOLUONGTON, PHIENBAN, THOIDIEMXOA, THOIGIANCHUANBI, MANX " +
                "FROM SANPHAM " +
                "WHERE THOIDIEMXOA IS NULL AND SOLUONGTON > 0 " +
                "ORDER BY MASP";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SanPhamDTO sp = mapSanPham(rs);
                    list.add(sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

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
                    SanPhamDTO sp = mapSanPham(rs);
                    list.add(sp);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private SanPhamDTO mapSanPham(ResultSet rs) throws SQLException {
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

        return sp;
    }

    public double tinhGiaBanhTuyChinh(int maSP, Integer maKC, Integer maCot, Integer maNhan, Integer maTrangTri) {
        String sql = "{ ? = call FUNC_TINH_GIA_TUY_CHINH(?, ?, ?, ?, ?) }";
        try (Connection conn = DBConnect.getConnection();
                java.sql.CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.registerOutParameter(1, java.sql.Types.NUMERIC);
            cstmt.setInt(2, maSP);

            if (maKC != null)
                cstmt.setInt(3, maKC);
            else
                cstmt.setNull(3, java.sql.Types.NUMERIC);
            if (maCot != null)
                cstmt.setInt(4, maCot);
            else
                cstmt.setNull(4, java.sql.Types.NUMERIC);
            if (maNhan != null)
                cstmt.setInt(5, maNhan);
            else
                cstmt.setNull(5, java.sql.Types.NUMERIC);
            if (maTrangTri != null)
                cstmt.setInt(6, maTrangTri);
            else
                cstmt.setNull(6, java.sql.Types.NUMERIC);

            cstmt.execute();
            return cstmt.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
