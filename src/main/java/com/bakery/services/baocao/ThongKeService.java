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

    /** UC50 — Tính giá vốn thực từ DB theo kỳ. */
    public double getGiaVon(String loai, String giaTri) throws Exception {
        return thongKeDAO.getGiaVon(loai, giaTri);
    }

    /** UC52 — Báo cáo tồn kho: đầu kỳ, nhập, xuất, cuối kỳ theo nguyên liệu. */
    public List<String[]> getBaoCaoTonKho(java.time.LocalDate tuNgay, java.time.LocalDate denNgay)
            throws Exception {
        List<String[]> res = thongKeDAO.getBaoCaoTonKho(tuNgay, denNgay);
        return res != null ? res : List.of();
    }

    /** UC52 / UC43 — Thống kê tồn kho NL theo trạng thái: HET_HANG, SAP_HET, DU_HANG. */
    public Map<String, Long> getTonKhoTongHop() throws Exception {
        Map<String, Long> res = thongKeDAO.getTonKhoTongHop();
        return res != null ? res : Map.of("HET_HANG", 0L, "SAP_HET", 0L, "DU_HANG", 0L);
    }

    /** UC43 — Danh sách NL dưới mức tồn an toàn (tối đa 10 items). */
    public List<String[]> getNguyenLieuSapHet() throws Exception {
        List<String[]> res = thongKeDAO.getNguyenLieuSapHet();
        return res != null ? res : List.of();
    }
}

