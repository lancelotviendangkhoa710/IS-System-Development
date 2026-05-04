package com.bakery.model.dao.hethong;
import com.bakery.model.dao.BaseDAO;

import com.bakery.model.dto.hethong.CaLamViecDTO;

import java.math.BigDecimal;
import java.sql.*;

public class CaLamViecDAO extends BaseDAO {

    /**
     * Mở ca làm việc mới, tạo bản ghi đối soát ban đầu với tiền khai báo đầu ca.
     * Dùng PL/SQL anonymous block với RETURNING INTO để lấy MACA vừa sinh.
     *
     * @return maCa vừa tạo, hoặc ném RuntimeException nếu thất bại
     */
    public int moCa(String maMayPOS, BigDecimal tienKhaiBaoDauCa, int maNV) throws Exception {
        String sqlCa = "BEGIN "
                + "INSERT INTO CALAMVIEC (MANV, MAMAYPOS, THOIGIANMOCA, TRANGTHAI) "
                + "VALUES (?, ?, CURRENT_TIMESTAMP, N'Đang mở') "
                + "RETURNING MACA INTO ?; "
                + "END;";
        String sqlDoiSoat = "INSERT INTO DOISOAT (MACA, TIENKHAIBAODAUCA) VALUES (?, ?)";

        try (Connection conn = moKetNoi()) {
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
     * Kiểm tra máy POS có đang có ca mở hay không, dùng để chặn mở trùng ca.
     *
     * @return true nếu máy đang có ca mở, false nếu không
     */
    public boolean kiemTraCaDangMo(String maMayPOS) throws Exception {
        String sql = "SELECT COUNT(*) AS SO_CA "
                + "FROM CALAMVIEC "
                + "WHERE MAMAYPOS = ? AND TRANGTHAI = N'Đang mở'";

        try (Connection conn = moKetNoi();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maMayPOS);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("SO_CA") > 0;
                }
            }

        } catch (SQLException e) {
            handleException("kiemTraCaDangMo", e);
            throw new RuntimeException("Lỗi hệ thống khi kiểm tra ca: " + e.getMessage(), e);
        }
        return false;
    }
}
