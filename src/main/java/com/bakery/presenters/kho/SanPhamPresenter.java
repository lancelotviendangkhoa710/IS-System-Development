package com.bakery.presenters.kho;

import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.services.kho.SanPhamService;
import com.bakery.views.interfaces.kho.ISanPhamView;

import java.util.List;
import java.util.Map;

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
            view.hienThiLoi("Lỗi tải dữ liệu ban đầu: " + e.getMessage());
        }
    }

    public void taiDanhSachSanPham() {
        try {
            List<SanPhamDTO> ds = sanPhamService.layDanhSachSanPhamQuanLy();
            view.hienThiDanhSachSanPham(ds);
        } catch (Exception e) {
            view.hienThiLoi("Lỗi tải danh sách sản phẩm: " + e.getMessage());
        }
    }

    public void onChonSanPham(SanPhamDTO sp) {
        if (sp != null) {
            view.hienThiChiTiet(sp);
        } else {
            view.lamMoiForm();
        }
    }

    public void themSanPham() {
        SanPhamDTO sp = view.layDuLieuTuForm();
        if (sp == null) return;
        
        sp.setMaNX(maNhanVien);
        try {
            int maMoi = sanPhamService.themSanPham(sp);
            view.hienThiThanhCong("Thêm sản phẩm thành công (Mã: " + maMoi + ")");
            view.lamMoiForm();
            taiDanhSachSanPham();
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
        if (sp == null) return;
        
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
