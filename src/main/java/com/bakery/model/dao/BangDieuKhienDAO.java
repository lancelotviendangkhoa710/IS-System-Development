package com.bakery.model.dao;

import com.bakery.model.dto.BangDieuKhienKPIDTO;
import com.bakery.model.dto.TopSanPhamDTO;
import com.bakery.utils.DBConnect;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BangDieuKhienDAO {

    public BangDieuKhienKPIDTO layKPI() throws SQLException {
        BigDecimal doanhThu = BigDecimal.ZERO;
        int soHoaDon = 0;
        int donDangXuLy = 0;
        int canhBaoTonKho = 0;

        try (Connection conn = DBConnect.getConnection()) {

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
                    "WHERE  SOLUONGTON <= SOLUONGTOITHIEU " +
                    "  AND  THOIDIEMXOA IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(sqlTonKho);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    canhBaoTonKho = rs.getInt(1);
            }
        }

        return new BangDieuKhienKPIDTO(doanhThu, soHoaDon, donDangXuLy, canhBaoTonKho);
    }

    public List<TopSanPhamDTO> layTop5SanPhamThang() throws SQLException {
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

        try (Connection conn = DBConnect.getConnection();
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
}
