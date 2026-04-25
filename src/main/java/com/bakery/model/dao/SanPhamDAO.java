package com.bakery.model.dao;

import com.bakery.model.dto.SanPhamDTO;
import com.bakery.utils.DBConnect;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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
                    sp.setGiaCoBan(rs.getBigDecimal("GIACOBAN"));
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

    public List<SanPhamDTO> layTatCaSanPham() {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT MASP, MADM, TENSP, GIACOBAN, HINHANH, CHOPHEPTUYCHINH, " +
                "THOIGIANBAOQUAN, SOLUONGTON, PHIENBAN, THOIDIEMXOA, THOIGIANCHUANBI, MANX " +
                "FROM SANPHAM WHERE THOIDIEMXOA IS NULL";

        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                SanPhamDTO sp = new SanPhamDTO();
                sp.setMaSP(rs.getInt("MASP"));
                sp.setMaDM(rs.getInt("MADM"));
                sp.setTenSP(rs.getString("TENSP"));
                sp.setGiaCoBan(rs.getBigDecimal("GIACOBAN"));
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int themSanPham(SanPhamDTO sp) throws Exception {
        String sql = "{CALL PROC_THEM_SANPHAM(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnect.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, sp.getMaDM());
            cstmt.setNString(2, sp.getTenSP());
            cstmt.setBigDecimal(3, sp.getGiaCoBan());
            cstmt.setString(4, sp.getHinhAnh());
            cstmt.setInt(5, sp.getChoPhepTuyChinh());
            cstmt.setInt(6, sp.getThoiGianBaoQuan());
            cstmt.setInt(7, sp.getThoiGianChuanBi());
            cstmt.registerOutParameter(8, Types.NUMERIC);

            cstmt.execute();
            return cstmt.getInt(8);
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - themSanPham: " + e.getMessage());
            throw new Exception("Lỗi khi thêm Sản phẩm: " + e.getMessage());
        }
    }

    public boolean suaSanPham(SanPhamDTO sp) throws Exception {
        String sql = "{CALL PROC_SUA_SANPHAM(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnect.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, sp.getMaSP());
            cstmt.setInt(2, sp.getMaDM());
            cstmt.setNString(3, sp.getTenSP());
            cstmt.setBigDecimal(4, sp.getGiaCoBan());
            cstmt.setString(5, sp.getHinhAnh());
            cstmt.setInt(6, sp.getChoPhepTuyChinh());
            cstmt.setInt(7, sp.getThoiGianBaoQuan());
            cstmt.setInt(8, sp.getThoiGianChuanBi());

            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - suaSanPham: " + e.getMessage());
            throw new Exception("Lỗi khi cập nhật Sản phẩm: " + e.getMessage());
        }
    }

    public boolean xoaSanPham(int maSP, int maNhanVienXoa) throws Exception {
        String sql = "{CALL PROC_XOA_SANPHAM(?, ?)}";
        try (Connection conn = DBConnect.getConnection();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, maSP);
            cstmt.setInt(2, maNhanVienXoa); // Mã NV thực hiện lệnh (phục vụ log)
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi DAO - xoaSanPham: " + e.getMessage());
            throw new Exception("Lỗi khi xóa Sản phẩm: " + e.getMessage());
        }
    }
}
