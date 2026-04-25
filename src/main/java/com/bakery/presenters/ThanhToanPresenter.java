package com.bakery.presenters;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.HoaDonDTO;
import com.bakery.model.dto.SanPhamDTO;
import com.bakery.model.dto.YeuCauChiTietDonHangDTO;
import com.bakery.model.dto.YeuCauChiTietDonTuyChinhDTO;
import com.bakery.model.dto.YeuCauTaoDonHangDTO;
import com.bakery.services.DonHangService;
import com.bakery.services.ThanhToanService;
import com.bakery.views.interfaces.IOrderView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ThanhToanPresenter {

    private final IOrderView view;
    private final DonHangService donHangService;
    private final ThanhToanService thanhToanService;
    private final GioHangPresenter gioHangPresenter;

    // Thông tin lưu tạm sau khi lưu đơn thành công, chờ in phiếu
    private final List<YeuCauChiTietDonHangDTO> gioHangDaThanhToan = new ArrayList<>();
    private double tongTienDaThanhToan = 0.0;
    private double phanTramGiamGiaDaThanhToan = 0.0;
    private Integer maDonDaThanhToan = null;
    private Integer maHoaDonDaThanhToan = null;
    private LocalDateTime ngayLapHoaDonDaThanhToan = null;

    private static final int MOCK_CURRENT_USER_ID = 1;

    public ThanhToanPresenter(IOrderView view, DonHangService donHangService,
            ThanhToanService thanhToanService, GioHangPresenter gioHangPresenter) {
        this.view = view;
        this.donHangService = donHangService;
        this.thanhToanService = thanhToanService;
        this.gioHangPresenter = gioHangPresenter;
    }

    // =========================================================
    // 1. ĐẶT BÁNH CÓ CỌC – xuất phiếu hẹn
    // =========================================================

    @SuppressWarnings("deprecation")
    public void xuLyDatBanh(List<SanPhamDTO> tatCaSanPham) {
        if (!kiemTraNgayNhanHopLe())
            return;

        double tongTienPhaiTra = view.getTongThanhToanHienTai();
        double tienKhachDua = view.getTienKhachDua();
        double tienCocNhapVao = view.getTienCoc();
        double soTienGhiNhan = (tienCocNhapVao > 0)
                ? tienCocNhapVao
                : Math.min(tongTienPhaiTra, tienKhachDua);

        // Validate cọc theo rule nghiệp vụ 50%
        if (soTienGhiNhan > tongTienPhaiTra) {
            view.hienThiLoi("Tiền cọc không được vượt quá tổng tiền đơn.");
            return;
        }
        if (soTienGhiNhan < (tongTienPhaiTra * 0.5)) {
            view.hienThiLoi("Cọc chưa đủ 50% tổng giá trị đơn hàng.");
            return;
        }
        if (tienKhachDua > 0 && tienKhachDua < soTienGhiNhan) {
            view.hienThiLoi("Khách đưa không đủ tiền cọc.");
            return;
        }

        // Validate giao hàng
        Integer hinhThucNhan = view.getHinhThucNhan();
        if (hinhThucNhan == 2) {
            if (view.getDiaChiGiao().isEmpty()) {
                view.hienThiLoi("Bắt buộc nhập địa chỉ giao hàng.");
                return;
            }
            if (view.getSoDienThoai().isEmpty()) {
                view.hienThiLoi("Bắt buộc nhập số điện thoại khi giao hàng.");
                return;
            }
        }

        try {
            YeuCauTaoDonHangDTO request = taoYeuCauDonHangDTO(
                    hinhThucNhan, soTienGhiNhan, gioHangPresenter.getGioHangItems());
            int maDonMoi = donHangService.taoDonHang(request);

            view.hienThiThanhCong("Tạo đơn mới thành công – Mã đơn: " + maDonMoi);
            view.inPhieuHoaDon("PHIẾU HẸN ĐẶT HÀNG", maDonMoi, null, LocalDateTime.now(),
                    tongTienPhaiTra, soTienGhiNhan,
                    convertToCTDonHangList(gioHangPresenter.getGioHangItems()),
                    tatCaSanPham, gioHangPresenter.getPhanTramGiamGia());

            lamMoiTrangThaiSauThanhToan();
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
        }
    }

    // =========================================================
    // 2. THANH TOÁN TRỰC TIẾP TẠI QUẦY
    // =========================================================

    /**
     * Bước 1: Lưu đơn hàng và hóa đơn vào DB.
     * Trả về true nếu thành công để gọi bước in phiếu.
     */
    @SuppressWarnings("deprecation")
    public boolean xuLyLuuDonHangVaoDB() {
        if (!kiemTraNgayNhanHopLe())
            return false;

        double tongTienPhaiTra = view.getTongThanhToanHienTai();
        double tienKhachDua = view.getTienKhachDua();

        if (tienKhachDua < tongTienPhaiTra) {
            view.hienThiLoi("Khách đưa chưa đủ tiền!");
            return false;
        }

        try {
            YeuCauTaoDonHangDTO request = taoYeuCauDonHangDTO(
                    1, tongTienPhaiTra, gioHangPresenter.getGioHangItems());
            HoaDonDTO hoaDonDaTao = thanhToanService.thanhToanTrucTiep(request, tienKhachDua);
            luuThongTinDonHangDaThanhToan(tongTienPhaiTra, hoaDonDaTao);
            return true;
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
            return false;
        }
    }

    /** Bước 2: Hiển thị thông báo thành công và in hóa đơn bán lẻ. */
    public void xuLySauKhiLuuDonThanhCong(List<SanPhamDTO> tatCaSanPham) {
        if (gioHangDaThanhToan.isEmpty())
            return;

        view.hienThiThanhCong("Thanh toán hoàn tất!");
        view.inPhieuHoaDon("HÓA ĐƠN BÁN LẺ", maDonDaThanhToan, maHoaDonDaThanhToan,
                ngayLapHoaDonDaThanhToan, tongTienDaThanhToan, tongTienDaThanhToan,
                convertToCTDonHangList(gioHangDaThanhToan), tatCaSanPham,
                phanTramGiamGiaDaThanhToan);

        lamMoiTrangThaiSauThanhToan();
        xoaThongTinDonHangDaThanhToan();
    }

    /** Gọi liên tiếp 2 bước trên. */
    public void xuLyThanhToanTrucTiep(List<SanPhamDTO> tatCaSanPham) {
        if (xuLyLuuDonHangVaoDB()) {
            xuLySauKhiLuuDonThanhCong(tatCaSanPham);
        }
    }

    // =========================================================
    // 3. KIỂM TRA NGÀY NHẬN HỢP LỆ
    // =========================================================

    public boolean kiemTraNgayNhanHopLe() {
        LocalDateTime ngayGioNhanBanh = view.getNgayGioNhanBanh();
        if (ngayGioNhanBanh == null) {
            view.hienThiLoi("Ngày giờ nhận bánh bắt buộc nhập.");
            return false;
        }
        if (ngayGioNhanBanh.isBefore(LocalDateTime.now())) {
            view.hienThiLoi("Ngày giờ nhận bánh không được nằm trong quá khứ.");
            return false;
        }
        // Bánh tùy chỉnh phải đặt trước ít nhất 1 ngày
        boolean hasCustomCake = gioHangPresenter.getGioHangItems().stream()
                .anyMatch(YeuCauChiTietDonHangDTO::isCustom);
        if (hasCustomCake && ngayGioNhanBanh.toLocalDate().isBefore(LocalDate.now().plusDays(1))) {
            view.hienThiLoi("Bánh tùy chỉnh phải được đặt trước ít nhất 1 ngày.");
            return false;
        }
        return true;
    }

    // =========================================================
    // PRIVATE – HỖ TRỢ NỘI BỘ
    // =========================================================

    @SuppressWarnings("deprecation")
    private YeuCauTaoDonHangDTO taoYeuCauDonHangDTO(Integer hinhThuc, double tienDaCoc,
            List<YeuCauChiTietDonHangDTO> items) {
        YeuCauTaoDonHangDTO req = new YeuCauTaoDonHangDTO();
        req.setNgayGioNhanBanh(view.getNgayGioNhanBanh());
        req.setMaNVLap(MOCK_CURRENT_USER_ID);
        req.setMaTrangThai(0);
        req.setTienDaCoc(tienDaCoc);
        req.setHinhThucNhan(hinhThuc);

        String diaChi = null;
        if (hinhThuc == 2) {
            diaChi = view.getDiaChiGiao();
            String sdt = view.getSoDienThoai();
            if (sdt != null && !sdt.isEmpty()) {
                diaChi = diaChi + " - SĐT liên hệ: " + sdt;
            }
        }
        req.setDiaChiGiao(diaChi);
        req.setItems(new ArrayList<>(items));
        return req;
    }

    private void lamMoiTrangThaiSauThanhToan() {
        gioHangPresenter.xoaGioHang();
        gioHangPresenter.setPhanTramGiamGia(0.0);
        view.lamMoiForm();
        gioHangPresenter.capNhatGioHangVaTien();
    }

    private void luuThongTinDonHangDaThanhToan(double tongTienPhaiTra, HoaDonDTO hoaDonDaTao) {
        gioHangDaThanhToan.clear();
        for (YeuCauChiTietDonHangDTO item : gioHangPresenter.getGioHangItems()) {
            if (item instanceof YeuCauChiTietDonTuyChinhDTO) {
                YeuCauChiTietDonTuyChinhDTO c = (YeuCauChiTietDonTuyChinhDTO) item;
                gioHangDaThanhToan.add(new YeuCauChiTietDonTuyChinhDTO(
                        c.getMaSP(), c.getSoLuong(), c.getDonGia(), c.getGhiChu(), c.getPhuKien(),
                        c.getMaKC(), c.getMaCot(), c.getMaNhan(), c.getMaTrangTri(),
                        c.getLoiChucTrenBanh(), c.getGhiChuThoBanh()));
            } else {
                YeuCauChiTietDonHangDTO clone = new YeuCauChiTietDonHangDTO();
                clone.setMaSP(item.getMaSP());
                clone.setSoLuong(item.getSoLuong());
                clone.setDonGia(item.getDonGia());
                clone.setCustom(false);
                gioHangDaThanhToan.add(clone);
            }
        }
        tongTienDaThanhToan = tongTienPhaiTra;
        phanTramGiamGiaDaThanhToan = gioHangPresenter.getPhanTramGiamGia();
        maDonDaThanhToan = hoaDonDaTao.getMaDon();
        maHoaDonDaThanhToan = hoaDonDaTao.getMaHD();
        ngayLapHoaDonDaThanhToan = hoaDonDaTao.getNgayXuatHd();
    }

    private void xoaThongTinDonHangDaThanhToan() {
        gioHangDaThanhToan.clear();
        tongTienDaThanhToan = 0.0;
        phanTramGiamGiaDaThanhToan = 0.0;
        maDonDaThanhToan = null;
        maHoaDonDaThanhToan = null;
        ngayLapHoaDonDaThanhToan = null;
    }

    private List<CTDonHangDTO> convertToCTDonHangList(List<YeuCauChiTietDonHangDTO> items) {
        List<CTDonHangDTO> list = new ArrayList<>();
        for (YeuCauChiTietDonHangDTO item : items) {
            CTDonHangDTO dto = new CTDonHangDTO();
            dto.setMaSP(item.getMaSP());
            dto.setSoLuong(item.getSoLuong());
            dto.setDonGia(item.getDonGia());
            list.add(dto);
        }
        return list;
    }
}
