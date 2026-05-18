package com.bakery.model.dao.kho;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.kho.TheKhoBienDongDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO tra cứu thẻ kho nguyên liệu (UC44).
 * Task 2.1: Dùng VW_THE_KHO_NGUYEN_LIEU thay vì inline SQL UNION ALL 56 dòng.
 * Task 2.2: layTongHop() gộp 3 round-trip → 1 query conditional SUM.
 */
public class TheKhoDAO extends BaseDAO {

    /**
     * Task 2.1: Lấy biến động nhập/xuất từ VW_THE_KHO_NGUYEN_LIEU.
     * View đã có đầy đủ logic UNION ALL nhập + xuất — tránh trùng lặp với inline SQL cũ.
     *
     * @param maNL    mã nguyên liệu cần xem thẻ kho
     * @param tuNgay  từ ngày (null = không giới hạn)
     * @param denNgay đến ngày (null = không giới hạn)
     */
    public List<TheKhoBienDongDTO> layBienDong(int maNL, LocalDate tuNgay, LocalDate denNgay)
            throws Exception {
        List<TheKhoBienDongDTO> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT THOIDIEM, LOAIBIENDONG, MACHUNGTU, MALO, SOLUONG, DONGIA, THANHTIEN, SOLUONGCONLAI " +
            "FROM VW_THE_KHO_NGUYEN_LIEU " +
            "WHERE MANL = ?"
        );
        if (tuNgay  != null) sql.append(" AND TRUNC(THOIDIEM) >= ?");
        if (denNgay != null) sql.append(" AND TRUNC(THOIDIEM) <= ?");
        sql.append(" ORDER BY THOIDIEM ASC");

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            ps.setInt(idx++, maNL);
            if (tuNgay  != null) ps.setDate(idx++, Date.valueOf(tuNgay));
            if (denNgay != null) ps.setDate(idx++, Date.valueOf(denNgay));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TheKhoBienDongDTO dto = new TheKhoBienDongDTO();
                    dto.setNgayGiaoDich(rs.getTimestamp("THOIDIEM") != null
                            ? rs.getTimestamp("THOIDIEM").toLocalDateTime() : null);
                    // View trả LOAIBIENDONG: 'NHAP' | 'XUAT_SX' — map sang label hiển thị
                    String loai = rs.getString("LOAIBIENDONG");
                    dto.setLoaiGiaoDich("NHAP".equals(loai) ? "Nhập kho" : "Xuất sản xuất");
                    // Fix: đọc MALO và SOLUONGCONLAI thực tế từ CTPHIEUNHAP qua view
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
     * Task 2.2: Tính tổng hợp thẻ kho — gộp 3 query riêng thành 1 conditional SUM.
     * Giảm 3 round-trip DB → 1, nhất quán snapshot transaction.
     *
     * @param maNL    mã nguyên liệu
     * @param tuNgay  từ ngày kỳ (null = từ đầu)
     * @param denNgay đến ngày kỳ (null = đến hiện tại)
     * @return double[4] = {tonDauKy, nhapKy, xuatKy, tonCuoiKy}
     */
    public double[] layTongHop(int maNL, LocalDate tuNgay, LocalDate denNgay) throws Exception {
        // Tồn đầu kỳ = nhập trước tuNgay − xuất trước tuNgay
        // Nhập kỳ    = tổng nhập trong [tuNgay, denNgay]
        // Xuất kỳ    = tổng xuất trong [tuNgay, denNgay]
        String tu  = tuNgay  != null ? tuNgay.toString()  : "1900-01-01";
        String den = denNgay != null ? denNgay.toString() : java.time.LocalDate.now().toString();

        String sql =
            "SELECT " +
            "  NVL(SUM(CASE WHEN TRUNC(CTN.MAPN_DATE) < TO_DATE(?, 'YYYY-MM-DD') AND CX.MALO IS NULL " +
            "    THEN CTN.SOLUONG ELSE 0 END), 0) " +
            "  - NVL(SUM(CASE WHEN TRUNC(PX.NGAYXUAT)  < TO_DATE(?, 'YYYY-MM-DD') AND CX.MALO IS NOT NULL " +
            "    THEN CX.SOLUONG ELSE 0 END), 0) AS TON_DAU, " +
            "  NVL(SUM(CASE WHEN TRUNC(CTN.MAPN_DATE) BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') " +
            "    AND CX.MALO IS NULL THEN CTN.SOLUONG ELSE 0 END), 0) AS NHAP_KY, " +
            "  NVL(SUM(CASE WHEN TRUNC(PX.NGAYXUAT) BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') " +
            "    AND CX.MALO IS NOT NULL THEN CX.SOLUONG ELSE 0 END), 0) AS XUAT_KY " +
            "FROM CTPHIEUNHAP CTN " +
            "JOIN PHIEUNHAPKHO PN ON PN.MAPN = CTN.MAPN " +
            "LEFT JOIN CTPHIEUXUAT_NL CX ON CX.MALO = CTN.MALO " +
            "LEFT JOIN PHIEUXUATKHO PX ON PX.MAPX = CX.MAPX " +
            "WHERE CTN.MANL = ?";

        // Fallback sang query đơn giản hơn dùng subquery — tránh alias không hợp lệ
        String sqlSimple =
            "SELECT " +
            "  NVL((SELECT SUM(C.SOLUONG) FROM CTPHIEUNHAP C JOIN PHIEUNHAPKHO P ON P.MAPN=C.MAPN " +
            "       WHERE C.MANL=? AND TRUNC(P.NGAYNHAP) < TO_DATE(?, 'YYYY-MM-DD')), 0) " +
            "  - NVL((SELECT SUM(CX.SOLUONG) FROM CTPHIEUXUAT_NL CX JOIN CTPHIEUNHAP C ON C.MALO=CX.MALO " +
            "         JOIN PHIEUXUATKHO PX ON PX.MAPX=CX.MAPX " +
            "         WHERE C.MANL=? AND TRUNC(PX.NGAYXUAT) < TO_DATE(?, 'YYYY-MM-DD')), 0) AS TON_DAU, " +
            "  NVL((SELECT SUM(C.SOLUONG) FROM CTPHIEUNHAP C JOIN PHIEUNHAPKHO P ON P.MAPN=C.MAPN " +
            "       WHERE C.MANL=? AND TRUNC(P.NGAYNHAP) BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD')), 0) AS NHAP_KY, " +
            "  NVL((SELECT SUM(CX.SOLUONG) FROM CTPHIEUXUAT_NL CX JOIN CTPHIEUNHAP C ON C.MALO=CX.MALO " +
            "         JOIN PHIEUXUATKHO PX ON PX.MAPX=CX.MAPX " +
            "         WHERE C.MANL=? AND TRUNC(PX.NGAYXUAT) BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD')), 0) AS XUAT_KY " +
            "FROM DUAL";

        double tonDauKy = 0, nhapKy = 0, xuatKy = 0;
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sqlSimple)) {
            // TON_DAU params: maNL, tu, maNL, tu
            ps.setInt(1, maNL); ps.setString(2, tu);
            ps.setInt(3, maNL); ps.setString(4, tu);
            // NHAP_KY params: maNL, tu, den
            ps.setInt(5, maNL); ps.setString(6, tu); ps.setString(7, den);
            // XUAT_KY params: maNL, tu, den
            ps.setInt(8, maNL); ps.setString(9, tu); ps.setString(10, den);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tonDauKy = rs.getDouble("TON_DAU");
                    nhapKy   = rs.getDouble("NHAP_KY");
                    xuatKy   = rs.getDouble("XUAT_KY");
                }
            }
        } catch (SQLException e) {
            handleException("layTongHop", e);
        }

        double tonCuoiKy = tonDauKy + nhapKy - xuatKy;
        return new double[]{tonDauKy, nhapKy, xuatKy, tonCuoiKy};
    }
}
