package com.bakery.views.interfaces;

import java.math.BigDecimal;

/**
 * Contract giữa DoiSoatPresenter và các View liên quan đến ca làm việc.
 * Presenter chỉ giao tiếp với View qua interface này — không phụ thuộc
 * vào class concrete (DoiSoatDongCaPanel hoặc DoiSoatDongCaDialog).
 */
public interface IDoiSoatView {

    /**
     * Hiển thị thông báo lỗi lên View.
     * Dùng cho lỗi nghiệp vụ (validation) và lỗi hệ thống.
     */
    void hienThiLoi(String message);

    /**
     * Hiển thị thông báo thành công lên View.
     * VD: "Mở ca thành công!" hoặc "Đóng ca thành công!"
     */
    void hienThiThanhCong(String message);

    /**
     * Bật/tắt nút [Xác nhận Đóng Ca].
     * Chỉ enable khi: không có chênh lệch, hoặc có chênh lệch nhưng đã nhập lý do.
     */
    void setNutDongCaEnabled(boolean enabled);

    /**
     * Hiển thị kết quả chênh lệch sau khi thu ngân nhập tiền thực tế đếm được.
     * Màu đỏ nếu chenhLech != 0, màu xanh nếu khớp (= 0).
     */
    void hienThiCanhBaoChenhLech(BigDecimal chenhLech);

    /**
     * Bật/tắt ô nhập lý do chênh lệch.
     * @param batBuoc true → hiện ô nhập lý do và đánh dấu bắt buộc; false → ẩn đi
     */
    void yeuCauNhapLyDo(boolean batBuoc);
}
