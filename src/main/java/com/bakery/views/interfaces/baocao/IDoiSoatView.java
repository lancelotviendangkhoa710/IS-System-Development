package com.bakery.views.interfaces.baocao;

import java.math.BigDecimal;

/**
 * Interface IDoiSoatView là contract giữa DoiSoatPresenter và các View liên quan đến nghiệp vụ đóng/mở ca.
 * Đảm bảo Presenter không phụ thuộc vào lớp cụ thể (Panel hay Dialog) hay Framework UI.
 */
public interface IDoiSoatView {

    /**
     * Hiển thị thông báo lỗi lên giao diện.
     * Dùng cho các lỗi nghiệp vụ (sai lệch tiền) hoặc lỗi hệ thống (mất kết nối CSDL).
     */
    void hienThiLoi(String message);

    /**
     * Hiển thị thông báo thành công.
     * Ví dụ: "Mở ca thành công!" hoặc "Đóng ca thành công!".
     */
    void hienThiThanhCong(String message);

    /**
     * Bật hoặc tắt trạng thái tương tác của nút "Xác nhận Đóng Ca".
     * Thường dùng để bắt buộc người dùng thực hiện đủ các bước kiểm đếm trước khi cho phép đóng.
     */
    void setNutDongCaEnabled(boolean enabled);

    /**
     * Hiển thị kết quả chênh lệch giữa số dư hệ thống và tiền mặt thực tế.
     * Quy tắc hiển thị: Màu đỏ nếu có chênh lệch, màu xanh nếu khớp (bằng 0).
     */
    void hienThiCanhBaoChenhLech(BigDecimal chenhLech);

    /**
     * Yêu cầu hoặc ẩn đi ô nhập lý do chênh lệch.
     * @param batBuoc true -> hiển thị ô nhập lý do và yêu cầu bắt buộc; false -> ẩn đi.
     */
    void yeuCauNhapLyDo(boolean batBuoc);
}
