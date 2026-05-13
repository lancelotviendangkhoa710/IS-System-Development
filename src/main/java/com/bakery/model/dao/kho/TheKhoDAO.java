package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.kho.TheKhoBienDongDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO tra cứu thẻ kho nguyên liệu (UC44).
 * Chỉ đọc — UNION ALL giao dịch nhập (CTPHIEUNHAP) + xuất (CTPHIEUXUAT_NL)
 * theo MANL và khoảng thời gian.
 */
public class TheKhoDAO extends BaseDAO {

    /**
     * Lấy danh sách biến động nhập/xuất của 1 nguyên liệu theo khoảng ngày.
     * Kết quả sắp xếp theo thời gian tăng dần.
     *
     * @param maNL    mã nguyên liệu cần xem thẻ kho
     * @param tuNgay  từ ngày (null = không giới hạn)
     * @param denNgay đến ngày (null = không giới hạn)
     */
    public List<TheKhoBienDongDTO> layBienDong(int maNL, LocalDate tuNgay, LocalDate denNgay)
            throws Exception {
        List<TheKhoBienDongDTO> list = new ArrayList<>();

        // UNION ALL: nhập kho + xuất kho, lọc theo MANL và date range
        StringBuilder sql = new StringBuilder(
            "SELECT NGAY, LOAI, MALO, SOLUONG, SOLUONGCONLAI FROM (" +
            "  SELECT PN.NGAYNHAP       AS NGAY," +
            "         N'Nhập kho'       AS LOAI," +
            "         CTN.MALO          AS MALO," +
            "         CTN.SOLUONG       AS SOLUONG," +
            "         CTN.SOLUONGCONLAI AS SOLUONGCONLAI" +
            "  FROM CTPHIEUNHAP CTN" +
            "  JOIN PHIEUNHAPKHO PN ON PN.MAPN = CTN.MAPN" +
            "  WHERE CTN.MANL = ?" +
            (tuNgay  != null ? " AND TRUNC(PN.NGAYNHAP) >= ?" : "") +
            (denNgay != null ? " AND TRUNC(PN.NGAYNHAP) <= ?" : "") +
            "  UNION ALL" +
            "  SELECT PX.NGAYXUAT   AS NGAY," +
            "         PX.LYDOXUAT   AS LOAI," +
            "         CX.MALO       AS MALO," +
            "         -CX.SOLUONG   AS SOLUONG," +
            "         0             AS SOLUONGCONLAI" +
            "  FROM CTPHIEUXUAT_NL CX" +
            "  JOIN CTPHIEUNHAP CTN ON CTN.MALO = CX.MALO" +
            "  JOIN PHIEUXUATKHO PX ON PX.MAPX  = CX.MAPX" +
            "  WHERE CTN.MANL = ?" +
            (tuNgay  != null ? " AND TRUNC(PX.NGAYXUAT) >= ?" : "") +
            (denNgay != null ? " AND TRUNC(PX.NGAYXUAT) <= ?" : "") +
            ") ORDER BY NGAY ASC"
        );

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            // Nhập params
            ps.setInt(idx++, maNL);
            if (tuNgay  != null) ps.setDate(idx++, Date.valueOf(tuNgay));
            if (denNgay != null) ps.setDate(idx++, Date.valueOf(denNgay));
            // Xuất params
            ps.setInt(idx++, maNL);
            if (tuNgay  != null) ps.setDate(idx++, Date.valueOf(tuNgay));
            if (denNgay != null) ps.setDate(idx++, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TheKhoBienDongDTO dto = new TheKhoBienDongDTO();
                    dto.setNgayGiaoDich(rs.getTimestamp("NGAY") != null
                            ? rs.getTimestamp("NGAY").toLocalDateTime() : null);
                    dto.setLoaiGiaoDich(rs.getString("LOAI"));
                    dto.setMaLo(rs.getInt("MALO"));
                    dto.setSoLuong(rs.getDouble("SOLUONG"));
                    dto.setSoLuongConLai(rs.getDouble("SOLUONGCONLAI"));
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            handleException("layBienDong", e);
        }
        return list;
    }

