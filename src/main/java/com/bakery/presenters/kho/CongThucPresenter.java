package com.bakery.presenters.kho;

import com.bakery.model.dto.kho.CongThucDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.services.kho.CongThucService;
import com.bakery.views.interfaces.kho.ICongThucView;

import java.util.List;

/**
 * Presenter quản lý công thức nguyên liệu (BOM).
 * Điều phối giữa ICongThucView và CongThucService.
 */
public class CongThucPresenter {

    private final ICongThucView view;
    private final CongThucService congThucService;

    /** Mã sản phẩm đang được chọn để xem/sửa công thức. */
    private int maSPDangChon = -1;

    public CongThucPresenter(ICongThucView view) {
        this.view = view;
        this.congThucService = new CongThucService();
    }

    /** Gọi khi Tab Công thức được hiển thị — load danh sách nguyên liệu và sản phẩm. */
    public void khoiTao() {
        taiDanhSachNguyenLieu();
        taiDanhSachSanPham();
    }

    private void taiDanhSachNguyenLieu() {
        try {
            List<NguyenLieuDTO> dsNL = congThucService.layDanhSachNguyenLieu();
            view.hienThiDanhSachNguyenLieu(dsNL);
        } catch (Exception e) {
            view.hienThiLoi("Lỗi tải danh sách nguyên liệu: " + e.getMessage());
        }
    }

    private void taiDanhSachSanPham() {
        try {
            List<SanPhamDTO> dsSP = congThucService.layDanhSachSanPham();
            view.hienThiDanhSachSanPham(dsSP);
        } catch (Exception e) {
            view.hienThiLoi("Lỗi tải danh sách sản phẩm: " + e.getMessage());
        }
    }

    /**
     * Được gọi từ Tab Sản phẩm khi user chọn một sản phẩm.
     * Tự động load công thức của sản phẩm đó.
     */
    public void chonSanPham(int maSP) {
        this.maSPDangChon = maSP;
        if (maSP <= 0) {
            view.hienThiDanhSachCongThuc(List.of());
            return;
        }
        taiCongThuc();
    }

    public void taiCongThuc() {
        if (maSPDangChon <= 0) {
            view.hienThiLoi("Vui lòng chọn sản phẩm trước.");
            return;
        }
        try {
            List<CongThucDTO> ds = congThucService.layCongThucTheoSP(maSPDangChon);
            view.hienThiDanhSachCongThuc(ds);
            // Tính và hiển thị tổng giá vốn
            double tongGiaVon = congThucService.tinhGiaVonBOM(maSPDangChon);
            view.hienThiThanhCong(String.format("Giá vốn BOM: %,.0f đ", tongGiaVon));
        } catch (Exception e) {
            view.hienThiLoi("Lỗi tải công thức: " + e.getMessage());
        }
    }

    /** Lưu (thêm/sửa) một dòng nguyên liệu trong công thức. */
    public void luuCongThuc(int maNL, double soLuong) {
        try {
            congThucService.luuCongThuc(maSPDangChon, maNL, soLuong);
            view.hienThiThanhCong("Đã lưu công thức thành công.");
            view.lamMoiForm();
            taiCongThuc();
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
        }
    }

    /** Xóa dòng công thức đang chọn. */
    public void xoaCongThuc() {
        CongThucDTO selected = view.getSelectedCongThuc();
        if (selected == null) {
            view.hienThiLoi("Vui lòng chọn một dòng nguyên liệu để xóa.");
            return;
        }
        try {
            congThucService.xoaCongThuc(selected.getMaSP(), selected.getMaNL());
            view.hienThiThanhCong("Đã xóa nguyên liệu khỏi công thức.");
            view.lamMoiForm();
            taiCongThuc();
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
        }
    }

    /** Điền thông tin dòng đang chọn vào form để sửa định mức. */
    public void chonCongThuc(CongThucDTO ct) {
        if (ct != null) view.hienThiChiTiet(ct);
    }

    public int getMaSPDangChon() { return maSPDangChon; }
}
