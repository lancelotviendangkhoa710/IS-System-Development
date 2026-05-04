package com.bakery.views.interfaces.banhang;
import com.bakery.model.dto.khachhang.KhachHangDTO;

import com.bakery.model.dto.banhang.CTDonHangDTO;
import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.model.dto.kho.KichCoBanhDTO;
import com.bakery.model.dto.kho.CotBanhDTO;
import com.bakery.model.dto.kho.NhanBanhDTO;
import com.bakery.model.dto.kho.KieuTrangTriDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface IOrderView định nghĩa các phương thức giao tiếp giữa Presenter và View 
 * trong kiến trúc MVP cho màn hình Bán hàng (POS) và Theo dõi đơn hàng.
 *
 * Tuân thủ quy tắc: Tầng Presenter KHÔNG ĐƯỢC import bất kỳ class UI cụ thể nào (JavaFX).
 * Tất cả tương tác UI phải đi qua interface này để đảm bảo tính tách biệt (Decoupling).
 */
public interface IDonHangView {

    // ——— QUẢN LÝ GIỎ HÀNG & TÀI CHÍNH ———

    /**
     * Làm mới bảng giỏ hàng với danh sách chi tiết đơn hàng và dữ liệu gốc sản phẩm.
     * 
     * @param items Danh sách các mục trong giỏ hàng hiện tại.
     * @param originData Danh sách dữ liệu gốc của sản phẩm để tham chiếu.
     */
    void lamMoiBangGioHang(List<CTDonHangDTO> items, List<SanPhamDTO> originData);

    /**
     * Cập nhật toàn bộ panel báo cáo tài chính hiển thị các thông số tiền tệ.
     *
     * @param tongHang Tổng giá trị hàng hóa.
     * @param giamGia Số tiền được giảm giá.
     * @param tongThanhToan Tổng số tiền khách phải trả sau giảm giá.
     * @param minCoc Số tiền cọc tối thiểu (nếu là đơn đặt trước).
     * @param conLai Số tiền còn lại khách phải trả.
     * @param tienThua Số tiền thối lại cho khách.
     * @param isThieuTienThua Cờ báo hiệu nếu khách đưa thiếu tiền.
     */
    void lamMoiBaoCaoTien(double tongHang, double giamGia, double tongThanhToan,
                          double minCoc, double conLai, double tienThua, boolean isThieuTienThua);

    /**
     * Bật hoặc tắt nút "Thanh toán" dựa trên trạng thái của giỏ hàng (có trống hay không).
     * 
     * @param state true để bật, false để tắt.
     */
    void batTatNutThanhToan(boolean state);

    /**
     * Hiển thị thông tin khách hàng và trạng thái hội viên (VIP) trên giao diện.
     * 
     * @param text Chuỗi văn bản hiển thị (Tên/SĐT khách).
     * @param isVip true nếu là khách hàng thân thiết/VIP.
     */
    void hienThiThongTinKhach(String text, boolean isVip);

    /**
     * Cập nhật đối tượng DTO khách hàng hiện tại vào View để quản lý.
     * 
     * @param kh Đối tượng KhachHangDTO.
     */
    void capNhatKhachHangHienTai(KhachHangDTO kh);

    // ——— HIỂN THỊ SẢN PHẨM & TÙY CHỈNH ———

    /**
     * Tải và hiển thị danh sách sản phẩm lên lưới (Grid Tile) của màn hình POS.
     * 
     * @param ds Danh sách sản phẩm.
     * @param dict Bản đồ ánh xạ mã danh mục sang tên danh mục.
     */
    void hienThiDanhSachSanPham(List<SanPhamDTO> ds, Map<Integer, String> dict);

    /**
     * Tải dữ liệu các thành phần (Kích cỡ, Cốt bánh, Nhân, Trang trí) cho form Bánh Tùy Chỉnh.
     */
    void hienThiDuLieuTuyChinh(List<SanPhamDTO> spTuyChinh, List<KichCoBanhDTO> kichCo,
                                List<CotBanhDTO> cotBanh, List<NhanBanhDTO> nhanBanh,
                                List<KieuTrangTriDTO> trangTri);

