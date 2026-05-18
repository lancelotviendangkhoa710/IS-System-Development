package com.bakery.presenters.kho;

import com.bakery.model.dto.kho.CongThucDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.services.kho.CongThucService;
import com.bakery.services.kho.NguyenLieuService;
import com.bakery.utils.UserSession;
import com.bakery.views.interfaces.kho.ICongThucView;

import java.util.List;

/**
 * Presenter quản lý công thức nguyên liệu (BOM).
 * Điều phối giữa ICongThucView và CongThucService / NguyenLieuService.
 */
public class CongThucPresenter {

    private final ICongThucView    view;
    private final CongThucService  congThucService;
    private final NguyenLieuService nguyenLieuService;

    /** Mã sản phẩm đang được chọn để xem/sửa công thức. */
    private int maSPDangChon = -1;

    public CongThucPresenter(ICongThucView view) {
        this.view              = view;
        this.congThucService   = new CongThucService();
        this.nguyenLieuService = new NguyenLieuService();
    }

    /** Gọi khi Tab Công thức được hiển thị — load NL, SP, DVT, NCC. */
    public void khoiTao() {
        taiDanhSachNguyenLieu();
        taiDanhSachSanPham();
        taiDanhSachDonViTinh();
        taiDanhSachNhaCungCap();
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

    private void taiDanhSachDonViTinh() {
        try {
            view.napDanhSachDonViTinh(nguyenLieuService.layDanhSachDonViTinh());
        } catch (Exception e) {
            view.hienThiLoi("Lỗi tải đơn vị tính: " + e.getMessage());
        }
    }

    private void taiDanhSachNhaCungCap() {
        try {
            view.napDanhSachNhaCungCap(nguyenLieuService.layDanhSachNhaCungCap());
        } catch (Exception e) {
            view.hienThiLoi("Lỗi tải nhà cung cấp: " + e.getMessage());
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
            double tongGiaVon = congThucService.tinhGiaVonBOM(maSPDangChon);
            view.hienThiThanhCong(String.format("Giá vốn BOM: %,.0f đ", tongGiaVon));
        } catch (Exception e) {
            view.hienThiLoi("Lỗi tải công thức: " + e.getMessage());
        }
    }

    /** Lưu (thêm/sửa) một dòng nguyên liệu có sẵn trong công thức. */
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

    /**
     * Thêm nguyên liệu HOÀN TOÀN MỚI (chưa từng tồn tại) vào công thức.
     * Bước 1: Tạo nguyên liệu + nhập kho lần đầu qua NguyenLieuService.
     * Bước 2: Lưu định mức vào công thức qua CongThucService.
     */
    public void themNguyenLieuMoiVaoCongThuc(
            String tenNL, String xuatXu,
            int maDVT, int maNCC,
            double soLuong, double donGia,
            double dinhMuc) {

        if (maSPDangChon <= 0) {
            view.hienThiLoi("Vui lòng chọn sản phẩm trước khi thêm nguyên liệu.");
            return;
        }
        try {
            // Lấy mã nhân viên từ session (fallback = 1 nếu chưa đăng nhập)
            int maNV = 1;
            if (UserSession.getCurrentUser() != null) {
                maNV = UserSession.getCurrentUser().getMaNV();
            }

            // Bước 1: Tạo nguyên liệu mới + nhập kho lần đầu (atomic)
            int[] result = nguyenLieuService.themNguyenLieuVaNhapKho(
                    tenNL, xuatXu, 0, maDVT,
                    maNCC, maNV,
                    soLuong, donGia,
                    null, null);
            int maNLMoi = result[0];

            // Bước 2: Gắn nguyên liệu vừa tạo vào công thức với định mức đã nhập
            congThucService.luuCongThuc(maSPDangChon, maNLMoi, dinhMuc);

            view.hienThiThanhCong("Đã thêm nguyên liệu '" + tenNL + "' vào công thức thành công.");
            view.lamMoiForm();
            taiCongThuc();
            // Làm mới lại danh sách NL trong ComboBox sửa định mức
            taiDanhSachNguyenLieu();
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
