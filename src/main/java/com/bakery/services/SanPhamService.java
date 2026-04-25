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
     * Kiểm tra tồn kho realtime cho từng sản phẩm trong giỏ hàng.
     * Bỏ qua bánh tùy chỉnh (custom) vì là sản xuất theo yêu cầu.
     * @return Danh sách thông báo thiếu hàng (rỗng = đủ tồn)
     */
    public List<String> kiemTraTonKhoGioHang(List<com.bakery.model.dto.YeuCauChiTietDonHangDTO> gioHang) {
        List<String> dsThieu = new java.util.ArrayList<>();
        for (com.bakery.model.dto.YeuCauChiTietDonHangDTO item : gioHang) {
            if (item.isCustom()) {
                continue; // Bánh tùy chỉnh – sản xuất theo đơn, không kiểm tồn
            }
            double tonKho = sanPhamDAO.laySoLuongTon(item.getMaSP());
            if (tonKho < item.getSoLuong()) {
                // Tìm tên SP để thông báo cho nhân viên
                String tenSP = "SP #" + item.getMaSP();
                for (com.bakery.model.dto.SanPhamDTO sp : layDanhSachSanPhamPOS()) {
                    if (sp.getMaSP() == item.getMaSP()) {
                        tenSP = sp.getTenSP();
                        break;
                    }
                }
                dsThieu.add(tenSP + " (Yêu cầu: " + item.getSoLuong()
                        + ", Tồn kho: " + (int) tonKho + ")");
            }
        }
        return dsThieu;
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
