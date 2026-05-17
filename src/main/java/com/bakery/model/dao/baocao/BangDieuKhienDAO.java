package com.bakery.model.dao.baocao;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.baocao.BangDieuKhienKPIDTO;
import com.bakery.model.dto.baocao.TopSanPhamDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BangDieuKhienDAO extends BaseDAO {

    public BangDieuKhienKPIDTO layKPI() throws Exception {
        BigDecimal doanhThu = BigDecimal.ZERO;
        int soHoaDon = 0;
        int donDangXuLy = 0;
        int canhBaoTonKho = 0;

        try (Connection conn = moKetNoi()) {

            // 1. Doanh thu hôm nay — tổng tất cả hóa đơn xuất trong ngày
            String sqlDoanhThu = "SELECT NVL(SUM(TONGTIENTHANHTOAN), 0) " +
                    "FROM   HOADON " +
                    "WHERE  TRUNC(NGAYXUATHD) = TRUNC(SYSDATE)";
            try (PreparedStatement ps = conn.prepareStatement(sqlDoanhThu);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    doanhThu = rs.getBigDecimal(1);
            }

            // 2. Số hóa đơn hôm nay (= đơn đã hoàn thành)
            String sqlHoaDon = "SELECT COUNT(*) " +
                    "FROM   HOADON " +
                    "WHERE  TRUNC(NGAYXUATHD) = TRUNC(SYSDATE)";
            try (PreparedStatement ps = conn.prepareStatement(sqlHoaDon);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    soHoaDon = rs.getInt(1);
            }

            String sqlDangXuLy = "SELECT COUNT(*) " +
                    "FROM   DONHANG " +
                    "WHERE  TRANGTHAI NOT IN ('HOANTHANH', 'DAHUY') " +
                    "  AND  THOIDIEMXOA IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(sqlDangXuLy);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    donDangXuLy = rs.getInt(1);
            }

            String sqlTonKho = "SELECT COUNT(*) " +
                    "FROM   NGUYENLIEU " +
                    "WHERE  SOLUONGTONTONG <= MUCTONANTOAN " +
                    "  AND  THOIDIEMXOA IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(sqlTonKho);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    canhBaoTonKho = rs.getInt(1);
            }
        } catch (SQLException e) {
            handleException("layKPI", e);
        }

        return new BangDieuKhienKPIDTO(doanhThu, soHoaDon, donDangXuLy, canhBaoTonKho);
    }

    public List<TopSanPhamDTO> layTop5SanPhamThang() throws Exception {
        List<TopSanPhamDTO> list = new ArrayList<>();

        String sql = "SELECT sp.TENSP, SUM(ct.SOLUONG) AS TONGBAN " +
                "FROM   CTDONHANG ct " +
                "  JOIN SANPHAM  sp ON ct.MASP  = sp.MASP " +
                "  JOIN DONHANG  dh ON ct.MADON = dh.MADON " +
                "WHERE  dh.TRANGTHAI = 'HOANTHANH' " +
                "  AND  TRUNC(dh.NGAYDAT) >= TRUNC(ADD_MONTHS(SYSDATE, -1)) " +
                "GROUP  BY sp.MASP, sp.TENSP " +
                "ORDER  BY TONGBAN DESC " +
                "FETCH  FIRST 5 ROWS ONLY";

        try (Connection conn = moKetNoi();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TopSanPhamDTO(
                        rs.getString("TENSP"),
                        rs.getInt("TONGBAN")));
            }
        }
        return list;
    }

    /**
     * Gọi FUNC_DIEMKHADUNG(P_NGAYCANKIEMTRA) — DB tính số slot còn trống nhận bánh tùy chỉnh.
     *
     * Công thức tại DB: GIOIHANSOBANH (NANGLUCSANXUAT) − SUM đơn tùy chỉnh chưa hủy trong ngày.
     * Java không cần biết cấu trúc NANGLUCSANXUAT — logic thay đổi chỉ cần sửa function trên DB.
     *
     * @param ngay ngày cần kiểm tra sức chứa
     * @return số slot còn trống (≥ 0); 0 = hết công suất hoặc chưa cấu hình ngày đó
     */
    public int getDiemKhaDung(java.time.LocalDate ngay) throws Exception {
        String sql = "{ ? = call FUNC_DIEMKHADUNG(?) }";
        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.setDate(2, java.sql.Date.valueOf(ngay != null ? ngay : java.time.LocalDate.now()));
            cs.execute();
            int result = cs.getInt(1);
            return cs.wasNull() ? 0 : result;
        } catch (SQLException e) {
            handleException("getDiemKhaDung", e);
        }
        return 0;
    }
}
