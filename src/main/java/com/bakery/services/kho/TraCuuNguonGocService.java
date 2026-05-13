package com.bakery.services.kho;

import com.bakery.model.dao.kho.TraCuuNguonGocDAO;
import com.bakery.model.dto.kho.MeSanXuatDTO;
import com.bakery.model.dto.kho.TraCuuNguonGocDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service tra cứu nguồn gốc nguyên liệu.
 * Không chứa SQL — gọi DAO và trả kết quả cho Presenter.
 */
public class TraCuuNguonGocService {

    private final TraCuuNguonGocDAO dao;

    public TraCuuNguonGocService() {
        this.dao = new TraCuuNguonGocDAO();
    }

    /**
     * Lấy danh sách mẻ sản xuất, lọc theo từ khóa tên SP và khoảng ngày.
     *
     * @param tuKhoa  tên sản phẩm (null/rỗng = tất cả)
     * @param tuNgay  từ ngày (null = không giới hạn)
     * @param denNgay đến ngày (null = không giới hạn)
     */
    public List<MeSanXuatDTO> layDanhSachMe(String tuKhoa, LocalDate tuNgay, LocalDate denNgay) {
        try {
            return dao.layDanhSachMe(tuKhoa, tuNgay, denNgay);
        } catch (Exception e) {
            System.err.println("[TraCuuNguonGocService] layDanhSachMe: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Lấy toàn bộ chi tiết nguồn gốc nguyên liệu của 1 mẻ sản xuất.
     *
     * @param maMe mã mẻ sản xuất
     */
    public List<TraCuuNguonGocDTO> layChiTietNguonGoc(int maMe) {
        try {
            return dao.layChiTietNguonGoc(maMe);
        } catch (Exception e) {
            System.err.println("[TraCuuNguonGocService] layChiTietNguonGoc: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
