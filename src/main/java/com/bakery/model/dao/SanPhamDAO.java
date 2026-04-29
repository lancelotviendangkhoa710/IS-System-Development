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

    public List<SanPhamDTO> layTatCaSanPhamDeBan() {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT MASP, MADM, TENSP, GIACOBAN, HINHANH, CHOPHEPTUYCHINH, " +
                "THOIGIANBAOQUAN, NVL(SOLUONGTON, 0) AS SOLUONGTON, PHIENBAN, THOIDIEMXOA, THOIGIANCHUANBI, MANX " +
                "FROM SANPHAM " +
                "WHERE THOIDIEMXOA IS NULL " +
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
            System.err.println("[SanPhamDAO] Lỗi: " + e.getMessage());
        }
        return list;
    }

    public List<SanPhamDTO> layTatCaSanPhamQuanLy() {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT MASP, MADM, TENSP, GIACOBAN, HINHANH, CHOPHEPTUYCHINH, " +
                "THOIGIANBAOQUAN, SOLUONGTON, PHIENBAN, THOIDIEMXOA, THOIGIANCHUANBI, MANX " +
                "FROM SANPHAM " +
                "WHERE THOIDIEMXOA IS NULL " +
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
            System.err.println("Loi DAO - layTatCaSanPhamQuanLy: " + e.getMessage());
        }
        return list;
    }

    public List<SanPhamDTO> layDanhSachSanPhamDeBan(int maDanhMuc) {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT MASP, MADM, TENSP, GIACOBAN, HINHANH, CHOPHEPTUYCHINH, " +
                "THOIGIANBAOQUAN, NVL(SOLUONGTON, 0) AS SOLUONGTON, PHIENBAN, THOIDIEMXOA, THOIGIANCHUANBI, MANX " +
                "FROM SANPHAM WHERE MADM = ? AND THOIDIEMXOA IS NULL";

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
            System.err.println("[SanPhamDAO] Lỗi: " + e.getMessage());
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

    /**
     * Lấy số lượng tồn kho realtime từ DB.
     * Dùng cho kiểm tra Fail-Fast trước khi mở dialog thanh toán.
     */
    public double laySoLuongTon(int maSP) {
        String sql = "SELECT NVL(SOLUONGTON, 0) FROM SANPHAM WHERE MASP = ?";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maSP);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[SanPhamDAO] Lỗi: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Đếm số sản phẩm chưa bị xóa mềm thuộc một danh mục.
     * Dùng để chặn xóa danh mục đang có sản phẩm.
     *
     * @param maDM mã danh mục cần kiểm tra
     * @return số sản phẩm còn tồn tại (>= 0)
     */
    public int demSanPhamTheoDanhMuc(int maDM) {
        String sql = "SELECT COUNT(*) FROM SANPHAM WHERE MADM = ? AND THOIDIEMXOA IS NULL";
        try (Connection conn = DBConnect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maDM);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi DAO - demSanPhamTheoDanhMuc: " + e.getMessage());
        }
        return 0;
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
            System.err.println("[SanPhamDAO] Lỗi: " + e.getMessage());
            return 0;
        }
    }

    public int themSanPham(SanPhamDTO sp) {
        String sql = "{CALL PROC_THEM_SANPHAM(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, sp.getMaDM());
            cstmt.setString(2, sp.getTenSP());
            cstmt.setDouble(3, sp.getGiaCoBan());
            cstmt.setString(4, sp.getHinhAnh());
            cstmt.setInt(5, sp.getChoPhepTuyChinh());
            cstmt.setInt(6, sp.getThoiGianBaoQuan());
            cstmt.setInt(7, sp.getThoiGianChuanBi());
            cstmt.registerOutParameter(8, Types.NUMERIC);

            cstmt.execute();
            return cstmt.getInt(8);
        } catch (SQLException e) {
            System.err.println("Loi DAO - themSanPham: " + e.getMessage());
        }
        return -1;
    }

    public boolean capNhatSanPham(SanPhamDTO sp) {
        String sql = "{CALL PROC_SUA_SANPHAM(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, sp.getMaSP());
            cstmt.setInt(2, sp.getMaDM());
            cstmt.setString(3, sp.getTenSP());
            cstmt.setDouble(4, sp.getGiaCoBan());
            cstmt.setString(5, sp.getHinhAnh());
            cstmt.setInt(6, sp.getChoPhepTuyChinh());
            cstmt.setInt(7, sp.getThoiGianBaoQuan());
            cstmt.setInt(8, sp.getThoiGianChuanBi());

            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Loi DAO - capNhatSanPham: " + e.getMessage());
        }
        return false;
    }

    public boolean xoaSanPham(int maSP, int maNX) {
        String sql = "{CALL PROC_XOA_SANPHAM(?, ?)}";
        try (Connection conn = DBConnect.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, maSP);
            cstmt.setInt(2, maNX);

            cstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Loi DAO - xoaSanPham: " + e.getMessage());
        }
        return false;
    }
}
