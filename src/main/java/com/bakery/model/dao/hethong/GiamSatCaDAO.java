package com.bakery.model.dao.hethong;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.hethong.GiamSatCaDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO lấy dữ liệu lịch sử ca làm việc cho màn hình "Giám sát tiền mặt đóng ca".
 * Chỉ có quyền SELECT — không CUD.
 */
public class GiamSatCaDAO extends BaseDAO {

    /**
     * Lấy danh sách ca đã đóng, join với NHANVIEN và DOISOAT.
     * Sắp xếp mới nhất lên trên.
     *
     * @param limit  số ca tối đa trả về (0 = không giới hạn)
     * @return danh sách GiamSatCaDTO
     */
    public List<GiamSatCaDTO> layLichSuCa(int limit) throws Exception {
        String sql =
                "SELECT CA.MACA, NV.HOTEN, CA.MAMAYPOS, " +
                "       CA.THOIGIANMOCA, CA.THOIGIANDONGCA, CA.TRANGTHAI, " +
                "       DS.TIENKHAIBAODAUCA, DS.TONGTIENHETHONG, " +
                "       DS.TIENTHUCTEDEM, DS.CHENHLECH, DS.LYDOCHENHLECH " +
                "FROM CALAMVIEC CA " +
                "JOIN NHANVIEN NV ON NV.MANV = CA.MANV " +
                "LEFT JOIN DOISOAT DS ON DS.MACA = CA.MACA " +
                "ORDER BY CA.THOIGIANMOCA DESC" +
                (limit > 0 ? " FETCH FIRST " + limit + " ROWS ONLY" : "");

        List<GiamSatCaDTO> result = new ArrayList<>();
        try (Connection conn = moKetNoi();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                GiamSatCaDTO dto = new GiamSatCaDTO();
                dto.setMaCa(rs.getInt("MACA"));
                dto.setHoTenNV(rs.getString("HOTEN"));
                dto.setMaMayPOS(rs.getString("MAMAYPOS"));

                if (rs.getTimestamp("THOIGIANMOCA") != null)
                    dto.setThoiGianMoCa(rs.getTimestamp("THOIGIANMOCA").toLocalDateTime());
                if (rs.getTimestamp("THOIGIANDONGCA") != null)
                    dto.setThoiGianDongCa(rs.getTimestamp("THOIGIANDONGCA").toLocalDateTime());

                dto.setTrangThai(rs.getString("TRANGTHAI"));
                dto.setTienKhaiBaoDauCa(rs.getBigDecimal("TIENKHAIBAODAUCA"));
                dto.setTongTienHeThong(rs.getBigDecimal("TONGTIENHETHONG"));
                dto.setTienThucTeDem(rs.getBigDecimal("TIENTHUCTEDEM"));
                dto.setChenhLech(rs.getBigDecimal("CHENHLECH"));
                dto.setLyDoChenhLech(rs.getString("LYDOCHENHLECH"));
                result.add(dto);
            }
        } catch (SQLException e) {
            handleException("layLichSuCa", e);
            throw new RuntimeException("Loi he thong khi lay lich su ca: " + e.getMessage(), e);
        }
        return result;
    }
}
