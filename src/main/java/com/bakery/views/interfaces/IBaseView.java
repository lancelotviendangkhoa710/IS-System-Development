package com.bakery.views.interfaces;

/**
 * Giao diện cơ sở cho tất cả các View trong hệ thống.
 * Định nghĩa các phương thức chung về thông báo và trạng thái xử lý.
 */
public interface IBaseView {
    
    /** Hiển thị thông báo lỗi lên giao diện. */
    void hienThiLoi(String msg);
    
    /** Hiển thị thông báo thành công hoặc thông tin. */
    default void hienThiThanhCong(String msg) {}
    
    /** Xóa bỏ các thông báo cũ. */
    void xoaLoi();
    
    /** Bật/tắt trạng thái đang xử lý (loading spinner, disable buttons). */
    void setLoading(boolean loading);
}
