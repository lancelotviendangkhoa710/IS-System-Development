package com.bakery.views.interfaces;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.SanPhamDTO;
import com.bakery.model.dto.KichCoBanhDTO;
import com.bakery.model.dto.CotBanhDTO;
import com.bakery.model.dto.NhanBanhDTO;
import com.bakery.model.dto.KieuTrangTriDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Contract giao tiếp giữa Presenter và View trong kiến trúc MVP.
 * Tầng Presenter KHÔNG ĐƯỢC import bất kỳ class UI cụ thể nào (Swing hay JavaFX).
 * Tất cả tương tác UI phải đi qua interface này.
 *
 * Lưu ý khi migrate sang JavaFX:
 * - Các method "hiện dialog" chuyển sang IOrderDialogFactory (Phương án A)
 * - Giữ nguyên các method trả về void / primitive / DTO
 */
public interface IOrderView {

    // ─── GIỎ HÀNG & TIỀN ───────────────────────────────────────────────

    /**
     * Làm mới bảng giỏ hàng với danh sách item và dữ liệu gốc sản phẩm.
     * JavaFX: ObservableList của TableView
     */
    void lamMoiBangGioHang(List<CTDonHangDTO> items, List<SanPhamDTO> originData);

    /**
     * Cập nhật toàn bộ panel báo cáo tài chính.
     */
    void lamMoiBaoCaoTien(double tongHang, double giamGia, double tongThanhToan,
                          double minCoc, double conLai, double tienThua, boolean isThieuTienThua);

    /** Bật/tắt nút Thanh toán theo trạng thái giỏ hàng */
    void batTatNutThanhToan(boolean state);

    /** Hiển thị tên khách hàng và trạng thái VIP trên UI */
    void hienThiThongTinKhach(String text, boolean isVip);

    // ─── SẢN PHẨM & DANH MỤC ───────────────────────────────────────────

    /** Tải và hiển thị danh sách sản phẩm lên grid tile */
    void hienThiDanhSachSanPham(List<SanPhamDTO> ds, Map<Integer, String> dict);

    /** Tải dữ liệu cho form Bánh Tùy Chỉnh */
    void hienThiDuLieuTuyChinh(List<SanPhamDTO> spTuyChinh, List<KichCoBanhDTO> kichCo,
                                List<CotBanhDTO> cotBanh, List<NhanBanhDTO> nhanBanh,
                                List<KieuTrangTriDTO> trangTri);

    // ─── THEO DÕI ĐƠN HÀNG ─────────────────────────────────────────────

    /** Tải danh sách trạng thái vào ComboBox lọc */
    void taiDanhSachTrangThai(List<String> list);

    /** Cập nhật danh sách card đơn hàng trên panel theo dõi */
    void hienThiDanhSachDonTheoDoi(List<DonDatHangDTO> dsDonTheoDoi);

    /** Hiển thị chi tiết 1 đơn hàng (expand card hoặc dialog) */
    void showOrderDetails(DonDatHangDTO order);

    // ─── THÔNG BÁO ─────────────────────────────────────────────────────

    /** Hiển thị thông báo lỗi nghiệp vụ trên tab POS */
    void hienThiLoi(String msg);

    /** Hiển thị thông báo thành công trên tab POS */
    void hienThiThanhCong(String msg);

    /** Alias cho hienThiLoi — dùng từ Presenter cũ */
    default void showError(String msg) { hienThiLoi(msg); }

    /** Hiển thị thông báo lỗi trên tab Theo dõi */
    void hienThiLoiTraCuu(String msg);

    /** Hiển thị thông báo thành công trên tab Theo dõi */
    void hienThiThongBaoTraCuu(String msg);

    /** Hiển thị kết quả tra cứu đơn hàng */
    void hienThiKetQuaTraCuu(String kh, String tt, double tongTien);

    // ─── FORM ──────────────────────────────────────────────────────────

    /** Làm trống form POS sau khi hoàn tất đơn */
    void lamMoiForm();

    // ─── IN HÓA ĐƠN ────────────────────────────────────────────────────

    /** Mở cửa sổ preview hóa đơn */
    void inPhieuHoaDon(String tieuDe, Integer maDon, Integer maHoaDon,
                       LocalDateTime ngayLapHoaDon, double tongTien, double daThu,
                       List<CTDonHangDTO> cart, List<SanPhamDTO> data, double pGiam);

    /** Mở cửa sổ preview hóa đơn hoàn tất (có đầy đủ thông tin đơn + HĐ) */
    void inHoaDonHoanThanh(DonDatHangDTO don, com.bakery.model.dto.HoaDonDTO hd,
                           List<CTDonHangDTO> dsItems);

    // ─── GETTERS (Presenter cần đọc state View) ─────────────────────────

    /** @return Ngày giờ nhận bánh từ UI (dùng cho validation) */
    LocalDateTime getNgayGioNhanBanh();

    /** @return Tổng thanh toán hiện đang hiển thị trên UI */
    double getTongThanhToanHienTai();

    // ─── CÁC METHOD CỮ (Giữ để tương thích ngược — Presenter cũ gọi) ────

    /** @deprecated Không còn dùng sau khi chuyển sang IOrderDialogFactory */
    @Deprecated default double getTienKhachDua() { return 0; }

    /** @deprecated Không còn dùng sau khi chuyển sang IOrderDialogFactory */
    @Deprecated default double getTienCoc() { return 0; }

    /** @deprecated Không còn dùng sau khi chuyển sang IOrderDialogFactory */
    @Deprecated default String getDiaChiGiao() { return ""; }

    /** @deprecated Không còn dùng sau khi chuyển sang IOrderDialogFactory */
    @Deprecated default String getSoDienThoai() { return ""; }

    /** @deprecated Không còn dùng sau khi chuyển sang IOrderDialogFactory */
    @Deprecated default Integer getHinhThucNhan() { return 1; }

    /** @deprecated Không còn dùng sau khi chuyển sang IOrderDialogFactory */
    @Deprecated default String getTrangThaiHienTaiTraCuu() { return ""; }

    // ─── PAYMENT (Deprecated — chuyển sang IOrderDialogFactory) ─────────

    /**
     * @deprecated Dùng IOrderDialogFactory.showPaymentConfirmation() thay thế.
     * Giữ lại để tương thích ngược với OrderPresenter trong quá trình migration.
     */
    @Deprecated
    default boolean hienThiXacNhanThanhToan(int maDon, double tongTien, double daCoc, double conLai) {
        return false;
    }

    /**
     * @deprecated Không dùng nữa — QR hiển thị trực tiếp trong CreateOrderDialog (JavaFX).
     */
    @Deprecated default int hienThiChonHinhThucThanhToan() { return 0; }

    /** @deprecated */
    @Deprecated default boolean hienThiXacNhanThuTien(double soTien) { return false; }

    /** @deprecated */
    @Deprecated default boolean hienThiQRVaXacNhan(double soTien, String moTa) { return false; }

    /** @deprecated */
    @Deprecated default void hienThiPopupQR(double amount, String orderId) {}

    /**
     * @deprecated Luồng thanh toán hiện đại được xử lý bởi IOrderDialogFactory.
     */
    @Deprecated
    record PaymentResult(boolean confirmed, boolean isRetail,
                         double soTienThanhToan, String hinhThuc) {}

    /** @deprecated */
    @Deprecated
    default PaymentResult hienThiManHinhThanhToanModern(double tongTien, String moTa) {
        return new PaymentResult(false, true, tongTien, "Tiền mặt");
    }
}
