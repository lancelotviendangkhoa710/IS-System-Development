package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.kho.MeSanXuatDTO;
import com.bakery.model.dto.kho.TraCuuNguonGocDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO tra cứu nguồn gốc nguyên liệu.
 * Chỉ đọc — mọi ghi dữ liệu vào MESANXUAT thực hiện qua PROC_XUATKHOSANXUAT.
 */
public class TraCuuNguonGocDAO extends BaseDAO {

    /**
     * Lấy danh sách mẻ sản xuất, hỗ trợ lọc theo tên sản phẩm và khoảng ngày.
     *
     * @param tuKhoa   tên SP tìm kiếm (null/rỗng = không lọc)
     * @param tuNgay   từ ngày (null = không lọc)
     * @param denNgay  đến ngày (null = không lọc)
     */
    public List<MeSanXuatDTO> layDanhSachMe(String tuKhoa, LocalDate tuNgay, LocalDate denNgay) throws Exception {
        List<MeSanXuatDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT ME.MAME, ME.MASP, SP.TENSP, ME.SOLUONGSANXUAT, ME.NGAYSANXUAT, " +
            "ME.MANV, NV.HOTEN AS TENNV, ME.MAPX " +
            "FROM MESANXUAT ME " +
            "JOIN SANPHAM SP ON SP.MASP = ME.MASP " +
            "JOIN NHANVIEN NV ON NV.MANV = ME.MANV " +
            "WHERE 1=1 "
        );

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append("AND UPPER(SP.TENSP) LIKE UPPER(?) ");
        }
        if (tuNgay != null) {
            sql.append("AND TRUNC(ME.NGAYSANXUAT) >= ? ");
        }
        if (denNgay != null) {
            sql.append("AND TRUNC(ME.NGAYSANXUAT) <= ? ");
        }
        sql.append("ORDER BY ME.MAME DESC FETCH FIRST 200 ROWS ONLY");

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (tuKhoa != null && !tuKhoa.isBlank()) {
                ps.setString(idx++, "%" + tuKhoa + "%");
            }
            if (tuNgay != null) {
                ps.setDate(idx++, Date.valueOf(tuNgay));
            }
            if (denNgay != null) {
                ps.setDate(idx++, Date.valueOf(denNgay));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MeSanXuatDTO dto = new MeSanXuatDTO();
                    dto.setMaMe(rs.getInt("MAME"));
                    dto.setMaSP(rs.getInt("MASP"));
                    dto.setTenSP(rs.getString("TENSP"));
                    dto.setSoLuongSanXuat(rs.getDouble("SOLUONGSANXUAT"));
                    dto.setNgaySanXuat(rs.getTimestamp("NGAYSANXUAT") != null
                            ? rs.getTimestamp("NGAYSANXUAT").toLocalDateTime() : null);
                    dto.setMaNV(rs.getInt("MANV"));
                    dto.setTenNhanVien(rs.getString("TENNV"));
                    dto.setMaPX(rs.getInt("MAPX"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            handleException("layDanhSachMe", e);
        }
        return list;
    }

    /**
     * Lấy chi tiết nguồn gốc nguyên liệu của 1 mẻ sản xuất cụ thể.
     *
     * @param maMe mã mẻ sản xuất cần truy vết
     */
    public List<TraCuuNguonGocDTO> layChiTietNguonGoc(int maMe) throws Exception {
        List<TraCuuNguonGocDTO> list = new ArrayList<>();
        String sql =
            "SELECT MAME, NGAY_SAN_XUAT, SOLUONGSANXUAT, MASP, TENSP, " +
            "MANL, TEN_NGUYEN_LIEU, SOLUONG_DA_DUNG, " +
            "MALO, MAVACH_LO, NSX_NGUYEN_LIEU, HANSUDUNG, GIA_NHAP, " +
            "MAPN, NGAYNHAP, MANCC, TENNCC, SDT_NCC, DIACHI_NCC " +
            "FROM VW_TRACUUNGUONGOC " +
            "WHERE MAME = ? " +
            "ORDER BY MANL, MALO";

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maMe);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TraCuuNguonGocDTO dto = new TraCuuNguonGocDTO();
                    dto.setMaMe(rs.getInt("MAME"));
                    dto.setNgaySanXuat(rs.getTimestamp("NGAY_SAN_XUAT") != null
                            ? rs.getTimestamp("NGAY_SAN_XUAT").toLocalDateTime() : null);
                    dto.setSoLuongSanXuat(rs.getDouble("SOLUONGSANXUAT"));
                    dto.setMaSP(rs.getInt("MASP"));
                    dto.setTenSP(rs.getString("TENSP"));
                    dto.setMaNL(rs.getInt("MANL"));
                    dto.setTenNguyenLieu(rs.getString("TEN_NGUYEN_LIEU"));
                    dto.setSoLuongDaDung(rs.getDouble("SOLUONG_DA_DUNG"));
                    dto.setMaLo(rs.getInt("MALO"));
                    dto.setMaVachLo(rs.getString("MAVACH_LO"));
                    dto.setNsxNguyenLieu(rs.getDate("NSX_NGUYEN_LIEU") != null
                            ? rs.getDate("NSX_NGUYEN_LIEU").toLocalDate() : null);
                    dto.setHanSuDung(rs.getDate("HANSUDUNG") != null
                            ? rs.getDate("HANSUDUNG").toLocalDate() : null);
                    dto.setGiaNhap(rs.getDouble("GIA_NHAP"));
                    dto.setMaPn(rs.getInt("MAPN"));
                    dto.setNgayNhap(rs.getTimestamp("NGAYNHAP") != null
                            ? rs.getTimestamp("NGAYNHAP").toLocalDateTime() : null);
                    dto.setMaNCC(rs.getInt("MANCC"));
                    dto.setTenNCC(rs.getString("TENNCC"));
                    dto.setSdtNCC(rs.getString("SDT_NCC"));
                    dto.setDiaChiNCC(rs.getString("DIACHI_NCC"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            handleException("layChiTietNguonGoc", e);
        }
        return list;
    }
}
