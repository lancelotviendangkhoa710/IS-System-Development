package com.bakery.services.baocao;

import com.bakery.model.dao.baocao.ThongKeDAO;
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
        Map<String, Double> res = thongKeDAO.getDoanhThuTheoDanhMuc(loai, giaTri);
        return res != null ? res : Map.of();
    }

    public Map<String, Integer> getTop5BanChay() throws Exception {
        Map<String, Integer> res = thongKeDAO.getTop5BanChay();
        return res != null ? res : Map.of();
    }

    public Map<String, Double> getXuHuongDoanhThu(String loai, String giaTri) throws Exception {
        Map<String, Double> res = thongKeDAO.getXuHuongDoanhThu(loai, giaTri);
        return res != null ? res : Map.of();
    }

    public List<String[]> getChiTietGiaoDich(String loai, String giaTri) throws Exception {
        List<String[]> res = thongKeDAO.getChiTietGiaoDich(loai, giaTri);
        return res != null ? res : List.of();
    }
}
