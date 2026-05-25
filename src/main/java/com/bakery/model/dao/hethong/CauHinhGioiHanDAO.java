package com.bakery.model.dao.hethong;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.hethong.CauHinhGioiHanDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO truy cập bảng NANGLUCSANXUAT — đọc/ghi giới hạn nhận đơn theo ngày.
 * Chỉ dùng SELECT và MERGE/INSERT+UPDATE (không có Stored Procedure CUD riêng cho bảng này).
 */
public class CauHinhGioiHanDAO extends BaseDAO {

    /** Lấy danh sách cấu hình giới hạn từ hôm nay trở đi (30 ngày). */
    public List<CauHinhGioiHanDTO> layDanhSachCauHinh() throws Exception {
        String sql = "SELECT NGAYSANXUAT, GIOIHANSOBANH, SOBANHDANHAN " +
                     "FROM NANGLUCSANXUAT " +
                     "ORDER BY NGAYSANXUAT DESC " +
                     "FETCH FIRST 60 ROWS ONLY";
        List<CauHinhGioiHanDTO> list = new ArrayList<>();
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                CauHinhGioiHanDTO dto = new CauHinhGioiHanDTO();
                if (rs.getDate("NGAYSANXUAT") != null) {
                    dto.setNgaySanXuat(rs.getDate("NGAYSANXUAT").toLocalDate());
                }
                dto.setGioiHanSoBanh(rs.getInt("GIOIHANSOBANH"));
                dto.setSoBanhDaNhan(rs.getInt("SOBANHDANHAN"));
                list.add(dto);
            }
        } catch (SQLException e) {
            handleException("layDanhSachCauHinh", e);
        }
        return list;
    }

    /**
     * Upsert giới hạn sản xuất cho một ngày cụ thể.
     * Nếu ngày đã tồn tại → UPDATE GIOIHANSOBANH; chưa có → INSERT.
     */
    public void luuCauHinh(LocalDate ngaySanXuat, int gioiHanSoBanh) throws Exception {
        String sql = "MERGE INTO NANGLUCSANXUAT T " +
                     "USING (SELECT ? AS NGAY, ? AS GIOI_HAN FROM DUAL) S " +
                     "ON (TRUNC(T.NGAYSANXUAT) = TRUNC(S.NGAY)) " +
                     "WHEN MATCHED THEN UPDATE SET T.GIOIHANSOBANH = S.GIOI_HAN " +
                     "WHEN NOT MATCHED THEN INSERT (NGAYSANXUAT, GIOIHANSOBANH, SOBANHDANHAN) " +
                     "VALUES (S.NGAY, S.GIOI_HAN, 0)";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(ngaySanXuat));
            pstmt.setInt(2, gioiHanSoBanh);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleException("luuCauHinh", e);
        }
    }

    /**
     * Lấy năng lực sản xuất cho một ngày cụ thể.
     * @return DTO với soBanhDaNhan/gioiHanSoBanh, hoặc null nếu chưa có cấu hình.
     */
    public CauHinhGioiHanDTO layTheoNgay(LocalDate ngay) throws Exception {
        String sql = "SELECT NGAYSANXUAT, GIOIHANSOBANH, SOBANHDANHAN " +
                     "FROM NANGLUCSANXUAT WHERE TRUNC(NGAYSANXUAT) = TRUNC(?)";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(ngay));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    CauHinhGioiHanDTO dto = new CauHinhGioiHanDTO();
                    if (rs.getDate("NGAYSANXUAT") != null)
                        dto.setNgaySanXuat(rs.getDate("NGAYSANXUAT").toLocalDate());
                    dto.setGioiHanSoBanh(rs.getInt("GIOIHANSOBANH"));
                    dto.setSoBanhDaNhan(rs.getInt("SOBANHDANHAN"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            handleException("layTheoNgay", e);
        }
        return null;
    }

    /**
     * Gọi FUNC_DIEMKHADUNG để lấy số slot bánh tùy chỉnh còn trống cho ngày chỉ định.
     * @param ngayNhan ngày khách hẹn nhận bánh
     * @return số bánh tùy chỉnh còn có thể nhận, 0 nếu đã đầy hoặc chưa cấu hình
     */
    public int laySlotsConTrong(LocalDate ngayNhan) throws Exception {
        String sql = "SELECT FUNC_DIEMKHADUNG(?) FROM DUAL";
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(ngayNhan));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            handleException("laySlotsConTrong", e);
        }
        return 0;
    }
}
