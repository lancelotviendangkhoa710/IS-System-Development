package com.bakery.services;

import com.bakery.model.dao.DanhMucSPDAO;
import com.bakery.model.dao.SanPhamDAO;
import com.bakery.model.dto.DanhMucSPDTO;
import com.bakery.model.dto.SanPhamDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service duy nhất chịu trách nhiệm về Sản phẩm và Danh mục
 * phục vụ màn hình POS bán hàng.
 * (SRP – Single Responsibility Principle)
 */
public class SanPhamService {

    private final SanPhamDAO sanPhamDAO;
    private final DanhMucSPDAO danhMucSPDAO;

    public SanPhamService() {
        this.sanPhamDAO = new SanPhamDAO();
        this.danhMucSPDAO = new DanhMucSPDAO();
    }

    public SanPhamService(SanPhamDAO sanPhamDAO, DanhMucSPDAO danhMucSPDAO) {
        this.sanPhamDAO = sanPhamDAO;
        this.danhMucSPDAO = danhMucSPDAO;
    }

    /** Lấy tất cả sản phẩm đang được bán trên POS. */
    public List<SanPhamDTO> layDanhSachSanPhamPOS() {
        return sanPhamDAO.layTatCaSanPhamDeBan();
    }

    /**
     * Lấy map (MaDM → TenDM) của tất cả danh mục đang hoạt động.
     * Dùng để nhóm sản phẩm trên giao diện POS.
     */
    public Map<Integer, String> layMapDanhMucSanPham() {
        Map<Integer, String> mapDanhMuc = new LinkedHashMap<>();
        List<DanhMucSPDTO> dsDanhMuc = danhMucSPDAO.layTatCaDanhMucConHoatDong();
        for (DanhMucSPDTO dm : dsDanhMuc) {
            mapDanhMuc.put(dm.getMaDM(), dm.getTenDM());
        }
        return mapDanhMuc;
    }

    /**
     * Tính giá bánh tùy chỉnh dựa vào giá cơ bản + phụ phí
     * (kích cỡ, cốt, nhân, trang trí). Logic tính do DB đảm nhiệm.
     */
    public double tinhGiaBanhTuyChinh(int maSP, Integer maKC, Integer maCot,
            Integer maNhan, Integer maTrangTri) {
        return sanPhamDAO.tinhGiaBanhTuyChinh(maSP, maKC, maCot, maNhan, maTrangTri);
    }
}
