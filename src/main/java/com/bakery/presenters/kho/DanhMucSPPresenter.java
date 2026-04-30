package com.bakery.presenters.kho;

import com.bakery.model.dto.kho.DanhMucSPDTO;
import com.bakery.services.kho.DanhMucSPService;
import com.bakery.views.interfaces.kho.IDanhMucSPView;

import java.util.List;

/**
 * Presenter duy nhất điều phối toàn bộ màn hình Quản lý Danh mục Sản phẩm.
 * Tuân thủ MVP (Single Responsibility Principle):
 *  - Không chứa logic nghiệp vụ (uỷ thác cho DanhMucSPService).
 *  - Không biết về JavaFX Node hay FXML (giao tiếp qua IDanhMucSPView).
 *  - Chỉ đóng vai Orchestrator: View → Presenter → Service → Presenter → View.
 */
public class DanhMucSPPresenter {

    private final IDanhMucSPView    view;
    private final DanhMucSPService  danhMucSPService;

    /**
     * ID nhân viên đang đăng nhập — dùng làm maNvXoa khi xóa mềm.
     * Sau khi AuthService/SessionManager hoàn thiện, thay bằng:
     *   SessionManager.getInstance().getCurrentUser().getMaNV()
     */
    private final int maNvHienTai;

    // =========================================================
    // KHỞI TẠO
    // =========================================================

    /**
     * Constructor production — View tự tạo Service.
     *
     * @param view         FXML Controller implement IDanhMucSPView
     * @param maNvHienTai  mã nhân viên đang đăng nhập
     */
    public DanhMucSPPresenter(IDanhMucSPView view, int maNvHienTai) {
        this.view            = view;
        this.danhMucSPService = new DanhMucSPService();
        this.maNvHienTai     = maNvHienTai;
    }

    /**
     * Constructor injection — dùng trong unit test (mock service).
     */
    public DanhMucSPPresenter(IDanhMucSPView view, DanhMucSPService danhMucSPService, int maNvHienTai) {
        this.view            = view;
        this.danhMucSPService = danhMucSPService;
        this.maNvHienTai     = maNvHienTai;
    }

    // =========================================================
    // 1. TẢI DANH SÁCH
    // =========================================================

    /**
     * Tải và hiển thị toàn bộ danh mục đang hoạt động lên TableView.
     * Gọi khi View khởi tạo (initialize) hoặc sau khi thao tác CUD thành công.
     */
    public void taiDanhSach() {
        try {
            List<DanhMucSPDTO> ds = danhMucSPService.layDanhSachDanhMuc();
            view.hienThiDanhSach(ds);
        } catch (Exception e) {
            view.hienThiLoi("Không thể tải danh sách danh mục: " + e.getMessage());
        }
    }

    // =========================================================
    // 2. THÊM DANH MỤC
    // =========================================================

    /**
     * Xử lý sự kiện bấm nút "Thêm".
     * Đọc tên từ View → gọi Service → refresh TableView hoặc báo lỗi.
     */
    public void themDanhMuc() {
        String tenNhap = view.getTenDanhMucInput();
        try {
            int maMoi = danhMucSPService.themDanhMuc(tenNhap);
            view.hienThiThanhCong("Thêm danh mục thành công (Mã: " + maMoi + ").");
            view.lamMoiForm();
            taiDanhSach();
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
        }
    }

    /**
     * Xử lý sự kiện Tìm kiếm.
     */
    public void timKiem() {
        String keyword = view.getTuKhoaTimKiemInput();
        try {
            List<DanhMucSPDTO> ds = danhMucSPService.timKiemDanhMuc(keyword);
            view.hienThiDanhSach(ds);
            view.hienThiThanhCong("Tìm thấy " + ds.size() + " danh mục.");
        } catch (Exception e) {
            // "Không tìm thấy danh mục nào phù hợp"
            view.hienThiLoi(e.getMessage());
            view.hienThiDanhSach(java.util.Collections.emptyList());
        }
    }

    // =========================================================
    // 3. SỬA DANH MỤC
    // =========================================================

    /**
     * Xử lý sự kiện bấm nút "Lưu / Cập nhật".
     * Bắt buộc phải có một hàng đang được chọn trên TableView.
     */
    public void suaDanhMuc() {
        DanhMucSPDTO selected = view.getSelectedCategory();
        if (selected == null) {
            view.hienThiLoi("Vui lòng chọn danh mục cần sửa.");
            return;
        }

        String tenNhap = view.getTenDanhMucInput();
        try {
            danhMucSPService.suaDanhMuc(selected.getMaDM(), tenNhap);
            view.hienThiThanhCong("Cập nhật danh mục thành công.");
            view.lamMoiForm();
            taiDanhSach();
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
        }
    }

    // =========================================================
    // 4. XÓA DANH MỤC
    // =========================================================

    /**
     * Xử lý sự kiện bấm nút "Xóa".
     * Bắt buộc phải có một hàng đang được chọn trên TableView.
     * Guard xóa khi danh mục có sản phẩm được thực hiện bởi DanhMucSPService.
     */
    public void xoaDanhMuc() {
        DanhMucSPDTO selected = view.getSelectedCategory();
        if (selected == null) {
            view.hienThiLoi("Vui lòng chọn danh mục cần xóa.");
            return;
        }

        try {
            danhMucSPService.xoaDanhMuc(selected.getMaDM(), maNvHienTai);
            view.hienThiThanhCong("Xóa danh mục thành công.");
            view.lamMoiForm();
            taiDanhSach();
        } catch (Exception e) {
            // "Không thể xóa danh mục đang có sản phẩm." hoặc lỗi hệ thống
            view.hienThiLoi(e.getMessage());
        }
    }

    // =========================================================
    // 5. CHỌN HÀNG TRÊN TABLEVIEW
    // =========================================================

    /**
     * Xử lý sự kiện chọn hàng trên TableView.
     * Điền thông tin của danh mục được chọn vào form.
     *
     * @param selected danh mục được người dùng click; null nếu bỏ chọn
     */
    public void onChonDanhMuc(DanhMucSPDTO selected) {
        view.hienThiChiTiet(selected);
    }
}
