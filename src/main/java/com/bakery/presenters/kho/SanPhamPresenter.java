package com.bakery.presenters.kho;

import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.services.kho.SanPhamService;
import com.bakery.views.interfaces.kho.ISanPhamView;

import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

public class SanPhamPresenter {
    private final ISanPhamView view;
    private final SanPhamService sanPhamService;
    private final int maNhanVien;

    public SanPhamPresenter(ISanPhamView view, int maNhanVien) {
        this.view = view;
        this.sanPhamService = new SanPhamService();
        this.maNhanVien = maNhanVien;
    }

    public void taiDuLieuBanDau() {
        try {
            Map<Integer, String> danhMucMap = sanPhamService.layMapDanhMucSanPham();
            view.hienThiDanhSachDanhMuc(danhMucMap);
            taiDanhSachSanPham();
        } catch (Exception e) {
            view.hienThiLoi("Lỗi tải danh mục sản phẩm: " + e.getMessage());
            view.hienThiDanhSachDanhMuc(java.util.Map.of());
            taiDanhSachSanPham();
        }
    }

    /**
     * Query danh mục trực tiếp từ DB — dùng khi mở dialog Thêm/Sửa sản phẩm
     * để đảm bảo danh mục vừa thêm cũng xuất hiện trong ComboBox.
     */
    public Map<Integer, String> layDanhMucFresh() {
        try {
            Map<Integer, String> fresh = sanPhamService.layMapDanhMucSanPham();
            view.hienThiDanhSachDanhMuc(fresh); // cập nhật cache luôn
            return fresh;
        } catch (Exception e) {
            view.hienThiLoi("Lỗi tải danh mục: " + e.getMessage());
            return java.util.Map.of();
        }
    }

    public void taiDanhSachSanPham() {
        try {
            List<SanPhamDTO> ds = sanPhamService.layDanhSachSanPhamQuanLy();
            view.hienThiDanhSachSanPham(ds);
        } catch (Exception e) {
            view.hienThiLoi("Lỗi tải danh sách sản phẩm: " + e.getMessage());
            view.hienThiDanhSachSanPham(java.util.List.of());
        }
    }

    public void onChonSanPham(SanPhamDTO sp) {
        if (sp != null) {
            view.hienThiChiTiet(sp);
        } else {
            view.lamMoiForm();
        }
    }

    /**
     * Thêm sản phẩm từ DTO (nhận từ Dialog). Gọi callback onThemXong(maMoi) sau khi thành công
     * để View tự navigate sang Tab Công thức.
     *
     * @param sp       DTO từ ThemSanPhamDialog
     * @param onThemXong callback nhận maSP mới (int), null nếu không cần navigate
     */
    public void themSanPham(SanPhamDTO sp, IntConsumer onThemXong) {
        if (sp == null) return;
        sp.setMaNX(maNhanVien);
        try {
            int maMoi = sanPhamService.themSanPham(sp);
            view.hienThiThanhCong("Thêm sản phẩm '" + sp.getTenSP() + "' thành công (Mã: " + maMoi + ").");
            view.lamMoiForm();
            taiDanhSachSanPham();
            if (onThemXong != null) onThemXong.accept(maMoi);
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
        }
    }

    public void suaSanPham() {
        SanPhamDTO selected = view.getSelectedSanPham();
        if (selected == null) {
            view.hienThiLoi("Vui lòng chọn một sản phẩm để sửa.");
            return;
        }

        SanPhamDTO sp = view.layDuLieuTuForm();
        if (sp == null)
            return;

        sp.setMaSP(selected.getMaSP());
        try {
            sanPhamService.capNhatSanPham(sp);
            view.hienThiThanhCong("Cập nhật sản phẩm thành công.");
            view.lamMoiForm();
            taiDanhSachSanPham();
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
        }
    }

    public void xoaSanPham() {
        SanPhamDTO selected = view.getSelectedSanPham();
        if (selected == null) {
            view.hienThiLoi("Vui lòng chọn một sản phẩm để xóa.");
            return;
        }

        try {
            sanPhamService.xoaSanPham(selected.getMaSP(), maNhanVien);
            view.hienThiThanhCong("Xóa sản phẩm thành công.");
            view.lamMoiForm();
            taiDanhSachSanPham();
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
        }
    }
}
