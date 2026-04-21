package com.bakery.presenters;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.HoaDonDTO;
import com.bakery.model.dto.KhachHangDTO;
import com.bakery.model.dto.SanPhamDTO;
import com.bakery.model.dto.TrangThaiDonDTO;
import com.bakery.model.dto.YeuCauChiTietDonHangDTO;
import com.bakery.model.dto.YeuCauChiTietDonTuyChinhDTO;
import com.bakery.model.dto.YeuCauTaoDonHangDTO;
import com.bakery.services.OrderService;
import com.bakery.views.interfaces.IOrderView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderPresenter {
    private final IOrderView view;
    private final OrderService orderService;

    private List<SanPhamDTO> tatCaSanPham = new ArrayList<>();
    private final List<YeuCauChiTietDonHangDTO> gioHangItems = new ArrayList<>();
    private final Map<String, Integer> mapTrangThaiMoi = new HashMap<>();
    private final Map<Integer, String> mapDanhMuc = new HashMap<>();
    private Integer maKhachHangDangChon = null;
    private double phanTramGiamGia = 0.0;
    private static final int MOCK_CURRENT_USER_ID = 1;

    private final List<YeuCauChiTietDonHangDTO> gioHangDaThanhToan = new ArrayList<>();
    private double tongTienDaThanhToan = 0.0;
    private double phanTramGiamGiaDaThanhToan = 0.0;
    private Integer maDonDaThanhToan = null;
    private Integer maHoaDonDaThanhToan = null;
    private LocalDateTime ngayLapHoaDonDaThanhToan = null;

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

    public OrderPresenter(IOrderView view, OrderService orderService) {
        this.view = view;
        this.orderService = orderService;
    }

    public void taiDuLieuBanDau() {
        tatCaSanPham = orderService.layDanhSachSanPhamPOS();
        mapDanhMuc.putAll(orderService.layMapDanhMucSanPham());

        try {
            mapTrangThaiMoi.clear();
            List<TrangThaiDonDTO> dsTrangThai = orderService.layDanhSachTrangThaiDon();
            for (TrangThaiDonDTO tt : dsTrangThai) {
                mapTrangThaiMoi.put(tt.getTenTrangThai(), tt.getMaTrangThai());
            }
        } catch (Exception e) {
            view.hienThiLoi("Không tải được danh sách trạng thái: " + e.getMessage());
        }

        view.hienThiDanhSachSanPham(tatCaSanPham, mapDanhMuc);

        List<SanPhamDTO> spTuyChinh = new ArrayList<>();
        for (SanPhamDTO sp : tatCaSanPham) {
            String tenDM = mapDanhMuc.getOrDefault(sp.getMaDM(), "");
            if (tenDM.equalsIgnoreCase("Cake")) {
                spTuyChinh.add(sp);
            }
        }
        view.hienThiDuLieuTuyChinh(spTuyChinh, orderService.layDanhSachKichCo(), orderService.layDanhSachCotBanh(),
                orderService.layDanhSachNhanBanh(), orderService.layDanhSachKieuTrangTri());

        view.taiDanhSachTrangThai(new ArrayList<>(mapTrangThaiMoi.keySet()));
        timKiemDonTheoDoi(LocalDate.now(), null);
    }

    public void themSanPhamVaoGio(SanPhamDTO sp) {
        YeuCauChiTietDonHangDTO existed = gioHangItems.stream()
                .filter(i -> i.getMaSP() == sp.getMaSP() && !i.isCustom()).findFirst().orElse(null);
        if (existed != null) {
            existed.setSoLuong(existed.getSoLuong() + 1);
        } else {
            YeuCauChiTietDonHangDTO newItem = new YeuCauChiTietDonHangDTO();
            newItem.setMaSP(sp.getMaSP());
            newItem.setSoLuong(1);
            newItem.setDonGia(sp.getGiaCoBan());
            newItem.setCustom(false);
            gioHangItems.add(newItem);
        }
        capNhatGioHangVaTien();
    }

    public void themBanhTuyChinhVaoGio(SanPhamDTO sp, int soLuong, double donGia, Integer maKC, Integer maCot,
            Integer maNhan, Integer maTrangTri, String loiChuc, String ghiChu) {
        YeuCauChiTietDonTuyChinhDTO newItem = new YeuCauChiTietDonTuyChinhDTO(
                sp.getMaSP(), soLuong, donGia, "", "",
                maKC, maCot, maNhan, maTrangTri, loiChuc, ghiChu);
        gioHangItems.add(newItem);
        capNhatGioHangVaTien();
    }

    public double tinhGiaBanhTuyChinh(int maSP, Integer maKC, Integer maCot, Integer maNhan, Integer maTrangTri) {
        return orderService.tinhGiaBanhTuyChinh(maSP, maKC, maCot, maNhan, maTrangTri);
    }

    public void thayDoiSoLuongMon(int index, int change) {
        if (index >= 0 && index < gioHangItems.size()) {
            YeuCauChiTietDonHangDTO item = gioHangItems.get(index);
            if (change == 0) {
                gioHangItems.remove(index);
            } else {
                item.setSoLuong(item.getSoLuong() + change);
                if (item.getSoLuong() <= 0) {
                    gioHangItems.remove(index);
                }
            }
            capNhatGioHangVaTien();
        }
    }

    public void timKhachHang(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) {
            lamMoiKhachHang();
            return;
        }
        try {
            KhachHangDTO kh = orderService.timKhachHangTheoSoDienThoai(sdt);
            if (kh != null) {
                maKhachHangDangChon = kh.getMaKH();
                phanTramGiamGia = 0.10;
                view.hienThiThongTinKhach(kh.getHoTen() + " (Thành viên -10%)", true);
            } else {
                lamMoiKhachHang();
                view.hienThiThongTinKhach("Khách vãng lai (Chưa ĐK)", false);
            }
        } catch (Exception e) {
            lamMoiKhachHang();
            view.hienThiLoi("Không tìm được khách hàng: " + e.getMessage());
        }
        capNhatGioHangVaTien();
    }

    private void lamMoiKhachHang() {
        maKhachHangDangChon = null;
        phanTramGiamGia = 0.0;
        view.hienThiThongTinKhach("Khách vãng lai", false);
    }

    public void capNhatGioHangVaTien() {
        double tongTienHang = gioHangItems.stream().mapToDouble(i -> i.getDonGia() * i.getSoLuong()).sum();
        double tienGiamGia = tongTienHang * phanTramGiamGia;
        double tongTienPhaiTra = tongTienHang - tienGiamGia;
        double tienKhachDua = view.getTienKhachDua();
        double tienCocNhapVao = view.getTienCoc();

        double soTienGhiNhan = (tienCocNhapVao > 0) ? tienCocNhapVao : Math.min(tongTienPhaiTra, tienKhachDua);
        double conLai = Math.max(0, tongTienPhaiTra - soTienGhiNhan);
        double tienThua = Math.max(0, tienKhachDua - soTienGhiNhan);

        view.lamMoiBaoCaoTien(tongTienHang, tienGiamGia, tongTienPhaiTra, tongTienPhaiTra * 0.5, conLai, tienThua,
                tienKhachDua < soTienGhiNhan);
        view.lamMoiBangGioHang(convertToCTDonHangList(gioHangItems), tatCaSanPham);
        view.batTatNutThanhToan(!gioHangItems.isEmpty() && view.isXacNhanThuTien());
    }

    public void xuLyDatBanh() {
        if (!kiemTraNgayNhanHopLeChoThanhToan()) {
            return;
        }
        double tongTienPhaiTra = view.getTongThanhToanHienTai();
        double tienKhachDua = view.getTienKhachDua();
        double tienCocNhapVao = view.getTienCoc();
        double soTienGhiNhan = (tienCocNhapVao > 0) ? tienCocNhapVao : Math.min(tongTienPhaiTra, tienKhachDua);

        if (soTienGhiNhan > tongTienPhaiTra) {
            view.hienThiLoi("Tiền cọc không được vượt quá tổng tiền đơn");
            return;
        }
        if (soTienGhiNhan < (tongTienPhaiTra * 0.5)) {
            view.hienThiLoi("Cọc chưa đủ 50%");
            return;
        }
        if (tienKhachDua > 0 && tienKhachDua < soTienGhiNhan) {
            view.hienThiLoi("Khách đưa không đủ tiền cọc");
            return;
        }

        Integer hinhThucNhan = view.getHinhThucNhan();
        if (hinhThucNhan == 2 && view.getDiaChiGiao().isEmpty()) {
            view.hienThiLoi("Bắt buộc nhập địa chỉ");
            return;
        }

        try {
            YeuCauTaoDonHangDTO request = taoYeuCauDonHangDTO(hinhThucNhan, soTienGhiNhan);
            int maDonMoi = orderService.submitNewOrder(request);
            view.hienThiThanhCong("Tạo đơn mới thành công-Mã đơn: " + maDonMoi);
            view.inPhieuHoaDon("PHIẾU HẸN ĐẶT HÀNG", maDonMoi, null, LocalDateTime.now(),
                    tongTienPhaiTra, soTienGhiNhan, convertToCTDonHangList(gioHangItems), tatCaSanPham,
                    phanTramGiamGia);
            lamMoiTrangThai();
            timKiemDonTheoDoi(LocalDate.now(), null);
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
        }
    }

    public void xuLyThanhToanTrucTiep() {
        if (xuLyLuuDonHangVaoDB()) {
            xuLySauKhiLuuDonThanhCong();
        }
    }

    public boolean xuLyLuuDonHangVaoDB() {
        if (!kiemTraNgayNhanHopLeChoThanhToan()) {
            return false;
        }

        double tongTienPhaiTra = view.getTongThanhToanHienTai();
        double tienKhachDua = view.getTienKhachDua();

        if (tienKhachDua < tongTienPhaiTra) {
            view.hienThiLoi("Khách đưa chưa đủ tiền!");
            return false;
        }

        try {
            YeuCauTaoDonHangDTO request = taoYeuCauDonHangDTO(1, tongTienPhaiTra);
            HoaDonDTO hoaDonDaTao = orderService.thanhToanTrucTiep(request, tienKhachDua);
            luuThongTinDonHangDaThanhToan(tongTienPhaiTra, hoaDonDaTao);
            return true;
        } catch (Exception e) {
            view.hienThiLoi(e.getMessage());
            return false;
        }
    }

    public void xuLySauKhiLuuDonThanhCong() {
        if (gioHangDaThanhToan.isEmpty()) {
            return;
        }
        view.hienThiThanhCong("Thanh toán hoàn tất!");
        view.inPhieuHoaDon("HÓA ĐƠN BÁN LẺ", maDonDaThanhToan, maHoaDonDaThanhToan, ngayLapHoaDonDaThanhToan,
                tongTienDaThanhToan, tongTienDaThanhToan, convertToCTDonHangList(gioHangDaThanhToan), tatCaSanPham,
                phanTramGiamGiaDaThanhToan);
        lamMoiTrangThai();
        xoaThongTinDonHangDaThanhToan();
    }

    public void traCuuDonHang(String maDonStr) {
        try {
            int maDon = Integer.parseInt(maDonStr.trim());
            DonDatHangDTO tomTat = orderService.loadOrderById(maDon);
            view.showOrderDetails(tomTat);
            view.hienThiKetQuaTraCuu(tomTat.getMaKH() == null ? "Khách lẻ" : "Mã KH: " + tomTat.getMaKH(),
                    tomTat.getTenTrangThai(), tomTat.getTongTienHDBan());
        } catch (Exception e) {
            view.hienThiLoiTraCuu(e.getMessage());
        }
    }

    public void capNhatTrangThai(String maDonStr, String ttMoi) {
        try {
            int maDon = Integer.parseInt(maDonStr.trim());
            Integer maTtMoi = mapTrangThaiMoi.get(ttMoi);
            if (maTtMoi == null) {
                throw new Exception("Trạng thái không hợp lệ.");
            }

            DonDatHangDTO donHienTai = orderService.loadOrderById(maDon);
            orderService.chuyenTrangThaiDon(maDon, maTtMoi, MOCK_CURRENT_USER_ID, donHienTai.getHinhThucNhan(),
                    view.getTrangThaiHienTaiTraCuu(), ttMoi);
            view.hienThiKetQuaTraCuu(null, ttMoi, -1);
            view.hienThiThongBaoTraCuu("Cập nhật thành công!");
            timKiemDonTheoDoi(LocalDate.now(), null);
        } catch (Exception e) {
            view.hienThiLoiTraCuu(e.getMessage());
        }
    }

    public void timKiemDonTheoDoi(LocalDate ngayNhan, LocalTime gioNhan) {
        try {
            Integer gio = gioNhan == null ? null : gioNhan.getHour();
            List<DonDatHangDTO> dsDon = orderService.layDanhSachDonTheoDoi(ngayNhan, gio);
            view.hienThiDanhSachDonTheoDoi(dsDon);
        } catch (Exception e) {
            view.hienThiLoiTraCuu(e.getMessage());
        }
    }

    private YeuCauTaoDonHangDTO taoYeuCauDonHangDTO(Integer hinhThuc, double tienDaCoc) {
        YeuCauTaoDonHangDTO req = new YeuCauTaoDonHangDTO();
        req.setNgayGioNhanBanh(view.getNgayGioNhanBanh());
        req.setMaKH(maKhachHangDangChon);
        req.setMaNVLap(MOCK_CURRENT_USER_ID);
        req.setMaTrangThai(0);
        req.setTienDaCoc(tienDaCoc);
        req.setHinhThucNhan(hinhThuc);
        req.setDiaChiGiao(hinhThuc == 2 ? view.getDiaChiGiao() : null);

        req.setItems(new ArrayList<>(gioHangItems));
        return req;
    }

    private void lamMoiTrangThai() {
        gioHangItems.clear();
        lamMoiKhachHang();
        view.lamMoiForm();
        capNhatGioHangVaTien();
    }

    private void luuThongTinDonHangDaThanhToan(double tongTienPhaiTra, HoaDonDTO hoaDonDaTao) {
        gioHangDaThanhToan.clear();
        for (YeuCauChiTietDonHangDTO item : gioHangItems) {
            if (item instanceof YeuCauChiTietDonTuyChinhDTO) {
                YeuCauChiTietDonTuyChinhDTO c = (YeuCauChiTietDonTuyChinhDTO) item;
                gioHangDaThanhToan.add(new YeuCauChiTietDonTuyChinhDTO(
                        c.getMaSP(), c.getSoLuong(), c.getDonGia(), c.getGhiChu(), c.getPhuKien(),
                        c.getMaKC(), c.getMaCot(), c.getMaNhan(), c.getMaTrangTri(), c.getLoiChucTrenBanh(),
                        c.getGhiChuThoBanh()));
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
        phanTramGiamGiaDaThanhToan = phanTramGiamGia;
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

    public boolean kiemTraNgayNhanHopLeChoThanhToan() {
        LocalDateTime ngayGioNhanBanh = view.getNgayGioNhanBanh();
        if (ngayGioNhanBanh == null) {
            view.hienThiLoi("Ngày giờ nhận bánh bắt buộc nhập.");
            return false;
        }
        if (ngayGioNhanBanh.isBefore(LocalDateTime.now())) {
            view.hienThiLoi("Ngày giờ nhận bánh không được nằm trong quá khứ.");
            return false;
        }
        boolean hasCustomCake = gioHangItems.stream().anyMatch(YeuCauChiTietDonHangDTO::isCustom);
        if (hasCustomCake) {
            if (ngayGioNhanBanh.toLocalDate().isBefore(LocalDate.now().plusDays(1))) {
                view.hienThiLoi("Bánh tùy chỉnh phải được đặt trước ít nhất 1 ngày.");
                return false;
            }
        }
        return true;
    }
}
