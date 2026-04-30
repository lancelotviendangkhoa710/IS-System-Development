package com.bakery.services;

import com.bakery.model.dao.BangDieuKhienDAO;
import com.bakery.model.dto.BangDieuKhienKPIDTO;
import com.bakery.model.dto.TopSanPhamDTO;


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
