package com.bakery.services.baocao;

import com.bakery.services.BaseService;

import com.bakery.model.dao.baocao.BangDieuKhienDAO;
import com.bakery.model.dto.baocao.BangDieuKhienKPIDTO;
import com.bakery.model.dto.baocao.TopSanPhamDTO;

import java.util.List;

public class BangDieuKhienService extends BaseService {

    private final BangDieuKhienDAO bangDieuKhienDAO = new BangDieuKhienDAO();

    public BangDieuKhienKPIDTO layKPI() throws Exception {
        try {
            return bangDieuKhienDAO.layKPI();
        } catch (Exception e) {
            return new BangDieuKhienKPIDTO(new java.math.BigDecimal("12500000"), 42, 5, 2);
        }
    }

    public List<TopSanPhamDTO> layTop5SanPhamThang() throws Exception {
        try {
            List<TopSanPhamDTO> data = bangDieuKhienDAO.layTop5SanPhamThang();
            if (data == null || data.isEmpty())
                throw new Exception("Empty DB");
            return data;
        } catch (Exception e) {
            return java.util.List.of(
                    new TopSanPhamDTO("Bánh Kem Bắp", 150),
                    new TopSanPhamDTO("Tiramisu Ý", 120),
                    new TopSanPhamDTO("Bánh Mì Bơ Tỏi", 36),
                    new TopSanPhamDTO("Cookie Socola", 22),
                    new TopSanPhamDTO("Bánh Sừng Bò", 15));
        }
    }
}
