package com.bakery.views.interfaces;

import com.bakery.models.dto.KhachHangDTO;
import java.util.List;

/**
 * View interface cho màn hình Thùng rác (CustomerDeletedController).
 * Presenter sẽ gọi các method này để update UI.
 */
public interface CustomerDeletedView {

    /**
     * Hiển thị danh sách khách hàng đã xóa trên TableView.
     *
     * @param customers danh sách khách hàng đã xóa
     */
    void displayDeletedCustomers(List<KhachHangDTO> customers);

    /**
     * Cập nhật thông tin phân trang (ví dụ "Hiển thị 1-8 của 15").
     *
     * @param pageInfo text thông tin phân trang
     */
    void updatePaginationInfo(String pageInfo);

    /**
     * Cập nhật các nút phân trang (previous, next, page buttons).
     *
     * @param currentPage trang hiện tại
     * @param totalPages tổng số trang
     */
    void updatePaginationControls(int currentPage, int totalPages);

    /**
     * Hiển thị thông báo lỗi.
     *
     * @param title tiêu đề
     * @param message nội dung
     */
    void showErrorAlert(String title, String message);

    /**
     * Hiển thị thông báo thành công.
     *
     * @param title tiêu đề
     * @param message nội dung
     */
    void showSuccessAlert(String title, String message);

    /**
     * Hỏi xác nhận khôi phục khách hàng.
     *
     * @param customerName tên khách hàng
     * @return true nếu xác nhận, false nếu hủy
     */
    boolean confirmRestore(String customerName);

    /**
     * Tắt/bật trạng thái busy (disable UI...).
     *
     * @param busy true để tắt, false để bật
     */
    void setBusy(boolean busy);

    /**
     * Đóng dialog (đóng Stage modal).
     */
    void closeDialog();
}
