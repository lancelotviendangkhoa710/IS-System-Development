package com.bakery.presenters.kho;
import com.bakery.presenters.BasePresenter;

import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.services.kho.NguyenLieuService;
import com.bakery.views.interfaces.kho.INguyenLieuView;



/**
 * Presenter điều phối màn hình Quản lý Nguyên liệu.
 * Tuân thủ MVP: không chứa logic nghiệp vụ, không biết JavaFX.
 * Chỉ đóng vai Orchestrator: View → Presenter → Service → Presenter → View.
 */
public class NguyenLieuPresenter extends BasePresenter<INguyenLieuView> {

    private final NguyenLieuService nguyenLieuService;

    /**
     * maNvHienTai dùng làm tham số P_MANV khi gọi Procedure Thêm/Xóa.
     * Thay bằng SessionManager.getInstance().getCurrentUser().getMaNV()
     * khi AuthService hoàn thiện.
     */
    private final int maNvHienTai;

    public NguyenLieuPresenter(INguyenLieuView view, int maNvHienTai) {
        super(view);
        this.nguyenLieuService = new NguyenLieuService();
        this.maNvHienTai       = maNvHienTai;
    }

    /** Constructor injection — dùng cho unit test. */
    public NguyenLieuPresenter(INguyenLieuView view,
                                NguyenLieuService nguyenLieuService,
                                int maNvHienTai) {
        super(view);
        this.nguyenLieuService = nguyenLieuService;
        this.maNvHienTai       = maNvHienTai;
    }

    // =========================================================
    // 1. KHỞI TẠO MÀN HÌNH
    // =========================================================

    /** Gọi khi View initialize(): nạp ComboBox DVT rồi tải bảng. */
    public void khoiTao() {
        runTask(
            () -> nguyenLieuService.layDanhSachDonViTinh(),
            dsDVT -> {
                view.napDanhSachDonViTinh(dsDVT);
                taiDanhSach();
            }
        );
    }

    // =========================================================
    // 2. TẢI DANH SÁCH
    // =========================================================

    /** Tải lại toàn bộ bảng nguyên liệu. */
    public void taiDanhSach() {
        runTask(
            () -> nguyenLieuService.layDanhSachNguyenLieu(),
            ds -> view.hienThiDanhSach(ds)
        );
    }

    // =========================================================
    // 3. THÊM NGUYÊN LIỆU
    // =========================================================

    public void themNguyenLieu() {
        DonViTinhDTO dvt = view.getDonViTinhSelected();
        int maDVT = dvt != null ? dvt.getMaDVT() : 0;
        
        runTask(
            () -> nguyenLieuService.themNguyenLieu(
                    view.getTenNLInput(),
                    view.getXuatXuInput(),
                    view.getMucTonAnToanInput(),
                    maDVT,
                    maNvHienTai),
            maMoi -> {
                view.hienThiThanhCong("Thêm nguyên liệu thành công (Mã: " + maMoi + ").");
                view.lamMoiForm();
                taiDanhSach();
            }
        );
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
        
        runTask(
            () -> nguyenLieuService.suaNguyenLieu(
                    selected.getMaNL(),
                    view.getTenNLInput(),
                    view.getXuatXuInput(),
                    view.getMucTonAnToanInput(),
                    maDVT),
            () -> {
                view.hienThiThanhCong("Cập nhật nguyên liệu thành công.");
                view.lamMoiForm();
                taiDanhSach();
            }
        );
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
        
        runTask(
            () -> nguyenLieuService.xoaNguyenLieu(selected.getMaNL(), maNvHienTai),
            () -> {
                view.hienThiThanhCong("Xóa nguyên liệu thành công.");
                view.lamMoiForm();
                taiDanhSach();
            }
        );
    }

    // =========================================================
    // 6. TÌM KIẾM
    // =========================================================

    public void timKiem() {
        String keyword = view.getTuKhoaTimKiemInput();
        runTask(
            () -> nguyenLieuService.timKiemNguyenLieu(keyword),
            ds -> {
                view.hienThiDanhSach(ds);
                view.hienThiThanhCong("Tìm thấy " + ds.size() + " nguyên liệu.");
            }
        );
    }

    // =========================================================
    // 7. CHỌN HÀNG TABLEVIEW
    // =========================================================

    public void onChonNguyenLieu(NguyenLieuDTO selected) {
        view.hienThiChiTiet(selected);
    }
}
