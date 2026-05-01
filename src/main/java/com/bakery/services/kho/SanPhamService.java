package com.bakery.services.kho;
import com.bakery.model.dto.banhang.YeuCauChiTietDonHangDTO;
import com.bakery.services.BaseService;

import com.bakery.model.dao.kho.DanhMucSPDAO;
import com.bakery.model.dao.kho.SanPhamDAO;
import com.bakery.model.dto.kho.DanhMucSPDTO;
import com.bakery.model.dto.kho.SanPhamDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service duy nhất chịu trách nhiệm về Sản phẩm và Danh mục
 * phục vụ màn hình POS bán hàng.
 * (SRP – Single Responsibility Principle)
 */
public class SanPhamService extends BaseService {

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
    public List<SanPhamDTO> layDanhSachSanPhamPOS() throws Exception {
        return sanPhamDAO.layTatCaSanPhamDeBan();
    }

    /**
     * Lấy map (MaDM → TenDM) của tất cả danh mục đang hoạt động.
     * Dùng để nhóm sản phẩm trên giao diện POS.
     */
    public Map<Integer, String> layMapDanhMucSanPham() throws Exception {
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
    public List<String> kiemTraTonKhoGioHang(List<YeuCauChiTietDonHangDTO> gioHang) throws Exception {
        List<String> dsThieu = new java.util.ArrayList<>();
        for (YeuCauChiTietDonHangDTO item : gioHang) {
            if (item.isCustom()) {
                continue; // Bánh tùy chỉnh – sản xuất theo đơn, không kiểm tồn
            }
            double tonKho = sanPhamDAO.laySoLuongTon(item.getMaSP());
            if (tonKho < item.getSoLuong()) {
                // Tìm tên SP để thông báo cho nhân viên
                String tenSP = "SP #" + item.getMaSP();
                for (SanPhamDTO sp : layDanhSachSanPhamPOS()) {
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
            Integer maNhan, Integer maTrangTri) throws Exception {
        return sanPhamDAO.tinhGiaBanhTuyChinh(maSP, maKC, maCot, maNhan, maTrangTri);
    }

    /** Lấy danh sách sản phẩm (không lọc số lượng) cho màn hình Quản lý */
    public List<SanPhamDTO> layDanhSachSanPhamQuanLy() throws Exception {
        return sanPhamDAO.layTatCaSanPhamQuanLy();
    }

    public int themSanPham(SanPhamDTO sp) throws Exception {
        validateSanPham(sp);
        int maSPMoi = sanPhamDAO.themSanPham(sp);
        if (maSPMoi <= 0) {
            throw new Exception("Thêm sản phẩm thất bại do lỗi CSDL.");
        }
        return maSPMoi;
    }

    public void capNhatSanPham(SanPhamDTO sp) throws Exception {
        validateSanPham(sp);
        boolean success = sanPhamDAO.capNhatSanPham(sp);
        if (!success) {
            throw new Exception("Cập nhật sản phẩm thất bại do lỗi CSDL.");
        }
    }

    public void xoaSanPham(int maSP, int maNX) throws Exception {
        if (maSP <= 0) {
            throw new Exception("Mã sản phẩm không hợp lệ.");
        }
        boolean success = sanPhamDAO.xoaSanPham(maSP, maNX);
        if (!success) {
            throw new Exception("Xóa sản phẩm thất bại do lỗi CSDL.");
        }
    }

    private void validateSanPham(SanPhamDTO sp) throws Exception {
        validateString(sp.getTenSP(), "Tên sản phẩm");
        if (sp.getMaDM() <= 0) {
            throw new Exception("Vui lòng chọn danh mục hợp lệ.");
        }
        if (sp.getGiaCoBan() < 0) {
            throw new Exception("Giá cơ bản không được âm.");
        }
        if (sp.getThoiGianBaoQuan() < 0) {
            throw new Exception("Thời gian bảo quản không được âm.");
        }
        if (sp.getThoiGianChuanBi() < 0) {
            throw new Exception("Thời gian chuẩn bị không được âm.");
        }
    }
}
