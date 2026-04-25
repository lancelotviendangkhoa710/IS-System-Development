package com.bakery.model.dao;

import com.bakery.model.dto.CaLamViecDTO;
import com.bakery.utils.DBConnect;

import java.math.BigDecimal;
import java.sql.*;

public class CaLamViecDAO {

    /**
     * Mở ca làm việc mới, tạo bản ghi đối soát ban đầu với tiền khai báo đầu ca.
     * Dùng PL/SQL anonymous block với RETURNING INTO để lấy MACA vừa sinh.
     *
     * @return maCa vừa tạo, hoặc ném RuntimeException nếu thất bại
     */
    public int moCa(String maMayPOS, BigDecimal tienKhaiBaoDauCa, int maNV) {
        String sqlCa = "BEGIN "
                + "INSERT INTO CALAMVIEC (MANV, MAMAYPOS, THOIGIANMOCA, TRANGTHAI) "
                + "VALUES (?, ?, CURRENT_TIMESTAMP, N'Đang mở') "
                + "RETURNING MACA INTO ?; "
                + "END;";
        String sqlDoiSoat = "INSERT INTO DOISOAT (MACA, TIENKHAIBAODAUCA) VALUES (?, ?)";

        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cs = conn.prepareCall(sqlCa)) {
                cs.setInt(1, maNV);
                cs.setString(2, maMayPOS);
                cs.registerOutParameter(3, Types.INTEGER);
                cs.execute();
                int maCa = cs.getInt(3);

                try (PreparedStatement ps = conn.prepareStatement(sqlDoiSoat)) {
                    ps.setInt(1, maCa);
                    ps.setBigDecimal(2, tienKhaiBaoDauCa);
                    ps.executeUpdate();
                }

                conn.commit();
                return maCa;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() >= -20599 && e.getErrorCode() <= -20001) {
                String msg = e.getMessage().replaceAll("ORA-\\d+: ", "").trim();
                throw new RuntimeException(msg, e);
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi hệ thống khi mở ca: " + e.getMessage(), e);
        }
    }

    /**
     * Đóng ca làm việc: ghi thời gian đóng và chuyển trạng thái sang 'Đã đóng'.
     */
    public void dongCa(int maCa) {
        String sql = "UPDATE CALAMVIEC "
                + "SET THOIGIANDONGCA = SYSDATE, TRANGTHAI = N'Đã đóng' "
                + "WHERE MACA = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maCa);
            ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() >= -20599 && e.getErrorCode() <= -20001) {
                String msg = e.getMessage().replaceAll("ORA-\\d+: ", "").trim();
                throw new RuntimeException(msg, e);
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi hệ thống khi đóng ca: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy ca làm việc đang mở của nhân viên.
     *
     * @return CaLamViecDTO nếu có ca đang mở, null nếu chưa mở ca
     */
    public CaLamViecDTO layCaHienTai(int maNV) {
        String sql = "SELECT MACA, MANV, MAMAYPOS, THOIGIANMOCA, THOIGIANDONGCA, TRANGTHAI "
                + "FROM CALAMVIEC "
                + "WHERE MANV = ? AND TRANGTHAI = N'Đang mở' "
                + "AND ROWNUM = 1";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maNV);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CaLamViecDTO ca = new CaLamViecDTO();
                    ca.setMaCa(rs.getInt("MACA"));
                    ca.setMaNV(rs.getInt("MANV"));
                    ca.setMaMayPOS(rs.getString("MAMAYPOS"));

                    if (rs.getTimestamp("THOIGIANMOCA") != null) {
                        ca.setThoiGianMoCa(rs.getTimestamp("THOIGIANMOCA").toLocalDateTime());
                    }
                    if (rs.getTimestamp("THOIGIANDONGCA") != null) {
                        ca.setThoiGianDongCa(rs.getTimestamp("THOIGIANDONGCA").toLocalDateTime());
                    }
                    ca.setTrangThai(rs.getString("TRANGTHAI"));
                    return ca;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi hệ thống khi lấy ca hiện tại: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Kiểm tra máy POS có đang có ca mở hay không, dùng để chặn mở trùng ca.
     *
     * @return true nếu máy đang có ca mở, false nếu không
     */
    public boolean kiemTraCaDangMo(String maMayPOS) {
        String sql = "SELECT COUNT(*) AS SO_CA "
                + "FROM CALAMVIEC "
                + "WHERE MAMAYPOS = ? AND TRANGTHAI = N'Đang mở'";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maMayPOS);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("SO_CA") > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi hệ thống khi kiểm tra ca: " + e.getMessage(), e);
        }
        return false;
    }

    public static void main(String[] args) {
        CaLamViecDAO dao = new CaLamViecDAO();

        System.out.println("=== Test CaLamViecDAO ===\n");

        // 1. Kiểm tra máy POS-01 có ca đang mở không (trước khi mở)
        boolean dangMo = dao.kiemTraCaDangMo("POS-01");
        System.out.println("POS-01 có ca đang mở không (trước): " + dangMo);

        if (dangMo) {
            System.out.println("Máy đã có ca mở — bỏ qua bước mở ca.");
        } else {
            // 2. Mở ca cho nhân viên mã 1, tiền khai báo đầu ca 1.000.000đ
            System.out.println("\nĐang mở ca cho NV mã 1 tại POS-01...");
            try {
                int maCaMoi = dao.moCa("POS-01", new BigDecimal("1000000"), 1);
                System.out.println("Mở ca thành công! Mã ca: " + maCaMoi);

                // 3. Lấy ca hiện tại để xác nhận
                CaLamViecDTO caHienTai = dao.layCaHienTai(1);
                if (caHienTai != null) {
                    System.out.println("\nThông tin ca hiện tại:");
                    System.out.println("  Mã ca        : " + caHienTai.getMaCa());
                    System.out.println("  Mã NV        : " + caHienTai.getMaNV());
                    System.out.println("  Máy POS      : " + caHienTai.getMaMayPOS());
                    System.out.println("  Giờ mở ca   : " + caHienTai.getThoiGianMoCa());
                    System.out.println("  Trạng thái  : " + caHienTai.getTrangThai());
                } else {
                    System.out.println("Không tìm thấy ca đang mở.");
                }

                // 4. Kiểm tra lại máy POS-01 sau khi mở
                System.out.println("\nPOS-01 có ca đang mở không (sau): " + dao.kiemTraCaDangMo("POS-01"));

            } catch (RuntimeException e) {
                System.err.println("Lỗi: " + e.getMessage());
            }
        }
    }
}
