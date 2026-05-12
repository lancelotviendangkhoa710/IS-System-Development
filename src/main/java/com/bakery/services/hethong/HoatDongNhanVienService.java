package com.bakery.services.hethong;

import com.bakery.model.dao.hethong.HoatDongNhanVienDAO;
import com.bakery.model.dto.hethong.HoatDongNhanVienDTO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/** Service lịch sử hệ thống — delegate xuống DAO, không có logic nghiệp vụ. */
public class HoatDongNhanVienService {

    private final HoatDongNhanVienDAO dao = new HoatDongNhanVienDAO();

    /**
     * Lấy danh sách hoạt động nhân viên có lọc.
     *
     * @param nhom    Nhóm module (null = tất cả)
     * @param tuKhoa  Từ khóa tìm kiếm (null = bỏ qua)
     * @param tuNgay  Từ ngày (null = bỏ qua)
     * @param denNgay Đến ngày (null = bỏ qua)
     */
    public List<HoatDongNhanVienDTO> layDanhSach(String nhom, String tuKhoa,
            LocalDate tuNgay, LocalDate denNgay) {
        try {
            List<HoatDongNhanVienDTO> ds = dao.layDanhSach(nhom, tuKhoa, tuNgay, denNgay);
            return ds != null ? ds : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("[HoatDongNhanVienService.layDanhSach] " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
