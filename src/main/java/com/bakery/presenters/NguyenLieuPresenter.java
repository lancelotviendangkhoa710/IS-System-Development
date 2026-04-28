package com.bakery.presenters;

import com.bakery.model.dto.DonViTinhDTO;
import com.bakery.model.dto.NguyenLieuDTO;
import com.bakery.services.NguyenLieuService;
import com.bakery.views.interfaces.INguyenLieuView;

import java.util.Collections;
import java.util.List;

/**
 * Presenter điều phối màn hình Quản lý Nguyên liệu.
 * Tuân thủ MVP: không chứa logic nghiệp vụ, không biết JavaFX.
 * Chỉ đóng vai Orchestrator: View → Presenter → Service → Presenter → View.
 */
public class NguyenLieuPresenter {

    private final INguyenLieuView   view;
    private final NguyenLieuService nguyenLieuService;

    /**
     * maNvHienTai dùng làm tham số P_MANV khi gọi Procedure Thêm/Xóa.
     * Thay bằng SessionManager.getInstance().getCurrentUser().getMaNV()
     * khi AuthService hoàn thiện.
     */
    private final int maNvHienTai;

    public NguyenLieuPresenter(INguyenLieuView view, int maNvHienTai) {
        this.view              = view;
        this.nguyenLieuService = new NguyenLieuService();
        this.maNvHienTai       = maNvHienTai;
    }

    /** Constructor injection — dùng cho unit test. */
    public NguyenLieuPresenter(INguyenLieuView view,
                                NguyenLieuService nguyenLieuService,
                                int maNvHienTai) {
        this.view              = view;
        this.nguyenLieuService = nguyenLieuService;
        this.maNvHienTai       = maNvHienTai;
    }

    // =========================================================
    // 1. KHỞI TẠO MÀN HÌNH
    // =========================================================

    /** Gọi khi View initialize(): nạp ComboBox DVT rồi tải bảng. */
    public void khoiTao() {
        try {
            List<DonViTinhDTO> dsDVT = nguyenLieuService.layDanhSachDonViTinh();
            view.napDanhSachDonViTinh(dsDVT);
        } catch (Exception e) {
            view.hienThiLoi("Không thể tải danh sách đơn vị tính: " + e.getMessage());
        }
        taiDanhSach();
    }

    // =========================================================
    // 2. TẢI DANH SÁCH
    // =========================================================

    /** Tải lại toàn bộ bảng nguyên liệu. */
    public void taiDanhSach() {
        try {
            List<NguyenLieuDTO> ds = nguyenLieuService.layDanhSachNguyenLieu();
            view.hienThiDanhSach(ds);
        } catch (Exception e) {
            view.hienThiLoi("Không thể tải danh sách nguyên liệu: " + e.getMessage());
        }
    }

    // =========================================================
    // 3. THÊM NGUYÊN LIỆU
    // =========================================================

    public void themNguyenLieu() {
        DonViTinhDTO dvt = view.getDonViTinhSelected();
        int maDVT = dvt != null ? dvt.getMaDVT() : 0;
        try {
            int maMoi = nguyenLieuService.themNguyenLieu(
                    view.getTenNLInput(),
                    view.getXuatXuInput(),
                    view.getMucTonAnToanInput(),
                    maDVT,
                    maNvHienTai);
            view.hienThiThanhCong("Thêm nguyên liệu thành công (Mã: " + maMoi + ").");
            view.lamMoiForm();
            taiDanhSach();
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
        }
    }

    // =========================================================
    // 4. SỬA NGUYÊN LIỆU
    // =========================================================

    public void suaNguyenLieu() {
        NguyenLieuDTO selected = view.getSelectedNguyenLieu();
        if (selected == null) {
            view.hienThiLoi("Vui lòng chọn nguyên liệu cần sửa.");
            return;
        }
        DonViTinhDTO dvt = view.getDonViTinhSelected();
        int maDVT = dvt != null ? dvt.getMaDVT() : 0;
        try {
            nguyenLieuService.suaNguyenLieu(
                    selected.getMaNL(),
                    view.getTenNLInput(),
                    view.getXuatXuInput(),
                    view.getMucTonAnToanInput(),
                    maDVT);
            view.hienThiThanhCong("Cập nhật nguyên liệu thành công.");
            view.lamMoiForm();
            taiDanhSach();
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
        }
    }

    // =========================================================
    // 5. XÓA NGUYÊN LIỆU
    // =========================================================

    public void xoaNguyenLieu() {
        NguyenLieuDTO selected = view.getSelectedNguyenLieu();
        if (selected == null) {
            view.hienThiLoi("Vui lòng chọn nguyên liệu cần xóa.");
            return;
        }
        try {
            nguyenLieuService.xoaNguyenLieu(selected.getMaNL(), maNvHienTai);
            view.hienThiThanhCong("Xóa nguyên liệu thành công.");
            view.lamMoiForm();
            taiDanhSach();
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
        }
    }

    // =========================================================
    // 6. TÌM KIẾM
    // =========================================================

    public void timKiem() {
        String keyword = view.getTuKhoaTimKiemInput();
        try {
            List<NguyenLieuDTO> ds = nguyenLieuService.timKiemNguyenLieu(keyword);
            view.hienThiDanhSach(ds);
            view.hienThiThanhCong("Tìm thấy " + ds.size() + " nguyên liệu.");
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
            view.hienThiDanhSach(Collections.emptyList());
        }
    }

    // =========================================================
    // 7. CHỌN HÀNG TABLEVIEW
    // =========================================================

    public void onChonNguyenLieu(NguyenLieuDTO selected) {
        view.hienThiChiTiet(selected);
    }
}
