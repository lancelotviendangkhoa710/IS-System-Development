package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.kho.NguyenLieuDTO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO truy xuất bảng NGUYENLIEU.
 * Tương tác với DB qua PreparedStatement (SELECT) và Stored Procedure (CUD).
 */
public class NguyenLieuDAO extends BaseDAO {

    /** Lấy tất cả nguyên liệu còn hoạt động, kèm tên đơn vị tính từ DONVITINH. */
    public List<NguyenLieuDTO> layTatCaNguyenLieu() throws Exception {
        List<NguyenLieuDTO> list = new ArrayList<>();
        String sql = "SELECT NL.MANL, NL.TENNL, NL.XUATXU, NL.MADVT, DVT.TENDVT, " +
                "NL.GIAVONTRUNGBINH, NL.MUCTONANTOAN, NL.SOLUONGTONTONG, " +
                "NL.DATCHUANVSATTP, NL.PHIENBAN, NL.THOIDIEMXOA, NL.MANX, NL.HESOQUYDOI " +
                "FROM NGUYENLIEU NL " +
                "LEFT JOIN DONVITINH DVT ON DVT.MADVT = NL.MADVT " +
                "WHERE NL.THOIDIEMXOA IS NULL " +
                "ORDER BY NL.MANL";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapNguyenLieu(rs));
            }
        } catch (SQLException e) {
            handleException("layTatCaNguyenLieu", e);
        }
        return list;
    }

    /** Tìm kiếm nguyên liệu theo tên (LIKE, không phân biệt hoa thường). */
    public List<NguyenLieuDTO> timKiemNguyenLieu(String keyword) throws Exception {
        List<NguyenLieuDTO> list = new ArrayList<>();
        String sql = "SELECT NL.MANL, NL.TENNL, NL.XUATXU, NL.MADVT, DVT.TENDVT, " +
                "NL.GIAVONTRUNGBINH, NL.MUCTONANTOAN, NL.SOLUONGTONTONG, " +
                "NL.DATCHUANVSATTP, NL.PHIENBAN, NL.THOIDIEMXOA, NL.MANX, NL.HESOQUYDOI " +
                "FROM NGUYENLIEU NL " +
                "LEFT JOIN DONVITINH DVT ON DVT.MADVT = NL.MADVT " +
                "WHERE NL.THOIDIEMXOA IS NULL " +
                "AND LOWER(NL.TENNL) LIKE ? " +
                "ORDER BY NL.MANL";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapNguyenLieu(rs));
                }
            }
        } catch (SQLException e) {
            handleException("timKiemNguyenLieu", e);
        }
        return list;
    }

    /**
     * Thêm mới nguyên liệu qua PROC_THEM_NGUYENLIEU.
     * @return mã nguyên liệu vừa tạo (>= 1), hoặc -1 nếu lỗi
     */
    public int themNguyenLieu(NguyenLieuDTO dto, int maNv) throws Exception {
        String sql = "{CALL PROC_THEM_NGUYENLIEU(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, dto.getTenNL());
            cstmt.setString(2, dto.getXuatXu());
            cstmt.setDouble(3, dto.getMucTonAnToan());
            cstmt.setInt(4, dto.getMaDVT());
            cstmt.registerOutParameter(5, Types.NUMERIC);
            cstmt.setDouble(6, dto.getHesoQuydoi()); // Hệ số quy đổi
            cstmt.execute();
            return cstmt.getInt(5);
        } catch (SQLException e) {
            handleException("themNguyenLieu", e);
        }
        return -1;
    }

    /** Cập nhật nguyên liệu qua PROC_SUA_NGUYENLIEU. */
    public boolean capNhatNguyenLieu(NguyenLieuDTO dto) throws Exception {
        String sql = "{CALL PROC_SUA_NGUYENLIEU(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, dto.getMaNL());
            cstmt.setString(2, dto.getTenNL());
            cstmt.setString(3, dto.getXuatXu());
            cstmt.setInt(4, dto.getMaDVT());
            cstmt.setDouble(5, dto.getMucTonAnToan());
            cstmt.setDouble(6, dto.getHesoQuydoi()); // Hệ số quy đổi
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            handleException("capNhatNguyenLieu", e);
        }
        return false;
    }

    /**
     * Xóa nguyên liệu qua PROC_XOA_NGUYENLIEU.
     * Procedure tự phân loại: xóa mềm nếu đã có lịch sử, xóa cứng nếu chưa.
     */
    public boolean xoaNguyenLieu(int maNL, int maNv) throws Exception {
        String sql = "{CALL PROC_XOA_NGUYENLIEU(?, ?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, maNL);
            cstmt.setInt(2, maNv);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            handleException("xoaNguyenLieu", e);
        }
        return false;
    }

    /**
     * Thêm nguyên liệu + phiếu nhập đầu tiên trong 1 transaction (có quy đổi đơn vị).
     * soLuong = số lượng theo đơn vị nhập, Procedure tự nhân hesoQuydoi để lưu đơn vị cơ bản.
     * @return int[]{maNL, maPN}
     */
    public int[] themNguyenLieuVaNhapKho(NguyenLieuDTO dto, int maNCC, int maNV,
                                          double soLuong, double donGia,
                                          java.sql.Date ngaySanXuat, java.sql.Date hanSuDung) throws Exception {
        String sql = "{CALL PROC_THEM_NGUYENLIEU_VA_NHAP_KHO(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, dto.getTenNL());
            cs.setString(2, dto.getXuatXu());
            cs.setDouble(3, dto.getMucTonAnToan());
            cs.setInt(4, dto.getMaDVT());
            cs.setInt(5, maNCC);
            cs.setInt(6, maNV);
            cs.setDouble(7, soLuong);
            cs.setDouble(8, donGia);
            if (ngaySanXuat != null) cs.setDate(9, ngaySanXuat); else cs.setNull(9, Types.DATE);
            if (hanSuDung != null) cs.setDate(10, hanSuDung); else cs.setNull(10, Types.DATE);
            cs.registerOutParameter(11, Types.NUMERIC);
            cs.registerOutParameter(12, Types.NUMERIC);
            cs.setDouble(13, dto.getHesoQuydoi()); // Hệ số quy đổi
            cs.execute();
            return new int[]{cs.getInt(11), cs.getInt(12)};
        } catch (SQLException e) {
            handleException("themNguyenLieuVaNhapKho", e);
        }
        return new int[]{-1, -1};
    }

    private NguyenLieuDTO mapNguyenLieu(ResultSet rs) throws SQLException {
        NguyenLieuDTO dto = new NguyenLieuDTO();
        dto.setMaNL(rs.getInt("MANL"));
        dto.setTenNL(rs.getString("TENNL"));
        dto.setXuatXu(rs.getString("XUATXU"));
        dto.setMaDVT(rs.getInt("MADVT"));
        // TENDVT có khi query JOIN DONVITINH
        try { dto.setTenDVT(rs.getString("TENDVT")); } catch (SQLException ignored) {}
        dto.setGiaVonTrungBinh(rs.getBigDecimal("GIAVONTRUNGBINH"));
        dto.setMucTonAnToan(rs.getDouble("MUCTONANTOAN"));
        dto.setSoLuongTonTong(rs.getDouble("SOLUONGTONTONG"));
        dto.setDatChuanVSATTP(rs.getInt("DATCHUANVSATTP"));
        dto.setPhienBan(rs.getInt("PHIENBAN"));
        if (rs.getTimestamp("THOIDIEMXOA") != null) {
            dto.setThoiDiemXoa(rs.getTimestamp("THOIDIEMXOA").toLocalDateTime());
        }
        int maNX = rs.getInt("MANX");
        if (!rs.wasNull()) dto.setMaNX(maNX);
        // Hệ số quy đổi — mặc định 1.0 nếu cột chưa có (backward-compat)
        try {
            double heso = rs.getDouble("HESOQUYDOI");
            dto.setHesoQuydoi(rs.wasNull() ? 1.0 : heso);
        } catch (SQLException ignored) { dto.setHesoQuydoi(1.0); }
        return dto;
    }
}
