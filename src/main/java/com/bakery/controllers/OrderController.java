package com.bakery.controllers;

import com.bakery.dto.*;
import com.bakery.services.OrderService;
import com.bakery.views.OrderViewPanel;

import java.time.LocalDateTime;
import java.util.*;

public class OrderController {
    private final OrderViewPanel view;
    private final OrderService orderService;

    // --- STATE ---
    private List<SanPhamDTO> tatCaSanPham = new ArrayList<>();
    private final List<CTDonHangDTO> gioHangItems = new ArrayList<>();
    private final Map<String, Integer> mapTrangThaiMoi = new HashMap<>();
    private final Map<Integer, String> mapDanhMuc = new HashMap<>();
    private Integer maKhachHangDangChon = null;
    private double phanTramGiamGia = 0.0;
    private static final int MOCK_CURRENT_USER_ID = 1;

    public OrderController(OrderViewPanel view) {
        this.view = view;
        this.orderService = new OrderService();
    }

    public void taiDuLieuBanDau() {
        tatCaSanPham = orderService.layDanhSachSanPhamPOS();
        mapDanhMuc.putAll(orderService.layMapDanhMucSanPham());

        try {
            List<TrangThaiDonDTO> dsTrangThai = orderService.layDanhSachTrangThaiDon();
            List<String> validStates = Arrays.asList("DA_COC", "DANG_SAN_XUAT", "CHO_GIAO", "CHO_KHACH_LAY", "HOAN_THANH", "HUY", "HOAN_HANG");
            for (TrangThaiDonDTO tt : dsTrangThai) {
                String norm = normalize(tt.getTenTrangThai());
                if (validStates.contains(norm)) mapTrangThaiMoi.put(tt.getTenTrangThai(), tt.getMaTrangThai());
            }
        } catch (Exception ignored) {}

        view.renderDanhSachSanPham(tatCaSanPham, mapDanhMuc);
        view.loadDanhSachTrangThai(new ArrayList<>(mapTrangThaiMoi.keySet()));
    }

    public void themSanPhamVaoGio(SanPhamDTO sp) {
        CTDonHangDTO existed = gioHangItems.stream().filter(i -> i.getMaSP() == sp.getMaSP()).findFirst().orElse(null);
        if (existed != null) {
            existed.setSoLuong(existed.getSoLuong() + 1);
        } else {
            CTDonHangDTO newItem = new CTDonHangDTO();
            newItem.setMaSP(sp.getMaSP()); newItem.setSoLuong(1); newItem.setDonGia(sp.getGiaCoBan());
            gioHangItems.add(newItem);
        }
        capNhatGioHangVaTien();
    }

    public void thayDoiSoLuongMon(int index, int change) {
        if (index >= 0 && index < gioHangItems.size()) {
            CTDonHangDTO item = gioHangItems.get(index);
            if (change == 0) gioHangItems.remove(index);
            else {
                item.setSoLuong(item.getSoLuong() + change);
                if (item.getSoLuong() <= 0) gioHangItems.remove(index);
            }
            capNhatGioHangVaTien();
        }
    }

