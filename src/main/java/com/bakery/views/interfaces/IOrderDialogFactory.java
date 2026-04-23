package com.bakery.views.interfaces;

import com.bakery.views.interfaces.IOrderView.PaymentResult;
import java.time.LocalDateTime;

/**
 * Interface factory cho các Dialog trong luồng tạo đơn hàng.
 * Presenter gọi qua interface này — KHÔNG biết UI framework cụ thể (JavaFX hay Swing).
 * Tuân thủ Phương án A: MVP thuần túy, Presenter tách biệt hoàn toàn với Dialog.
 */
public interface IOrderDialogFactory {

    /** Kết quả từ wizard Tạo Đơn Hàng 2 bước */
    record OrderRequest(
            boolean confirmed,
            // Khách hàng
            Integer maKH,
            String tenKhach,
            String soDienThoai,
            // Loại đơn
            OrderType orderType,
            // Thanh toán ngay
            String hinhThucThanhToan,
            double soTienKhachDua,
            // Đặt trước
            LocalDateTime ngayGioNhan,
            String diaChiGiao,
            double tienCoc
    ) {
        /** Tạo OrderRequest báo hủy (không xác nhận) */
        public static OrderRequest cancelled() {
            return new OrderRequest(false, null, "Khách vãng lai", "", null,
                    null, 0, null, "", 0);
        }
    }

    enum OrderType { IMMEDIATE, PREORDER }

    /** Callback tra cứu khách hàng theo SĐT — Presenter inject vào Dialog */
    @FunctionalInterface
    interface CustomerLookup {
        /** @return [maKH, tenKH] nếu tìm thấy, null nếu không */
        String[] lookup(String sdt);
    }

    /**
     * Mở wizard 2 bước "Tạo Đơn Hàng".
     * Phương thức này phải BLOCK (showAndWait) cho đến khi user xác nhận hoặc hủy.
     *
     * @param tongTienPhaiTra Tổng tiền giỏ hàng hiện tại
     * @param customerLookup  Callback để tra cứu khách hàng
     * @return OrderRequest với confirmed=true nếu user xác nhận, false nếu hủy
     */
    OrderRequest showCreateOrderDialog(double tongTienPhaiTra, CustomerLookup customerLookup);

    /**
     * Hiện dialog xác nhận thanh toán khi chuyển trạng thái đơn sang "Hoàn thành".
     * Phương thức này phải BLOCK.
     *
     * @param maDon    Mã đơn hàng
     * @param tongTien Tổng tiền đơn hàng
     * @param daCoc    Số tiền đã cọc trước đó
     * @param conLai   Số tiền còn phải thu
     * @return true nếu thu ngân xác nhận đã thu đủ tiền
     */
    boolean showPaymentConfirmation(int maDon, double tongTien, double daCoc, double conLai);
}
