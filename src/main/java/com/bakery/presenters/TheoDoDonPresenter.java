package com.bakery.presenters;

import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.TrangThaiDonDTO;
import com.bakery.services.DonHangService;
import com.bakery.services.ThanhToanService;
import com.bakery.services.TheoDoiDonService;
import com.bakery.views.interfaces.IOrderView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter duy nhất chịu trách nhiệm về màn hình Theo dõi đơn hàng:
 * Tìm kiếm / lọc danh sách đơn, tra cứu chi tiết, cập nhật trạng thái.
 * (SRP – Single Responsibility Principle)
 */
public class TheoDoDonPresenter {

    private final IOrderView view;
    private final DonHangService donHangService;
    private final ThanhToanService thanhToanService;
    private final TheoDoiDonService theoDoDonService;

    // Map tên trạng thái → mã trạng thái (load 1 lần khi khởi tạo)
    private final Map<String, Integer> mapTrangThai = new HashMap<>();

    // Lưu bộ lọc cuối để refresh lại sau khi cập nhật
    private String lastSearchMaDon = "";
    private LocalDate lastSearchNgay = LocalDate.now();
    private LocalTime lastSearchTu = null;
    private LocalTime lastSearchDen = null;

    private static final int MOCK_CURRENT_USER_ID = 1;

    public TheoDoDonPresenter(IOrderView view, DonHangService donHangService,
            ThanhToanService thanhToanService, TheoDoiDonService theoDoDonService) {
        this.view = view;
        this.donHangService = donHangService;
        this.thanhToanService = thanhToanService;
        this.theoDoDonService = theoDoDonService;
    }

    // =========================================================
    // 1. NẠP MAP TRẠNG THÁI (gọi 1 lần khi khởi động View)
    // =========================================================

    public void napMapTrangThai() {
        try {
            mapTrangThai.clear();
            List<TrangThaiDonDTO> dsTrangThai = donHangService.layDanhSachTrangThaiDon();
            for (TrangThaiDonDTO tt : dsTrangThai) {
                mapTrangThai.put(tt.getTenTrangThai(), tt.getMaTrangThai());
            }
        } catch (Exception e) {
            view.hienThiLoi("Không tải được danh sách trạng thái: " + e.getMessage());
        }
    }

    public Map<String, Integer> getMapTrangThai() {
        return mapTrangThai;
    }

    // =========================================================
    // 2. TÌM KIẾM / LỌC DANH SÁCH ĐƠN THEO DÕI
    // =========================================================

    /** Lọc danh sách đơn theo mã đơn, ngày nhận, khoảng giờ rồi đẩy lên View. */
    public void timKiemDonTheoDoi(String maDonSearch, LocalDate ngayNhan,
            LocalTime gioTu, LocalTime gioDen) {
        this.lastSearchMaDon = maDonSearch;
        this.lastSearchNgay = ngayNhan;
        this.lastSearchTu = gioTu;
        this.lastSearchDen = gioDen;
        try {
            List<DonDatHangDTO> dsDon = theoDoDonService.layDanhSachDonTheoDoi(
                    maDonSearch, ngayNhan, gioTu, gioDen);
            view.hienThiDanhSachDonTheoDoi(dsDon);
        } catch (Exception e) {
            view.hienThiLoiTraCuu(e.getMessage());
        }
    }

    // =========================================================
    // 3. TRA CỨU CHI TIẾT ĐƠN HÀNG
    // =========================================================

    public void traCuuDonHang(String maDonStr) {
        try {
            int maDon = Integer.parseInt(maDonStr.trim());
            DonDatHangDTO tomTat = donHangService.layTomTatDonHang(maDon);
            view.showOrderDetails(tomTat);
            view.hienThiKetQuaTraCuu(
                    tomTat.getMaKH() == null ? "Khách lẻ" : "Mã KH: " + tomTat.getMaKH(),
                    tomTat.getTenTrangThai(),
                    tomTat.getTongTienHDBan() != null ? tomTat.getTongTienHDBan().doubleValue() : 0.0);
        } catch (NumberFormatException e) {
            view.hienThiLoiTraCuu("Mã đơn phải là số nguyên hợp lệ.");
        } catch (Exception e) {
            view.hienThiLoiTraCuu(e.getMessage());
        }
    }

    // =========================================================
    // 4. CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG
    // =========================================================

    /**
     * Chuyển trạng thái đơn hàng.
     * Nếu chuyển sang HOÀN_THÀNH → tự động tạo hóa đơn qua ThanhToanService.
     */
    public void capNhatTrangThai(String maDonStr, String ttMoi, String ttHienTai) {
        try {
            int maDon = Integer.parseInt(maDonStr.trim());
            Integer maTtMoi = mapTrangThai.get(ttMoi);
            if (maTtMoi == null) {
                throw new Exception("Trạng thái không hợp lệ.");
            }

            DonDatHangDTO donHienTai = donHangService.layTomTatDonHang(maDon);

            // Chuyển trạng thái và nhận lại đơn hàng nếu đã HOÀN_THÀNH
            DonDatHangDTO donHoanThanh = donHangService.chuyenTrangThaiDon(
                    maDon, maTtMoi, MOCK_CURRENT_USER_ID,
                    donHienTai.getHinhThucNhan(), ttHienTai, ttMoi);

            // Nếu chuyển sang HOÀN_THÀNH → tạo hóa đơn và in
            if (donHoanThanh != null) {
                com.bakery.model.dto.HoaDonDTO hd = thanhToanService.chotHoaDonDatHang(donHoanThanh);
                if (hd != null) {
                    List<com.bakery.model.dto.CTDonHangDTO> dsItems = donHangService.layChiTietDonHang(maDon);
                    view.inHoaDonHoanThanh(donHoanThanh, hd, dsItems);
                }
            }

            view.hienThiThongBaoTraCuu("Cập nhật thành công đơn #" + maDon);

            // Refresh lại danh sách với bộ lọc cũ
            timKiemDonTheoDoi(lastSearchMaDon, lastSearchNgay, lastSearchTu, lastSearchDen);
        } catch (NumberFormatException e) {
            view.hienThiLoiTraCuu("Mã đơn phải là số nguyên hợp lệ.");
        } catch (Exception e) {
            view.hienThiLoiTraCuu(e.getMessage());
        }
    }
}