    public void timKhachHang(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) {
            lamMoiKhachHang(); return;
        }
        try {
            KhachHangDTO kh = orderService.timKhachHangTheoSoDienThoai(sdt);
            if (kh != null) {
                maKhachHangDangChon = kh.getMaKH();
                phanTramGiamGia = 0.10; // Mock hạng thành viên
                view.hienThiThongTinKhach(kh.getHoTen() + " (Thành viên -10%)", true);
            } else {
                lamMoiKhachHang();
                view.hienThiThongTinKhach("Khách vãng lai (Chưa ĐK)", false);
            }
        } catch (Exception e) { lamMoiKhachHang(); }
        capNhatGioHangVaTien();
    }

    private void lamMoiKhachHang() {
        maKhachHangDangChon = null; phanTramGiamGia = 0.0;
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

        view.refreshBaoCaoTien(tongTienHang, tienGiamGia, tongTienPhaiTra, tongTienPhaiTra * 0.5, conLai, tienThua, tienKhachDua < soTienGhiNhan);
        view.refreshBangGioHang(gioHangItems, tatCaSanPham);
        view.toggleNutThanhToan(!gioHangItems.isEmpty() && view.isXacNhanThuTien());
    }

    public void xuLyDatBanh() {
        double tongTienPhaiTra = view.getTongThanhToanHienTai();
        double tienKhachDua = view.getTienKhachDua();
        double tienCocNhapVao = view.getTienCoc();
        double soTienGhiNhan = (tienCocNhapVao > 0) ? tienCocNhapVao : Math.min(tongTienPhaiTra, tienKhachDua);

        if (soTienGhiNhan < (tongTienPhaiTra * 0.5)) { view.hienThiLoi("Cọc chưa đủ 50%"); return; }
        if (tienKhachDua > 0 && tienKhachDua < soTienGhiNhan) { view.hienThiLoi("Đưa không đủ tiền cọc"); return; }

        Integer hinhThucNhan = view.getHinhThucNhan();
        if (hinhThucNhan == 2 && view.getDiaChiGiao().isEmpty()) { view.hienThiLoi("Bắt buộc nhập địa chỉ!"); return; }

        try {
            YeuCauTaoDonHangDTO request = mapToDonDatHangDTO(hinhThucNhan, soTienGhiNhan);
            int maDonMoi = orderService.taoDonHang(request);
            view.hienThiThanhCong("Tạo đơn đặt trước thành công! Mã: " + maDonMoi);
            view.inPhieuPopup("PHIẾU HẸN LẤY BÁNH", maDonMoi, tongTienPhaiTra, soTienGhiNhan, gioHangItems, tatCaSanPham, phanTramGiamGia);
            resetState();
        } catch (Exception e) { view.hienThiLoi(e.getMessage()); }
    }

    public void xuLyThanhToanTrucTiep() {
        double tongTienPhaiTra = view.getTongThanhToanHienTai();
        double tienKhachDua = view.getTienKhachDua();

        if (tienKhachDua < tongTienPhaiTra) { view.hienThiLoi("Khách đưa chưa đủ tiền!"); return; }

        try {
            YeuCauTaoDonHangDTO request = mapToDonDatHangDTO(1, tongTienPhaiTra);
            orderService.thanhToanTrucTiep(request, tienKhachDua);
            view.hienThiThanhCong("Thanh toán hoàn tất!");
            view.inPhieuPopup("HÓA ĐƠN BÁN LẺ", -1, tongTienPhaiTra, tongTienPhaiTra, gioHangItems, tatCaSanPham, phanTramGiamGia);
            resetState();
        } catch (Exception e) { view.hienThiLoi(e.getMessage()); }
    }

    public void traCuuDonHang(String maDonStr) {
        try {
            int maDon = Integer.parseInt(maDonStr.trim());
            DonDatHangDTO tomTat = orderService.layTomTatDonHang(maDon);
            String tenTT = orderService.theoDoiDonHang(maDon);
            view.hienThiKetQuaTraCuu(tomTat.getMaKH() == null ? "Khách lẻ" : "Mã KH: " + tomTat.getMaKH(), tenTT, tomTat.getTongTienHDBan());
        } catch (Exception e) { view.hienThiLoiTraCuu(e.getMessage()); }
    }

    public void capNhatTrangThai(String maDonStr, String ttMoi) {
        try {
            int maDon = Integer.parseInt(maDonStr.trim());
            Integer maTtMoi = mapTrangThaiMoi.get(ttMoi);
            if (maTtMoi == null) throw new Exception("Trạng thái không hợp lệ.");

            orderService.chuyenTrangThaiDon(maDon, maTtMoi, MOCK_CURRENT_USER_ID, null, view.getTrangThaiHienTaiTraCuu(), ttMoi);
            view.hienThiKetQuaTraCuu(null, ttMoi, -1); // Chỉ update trạng thái
            view.hienThiThongBaoTraCuu("Cập nhật thành công!");
        } catch (Exception e) { view.hienThiLoiTraCuu(e.getMessage()); }
    }

    private YeuCauTaoDonHangDTO mapToDonDatHangDTO(Integer hinhThuc, double tienDaCoc) {
        YeuCauTaoDonHangDTO req = new YeuCauTaoDonHangDTO();
        req.setNgayGioNhanBanh(view.getNgayGioNhanBanh());
        req.setMaKH(maKhachHangDangChon); req.setMaNVLap(MOCK_CURRENT_USER_ID); req.setMaTrangThai(0);
        req.setTienDaCoc(tienDaCoc); req.setHinhThucNhan(hinhThuc);
        req.setDiaChiGiao(hinhThuc == 2 ? view.getDiaChiGiao() : null);

        List<YeuCauChiTietDonHangDTO> items = new ArrayList<>();
        for (CTDonHangDTO g : gioHangItems) {
            YeuCauChiTietDonHangDTO dto = new YeuCauChiTietDonHangDTO();
            dto.setMaSP(g.getMaSP()); dto.setSoLuong(g.getSoLuong()); dto.setDonGia(g.getDonGia());
            dto.setCustom(false); dto.setGhiChu(""); dto.setPhuKien("");
            items.add(dto);
        }
        req.setItems(items);
        return req;
    }

    private void resetState() {
        gioHangItems.clear();
        lamMoiKhachHang();
        view.lamMoiForm();
        capNhatGioHangVaTien();
    }

    private String normalize(String raw) {
        if (raw == null) return "";
        return java.text.Normalizer.normalize(raw, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").replace("đ", "d").replace("Đ", "D")
                .toUpperCase(Locale.ROOT).replace(' ', '_');
    }
}