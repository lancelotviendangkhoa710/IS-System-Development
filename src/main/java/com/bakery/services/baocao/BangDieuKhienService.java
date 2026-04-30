package com.bakery.services.baocao;
import com.bakery.services.BaseService;

import com.bakery.model.dao.baocao.BangDieuKhienDAO;
import com.bakery.model.dto.baocao.BangDieuKhienKPIDTO;
import com.bakery.model.dto.baocao.TopSanPhamDTO;


import java.util.List;

public class BangDieuKhienService extends BaseService {
    
    private final BangDieuKhienDAO bangDieuKhienDAO = new BangDieuKhienDAO();

    public BangDieuKhienKPIDTO layKPI() throws Exception {
        return bangDieuKhienDAO.layKPI();
    }

    public List<TopSanPhamDTO> layTop5SanPhamThang() throws Exception {
        return bangDieuKhienDAO.layTop5SanPhamThang();
    }
}
