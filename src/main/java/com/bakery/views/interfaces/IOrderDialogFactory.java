package com.bakery.views.interfaces;

import java.time.LocalDateTime;

/**
 * Interface factory cho các Dialog trong luồng tạo đơn hàng.
 * Tầng Presenter gọi qua interface này để yêu cầu hiển thị các cửa sổ nhập liệu/xác nhận 
 * mà không cần biết chi tiết về cách cài đặt UI (Swing hay JavaFX).
 * 
 * Tuân thủ Phương án A (MVP thuần túy): Tách biệt hoàn toàn logic điều khiển và giao diện Dialog.
 */
public interface IOrderDialogFactory {

    /** 
     * Record chứa thông tin kết quả từ wizard "Tạo Đơn Hàng".
     */
    record OrderRequest(
            boolean confirmed,      // true nếu người dùng bấm Xác nhận
            // Thông tin Khách hàng
            Integer maKH,           // Mã khách hàng (null nếu là khách vãng lai)
            String tenKhach,        // Tên khách hàng
            String soDienThoai,     // Số điện thoại liên lạc
            // Loại đơn
            OrderType orderType,    // IMMEDIATE (Giao ngay) hoặc PREORDER (Đặt trước)
            // Thông tin Thanh toán (nếu có)
            String hinhThucThanhToan, // TM, Chuyển khoản, v.v.
            double soTienKhachDua,    // Số tiền mặt khách đưa
            // Thông tin Đặt trước
            LocalDateTime ngayGioNhan, // Thời điểm hẹn lấy bánh
            String diaChiGiao,         // Địa chỉ giao (nếu có)
            double tienCoc             // Tiền đặt cọc
    ) {
        /** Tạo OrderRequest báo hủy (khi người dùng bấm Hủy hoặc đóng cửa sổ) */
        public static OrderRequest cancelled() {
            return new OrderRequest(false, null, "Khách vãng lai", "", null,
                    null, 0, null, "", 0);
        }
    }

    /** 
     * Các loại hình đơn hàng.
     */
    enum OrderType {
        IMMEDIATE, // Đơn tại quầy, lấy ngay
        PREORDER   // Đơn đặt trước, lấy sau
    }

    /** 
     * Callback interface dùng để tra cứu khách hàng theo SĐT.
     * Presenter sẽ inject logic tra cứu vào Dialog để Dialog tự thực hiện khi nhập SĐT.
     */
    @FunctionalInterface
    interface CustomerLookup {
        /** 
         * @param sdt Số điện thoại cần tìm.
         * @return Mảng [maKH, tenKH] nếu tìm thấy, null nếu không thấy.
         */
        String[] lookup(String sdt);
    }

    /**
     * Record chứa thông tin kết quả từ dialog "Hủy Đơn Hàng".
     */
    record CancelOrderRequest(
            boolean confirmed,      // true nếu người dùng bấm Xác nhận hủy
            String reason,          // Lý do hủy đơn
            double refundAmount     // Số tiền hoàn trả cho khách (nếu có)
    ) {
        public static CancelOrderRequest cancelled() {
            return new CancelOrderRequest(false, "", 0);
        }
    }

    /**
     * Hiển thị wizard 2 bước để "Tạo Đơn Hàng".
     * Phương thức này phải chặn (BLOCK) luồng thực thi cho đến khi người dùng xác nhận hoặc hủy.
     *
     * @param tongTienPhaiTra Tổng giá trị giỏ hàng hiện tại.
     * @param customerLookup  Logic tra cứu khách hàng được truyền từ Presenter.
     * @return Đối tượng OrderRequest chứa toàn bộ dữ liệu nhập từ Dialog.
     */
    OrderRequest showCreateOrderDialog(double tongTienPhaiTra, CustomerLookup customerLookup);

    /**
     * Hiển thị dialog xác nhận thanh toán cuối cùng khi chuyển trạng thái đơn hàng sang "Hoàn thành".
     * Phương thức này phải chặn (BLOCK) luồng thực thi.
     *
     * @param maDon    Mã đơn hàng cần thanh toán.
     * @param tongTien Tổng tiền giá trị đơn hàng.
     * @param daCoc    Số tiền khách đã đặt cọc trước đó.
     * @param conLai   Số tiền còn lại thực tế cần thu.
     * @return true nếu thu ngân xác nhận đã thu đủ tiền, false nếu chưa.
     */
    boolean showPaymentConfirmation(int maDon, double tongTien, double daCoc, double conLai);

    /**
     * Hiển thị dialog để hủy đơn hàng và hoàn cọc.
     *
     * @param maDon         Mã đơn cần hủy.
     * @param depositAmount Số tiền khách đã cọc (để gợi ý số tiền hoàn).
     * @return Đối tượng CancelOrderRequest.
     */
    CancelOrderRequest showCancelOrderDialog(int maDon, double depositAmount);
}
