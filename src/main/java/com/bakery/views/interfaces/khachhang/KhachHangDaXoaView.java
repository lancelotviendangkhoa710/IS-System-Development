package com.bakery.views.interfaces.khachhang;

import com.bakery.model.dto.khachhang.KhachHangDTO;
import java.util.List;

/**
 * View interface cho màn hình Thùng rác (KhachHangDaXoaViewFXMLController).
 * Presenter sẽ gọi các method này để update UI.
 */
public interface KhachHangDaXoaView {

    /**
     * Hiển thị danh sách khách hàng đã xóa trên TableView.
     *
     * @param customers danh sách khách hàng đã xóa
     */
    void displayDeletedCustomers(List<KhachHangDTO> customers);

    /**
     * Cập nhật thông tin phân trang hoặc tìm kiếm (ví dụ "Hiển thị 15 khách hàng").
     *
     * @param pageInfo text thông tin
     */
    void updatePaginationInfo(String pageInfo);

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
