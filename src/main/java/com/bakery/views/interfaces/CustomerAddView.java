package com.bakery.views.interfaces;

/**
 * View interface cho màn hình Thêm Khách hàng (CustomerAddController).
 * Presenter sẽ gọi các method này để update UI.
 */
public interface CustomerAddView {

    /**
     * Lấy họ tên người dùng nhập vào.
     *
     * @return họ tên
     */
    String getFullName();

    /**
     * Lấy số điện thoại người dùng nhập vào.
     *
     * @return số điện thoại
     */
    String getPhoneNumber();

    /**
     * Lấy địa chỉ người dùng nhập vào.
     *
     * @return địa chỉ
     */
    String getAddress();

    /**
     * Xóa toàn bộ nội dung form.
     */
    void clearForm();

    /**
     * Đặt lỗi cho field họ tên.
     *
     * @param error thông báo lỗi, null nếu không có lỗi
     */
    void setFullNameError(String error);

    /**
     * Đặt lỗi cho field số điện thoại.
     *
     * @param error thông báo lỗi, null nếu không có lỗi
     */
    void setPhoneError(String error);

    /**
     * Đặt lỗi cho field địa chỉ.
     *
     * @param error thông báo lỗi, null nếu không có lỗi
     */
    void setAddressError(String error);

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
     * Đóng form (đóng Stage modal).
     */
    void closeForm();
}
