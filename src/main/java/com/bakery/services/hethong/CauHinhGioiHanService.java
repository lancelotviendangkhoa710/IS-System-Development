package com.bakery.services.hethong;

import com.bakery.model.dao.hethong.CauHinhGioiHanDAO;
import com.bakery.model.dto.hethong.CauHinhGioiHanDTO;

import java.time.LocalDate;
import java.util.List;

/** Tầng nghiệp vụ cho chức năng cấu hình giới hạn nhận đơn. */
public class CauHinhGioiHanService {

    private final CauHinhGioiHanDAO dao;

    public CauHinhGioiHanService() {
        this.dao = new CauHinhGioiHanDAO();
    }

    /** Trả về danh sách cấu hình hiện tại (60 bản ghi gần nhất). */
    public List<CauHinhGioiHanDTO> layDanhSachCauHinh() throws Exception {
        return dao.layDanhSachCauHinh();
    }

    /**
     * Lưu giới hạn cho ngày sản xuất.
     * @param ngaySanXuat ngày áp dụng giới hạn (không được là quá khứ)
     * @param gioiHanSoBanh số bánh tối đa (phải > 0)
     */
    public void luuCauHinh(LocalDate ngaySanXuat, int gioiHanSoBanh) throws Exception {
        if (ngaySanXuat == null) {
            throw new IllegalArgumentException("Ngày sản xuất không được để trống.");
        }
        if (gioiHanSoBanh <= 0) {
            throw new IllegalArgumentException("Giới hạn phải là số nguyên dương.");
        }
        dao.luuCauHinh(ngaySanXuat, gioiHanSoBanh);
    }
}
