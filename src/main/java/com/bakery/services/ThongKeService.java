package com.bakery.services;

import com.bakery.model.dao.ThongKeDAO;
import java.util.List;
import java.util.Map;

/**
 * Service xử lý logic thống kê báo cáo (MVP SRP).
 */
public class ThongKeService {

    private final ThongKeDAO thongKeDAO;

    public ThongKeService() {
        this.thongKeDAO = new ThongKeDAO();
    }

    public double getDoanhThu(String loaiThoiGian, String giaTri) throws Exception {
        return thongKeDAO.getDoanhThu(loaiThoiGian, giaTri);
    }

    public Map<String, Double> getDoanhThuTheoDanhMuc(String loai, String giaTri) throws Exception {
        return thongKeDAO.getDoanhThuTheoDanhMuc(loai, giaTri);
    }

    public Map<String, Integer> getTop5BanChay() throws Exception {
        return thongKeDAO.getTop5BanChay();
    }

    public Map<String, Double> getXuHuongDoanhThu(String loai, String giaTri) throws Exception {
        return thongKeDAO.getXuHuongDoanhThu(loai, giaTri);
    }

    public List<String[]> getChiTietGiaoDich(String loai, String giaTri) throws Exception {
        return thongKeDAO.getChiTietGiaoDich(loai, giaTri);
    }
}
