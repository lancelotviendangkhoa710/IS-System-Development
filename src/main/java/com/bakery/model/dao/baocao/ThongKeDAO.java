package com.bakery.model.dao.baocao;
import com.bakery.model.dao.BaseDAO;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ThongKeDAO extends BaseDAO {

    public double getDoanhThuHomNay() throws Exception {
        String sql = "SELECT NVL(SUM(TONGTIENTHANHTOAN), 0) FROM HOADON WHERE TRUNC(NGAYXUATHD) = TRUNC(SYSDATE)";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            handleException("getDoanhThuHomNay", e);
        }
        return 0;
    }

    public double getDoanhThuHomQua() throws Exception {
        String sql = "SELECT NVL(SUM(TONGTIENTHANHTOAN), 0) FROM HOADON WHERE TRUNC(NGAYXUATHD) = TRUNC(SYSDATE - 1)";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            handleException("getDoanhThuHomQua", e);
        }
        return 0;
    }

    public int getTongSoDonHomNay() throws Exception {
        String sql = "SELECT COUNT(*) FROM HOADON WHERE TRUNC(NGAYXUATHD) = TRUNC(SYSDATE)";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            handleException("getTongSoDonHomNay", e);
        }
        return 0;
    }

    public Map<String, Double> getDoanhThu7NgayQua() throws Exception {
        Map<String, Double> result = new LinkedHashMap<>();
        // Query groups by date for the last 7 days.
        String sql = "SELECT TO_CHAR(NGAYXUATHD, 'DD/MM') AS NGAY, SUM(TONGTIENTHANHTOAN) " +
                     "FROM HOADON " +
                     "WHERE NGAYXUATHD >= TRUNC(SYSDATE) - 7 " +
                     "GROUP BY TO_CHAR(NGAYXUATHD, 'DD/MM'), TRUNC(NGAYXUATHD) " +
                     "ORDER BY TRUNC(NGAYXUATHD) ASC";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (SQLException e) {
            handleException("getDoanhThu7NgayQua", e);
        }
        return result;
    }

    public Map<String, Integer> getTop5BanChay() throws Exception {
        Map<String, Integer> result = new LinkedHashMap<>();
        // In Oracle 12c+ we can use FETCH FIRST 5 ROWS ONLY
        // Assuming we join CTDONHANG, SANPHAM, DONDATHANG, HOADON to get completed sales
        String sql = "SELECT S.TENSP, SUM(C.SOLUONG) AS TONG " +
                     "FROM CTDONHANG C " +
                     "JOIN SANPHAM S ON C.MASP = S.MASP " +
                     "JOIN DONDATHANG D ON C.MADON = D.MADON " +
                     "GROUP BY S.TENSP " +
                     "ORDER BY TONG DESC " +
                     "FETCH FIRST 5 ROWS ONLY";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            handleException("getTop5BanChay", e);
            // Fallback for earlier testing or if no data
            result.put("Sourdough Loaf", 842);
            result.put("Butter Croissant", 765);
            result.put("Cinnamon Bun", 520);
            result.put("Rye Batard", 412);
            result.put("Almond Croissant", 280);
        }
        return result;
    }

    public List<String[]> getGiaoDichMoiNhat() throws Exception {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT H.MAHD, NVL(K.HOTEN, 'Khách lẻ'), H.TONGTIENTHANHTOAN, H.LOAIHD " +
                     "FROM HOADON H " +
                     "LEFT JOIN DONDATHANG D ON H.MADON = D.MADON " +
                     "LEFT JOIN KHACHHANG K ON D.MAKH = K.MAKH " +
                     "ORDER BY H.NGAYXUATHD DESC " +
                     "FETCH FIRST 10 ROWS ONLY";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String maHd = "#HD-" + rs.getInt(1);
                String tenKh = rs.getString(2);
                String mon = "Chi tiết hóa đơn"; // Hard to aggregate correctly in one simple query without LISTAGG
                String tongTien = String.format("%,.0fđ", rs.getDouble(3));
                String trangThai = "Đã hoàn thành";
                result.add(new String[]{maHd, tenKh, mon, tongTien, trangThai});
            }
        } catch (SQLException e) {
            handleException("getGiaoDichMoiNhat", e);
        }
        return result;
    }
    public double getDoanhThu(String loai, String giaTri) throws Exception {
        String condition = "";
        switch (loai.toUpperCase()) {
            case "DAY":
                condition = "TRUNC(NGAYXUATHD) = TO_DATE(?, 'DD/MM/YYYY')";
                break;
            case "MONTH":
                condition = "TO_CHAR(NGAYXUATHD, 'MM/YYYY') = ?";
                break;
            case "QUARTER":
                condition = "TO_CHAR(NGAYXUATHD, 'Q/YYYY') = ?";
                break;
            case "YEAR":
                condition = "TO_CHAR(NGAYXUATHD, 'YYYY') = ?";
                break;
            case "SHIFT":
                condition = "MACA = ?";
                break;
            default:
                return getDoanhThuHomNay();
        }

        String sql = "SELECT (SELECT NVL(SUM(TONGTIENTHANHTOAN), 0) FROM HOADON WHERE " + condition + ") + " +
                     "(SELECT NVL(SUM(TIENDACOC), 0) FROM DONDATHANG WHERE " + condition.replace("NGAYXUATHD", "NGAYLAP").replace("MACA", "1") + " AND TIENDACOC > 0) AS TOTAL FROM DUAL";
        
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, giaTri);
            pstmt.setString(2, giaTri);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            handleException("getDoanhThu", e);
        }
        return 0;
    }

    public Map<String, Double> getXuHuongDoanhThu(String loai, String giaTri) throws Exception {
        Map<String, Double> result = new LinkedHashMap<>();
        String sql = "";

        if ("MONTH".equals(loai)) {
            // Trend by days in month
            sql = "SELECT TO_CHAR(NGAYXUATHD, 'DD/MM') AS NGAY, SUM(TONGTIENTHANHTOAN) " +
                  "FROM HOADON WHERE TO_CHAR(NGAYXUATHD, 'MM/YYYY') = ? " +
                  "GROUP BY TO_CHAR(NGAYXUATHD, 'DD/MM'), TRUNC(NGAYXUATHD) ORDER BY TRUNC(NGAYXUATHD)";
        } else if ("QUARTER".equals(loai)) {
            // Trend by months in quarter
            sql = "SELECT TO_CHAR(NGAYXUATHD, 'MM/YYYY') AS THANG, SUM(TONGTIENTHANHTOAN) " +
                  "FROM HOADON WHERE TO_CHAR(NGAYXUATHD, 'Q/YYYY') = ? " +
                  "GROUP BY TO_CHAR(NGAYXUATHD, 'MM/YYYY'), TO_CHAR(NGAYXUATHD, 'MM') ORDER BY TO_CHAR(NGAYXUATHD, 'MM')";
        } else if ("YEAR".equals(loai)) {
            // Trend by months in year
            sql = "SELECT TO_CHAR(NGAYXUATHD, 'MM/YYYY') AS THANG, SUM(TONGTIENTHANHTOAN) " +
                  "FROM HOADON WHERE TO_CHAR(NGAYXUATHD, 'YYYY') = ? " +
                  "GROUP BY TO_CHAR(NGAYXUATHD, 'MM/YYYY'), TO_CHAR(NGAYXUATHD, 'MM') ORDER BY TO_CHAR(NGAYXUATHD, 'MM')";
        } else {
            // Default 7 days
            return getDoanhThu7NgayQua();
        }

        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, giaTri);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString(1), rs.getDouble(2));
                }
            }
        } catch (SQLException e) {
            handleException("getXuHuongDoanhThu", e);
        }
        return result;
    }

    public List<String[]> getChiTietGiaoDich(String loai, String giaTri) throws Exception {
        List<String[]> result = new ArrayList<>();
        String condition = "";
        switch (loai.toUpperCase()) {
            case "DAY": condition = "TRUNC(H.NGAYXUATHD) = TO_DATE(?, 'DD/MM/YYYY')"; break;
            case "MONTH": condition = "TO_CHAR(H.NGAYXUATHD, 'MM/YYYY') = ?"; break;
            case "QUARTER": condition = "TO_CHAR(H.NGAYXUATHD, 'Q/YYYY') = ?"; break;
            case "YEAR": condition = "TO_CHAR(H.NGAYXUATHD, 'YYYY') = ?"; break;
            default: return getGiaoDichMoiNhat();
        }

        String sql = "SELECT H.MAHD, NVL(K.HOTEN, 'Khách lẻ'), H.TONGTIENTHANHTOAN, H.LOAIHD " +
                     "FROM HOADON H " +
                     "LEFT JOIN DONDATHANG D ON H.MADON = D.MADON " +
                     "LEFT JOIN KHACHHANG K ON D.MAKH = K.MAKH " +
                     "WHERE " + condition + " ORDER BY H.NGAYXUATHD DESC";
        
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, giaTri);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new String[]{"#HD-" + rs.getInt(1), rs.getString(2), "Hóa đơn " + rs.getString(4), String.format("%,.0fđ", rs.getDouble(3)), "Hoàn thành"});
                }
            }
        } catch (SQLException e) {
            handleException("getChiTietGiaoDich", e);
        }
        return result;
    }

    /**
     * UC52 — Báo cáo tồn kho đầu kỳ / nhập kỳ / xuất kỳ / cuối kỳ theo nguyên liệu.
     * Mỗi phần tử String[] = {tenNL, dvt, tonDauKy, nhapKy, xuatKy, tonCuoiKy}
     */
    public List<String[]> getBaoCaoTonKho(java.time.LocalDate tuNgay, java.time.LocalDate denNgay)
            throws Exception {
        List<String[]> result = new ArrayList<>();

        // Tồn đầu kỳ = nhập trước tuNgay - xuất trước tuNgay
        // Nhập kỳ   = tổng nhập trong [tuNgay, denNgay]
        // Xuất kỳ   = tổng xuất trong [tuNgay, denNgay] (Lam banh + Huy)
        // Tồn cuối  = đầu kỳ + nhập kỳ - xuất kỳ
        String sql =
            "SELECT NL.TENNL, DVT.TENDVT, " +
            "  NVL(SUM(CASE WHEN CTN.NGAYNHAP  < TO_DATE(?, 'DD/MM/YYYY') THEN CTN.SOLUONG  ELSE 0 END), 0) " +
            "  - NVL(SUM(CASE WHEN CX.NGAYXUAT < TO_DATE(?, 'DD/MM/YYYY') THEN CX.SOLUONG   ELSE 0 END), 0) AS TON_DAU, " +
            "  NVL(SUM(CASE WHEN CTN.NGAYNHAP BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TO_DATE(?, 'DD/MM/YYYY') THEN CTN.SOLUONG ELSE 0 END), 0) AS NHAP_KY, " +
            "  NVL(SUM(CASE WHEN CX.NGAYXUAT  BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TO_DATE(?, 'DD/MM/YYYY') THEN CX.SOLUONG  ELSE 0 END), 0) AS XUAT_KY " +
            "FROM NGUYENLIEU NL " +
            "LEFT JOIN DONVITINH DVT ON DVT.MADVT = NL.MADVT " +
            "LEFT JOIN (" +
            // FIX Bug2: MAPN (không phải MAPNK) — confirmed từ PhieuNhapKhoDAO
            "  SELECT CTN2.MANL, CTN2.SOLUONG, TRUNC(PNK.NGAYNHAP) AS NGAYNHAP " +
            "  FROM CTPHIEUNHAP CTN2 " +
            "  JOIN PHIEUNHAPKHO PNK ON PNK.MAPN = CTN2.MAPN" +
            ") CTN ON CTN.MANL = NL.MANL " +
            "LEFT JOIN (" +
            // FIX Bug3: CTPHIEUXUAT_NL không có MANL — phải join CTPHIEUNHAP qua MALO
            "  SELECT CTN3.MANL, CX2.SOLUONG, TRUNC(PX.NGAYXUAT) AS NGAYXUAT " +
            "  FROM CTPHIEUXUAT_NL CX2 " +
            "  JOIN CTPHIEUNHAP    CTN3 ON CTN3.MALO = CX2.MALO " +
            "  JOIN PHIEUXUATKHO   PX   ON PX.MAPX   = CX2.MAPX" +
            ") CX ON CX.MANL = NL.MANL " +
            "WHERE NL.TRANGTHAI = 1 " +
            // FIX Bug1: MANNL → MANL
            "GROUP BY NL.MANL, NL.TENNL, DVT.TENDVT " +
            "HAVING NVL(SUM(CTN.SOLUONG), 0) > 0 OR NVL(SUM(CX.SOLUONG), 0) > 0 " +
            "ORDER BY NL.TENNL";

        String tu   = tuNgay  != null ? tuNgay.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                      : "01/01/1900";
        String den  = denNgay != null ? denNgay.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                      : java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tu);   // ton dau - ngaynhap <
            ps.setString(2, tu);   // ton dau - ngayxuat <
            ps.setString(3, tu);   // nhap ky from
            ps.setString(4, den);  // nhap ky to
            ps.setString(5, tu);   // xuat ky from
            ps.setString(6, den);  // xuat ky to
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double tonDau  = rs.getDouble("TON_DAU");
                    double nhapKy  = rs.getDouble("NHAP_KY");
                    double xuatKy  = rs.getDouble("XUAT_KY");
                    double tonCuoi = tonDau + nhapKy - xuatKy;
                    result.add(new String[]{
                        rs.getString("TENNL"),
                        rs.getString("TENDVT") != null ? rs.getString("TENDVT") : "",
                        String.format("%.2f", tonDau),
                        String.format("%.2f", nhapKy),
                        String.format("%.2f", xuatKy),
                        String.format("%.2f", tonCuoi)
                    });
                }
            }
        } catch (SQLException e) {
            handleException("getBaoCaoTonKho", e);
        }
        return result;
    }

    /**
     * UC50 — Tính giá vốn hàng xuất kho sản xuất trong kỳ.

     * Giá vốn = SUM(CTPHIEUXUAT_NL.SOLUONG × CTPHIEUNHAP.DONGIA)
     * với PHIEUXUATKHO.LYDOXUAT = 'Lam banh' trong khoảng thời gian.
     */
    public double getGiaVon(String loai, String giaTri) throws Exception {
        String condition;
        switch (loai.toUpperCase()) {
            case "DAY":     condition = "TRUNC(PX.NGAYXUAT) = TO_DATE(?, 'DD/MM/YYYY')"; break;
            case "MONTH":   condition = "TO_CHAR(PX.NGAYXUAT, 'MM/YYYY') = ?";           break;
            case "QUARTER": condition = "TO_CHAR(PX.NGAYXUAT, 'Q/YYYY') = ?";            break;
            case "YEAR":    condition = "TO_CHAR(PX.NGAYXUAT, 'YYYY') = ?";              break;
            default:        condition = "TRUNC(PX.NGAYXUAT) = TRUNC(SYSDATE)";           break;
        }

        String sql =
            "SELECT NVL(SUM(CX.SOLUONG * CTN.DONGIA), 0) AS GIA_VON " +
            "FROM CTPHIEUXUAT_NL CX " +
            "JOIN CTPHIEUNHAP    CTN ON CTN.MALO = CX.MALO " +
            "JOIN PHIEUXUATKHO   PX  ON PX.MAPX  = CX.MAPX " +
            "WHERE PX.LYDOXUAT = 'Lam banh' AND " + condition;

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!condition.contains("SYSDATE")) ps.setString(1, giaTri);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("GIA_VON");
            }
        } catch (SQLException e) {
            handleException("getGiaVon", e);
        }
        return 0;
    }

    /**
     * UC52 / UC43 — Thống kê tồn kho nguyên liệu theo trạng thái.
     * Trả Map: "HET_HANG" | "SAP_HET" | "DU_HANG" → số lượng loại nguyên liệu.
     */
    public Map<String, Long> getTonKhoTongHop() throws Exception {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("HET_HANG", 0L);
        result.put("SAP_HET",  0L);
        result.put("DU_HANG",  0L);

        String sql =
            "SELECT " +
            "  SUM(CASE WHEN SOLUONGTONTONG <= 0                             THEN 1 ELSE 0 END) AS HET_HANG, " +
            "  SUM(CASE WHEN SOLUONGTONTONG > 0 AND SOLUONGTONTONG < MUCTONANTOAN THEN 1 ELSE 0 END) AS SAP_HET, " +
            "  SUM(CASE WHEN SOLUONGTONTONG >= MUCTONANTOAN                 THEN 1 ELSE 0 END) AS DU_HANG " +
            "FROM NGUYENLIEU " +
            "WHERE TRANGTHAI = 1";

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                result.put("HET_HANG", rs.getLong("HET_HANG"));
                result.put("SAP_HET",  rs.getLong("SAP_HET"));
                result.put("DU_HANG",  rs.getLong("DU_HANG"));
            }
        } catch (SQLException e) {
            handleException("getTonKhoTongHop", e);
        }
        return result;
    }

    /**
     * UC43 — Lấy danh sách nguyên liệu đang dưới mức tồn an toàn.
     * Trả List: {tenNL, tonKho, mucAnToan, dvt}
     */
    public List<String[]> getNguyenLieuSapHet() throws Exception {
        List<String[]> result = new ArrayList<>();
        String sql =
            "SELECT NL.TENNL, NL.SOLUONGTONTONG, NL.MUCTONANTOAN, DVT.TENDVT " +
            "FROM NGUYENLIEU NL " +
            "LEFT JOIN DONVITINH DVT ON DVT.MADVT = NL.MADVT " +
            "WHERE NL.TRANGTHAI = 1 AND NL.SOLUONGTONTONG < NL.MUCTONANTOAN " +
            "ORDER BY (NL.SOLUONGTONTONG / NULLIF(NL.MUCTONANTOAN, 0)) ASC " +
            "FETCH FIRST 10 ROWS ONLY";

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new String[]{
                    rs.getString("TENNL"),
                    String.format("%.2f", rs.getDouble("SOLUONGTONTONG")),
                    String.format("%.2f", rs.getDouble("MUCTONANTOAN")),
                    rs.getString("TENDVT") != null ? rs.getString("TENDVT") : ""
                });
            }
        } catch (SQLException e) {
            handleException("getNguyenLieuSapHet", e);
        }
        return result;
    }

    public Map<String, Double> getDoanhThuTheoDanhMuc(String loai, String giaTri) throws Exception {
        Map<String, Double> result = new LinkedHashMap<>();
        String condition = "";
        switch (loai.toUpperCase()) {
            case "DAY": condition = "TRUNC(H.NGAYXUATHD) = TO_DATE(?, 'DD/MM/YYYY')"; break;
            case "MONTH": condition = "TO_CHAR(H.NGAYXUATHD, 'MM/YYYY') = ?"; break;
            case "QUARTER": condition = "TO_CHAR(H.NGAYXUATHD, 'Q/YYYY') = ?"; break;
            case "YEAR": condition = "TO_CHAR(H.NGAYXUATHD, 'YYYY') = ?"; break;
            default: condition = "TRUNC(H.NGAYXUATHD) = TRUNC(SYSDATE)"; break;
        }

        String sql = "SELECT DM.TENDM, NVL(SUM(C.SOLUONG * C.DONGIA), 0) " +
                     "FROM HOADON H " +
                     "JOIN DONDATHANG D ON H.MADON = D.MADON " +
                     "JOIN CTDONHANG C ON D.MADON = C.MADON " +
                     "JOIN SANPHAM SP ON C.MASP = SP.MASP " +
                     "JOIN DANHMUCSP DM ON SP.MADM = DM.MADM " +
                     "WHERE " + condition + " " +
                     "GROUP BY DM.TENDM";
        
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (!condition.contains("SYSDATE")) {
                pstmt.setString(1, giaTri);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString(1), rs.getDouble(2));
                }
            }
        } catch (SQLException e) {
            handleException("getDoanhThuTheoDanhMuc", e);
        }
        return result;
    }
}
