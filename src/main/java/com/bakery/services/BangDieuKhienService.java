package com.bakery.services;

import com.bakery.model.dao.BangDieuKhienDAO;
import com.bakery.model.dto.BangDieuKhienKPIDTO;
import com.bakery.model.dto.TopSanPhamDTO;

import java.sql.SQLException;
import java.util.List;

public class BangDieuKhienService {

    private final BangDieuKhienDAO bangDieuKhienDAO = new BangDieuKhienDAO();

    public BangDieuKhienKPIDTO layKPI() throws SQLException {
        return bangDieuKhienDAO.layKPI();
    }

    public List<TopSanPhamDTO> layTop5SanPhamThang() throws SQLException {
        return bangDieuKhienDAO.layTop5SanPhamThang();
    }
}