    // ——— THEO DÕI ĐƠN HÀNG ———

    /**
     * Tải danh sách các trạng thái đơn hàng vào ComboBox để lọc.
     */
    void taiDanhSachTrangThai(List<String> list);

    /**
     * Cập nhật danh sách các thẻ (Card) đơn hàng trên bảng điều khiển theo dõi.
     */
    void hienThiDanhSachDonTheoDoi(List<DonDatHangDTO> dsDonTheoDoi);

    /**
     * Hiển thị cửa sổ chi tiết cho một đơn hàng cụ thể.
     */
    void showOrderDetails(DonDatHangDTO order);

    // ——— THÔNG BÁO & PHẢN HỒI ———

    /**
     * Hiển thị thông báo lỗi nghiệp vụ trên tab POS.
     */
    void hienThiLoi(String msg);

    /**
     * Hiển thị thông báo thành công trên tab POS.
     */
    void hienThiThanhCong(String msg);

    /** 
     * Bí danh (Alias) cho hienThiLoi để tương thích với các Presenter cũ.
     */
    default void showError(String msg) { hienThiLoi(msg); }

    /**
     * Hiển thị thông báo lỗi trên tab Theo dõi đơn hàng.
     */
    void hienThiLoiTraCuu(String msg);

    /**
     * Hiển thị thông báo thành công hoặc thông tin trên tab Theo dõi đơn hàng.
     */
    void hienThiThongBaoTraCuu(String msg);

    /**
     * Hiển thị kết quả tóm tắt sau khi tra cứu nhanh đơn hàng.
     */
    void hienThiKetQuaTraCuu(String kh, String tt, double tongTien);

    // ——— QUẢN LÝ FORM ———

    /**
     * Làm trống (reset) toàn bộ form POS sau khi đơn hàng được hoàn tất.
     */
    void lamMoiForm();

    // ——— IN ẤN & XUẤT HÓA ĐƠN ———

    /**
     * Mở cửa sổ xem trước (Preview) và in hóa đơn/phiếu thu.
     */
    void inPhieuHoaDon(String tieuDe, com.bakery.model.dto.banhang.HoaDonDTO hd, DonDatHangDTO don,
                       List<CTDonHangDTO> cart, List<SanPhamDTO> data, double pGiam,
                       double khachDua, double tienThua, boolean laDonCoc);


    /**
     * Mở cửa sổ xem trước hóa đơn đã hoàn thành (bao gồm đầy đủ thông tin thanh toán).
     */
    void inHoaDonHoanThanh(DonDatHangDTO don, com.bakery.model.dto.banhang.HoaDonDTO hd,
                           List<CTDonHangDTO> dsItems, double khachDua, double tienThua, boolean laDonCoc);

    // ——— TRUY XUẤT TRẠNG THÁI (GETTERS) ———

    /** 
     * Lấy ngày giờ nhận bánh được chọn trên giao diện (phục vụ việc kiểm tra logic).
     * @return Đối tượng LocalDateTime.
     */
    LocalDateTime getNgayGioNhanBanh();

    /** 
     * Lấy tổng số tiền cần thanh toán hiện tại đang hiển thị trên View.
     */
    double getTongThanhToanHienTai();

    // ——— TƯƠNG THÍCH NGƯỢC (LEGACY BRIDGE) ———
    // Các phương thức này được giữ lại TẠM THỜI để tránh lỗi biên dịch ở các Presenter cũ.
    // Sẽ được gỡ bỏ hoàn toàn sau khi hoàn tất quá trình migrate.

    /** @deprecated Sử dụng các phương thức tài chính mới trong lamMoiBaoCaoTien */
    @Deprecated default double getTienKhachDua() { return 0; }

    /** @deprecated Sử dụng các phương thức tài chính mới */
    @Deprecated default double getTienCoc() { return 0; }

    /** @deprecated */
    @Deprecated default String getDiaChiGiao() { return ""; }

    /** @deprecated */
    @Deprecated default String getSoDienThoai() { return ""; }

    /** @deprecated */
    @Deprecated default Integer getHinhThucNhan() { return 1; }
}
