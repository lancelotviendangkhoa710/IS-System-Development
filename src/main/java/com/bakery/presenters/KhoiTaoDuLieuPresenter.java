package com.bakery.presenters;

import com.bakery.model.dto.KhachHangDTO;
import com.bakery.model.dto.SanPhamDTO;
import com.bakery.model.dto.TrangThaiDonDTO;
import com.bakery.services.DonHangService;
import com.bakery.services.KhachHangService;
import com.bakery.services.SanPhamService;
import com.bakery.services.TuyChinhBanhService;
import com.bakery.views.interfaces.IOrderView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Presenter duy nhất chịu trách nhiệm khởi tạo dữ liệu ban đầu cho View:
 * Sản phẩm POS, danh mục, tùy chọn tùy chỉnh, tìm kiếm khách hàng.
 * (SRP – Single Responsibility Principle)
 */
public class KhoiTaoDuLieuPresenter {

    private final IOrderView view;
    private final SanPhamService sanPhamService;
    private final TuyChinhBanhService tuyChinhBanhService;
    private final DonHangService donHangService;
    private final KhachHangService khachHangService;
    private final GioHangPresenter gioHangPresenter;
    private final TheoDoDonPresenter theoDoDonPresenter;

    public KhoiTaoDuLieuPresenter(IOrderView view, SanPhamService sanPhamService,
            TuyChinhBanhService tuyChinhBanhService, DonHangService donHangService,
            KhachHangService khachHangService, GioHangPresenter gioHangPresenter,
            TheoDoDonPresenter theoDoDonPresenter) {
        this.view = view;
        this.sanPhamService = sanPhamService;
        this.tuyChinhBanhService = tuyChinhBanhService;
        this.donHangService = donHangService;
        this.khachHangService = khachHangService;
        this.gioHangPresenter = gioHangPresenter;
        this.theoDoDonPresenter = theoDoDonPresenter;
    }

    // =========================================================
    // 1. TẢI TOÀN BỘ DỮ LIỆU BAN ĐẦU CHO VIEW
    // =========================================================

    /**
     * Phương thức duy nhất gọi lúc View khởi động.
     * Tải sản phẩm, danh mục, tùy chỉnh bánh, trạng thái đơn và danh sách theo dõi.
     */
    public List<SanPhamDTO> taiDuLieuBanDau() {
        // 1. Lấy sản phẩm và danh mục
        List<SanPhamDTO> tatCaSanPham = sanPhamService.layDanhSachSanPhamPOS();
        Map<Integer, String> mapDanhMuc = sanPhamService.layMapDanhMucSanPham();

        // 2. Cập nhật sanPham cho GioHangPresenter để render bảng
        gioHangPresenter.setTatCaSanPham(tatCaSanPham);

        // 3. Hiển thị danh sách sản phẩm lên POS
        view.hienThiDanhSachSanPham(tatCaSanPham, mapDanhMuc);

        // 4. Lọc sản phẩm thuộc danh mục "Cake" để hiển thị form tùy chỉnh
        List<SanPhamDTO> spTuyChinh = new ArrayList<>();
        for (SanPhamDTO sp : tatCaSanPham) {
            String tenDM = mapDanhMuc.getOrDefault(sp.getMaDM(), "");
            if (tenDM.equalsIgnoreCase("Cake")) {
                spTuyChinh.add(sp);
            }
        }
        view.hienThiDuLieuTuyChinh(spTuyChinh,
                tuyChinhBanhService.layDanhSachKichCo(),
                tuyChinhBanhService.layDanhSachCotBanh(),
                tuyChinhBanhService.layDanhSachNhanBanh(),
                tuyChinhBanhService.layDanhSachKieuTrangTri());

        // 5. Nạp danh sách trạng thái đơn cho màn hình theo dõi
        theoDoDonPresenter.napMapTrangThai();
        view.taiDanhSachTrangThai(
                new ArrayList<>(theoDoDonPresenter.getMapTrangThai().keySet()));

        // 6. Hiển thị danh sách đơn theo dõi mặc định (hôm nay)
        theoDoDonPresenter.timKiemDonTheoDoi(null, LocalDate.now(), null, null);

        return tatCaSanPham;
    }

    // =========================================================
    // 2. TÌM KIẾM KHÁCH HÀNG
    // =========================================================

    /**
     * Tra cứu khách hàng theo SĐT.
     * Nếu tìm thấy → áp dụng giảm giá thành viên 10%.
     * Nếu không   → reset về khách vãng lai.
     */
    public void timKhachHang(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) {
            lamMoiKhachHang();
            return;
        }
        try {
            KhachHangDTO kh = khachHangService.timKhachHangTheoSoDienThoai(sdt);
            if (kh != null) {
                // Khách thành viên được giảm 10%
                gioHangPresenter.setPhanTramGiamGia(0.10);
                view.hienThiThongTinKhach(kh.getHoTen() + " (Thành viên -10%)", true);
            } else {
                lamMoiKhachHang();
                view.hienThiThongTinKhach("Khách vãng lai (Chưa ĐK)", false);
            }
        } catch (Exception e) {
            lamMoiKhachHang();
            view.hienThiLoi("Không tìm được khách hàng: " + e.getMessage());
        }
        gioHangPresenter.capNhatGioHangVaTien();
    }

    // =========================================================
    // PRIVATE – HỖ TRỢ NỘI BỘ
    // =========================================================

    private void lamMoiKhachHang() {
        gioHangPresenter.setPhanTramGiamGia(0.0);
        view.hienThiThongTinKhach("Khách vãng lai", false);
    }
}
