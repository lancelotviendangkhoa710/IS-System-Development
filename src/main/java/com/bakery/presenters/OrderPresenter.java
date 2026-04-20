package com.bakery.presenters;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.KhachHangDTO;
import com.bakery.model.dto.SanPhamDTO;
import com.bakery.model.dto.TrangThaiDonDTO;
import com.bakery.model.dto.YeuCauChiTietDonHangDTO;
import com.bakery.model.dto.YeuCauTaoDonHangDTO;
import com.bakery.services.OrderService;
import com.bakery.views.interfaces.IOrderView;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderPresenter {
    private final IOrderView view;
    private final OrderService orderService;

    private List<SanPhamDTO> tatCaSanPham = new ArrayList<>();
    private final List<CTDonHangDTO> gioHangItems = new ArrayList<>();
    private final Map<String, Integer> mapTrangThaiMoi = new HashMap<>();
    private final Map<Integer, String> mapDanhMuc = new HashMap<>();
    private Integer maKhachHangDangChon = null;
    private double phanTramGiamGia = 0.0;
    private static final int MOCK_CURRENT_USER_ID = 1;

    private final List<CTDonHangDTO> gioHangDaThanhToan = new ArrayList<>();
    private double tongTienDaThanhToan = 0.0;
    private double phanTramGiamGiaDaThanhToan = 0.0;

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
            view.hienThiLoi("Khong tai duoc danh sach trang thai: " + e.getMessage());
        }

        view.hienThiDanhSachSanPham(tatCaSanPham, mapDanhMuc);
        view.taiDanhSachTrangThai(new ArrayList<>(mapTrangThaiMoi.keySet()));
        timKiemDonTheoDoi(LocalDate.now(), null);
    }

    public void themSanPhamVaoGio(SanPhamDTO sp) {
        CTDonHangDTO existed = gioHangItems.stream().filter(i -> i.getMaSP() == sp.getMaSP()).findFirst().orElse(null);
        if (existed != null) {
            existed.setSoLuong(existed.getSoLuong() + 1);
        } else {
            CTDonHangDTO newItem = new CTDonHangDTO();
            newItem.setMaSP(sp.getMaSP());
            newItem.setSoLuong(1);
            newItem.setDonGia(sp.getGiaCoBan());
            gioHangItems.add(newItem);
        }
        capNhatGioHangVaTien();
    }

    public void thayDoiSoLuongMon(int index, int change) {
        if (index >= 0 && index < gioHangItems.size()) {
            CTDonHangDTO item = gioHangItems.get(index);
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
                view.hienThiThongTinKhach(kh.getHoTen() + " (Thanh vien -10%)", true);
            } else {
                lamMoiKhachHang();
                view.hienThiThongTinKhach("Khach vang lai (Chua DK)", false);
            }
        } catch (Exception e) {
            lamMoiKhachHang();
            view.hienThiLoi("Khong tim duoc khach hang: " + e.getMessage());
        }
        capNhatGioHangVaTien();
    }

    private void lamMoiKhachHang() {
        maKhachHangDangChon = null;
        phanTramGiamGia = 0.0;
        view.hienThiThongTinKhach("Khach vang lai", false);
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

        view.lamMoiBaoCaoTien(tongTienHang, tienGiamGia, tongTienPhaiTra, tongTienPhaiTra * 0.5, conLai, tienThua, tienKhachDua < soTienGhiNhan);
        view.lamMoiBangGioHang(gioHangItems, tatCaSanPham);
        view.batTatNutThanhToan(!gioHangItems.isEmpty() && view.isXacNhanThuTien());
    }

    public void xuLyDatBanh() {
        double tongTienPhaiTra = view.getTongThanhToanHienTai();
        double tienKhachDua = view.getTienKhachDua();
        double tienCocNhapVao = view.getTienCoc();
        double soTienGhiNhan = (tienCocNhapVao > 0) ? tienCocNhapVao : Math.min(tongTienPhaiTra, tienKhachDua);

        if (soTienGhiNhan < (tongTienPhaiTra * 0.5)) {
            view.hienThiLoi("Cọc chưa đủ 50%");
            return;
        }
        if (tienKhachDua > 0 && tienKhachDua < soTienGhiNhan) {
            view.hienThiLoi("Đưa không đủ tiền cọc");
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
            view.hienThiThanhCong("Tạo đơn mới thành công mã đơn: " + maDonMoi);
            view.inPhieuHoaDon("PHIẾU HẸN LẤY BÁNH", maDonMoi, tongTienPhaiTra, soTienGhiNhan, gioHangItems, tatCaSanPham, phanTramGiamGia);
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
        double tongTienPhaiTra = view.getTongThanhToanHienTai();
        double tienKhachDua = view.getTienKhachDua();

        if (tienKhachDua < tongTienPhaiTra) {
            view.hienThiLoi("Khach dua chua du tien!");
            return false;
        }

        try {
            YeuCauTaoDonHangDTO request = taoYeuCauDonHangDTO(1, tongTienPhaiTra);
            orderService.thanhToanTrucTiep(request, tienKhachDua);
            luuThongTinDonHangDaThanhToan(tongTienPhaiTra);
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
        view.hienThiThanhCong("Thanh toan hoan tat!");
        view.inPhieuHoaDon("HOA DON BAN LE", -1, tongTienDaThanhToan, tongTienDaThanhToan, gioHangDaThanhToan, tatCaSanPham, phanTramGiamGiaDaThanhToan);
        lamMoiTrangThai();
        xoaThongTinDonHangDaThanhToan();
    }

    public void traCuuDonHang(String maDonStr) {
        try {
            int maDon = Integer.parseInt(maDonStr.trim());
            DonDatHangDTO tomTat = orderService.loadOrderById(maDon);
            view.showOrderDetails(tomTat);
            view.hienThiKetQuaTraCuu(tomTat.getMaKH() == null ? "Khach le" : "Ma KH: " + tomTat.getMaKH(), tomTat.getTenTrangThai(), tomTat.getTongTienHDBan());
        } catch (Exception e) {
            view.hienThiLoiTraCuu(e.getMessage());
        }
    }

    public void capNhatTrangThai(String maDonStr, String ttMoi) {
        try {
            int maDon = Integer.parseInt(maDonStr.trim());
            Integer maTtMoi = mapTrangThaiMoi.get(ttMoi);
            if (maTtMoi == null) {
                throw new Exception("Trang thai khong hop le.");
            }

            DonDatHangDTO donHienTai = orderService.loadOrderById(maDon);
            orderService.chuyenTrangThaiDon(maDon, maTtMoi, MOCK_CURRENT_USER_ID, donHienTai.getHinhThucNhan(), view.getTrangThaiHienTaiTraCuu(), ttMoi);
            view.hienThiKetQuaTraCuu(null, ttMoi, -1);
            view.hienThiThongBaoTraCuu("Cap nhat thanh cong!");
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

        List<YeuCauChiTietDonHangDTO> items = new ArrayList<>();
        for (CTDonHangDTO g : gioHangItems) {
            YeuCauChiTietDonHangDTO dto = new YeuCauChiTietDonHangDTO();
            dto.setMaSP(g.getMaSP());
            dto.setSoLuong(g.getSoLuong());
            dto.setDonGia(g.getDonGia());
            dto.setCustom(false);
            dto.setGhiChu("");
            dto.setPhuKien("");
            items.add(dto);
        }
        req.setItems(items);
        return req;
    }

    private void lamMoiTrangThai() {
        gioHangItems.clear();
        lamMoiKhachHang();
        view.lamMoiForm();
        capNhatGioHangVaTien();
    }

    private void luuThongTinDonHangDaThanhToan(double tongTienPhaiTra) {
        gioHangDaThanhToan.clear();
        for (CTDonHangDTO item : gioHangItems) {
            CTDonHangDTO clone = new CTDonHangDTO();
            clone.setMaSP(item.getMaSP());
            clone.setSoLuong(item.getSoLuong());
            clone.setDonGia(item.getDonGia());
            gioHangDaThanhToan.add(clone);
        }
        tongTienDaThanhToan = tongTienPhaiTra;
        phanTramGiamGiaDaThanhToan = phanTramGiamGia;
    }

    private void xoaThongTinDonHangDaThanhToan() {
        gioHangDaThanhToan.clear();
        tongTienDaThanhToan = 0.0;
        phanTramGiamGiaDaThanhToan = 0.0;
    }

    private String chuanHoaChuoi(String raw) {
        if (raw == null) {
            return "";
        }
        return Normalizer.normalize(raw, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d").replace("Đ", "D")
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_');
    }
}
