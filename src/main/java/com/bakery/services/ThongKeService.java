package com.bakery.services;

import com.bakery.model.dao.ThongKeDAO;
import java.time.LocalDate;
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

    public double getDoanhThu(String loaiThoiGian, int giaTri) {
        return thongKeDAO.getDoanhThu(loaiThoiGian, giaTri);
    }

    public Map<String, Double> getDoanhThuTheoDanhMuc(String loai, int giaTri) {
        return thongKeDAO.getDoanhThuTheoDanhMuc(loai, giaTri);
    }

    public Map<String, Integer> getTop5BanChay() {
        return thongKeDAO.getTop5BanChay();
    }

    public Map<String, Double> getXuHuongDoanhThu(String loai, int giaTri) {
        return thongKeDAO.getXuHuongDoanhThu(loai, giaTri);
    }

    public List<String[]> getChiTietGiaoDich(String loai, int giaTri) {
        return thongKeDAO.getChiTietGiaoDich(loai, giaTri);
    }
}
