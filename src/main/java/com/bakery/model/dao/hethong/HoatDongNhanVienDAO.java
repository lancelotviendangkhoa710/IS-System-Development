package com.bakery.model.dao.hethong;

import com.bakery.model.dao.BaseDAO;
import com.bakery.model.dto.hethong.HoatDongNhanVienDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** DAO truy vấn VW_HoatDongNhanVien — Employee Activity Audit Log. */
public class HoatDongNhanVienDAO extends BaseDAO {

    /**
     * Lấy danh sách hoạt động nhân viên có lọc theo nhóm, từ khóa và khoảng thời gian.
     *
     * @param nhom    Nhóm module ('DON_HANG' | 'KHACH_HANG' | 'KHO' | 'SAN_PHAM' | null = tất cả)
     * @param tuKhoa  Tìm theo tên NV hoặc mô tả hành động (nullable)
     * @param tuNgay  Từ ngày (nullable)
     * @param denNgay Đến ngày (nullable)
     * @return Danh sách hoạt động, sắp xếp mới nhất trước
     */
    public List<HoatDongNhanVienDTO> layDanhSach(String nhom, String tuKhoa,
                                                  LocalDate tuNgay, LocalDate denNgay) throws Exception {
        List<HoatDongNhanVienDTO> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT MAHOATDONG, MANV, TENNHANVIEN, CHUCVU, NHOM, HANHDONG, ENTITY_ID, THOIGIAN " +
            "FROM VW_HoatDongNhanVien WHERE 1=1"
        );
        if (nhom != null && !nhom.isBlank())   sql.append(" AND NHOM = ?");
        if (tuKhoa != null && !tuKhoa.isBlank()) sql.append(" AND (UPPER(TENNHANVIEN) LIKE ? OR UPPER(HANHDONG) LIKE ?)");
        if (tuNgay != null)   sql.append(" AND THOIGIAN >= ?");
        if (denNgay != null)  sql.append(" AND THOIGIAN < ?");
        sql.append(" ORDER BY THOIGIAN DESC FETCH FIRST 500 ROWS ONLY");

        try (Connection con = moKetNoi();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            if (nhom != null && !nhom.isBlank())     ps.setString(idx++, nhom);
            if (tuKhoa != null && !tuKhoa.isBlank()) {
                String pattern = "%" + tuKhoa.toUpperCase() + "%";
                ps.setString(idx++, pattern);
                ps.setString(idx++, pattern);
            }
            if (tuNgay != null)  ps.setDate(idx++, Date.valueOf(tuNgay));
            if (denNgay != null) ps.setDate(idx++, Date.valueOf(denNgay.plusDays(1)));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HoatDongNhanVienDTO dto = new HoatDongNhanVienDTO();
                    dto.setMaHoatDong(rs.getInt("MAHOATDONG"));
                    dto.setMaNV(rs.getInt("MANV"));
                    dto.setTenNhanVien(rs.getString("TENNHANVIEN"));
                    dto.setChucVu(rs.getString("CHUCVU"));
                    dto.setNhom(rs.getString("NHOM"));
                    dto.setHanhDong(rs.getString("HANHDONG"));
                    int eid = rs.getInt("ENTITY_ID");
                    dto.setEntityId(rs.wasNull() ? null : eid);
                    Timestamp ts = rs.getTimestamp("THOIGIAN");
                    if (ts != null) dto.setThoiGian(ts.toLocalDateTime());
                    ds.add(dto);
                }
            }
        } catch (Exception e) {
            handleException("layDanhSach", e);
        }
        return ds;
    }
}
