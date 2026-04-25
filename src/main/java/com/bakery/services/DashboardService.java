package com.bakery.services;

import com.bakery.model.dao.DashboardDAO;
import com.bakery.model.dto.DashboardKPIDTO;
import com.bakery.model.dto.TopSanPhamDTO;

import java.sql.SQLException;
import java.util.List;

public class DashboardService {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public DashboardKPIDTO layKPI() throws SQLException {
        return dashboardDAO.layKPI();
    }

    public List<TopSanPhamDTO> layTop5SanPhamThang() throws SQLException {
        return dashboardDAO.layTop5SanPhamThang();
    }
}
