package com.bakery.views.interfaces;

import com.bakery.models.dto.KhachHangDTO;
import java.util.List;

/**
 * View interface cho màn hình Danh sách Khách hàng (CustomerInfoController).
 * Presenter sẽ gọi các method này để update UI.
 */
public interface CustomerInfoView {

    /**
     * Hiển thị danh sách khách hàng trên TableView.
     *
     * @param customers danh sách khách hàng cần hiển thị
     */
    void displayCustomers(List<KhachHangDTO> customers);

    /**
     * Cập nhật thông tin phân trang (ví dụ "Hiển thị 1-10 của 50").
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
     * Cập nhật label tổng số khách hàng hoạt động.
     *
     * @param count số lượng
     */
    void updateTotalCustomersCount(int count);

    /**
     * Cập nhật label số khách hàng mới trong tháng.
     *
     * @param count số lượng
     */
    void updateNewCustomersThisMonth(int count);

    /**
     * Hiển thị thông báo lỗi cho người dùng.
     *
     * @param title tiêu đề
     * @param message nội dung thông báo
     */
    void showErrorAlert(String title, String message);

    /**
     * Hiển thị thông báo thành công.
     *
     * @param title tiêu đề
     * @param message nội dung thông báo
     */
    void showSuccessAlert(String title, String message);

    /**
     * Hiển thị thông báo thông tin.
     *
     * @param title tiêu đề
     * @param message nội dung thông báo
     */
    void showInfoAlert(String title, String message);

    /**
     * Lấy giá trị từ ô tìm kiếm (search field).
     *
     * @return từ khóa tìm kiếm
     */
    String getSearchKeyword();

    /**
     * Xóa nội dung ô tìm kiếm mà không trigger sự kiện listener.
     */
    void clearSearchField();

    /**
     * Tắt/bật trạng thái busy (disable UI, hiển thị loading...).
     *
     * @param busy true để tắt, false để bật
     */
    void setBusy(boolean busy);

    /**
     * Mở dialog thêm khách hàng mới và chờ kết quả (modal).
     * Presenter sẽ gọi callback khi dialog đóng.
     *
     * @param onAddedCallback callback được gọi nếu khách hàng được thêm thành công
     */
    void openAddCustomerDialog(Runnable onAddedCallback);

    /**
     * Mở dialog sửa khách hàng và chờ kết quả (modal).
     *
     * @param customer khách hàng cần sửa
     * @param onUpdatedCallback callback được gọi nếu cập nhật thành công
     */
    void openUpdateCustomerDialog(KhachHangDTO customer, Runnable onUpdatedCallback);

    /**
     * Mở dialog thùng rác khách hàng.
     * Presenter sẽ gọi callback khi dialog đóng.
     *
     * @param onClosedCallback callback được gọi khi dialog đóng
     */
    void openDeletedCustomersDialog(Runnable onClosedCallback);

    /**
     * Hỏi xác nhận xóa mềm khách hàng.
     *
     * @param customerName tên khách hàng
     * @return true nếu người dùng xác nhận xóa, false nếu hủy
     */
    boolean confirmDelete(String customerName);

    /**
     * Mở FileChooser để lưu file Excel.
     *
     * @return File được chọn, hoặc null nếu hủy
     */
    java.io.File chooseExcelFileToSave();
}
