package com.bakery.model.dao.hethong;
import com.bakery.model.dao.BaseDAO;

import java.math.BigDecimal;
import java.sql.*;

public class DoiSoatDAO extends BaseDAO {

    /**
     * Gọi FUNC_TinhTienMatLyTuong để tính tiền mặt lý tưởng trong két cuối ca.
     * Kết quả là con số BÍ MẬT — tầng Service giữ nội bộ, KHÔNG trả lên View.
     *
     * @return tổng tiền mặt lý tưởng theo hệ thống
     */
    /**
     * Lấy tiền khai báo đầu ca từ bảng DOISOAT.
     */
    public BigDecimal layTienKhaiBaoDauCa(int maCa) throws Exception {
        String sql = "SELECT NVL(TIENKHAIBAODAUCA, 0) FROM DOISOAT WHERE MACA = ?";
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, maCa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            handleException("layTienKhaiBaoDauCa", e);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal tinhTienMatLyTuong(int maCa, BigDecimal tienKhaiBaoDauCa) throws Exception {
        String sql = "SELECT FUNC_TINHTIENMATLYTUONG(?, ?) FROM DUAL";

        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, maCa);
            ps.setBigDecimal(2, tienKhaiBaoDauCa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }

        } catch (SQLException e) {
            handleException("tinhTienMatLyTuong", e);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Gọi PROC_DongCaDoiSoat để ghi kết quả đối soát và đóng ca làm việc.
     * lyDoChenhLech truyền null nếu tiền khớp (chênh lệch = 0).
     */
    public void dongCaDoiSoat(int maCa, BigDecimal tienThucTeDem,
                                BigDecimal chenhLech, String lyDoChenhLech) throws Exception {
        String sql = "{CALL PROC_DONGCADOISOAT(?, ?, ?, ?)}";

        try (Connection conn = moKetNoi();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, maCa);
            cs.setBigDecimal(2, tienThucTeDem);
            cs.setBigDecimal(3, chenhLech);

            if (lyDoChenhLech != null) {
                cs.setString(4, lyDoChenhLech);
            } else {
                cs.setNull(4, Types.NVARCHAR);
            }

            cs.execute();

        } catch (SQLException e) {
            handleException("dongCaDoiSoat", e);
        }
    }

}
