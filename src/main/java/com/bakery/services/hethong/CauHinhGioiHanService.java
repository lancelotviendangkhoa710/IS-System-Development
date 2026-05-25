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

    /**
     * Kiểm tra năng lực sản xuất còn đủ để nhận thêm đơn trong ngày chỉ định.
     * Gọi FUNC_DIEMKHADUNG để lấy số slot còn trống (fail-fast trước khi ghi DB).
     *
     * @param ngayNhan    ngày khách hẹn nhận bánh
     * @param soBanhDatThem số bánh tùy chỉnh trong đơn mới
     * @throws Exception nếu không đủ năng lực — thông báo tiếng Việt thân thiện
     */
    public void kiemTraNangLuc(LocalDate ngayNhan, int soBanhDatThem) throws Exception {
        if (ngayNhan == null) return; // defensive — để DB xử lý
        int slotsConTrong = dao.laySlotsConTrong(ngayNhan);
        if (slotsConTrong <= 0) {
            throw new Exception("Xưởng bánh đã kín đơn vào ngày "
                    + ngayNhan.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + ". Vui lòng chọn ngày khác hoặc giảm số lượng bánh tùy chỉnh.");
        }
        if (soBanhDatThem > slotsConTrong) {
            throw new Exception("Xưởng bánh chỉ còn " + slotsConTrong + " suất vào ngày "
                    + ngayNhan.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + " (bạn đặt " + soBanhDatThem + " bánh). "
                    + "Vui lòng chọn ngày khác hoặc giảm số lượng.");
        }
    }
}

