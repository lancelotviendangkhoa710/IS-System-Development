package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;

import com.bakery.model.dto.kho.SanPhamDTO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO extends BaseDAO {

    public List<SanPhamDTO> layTatCaSanPhamDeBan() throws Exception {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT MASP, MADM, TENSP, NVL(GIAVON,0) AS GIAVON, NVL(GIABAN,0) AS GIABAN, " +
                "HINHANH, CHOPHEPTUYCHINH, " +
                "THOIGIANBAOQUAN, NVL(SOLUONGTON, 0) AS SOLUONGTON, PHIENBAN, THOIDIEMXOA, THOIGIANCHUANBI, MANX " +
                "FROM SANPHAM " +
                "WHERE THOIDIEMXOA IS NULL " +
                "ORDER BY MASP";

        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSanPham(rs));
                }
            }
        } catch (SQLException e) {
            handleException("layTatCaSanPhamDeBan", e);
        }
        return list;
    }

    public List<SanPhamDTO> layTatCaSanPhamQuanLy() throws Exception {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT MASP, MADM, TENSP, NVL(GIAVON,0) AS GIAVON, NVL(GIABAN,0) AS GIABAN, " +
                "HINHANH, CHOPHEPTUYCHINH, " +
                "THOIGIANBAOQUAN, NVL(SOLUONGTON, 0) AS SOLUONGTON, PHIENBAN, THOIDIEMXOA, THOIGIANCHUANBI, MANX " +
                "FROM SANPHAM " +
                "WHERE THOIDIEMXOA IS NULL " +
                "ORDER BY MASP";

        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSanPham(rs));
                }
            }
        } catch (SQLException e) {
            handleException("layTatCaSanPhamQuanLy", e);
        }
        return list;
    }

    public List<SanPhamDTO> layDanhSachSanPhamDeBan(int maDanhMuc) throws Exception {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT MASP, MADM, TENSP, NVL(GIAVON,0) AS GIAVON, NVL(GIABAN,0) AS GIABAN, " +
                "HINHANH, CHOPHEPTUYCHINH, " +
                "THOIGIANBAOQUAN, NVL(SOLUONGTON, 0) AS SOLUONGTON, PHIENBAN, THOIDIEMXOA, THOIGIANCHUANBI, MANX " +
                "FROM SANPHAM WHERE MADM = ? AND THOIDIEMXOA IS NULL";

        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maDanhMuc);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSanPham(rs));
                }
            }

        } catch (SQLException e) {
            handleException("layDanhSachSanPhamDeBan", e);
        }
        return list;
    }

    private SanPhamDTO mapSanPham(ResultSet rs) throws SQLException {
        SanPhamDTO sp = new SanPhamDTO();
        sp.setMaSP(rs.getInt("MASP"));
        sp.setMaDM(rs.getInt("MADM"));
        sp.setTenSP(rs.getString("TENSP"));
        sp.setGiaVon(rs.getDouble("GIAVON"));
        sp.setGiaBan(rs.getDouble("GIABAN"));
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
    public double laySoLuongTon(int maSP) throws Exception {
        String sql = "SELECT NVL(SOLUONGTON, 0) FROM SANPHAM WHERE MASP = ?";
        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maSP);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            handleException("laySoLuongTon", e);
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
    public int demSanPhamTheoDanhMuc(int maDM) throws Exception {
        String sql = "SELECT COUNT(*) FROM SANPHAM WHERE MADM = ? AND THOIDIEMXOA IS NULL";
        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maDM);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            handleException("demSanPhamTheoDanhMuc", e);
        }
        return 0;
    }

    public double tinhGiaBanhTuyChinh(int maSP, Integer maKC, Integer maCot, Integer maNhan, Integer maTrangTri)
            throws Exception {
        String sql = "{ ? = call FUNC_GIABANHTUYCHINH(?, ?, ?, ?, ?) }";
        try (Connection conn = moKetNoi();
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
            handleException("tinhGiaBanhTuyChinh", e);
            return 0;
        }
    }

    public int themSanPham(SanPhamDTO sp) throws Exception {
        // 9 tham số: 6 thuộc tính + GIAVON + GIABAN + OUT MASP
        String sql = "{CALL PROC_THEM_SANPHAM(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = moKetNoi();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, sp.getMaDM());
            cstmt.setString(2, sp.getTenSP());
            cstmt.setString(3, sp.getHinhAnh());
            cstmt.setInt(4, sp.getChoPhepTuyChinh());
            cstmt.setInt(5, sp.getThoiGianBaoQuan());
            cstmt.setInt(6, sp.getThoiGianChuanBi());
            cstmt.setDouble(7, sp.getGiaVon());
            cstmt.setDouble(8, sp.getGiaBan());
            cstmt.registerOutParameter(9, Types.NUMERIC);

            cstmt.execute();
            return cstmt.getInt(9);
        } catch (SQLException e) {
            handleException("themSanPham", e);
        }
        return -1;
    }

    public boolean capNhatSanPham(SanPhamDTO sp) throws Exception {
        // 9 tham số: MASP + 6 thuộc tính + GIAVON + GIABAN
        String sql = "{CALL PROC_SUA_SANPHAM(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = moKetNoi();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, sp.getMaSP());
            cstmt.setInt(2, sp.getMaDM());
            cstmt.setString(3, sp.getTenSP());
            cstmt.setString(4, sp.getHinhAnh());
            cstmt.setInt(5, sp.getChoPhepTuyChinh());
            cstmt.setInt(6, sp.getThoiGianBaoQuan());
            cstmt.setInt(7, sp.getThoiGianChuanBi());
            cstmt.setDouble(8, sp.getGiaVon());
            cstmt.setDouble(9, sp.getGiaBan());

            cstmt.execute();
            return true;
        } catch (SQLException e) {
            handleException("capNhatSanPham", e);
        }
        return false;
    }

    public boolean xoaSanPham(int maSP, int maNX) throws Exception {
        String sql = "{CALL PROC_XOA_SANPHAM(?, ?)}";
        try (Connection conn = moKetNoi();
                CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, maSP);
            cstmt.setInt(2, maNX);

            cstmt.execute();
            return true;
        } catch (SQLException e) {
            handleException("xoaSanPham", e);
        }
        return false;
    }
}
