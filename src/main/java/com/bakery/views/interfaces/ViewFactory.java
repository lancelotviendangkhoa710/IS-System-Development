package com.bakery.views.interfaces;

import com.bakery.model.dto.khachhang.KhachHangDTO;

/**
 * Factory interface để khởi tạo và mở các dialog màn hình con (Add, Update, Deleted...).
 * Presenter sẽ gọi factory này khi cần mở dialog thay vì tự khởi tạo bằng FXMLLoader.
 */
public interface ViewFactory {

    /**
     * Mở dialog Thêm khách hàng mới (modal).
     * Callback sẽ được gọi nếu thành công.
     *
     * @param onAddedCallback callback được gọi khi khách hàng được thêm thành công
     */
    void openAddCustomerDialog(Runnable onAddedCallback);

    /**
     * Mở dialog Sửa khách hàng (modal).
     * Callback sẽ được gọi nếu cập nhật thành công.
     *
     * @param customer khách hàng cần sửa
     * @param onUpdatedCallback callback được gọi khi cập nhật thành công
     */
    void openUpdateCustomerDialog(KhachHangDTO customer, Runnable onUpdatedCallback);

    /**
     * Mở dialog Thùng rác (modal).
     * Callback sẽ được gọi khi dialog đóng.
     *
     * @param onClosedCallback callback được gọi khi dialog đóng
     */
    void openDeletedCustomersDialog(Runnable onClosedCallback);

    /**
     * Mở dialog Quản lý Hạng Thành viên (modal).
     * Callback sẽ được gọi khi dialog đóng.
     *
     * @param onClosedCallback callback được gọi khi dialog đóng
     */
    void openMembershipTierDialog(Runnable onClosedCallback);
}