    /**
     * Tính tổng hợp thẻ kho: tồn đầu kỳ, nhập kỳ, xuất kỳ, tồn cuối kỳ.
     *
     * @param maNL    mã nguyên liệu
     * @param tuNgay  từ ngày kỳ (null = từ đầu)
     * @param denNgay đến ngày kỳ (null = đến hiện tại)
     * @return double[4] = {tonDauKy, nhapKy, xuatKy, tonCuoiKy}
     */
    public double[] layTongHop(int maNL, LocalDate tuNgay, LocalDate denNgay) throws Exception {
        double tonDauKy = 0, nhapKy = 0, xuatKy = 0;

        // Tồn đầu kỳ = tổng nhập - tổng xuất trước tuNgay
        if (tuNgay != null) {
            String sqlDau =
                "SELECT " +
                "  NVL((SELECT SUM(CTN.SOLUONG) FROM CTPHIEUNHAP CTN JOIN PHIEUNHAPKHO PN ON PN.MAPN=CTN.MAPN" +
                "       WHERE CTN.MANL=? AND TRUNC(PN.NGAYNHAP) < ?), 0) -" +
                "  NVL((SELECT SUM(CX.SOLUONG) FROM CTPHIEUXUAT_NL CX JOIN CTPHIEUNHAP CTN ON CTN.MALO=CX.MALO" +
                "       JOIN PHIEUXUATKHO PX ON PX.MAPX=CX.MAPX" +
                "       WHERE CTN.MANL=? AND TRUNC(PX.NGAYXUAT) < ?), 0)" +
                "  AS TON_DAU FROM DUAL";
            try (Connection conn = moKetNoi();
                 PreparedStatement ps = conn.prepareStatement(sqlDau)) {
                ps.setInt(1, maNL);
                ps.setDate(2, Date.valueOf(tuNgay));
                ps.setInt(3, maNL);
                ps.setDate(4, Date.valueOf(tuNgay));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) tonDauKy = rs.getDouble("TON_DAU");
                }
            } catch (SQLException e) {
                handleException("layTongHop-tonDauKy", e);
            }
        }

        // Nhập kỳ
        StringBuilder sqlNhap = new StringBuilder(
            "SELECT NVL(SUM(CTN.SOLUONG), 0) AS TONG_NHAP " +
            "FROM CTPHIEUNHAP CTN JOIN PHIEUNHAPKHO PN ON PN.MAPN = CTN.MAPN " +
            "WHERE CTN.MANL = ?");
        if (tuNgay  != null) sqlNhap.append(" AND TRUNC(PN.NGAYNHAP) >= ?");
        if (denNgay != null) sqlNhap.append(" AND TRUNC(PN.NGAYNHAP) <= ?");

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sqlNhap.toString())) {
            int idx = 1;
            ps.setInt(idx++, maNL);
            if (tuNgay  != null) ps.setDate(idx++, Date.valueOf(tuNgay));
            if (denNgay != null) ps.setDate(idx++, Date.valueOf(denNgay));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) nhapKy = rs.getDouble("TONG_NHAP");
            }
        } catch (SQLException e) {
            handleException("layTongHop-nhapKy", e);
        }

        // Xuất kỳ
        StringBuilder sqlXuat = new StringBuilder(
            "SELECT NVL(SUM(CX.SOLUONG), 0) AS TONG_XUAT " +
            "FROM CTPHIEUXUAT_NL CX " +
            "JOIN CTPHIEUNHAP CTN ON CTN.MALO = CX.MALO " +
            "JOIN PHIEUXUATKHO PX ON PX.MAPX  = CX.MAPX " +
            "WHERE CTN.MANL = ?");
        if (tuNgay  != null) sqlXuat.append(" AND TRUNC(PX.NGAYXUAT) >= ?");
        if (denNgay != null) sqlXuat.append(" AND TRUNC(PX.NGAYXUAT) <= ?");

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sqlXuat.toString())) {
            int idx = 1;
            ps.setInt(idx++, maNL);
            if (tuNgay  != null) ps.setDate(idx++, Date.valueOf(tuNgay));
            if (denNgay != null) ps.setDate(idx++, Date.valueOf(denNgay));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) xuatKy = rs.getDouble("TONG_XUAT");
            }
        } catch (SQLException e) {
            handleException("layTongHop-xuatKy", e);
        }

        double tonCuoiKy = tonDauKy + nhapKy - xuatKy;
        return new double[]{tonDauKy, nhapKy, xuatKy, tonCuoiKy};
    }
}
