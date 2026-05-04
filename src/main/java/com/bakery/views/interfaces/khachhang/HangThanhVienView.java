package com.bakery.views.interfaces.khachhang;

import com.bakery.model.dto.khachhang.HangThanhVienDTO;
import java.util.List;

/**
 * View interface cho màn hình Quản lý Hạng Thành viên (HangThanhVienController).
 * Presenter sẽ gọi các method này để update UI.
 */
public interface HangThanhVienView {

    /**
     * Hiển thị danh sách hạng thành viên trên TableView.
     *
     * @param tiers danh sách hạng thành viên
     */
    void displayTiers(List<HangThanhVienDTO> tiers);

    /**
     * Lấy tên hạng (từ row được chọn hoặc từ input field).
     *
     * @return tên hạng
     */
    String getTierName();

    /**
     * Lấy điểm tối thiểu.
     *
     * @return điểm tối thiểu
     */
    int getMinimumPoints();

    /**
     * Lấy phần trăm giảm giá.
     *
     * @return phần trăm giảm giá (0-100)
     */
    double getDiscountPercentage();

    /**
     * Đặt lỗi cho field tên hạng.
     *
     * @param error thông báo lỗi, null nếu không có lỗi
     */
    void setTierNameError(String error);

    /**
     * Đặt lỗi cho field điểm tối thiểu.
     *
     * @param error thông báo lỗi, null nếu không có lỗi
     */
    void setMinPointsError(String error);

    /**
     * Đặt lỗi cho field phần trăm giảm giá.
     *
     * @param error thông báo lỗi, null nếu không có lỗi
     */
    void setDiscountError(String error);

    /**
     * Xóa nội dung form input.
     */
    void clearForm();

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
     * Tắt/bật trạng thái busy (disable nút Save...).
     *
     * @param busy true để tắt, false để bật
     */
    void setBusy(boolean busy);

    /**
     * Hỏi xác nhận cập nhật hạng.
     *
     * @param tierName tên hạng
     * @return true nếu xác nhận, false nếu hủy
     */
    boolean confirmUpdate(String tierName);
}
