package com.bakery.services.kho;

import com.bakery.model.dao.kho.TheKhoDAO;
import com.bakery.model.dto.kho.TheKhoBienDongDTO;
import com.bakery.services.BaseService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service tra cứu thẻ kho nguyên liệu (UC44).
 * Không chứa SQL — wrap TheKhoDAO và trả kết quả cho Presenter.
 */
public class TheKhoService extends BaseService {

    private final TheKhoDAO dao;

    public TheKhoService() {
        this.dao = new TheKhoDAO();
    }

    /**
     * Lấy danh sách biến động nhập/xuất của 1 nguyên liệu.
     *
     * @param maNL    mã nguyên liệu (phải > 0)
     * @param tuNgay  từ ngày (null = không giới hạn)
     * @param denNgay đến ngày (null = không giới hạn)
     */
    public List<TheKhoBienDongDTO> layBienDong(int maNL, LocalDate tuNgay, LocalDate denNgay) {
        if (maNL <= 0) return new ArrayList<>();
        try {
            return dao.layBienDong(maNL, tuNgay, denNgay);
        } catch (Exception e) {
            System.err.println("[TheKhoService] layBienDong: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Tính tổng hợp thẻ kho: tồn đầu kỳ, nhập kỳ, xuất kỳ, tồn cuối kỳ.
     *
     * @param maNL    mã nguyên liệu
     * @param tuNgay  từ ngày (null = từ đầu)
     * @param denNgay đến ngày (null = đến hiện tại)
     * @return double[4] = {tonDauKy, nhapKy, xuatKy, tonCuoiKy}
     */
    public double[] layTongHop(int maNL, LocalDate tuNgay, LocalDate denNgay) {
        if (maNL <= 0) return new double[]{0, 0, 0, 0};
        try {
            return dao.layTongHop(maNL, tuNgay, denNgay);
        } catch (Exception e) {
            System.err.println("[TheKhoService] layTongHop: " + e.getMessage());
            return new double[]{0, 0, 0, 0};
        }
    }
}
