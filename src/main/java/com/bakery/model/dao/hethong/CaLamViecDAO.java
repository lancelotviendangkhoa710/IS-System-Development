package com.bakery.model.dao.hethong;
import com.bakery.model.dao.BaseDAO;

import com.bakery.model.dto.hethong.CaLamViecDTO;

import java.math.BigDecimal;
import java.sql.*;

public class CaLamViecDAO extends BaseDAO {

    /**
     * Mở ca làm việc mới qua PROC_MOCA.
     * Proc tự INSERT CALAMVIEC + DOISOAT trong cùng 1 transaction.
     *
     * @return maCa vừa tạo, hoặc ném RuntimeException nếu thất bại
     */
    public int moCa(String maMayPOS, BigDecimal tienKhaiBaoDauCa, int maNV) throws Exception {
        // PROC_MOCA: INSERT CALAMVIEC + INSERT DOISOAT trong cùng 1 transaction
        String sql = "{CALL PROC_MOCA(?, ?, ?, ?)}";

        try (Connection conn = moKetNoi()) {
            conn.setAutoCommit(false);
            try (CallableStatement cs = conn.prepareCall(sql)) {
                cs.setInt(1, maNV);
                cs.setString(2, maMayPOS);
                cs.setBigDecimal(3, tienKhaiBaoDauCa != null ? tienKhaiBaoDauCa : BigDecimal.ZERO);
                cs.registerOutParameter(4, Types.INTEGER);
                cs.execute();
                int maCa = cs.getInt(4);
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
            handleException("moCa", e);
            throw new RuntimeException("Lỗi hệ thống khi mở ca: " + e.getMessage(), e);
        }
    }

    /**
     * Đóng ca làm việc: ghi thời gian đóng và chuyển trạng thái sang 'Đã đóng'.
     */
    public void dongCa(int maCa) throws Exception {
        String sql = "UPDATE CALAMVIEC "
                + "SET THOIGIANDONGCA = SYSDATE, TRANGTHAI = N'Đã đóng' "
                + "WHERE MACA = ?";

        try (Connection conn = moKetNoi();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maCa);
            ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() >= -20599 && e.getErrorCode() <= -20001) {
                String msg = e.getMessage().replaceAll("ORA-\\d+: ", "").trim();
                throw new RuntimeException(msg, e);
            }
            handleException("dongCa", e);
            throw new RuntimeException("Lỗi hệ thống khi đóng ca: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy ca làm việc đang mở của nhân viên.
     *
     * @return CaLamViecDTO nếu có ca đang mở, null nếu chưa mở ca
     */
    public CaLamViecDTO layCaHienTai(int maNV) throws Exception {
        String sql = "SELECT MACA, MANV, MAMAYPOS, THOIGIANMOCA, THOIGIANDONGCA, TRANGTHAI "
                + "FROM CALAMVIEC "
                + "WHERE MANV = ? AND TRANGTHAI = N'Đang mở' "
                + "AND ROWNUM = 1";

        try (Connection conn = moKetNoi();
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
            handleException("layCaHienTai", e);
            throw new RuntimeException("Lỗi hệ thống khi lấy ca hiện tại: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Kiểm tra nhân viên đang có ca mở hay không (dùng cho mọi vai trò).
     *
     * @return true nếu NV đang có ca mở
     */
    public boolean kiemTraNvDangMoCa(int maNV) throws Exception {
        String sql = "SELECT COUNT(*) FROM CALAMVIEC WHERE MANV = ? AND TRANGTHAI = N'Đang mở'";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            handleException("kiemTraNvDangMoCa", e);
        }
        return false;
    }

    /**
     * Mở ca chấm công cho nhân viên không phải thu ngân (MAMAYPOS = NULL).
     * Thu ngân dùng PROC_MOCA qua moCa().
     *
     * @return maCa vừa tạo
     */
    public int checkIn(int maNV) throws Exception {
        String sql = "INSERT INTO CALAMVIEC (MANV, MAMAYPOS, THOIGIANMOCA, TRANGTHAI) "
                + "VALUES (?, NULL, SYSDATE, N'Đang mở')";
        Connection conn = null;
        try {
            conn = moKetNoi();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, new String[]{"MACA"})) {
                ps.setInt(1, maNV);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int maCa = rs.getInt(1);
                        conn.commit();
                        return maCa;
                    }
                }
            }
            conn.rollback();
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (Exception ignored) {} }
            handleException("checkIn", e);
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {} }
        }
        return -1;
    }

    /**
     * Đóng ca chấm công — dùng cho cả thu ngân lẫn nhân viên thường.
     * Thu ngân gọi qua dongCa(maCa); nhân viên thường cũng gọi method này.
     */
    public void checkOut(int maCa) throws Exception {
        dongCa(maCa);
    }

    /**
     * Lấy lịch sử chấm công của nhân viên theo tháng.
     *
     * @param maNV  Mã nhân viên
     * @param thang Tháng (1–12)
     * @param nam   Năm (VD: 2026)
     * @return Danh sách ca trong tháng, sắp xếp mới nhất lên đầu
     */
    public java.util.List<CaLamViecDTO> layLichSuChamCong(int maNV, int thang, int nam) throws Exception {
        java.util.List<CaLamViecDTO> ds = new java.util.ArrayList<>();
        String sql = "SELECT MACA, MANV, MAMAYPOS, THOIGIANMOCA, THOIGIANDONGCA, TRANGTHAI "
                + "FROM CALAMVIEC "
                + "WHERE MANV = ? "
                + "  AND EXTRACT(MONTH FROM THOIGIANMOCA) = ? "
                + "  AND EXTRACT(YEAR  FROM THOIGIANMOCA) = ? "
                + "ORDER BY THOIGIANMOCA DESC";

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maNV);
            ps.setInt(2, thang);
            ps.setInt(3, nam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CaLamViecDTO ca = new CaLamViecDTO();
                    ca.setMaCa(rs.getInt("MACA"));
                    ca.setMaNV(rs.getInt("MANV"));
                    ca.setMaMayPOS(rs.getString("MAMAYPOS"));
                    if (rs.getTimestamp("THOIGIANMOCA") != null)
                        ca.setThoiGianMoCa(rs.getTimestamp("THOIGIANMOCA").toLocalDateTime());
                    if (rs.getTimestamp("THOIGIANDONGCA") != null)
                        ca.setThoiGianDongCa(rs.getTimestamp("THOIGIANDONGCA").toLocalDateTime());
                    ca.setTrangThai(rs.getString("TRANGTHAI"));
                    ds.add(ca);
                }
            }
        } catch (SQLException e) {
            handleException("layLichSuChamCong", e);
        }
        return ds;
    }
}
