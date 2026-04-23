package com.bakery.presenters;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.CTDonTuyChinhDTO;
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
    private double phanTramGiamGia = 0.0;
    private static final int MOCK_CURRENT_USER_ID = 1;

    private String lastSearchMaDon = "";
    private LocalDate lastSearchNgay = LocalDate.now();
    private LocalTime lastSearchTu = null;
    private LocalTime lastSearchDen = null;

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
        timKiemDonTheoDoi(null, LocalDate.now(), null, null);
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
                phanTramGiamGia = 0.10;
                view.hienThiThongTinKhach(kh.getHoTen() + " (Thành viên -10%)", true);
            } else {
                lamMoiKhachHang();
                view.hienThiThongTinKhach("Khách chưa là thành viên! ", false);
            }
        } catch (Exception e) {
            lamMoiKhachHang();
            view.hienThiLoi("Không tìm được khách hàng: " + e.getMessage());
        }
        capNhatGioHangVaTien();
    }

    private void lamMoiKhachHang() {
        phanTramGiamGia = 0.0;
    }

    public void capNhatGioHangVaTien() {
        double tongTienHang = gioHangItems.stream().mapToDouble(i -> i.getDonGia() * i.getSoLuong()).sum();
        double tienGiamGia = tongTienHang * phanTramGiamGia;
        double tongTienPhaiTra = tongTienHang - tienGiamGia;
        double minCoc = tongTienPhaiTra * 0.5;
        // Truyền 0 cho các giá trị không còn nhập trên màn hình chính
        view.lamMoiBaoCaoTien(tongTienHang, tienGiamGia, tongTienPhaiTra, minCoc, 0, 0, false);
        view.lamMoiBangGioHang(convertToCTDonHangList(gioHangItems), tatCaSanPham);
        view.batTatNutThanhToan(!gioHangItems.isEmpty());
    }

    /**
     * Mở CreateOrderDialog và xử lý kết quả.
     * Đây là phương thức trung tâm của luồng tạo đơn mới.
     */
    public void moDialogTaoDon() {
        if (gioHangItems.isEmpty()) {
            view.hienThiLoi("Đơn hàng đang trống!");
            return;
        }
        double tongTienPhaiTra = gioHangItems.stream()
                .mapToDouble(i -> i.getDonGia() * i.getSoLuong()).sum() * (1 - phanTramGiamGia);

        com.bakery.views.CreateOrderDialog.CustomerLookup lookup = sdt -> {
            try {
                KhachHangDTO kh = orderService.timKhachHangTheoSoDienThoai(sdt);
                if (kh != null) return new String[]{ String.valueOf(kh.getMaKH()), kh.getHoTen() };
            } catch (Exception ignored) {}
            return null;
        };

        com.bakery.views.CreateOrderDialog dialog = new com.bakery.views.CreateOrderDialog(
                null, tongTienPhaiTra, lookup);
        dialog.setVisible(true);

        com.bakery.views.CreateOrderDialog.OrderRequest req = dialog.getResult();
        if (!req.confirmed) return;

        // Áp dụng thông tin khách hàng từ dialog
        phanTramGiamGia = (req.maKH != null) ? 0.10 : 0.0;
        view.hienThiThongTinKhach(req.tenKhach, req.maKH != null);

        try {
            if (req.orderType == com.bakery.views.CreateOrderDialog.OrderType.IMMEDIATE) {
                xuLyThanhToanNgay(req, tongTienPhaiTra);
            } else {
                xuLyDatTruoc(req, tongTienPhaiTra);
            }
            lamMoiTrangThai();
            timKiemDonTheoDoi(lastSearchMaDon, lastSearchNgay, lastSearchTu, lastSearchDen);
        } catch (Exception e) {
            view.hienThiLoi("Lỗi: " + e.getMessage());
        }
    }

    /** Luồng 1: Thanh toán ngay (trực tiếp tại quầy) */
    private void xuLyThanhToanNgay(com.bakery.views.CreateOrderDialog.OrderRequest req, double tongTien) throws Exception {
        YeuCauTaoDonHangDTO request = new YeuCauTaoDonHangDTO();
        request.setMaKH(req.maKH);
        request.setMaNVLap(MOCK_CURRENT_USER_ID);
        request.setTienDaCoc(tongTien);
        request.setHinhThucNhan(1); // Trực tiếp
        // Đặt thời gian nhận = ngay bây giờ + đệm 30s để tránh lỗi validation "quá khứ" phía DB
        request.setNgayGioNhanBanh(LocalDateTime.now().plusSeconds(30));
        request.setDiaChiGiao(null);
        request.setItems(new ArrayList<>(gioHangItems));

        HoaDonDTO hd = orderService.thanhToanTrucTiep(request, req.soTienKhachDua);
        view.hienThiThanhCong("Đã thanh toán! Mã HĐ: #" + hd.getMaHD());
        view.inPhieuHoaDon("HÓA ĐƠN BÁN LẾ",
                hd.getMaDon(), hd.getMaHD(), hd.getNgayXuatHd(),
                tongTien, tongTien,
                convertToCTDonHangList(gioHangItems), tatCaSanPham, phanTramGiamGia);
    }

    /** Luồng 2: Đặt trước (pre-order) */
    private void xuLyDatTruoc(com.bakery.views.CreateOrderDialog.OrderRequest req, double tongTien) throws Exception {
        YeuCauTaoDonHangDTO request = new YeuCauTaoDonHangDTO();
        request.setMaKH(req.maKH);
        request.setMaNVLap(MOCK_CURRENT_USER_ID);
        request.setTienDaCoc(req.tienCoc);
        request.setHinhThucNhan(2); // Đặt hàng
        request.setNgayGioNhanBanh(req.ngayGioNhan);
        request.setDiaChiGiao(req.diaChiGiao);
        request.setItems(new ArrayList<>(gioHangItems));

        int maDon = orderService.submitNewOrder(request);
        view.hienThiThanhCong("Đặt đơn thành công! Mã đơn: #" + maDon);
        view.inPhieuHoaDon("PHIẾU HẸN LẤY BÁNH",
                maDon, null, req.ngayGioNhan,
                tongTien, req.tienCoc,
                convertToCTDonHangList(gioHangItems), tatCaSanPham, phanTramGiamGia);
    }

    /** Giữ lại để tương thích ngược */
    public void xuLyThanhToanModern() { moDialogTaoDon(); }
    public void xuLyDatBanh() { moDialogTaoDon(); }
    public void xuLyThanhToanTrucTiep() { moDialogTaoDon(); }

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

    public void capNhatTrangThai(String maDonStr, String ttMoi, String ttHienTai) {
        try {
            int maDon = Integer.parseInt(maDonStr.trim());
            Integer maTtMoi = mapTrangThaiMoi.get(ttMoi);
            if (maTtMoi == null) {
                throw new Exception("Trạng thái không hợp lệ.");
            }

            DonDatHangDTO donHienTai = orderService.loadOrderById(maDon);

            HoaDonDTO hoaDonMoi = null;
            // Nếu chuyển sang trạng thái Hoàn thành, yêu cầu xác nhận thu tiền còn lại
            if ("HOAN_THANH".equals(chuanHoaTrangThai(ttMoi))) {
                double tongTien = donHienTai.getTongTienHDBan();
                double daCoc = donHienTai.getTienDaCoc();
                double conLai = Math.max(0, tongTien - daCoc);

                if (conLai > 0) {
                    boolean xacNhan = view.hienThiXacNhanThanhToan(maDon, tongTien, daCoc, conLai);
                    if (!xacNhan) {
                        return; // Hủy cập nhật nếu không xác nhận thanh toán
                    }
                }
            }

            hoaDonMoi = orderService.chuyenTrangThaiDon(maDon, maTtMoi, MOCK_CURRENT_USER_ID,
                    donHienTai.getHinhThucNhan(),
                    ttHienTai, ttMoi);

            // Nếu có tạo hóa đơn (đã thanh toán), thực hiện in hóa đơn
            if (hoaDonMoi != null) {
                try {
                    List<CTDonHangDTO> items = orderService.layChiTietDonHang(maDon);
                    List<CTDonTuyChinhDTO> customItems = orderService.layChiTietTuyChinh(maDon);

                    // Gộp các món tùy chỉnh vào danh sách in (convert đơn giản)
                    for (CTDonTuyChinhDTO c : customItems) {
                        CTDonHangDTO clone = new CTDonHangDTO();
                        clone.setMaSP(c.getMaSP());
                        clone.setSoLuong(c.getSoLuong());
                        clone.setDonGia(c.getDonGia());
                        items.add(clone);
                    }

                    view.inPhieuHoaDon(
                            "HOÁ ĐƠN THANH TOÁN",
                            maDon,
                            hoaDonMoi.getMaHD(),
                            hoaDonMoi.getNgayXuatHd(),
                            donHienTai.getTongTienHDBan(),
                            donHienTai.getTongTienHDBan(), // Thu đủ tổng tiền sau khi nộp nốt
                            items,
                            tatCaSanPham, // Dùng dữ liệu đã load sẵn
                            0.0 // Giảm giá đã tính vào tổng tiền đơn hàng từ trước
                    );
                } catch (Exception ex) {
                    view.showError("Không thể tải chi tiết đơn hàng để in hóa đơn.");
                }
            }

            view.hienThiThongBaoTraCuu("Cập nhật thành công đơn #" + maDon);
            timKiemDonTheoDoi(lastSearchMaDon, lastSearchNgay, lastSearchTu, lastSearchDen);
        } catch (Exception e) {
            view.hienThiLoiTraCuu(e.getMessage());
        }
    }

    public void timKiemDonTheoDoi(String maDonSearch, LocalDate ngayNhan, LocalTime gioTu, LocalTime gioDen) {
        this.lastSearchMaDon = maDonSearch;
        this.lastSearchNgay = ngayNhan;
        this.lastSearchTu = gioTu;
        this.lastSearchDen = gioDen;
        try {
            List<DonDatHangDTO> dsDon = orderService.layDanhSachDonTheoDoi(maDonSearch, ngayNhan, gioTu, gioDen);
            view.hienThiDanhSachDonTheoDoi(dsDon);
        } catch (Exception e) {
            view.hienThiLoiTraCuu(e.getMessage());
        }
    }



    private void lamMoiTrangThai() {
        gioHangItems.clear();
        lamMoiKhachHang();
        view.lamMoiForm();
        capNhatGioHangVaTien();
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

    private String chuanHoaTrangThai(String rawStatus) {
        if (rawStatus == null)
            return "";
        String normalized = java.text.Normalizer.normalize(rawStatus.trim(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d").replace("Đ", "D")
                .replace("Ä‘", "d").replace("Ä ", "D")
                .toUpperCase().replace(' ', '_');
        if (normalized.contains("KHACH") && normalized.contains("LAY"))
            return "CHO_KHACH_LAY";
        return normalized;
    }
}
