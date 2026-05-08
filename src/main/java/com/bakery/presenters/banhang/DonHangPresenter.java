package com.bakery.presenters.banhang;

import com.bakery.model.dto.banhang.CTDonHangDTO;
import com.bakery.model.dto.banhang.CTDonTuyChinhDTO;
import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.banhang.HoaDonDTO;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.model.dto.banhang.TrangThaiDonDTO;
import com.bakery.model.dto.banhang.YeuCauChiTietDonHangDTO;
import com.bakery.model.dto.banhang.YeuCauChiTietDonTuyChinhDTO;
import com.bakery.model.dto.banhang.YeuCauTaoDonHangDTO;
import com.bakery.services.banhang.DonHangService;
import com.bakery.views.interfaces.banhang.IDonHangDialogFactory;
import com.bakery.views.interfaces.banhang.IDonHangView;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.bakery.utils.UserSession;

public class DonHangPresenter {
    private final IDonHangView view;
    private final DonHangService orderService;
    // Phương án A: Presenter gọi dialog qua interface factory
    private IDonHangDialogFactory dialogFactory;

    private List<SanPhamDTO> tatCaSanPham = new ArrayList<>();
    private final List<YeuCauChiTietDonHangDTO> gioHangItems = new ArrayList<>();
    private final Map<String, Integer> mapTrangThaiMoi = new HashMap<>();
    private final Map<Integer, String> mapDanhMuc = new HashMap<>();
    private double phanTramGiamGia = 0.0;

    private String lastSearchMaDon = "";
    private String lastSearchTenKhach = "";
    private LocalDate lastSearchNgay = LocalDate.now();
    private LocalTime lastSearchTu = null;
    private LocalTime lastSearchDen = null;
    private String lastSearchTrangThai = "ALL";

    private int getCurrentUserId() {
        if (UserSession.getCurrentUser() != null) {
            return UserSession.getCurrentUser().getMaNV();
        }
        return 1; // Fallback an toàn
    }

    private List<CTDonHangDTO> convertToCTDonHangList(List<YeuCauChiTietDonHangDTO> items) {
        List<CTDonHangDTO> list = new ArrayList<>();
        for (YeuCauChiTietDonHangDTO item : items) {
            CTDonHangDTO dto = new CTDonHangDTO();
            dto.setMaSP(item.getMaSP());
            dto.setSoLuong(item.getSoLuong());
            dto.setDonGia(java.math.BigDecimal.valueOf(item.getDonGia()));
            list.add(dto);
        }
        return list;
    }

    public DonHangPresenter(IDonHangView view, DonHangService orderService) {
        this.view = view;
        this.orderService = orderService;
    }

    public DonHangPresenter(IDonHangView view, DonHangService orderService, IDonHangDialogFactory dialogFactory) {
        this.view = view;
        this.orderService = orderService;
        this.dialogFactory = dialogFactory;
    }

    /** Inject dialog factory sau khi khởi tạo (Phương án A — tránh circular dep) */
    public void setDialogFactory(IDonHangDialogFactory dialogFactory) {
        this.dialogFactory = dialogFactory;
    }

    public void taiDuLieuBanDau() {
        try {
            // Bảo vệ phiên đăng nhập trước khi thực hiện nghiệp vụ
            com.bakery.utils.SessionValidator.requireValidSession();

            tatCaSanPham = orderService.layDanhSachSanPhamPOS();
            mapDanhMuc.putAll(orderService.layMapDanhMucSanPham());

            mapTrangThaiMoi.clear();
            List<TrangThaiDonDTO> dsTrangThai = orderService.layDanhSachTrangThaiDon();
            for (TrangThaiDonDTO tt : dsTrangThai) {
                mapTrangThaiMoi.put(tt.getTenTrangThai(), tt.getMaTrangThai());
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
            timKiemDonTheoDoi(null, null, LocalDate.now(), null, null, "ALL");
        } catch (Exception e) {
            System.err.println("[DonHangPresenter] Lỗi tải dữ liệu ban đầu: " + e.getMessage());
            view.hienThiLoi("Lỗi tải dữ liệu ban đầu: " + e.getMessage() + ". Đang sử dụng dữ liệu ảo.");
            view.hienThiDanhSachSanPham(new ArrayList<>(), new HashMap<>());
            view.hienThiDuLieuTuyChinh(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
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
            newItem.setDonGia(sp.getGiaBan());
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

    public void onSuaSanPhamTrongGio(YeuCauChiTietDonHangDTO currentItem) {
        try {
            if (currentItem instanceof YeuCauChiTietDonTuyChinhDTO custom) {
                double giaMoi = orderService.tinhGiaBanhTuyChinh(custom.getMaSP(),
                        custom.getMaKC(), custom.getMaCot(),
                        custom.getMaNhan(), custom.getMaTrangTri());
                custom.setDonGia(giaMoi);
            }
            capNhatGioHangVaTien();
        } catch (Exception e) {
            view.hienThiLoi("Không thể tính lại giá bánh: " + e.getMessage());
        }
    }

    public double tinhGiaBanhTuyChinh(int maSP, Integer maKC, Integer maCot, Integer maNhan, Integer maTrangTri) {
        try {
            return orderService.tinhGiaBanhTuyChinh(maSP, maKC, maCot, maNhan, maTrangTri);
        } catch (Exception e) {
            view.hienThiLoi("Lỗi tính giá bánh: " + e.getMessage());
            return 0.0;
        }
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

    public void timKiemSanPham(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                view.hienThiDanhSachSanPham(tatCaSanPham, mapDanhMuc);
                return;
            }

            List<SanPhamDTO> ketQua = new ArrayList<>();
            String kw = keyword.toLowerCase().trim();
            for (SanPhamDTO sp : tatCaSanPham) {
                if (sp.getTenSP().toLowerCase().contains(kw)) {
                    ketQua.add(sp);
                }
            }
            view.hienThiDanhSachSanPham(ketQua, mapDanhMuc);
        } catch (Exception e) {
            view.hienThiLoi("Lỗi tìm kiếm: " + e.getMessage());
        }
    }

    public void traCuuKhachHang(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) {
            lamMoiKhachHang();
            return;
        }
        try {
            KhachHangDTO kh = orderService.timKhachHangTheoSoDienThoai(sdt);
            if (kh != null) {
                phanTramGiamGia = 0.10;
                view.hienThiThongTinKhach(kh.getHoTen() + " (Thành viên -10%)", true);
                // Cập nhật khách hàng hiện tại vào View để dùng cho việc Sửa
                view.capNhatKhachHangHienTai(kh);
            } else {
                lamMoiKhachHang();
                view.hienThiThongTinKhach("Khách chưa là thành viên! ", false);
                view.capNhatKhachHangHienTai(null);
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
        YeuCauTaoDonHangDTO request = new YeuCauTaoDonHangDTO();
        request.setItems(new ArrayList<>(gioHangItems));

        double tongTienHang = orderService.tinhTienHoaDon(request);
        double tienGiamGia = tongTienHang * phanTramGiamGia;
        double tongTienPhaiTra = tongTienHang - tienGiamGia;
        double minCoc = tongTienPhaiTra * 0.5;

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
        if (dialogFactory == null) {
            view.hienThiLoi("Lỗi hệ thống: dialogFactory chưa được khởi tạo.");
            return;
        }

        // Kiểm tra tồn kho trước khi mở dialog thanh toán (Fail-Fast)
        java.util.List<String> dsThieuTon;
        try {
            dsThieuTon = orderService.kiemTraTonKhoGioHang(gioHangItems);
        } catch (Exception e) {
            view.hienThiLoi("Lỗi kiểm tra tồn kho: " + e.getMessage());
            return;
        }
        if (!dsThieuTon.isEmpty()) {
            StringBuilder sb = new StringBuilder("Không đủ tồn kho cho các sản phẩm:\n");
            for (String thieu : dsThieuTon) {
                sb.append("• ").append(thieu).append("\n");
            }
            view.hienThiLoi(sb.toString().trim());
            return;
        }

        YeuCauTaoDonHangDTO calcReq = new YeuCauTaoDonHangDTO();
        calcReq.setItems(new ArrayList<>(gioHangItems));
        double tongTienPhaiTra = orderService.tinhTienHoaDon(calcReq) * (1 - phanTramGiamGia);

        // Phương án A: Presenter gọi factory qua interface, không biết JavaFX
        IDonHangDialogFactory.TraCuuKhachHang lookup = sdt -> {
            try {
                KhachHangDTO kh = orderService.timKhachHangTheoSoDienThoai(sdt);
                if (kh != null)
                    return new String[] { String.valueOf(kh.getMaKH()), kh.getHoTen() };
            } catch (Exception ignored) {
            }
            return null;
        };

        IDonHangDialogFactory.YeuCauDonHang req = dialogFactory.showCreateOrderDialog(tongTienPhaiTra, lookup);
        if (!req.confirmed())
            return;

        // Áp dụng thông tin khách hàng từ dialog
        phanTramGiamGia = (req.maKH() != null) ? 0.10 : 0.0;
        view.hienThiThongTinKhach(req.tenKhach(), req.maKH() != null);

        try {
            if (req.orderType() == IDonHangDialogFactory.LoaiDonHang.IMMEDIATE) {
                xuLyThanhToanNgay(req, tongTienPhaiTra);
            } else {
                xuLyDatTruoc(req, tongTienPhaiTra);
            }
            lamMoiTrangThai();
            timKiemDonTheoDoi(lastSearchMaDon, lastSearchTenKhach, lastSearchNgay, lastSearchTu, lastSearchDen, lastSearchTrangThai);
        } catch (Exception e) {
            view.hienThiLoi("Lỗi: " + e.getMessage());
        }
    }

    /** Luồng 1: Thanh toán ngay (trực tiếp tại quầy) */
    private void xuLyThanhToanNgay(IDonHangDialogFactory.YeuCauDonHang req, double tongTien) throws Exception {
        YeuCauTaoDonHangDTO request = new YeuCauTaoDonHangDTO();
        request.setMaKH(req.maKH());
        request.setMaNVLap(getCurrentUserId());
        request.setTienDaCoc(tongTien);
        request.setHinhThucNhan(1); // Trực tiếp
        // Đặt thời gian nhận = ngay bây giờ + đệm 30s để tránh lỗi validation "quá khứ"
        // phía DB
        request.setNgayGioNhanBanh(LocalDateTime.now().plusSeconds(30));
        request.setDiaChiGiao(null);
        request.setItems(new ArrayList<>(gioHangItems));

        HoaDonDTO hd = orderService.thanhToanTrucTiep(request, req.soTienKhachDua());
        view.hienThiThanhCong("Đã thanh toán! Mã HĐ: #" + hd.getMaHD());
        view.inPhieuHoaDon("HÓA ĐƠN BÁN LẺ",
                hd, null,
                convertToCTDonHangList(gioHangItems), tatCaSanPham, phanTramGiamGia,
                req.soTienKhachDua(), Math.max(0, req.soTienKhachDua() - tongTien), false);
    }

    /** Luồng 2: Đặt trước (pre-order) */
    private void xuLyDatTruoc(IDonHangDialogFactory.YeuCauDonHang req, double tongTien) throws Exception {
        YeuCauTaoDonHangDTO request = new YeuCauTaoDonHangDTO();
        request.setMaKH(req.maKH());
        request.setMaNVLap(getCurrentUserId());
        request.setTienDaCoc(req.tienCoc());
        request.setHinhThucNhan(2); // Đặt hàng
        request.setNgayGioNhanBanh(req.ngayGioNhan());
        request.setDiaChiGiao(req.diaChiGiao());
        request.setItems(new ArrayList<>(gioHangItems));

        int maDon = orderService.submitNewOrder(request);
        view.hienThiThanhCong("Đặt đơn thành công! Mã đơn: #" + maDon);
        DonDatHangDTO don = new DonDatHangDTO();
        don.setMaDon(maDon);
        don.setTienDaCoc(java.math.BigDecimal.valueOf(req.tienCoc()));
        don.setNgayGioNhanBanh(req.ngayGioNhan());

        HoaDonDTO hd = orderService.taoHoaDonDTO(maDon, req.tienCoc(), "DAT_HANG");
        hd.setNgayXuatHd(LocalDateTime.now());

        view.inPhieuHoaDon("PHIẾU HẸN LẤY BÁNH",
                hd, don,
                convertToCTDonHangList(gioHangItems), tatCaSanPham, phanTramGiamGia,
                req.soTienKhachDua(), Math.max(0, req.soTienKhachDua() - req.tienCoc()), true);
    }

    public void traCuuDonHang(String maDonStr) {
        try {
            int maDon = Integer.parseInt(maDonStr.trim());
            DonDatHangDTO tomTat = orderService.loadOrderById(maDon);
            view.showOrderDetails(tomTat);
            view.hienThiKetQuaTraCuu(tomTat.getMaKH() == null ? "Khách lẻ" : "Mã KH: " + tomTat.getMaKH(),
                    tomTat.getTenTrangThai(),
                    tomTat.getTongTienHDBan() != null ? tomTat.getTongTienHDBan().doubleValue() : 0.0);
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
            if ("HOAN_THANH".equals(com.bakery.utils.StringUtil.chuanHoa(ttMoi))) {
                double tongTien = donHienTai.getTongTienHDBan() != null ? donHienTai.getTongTienHDBan().doubleValue()
                        : 0.0;
                double daCoc = donHienTai.getTienDaCoc() != null ? donHienTai.getTienDaCoc().doubleValue() : 0.0;
                double conLai = Math.max(0, tongTien - daCoc);

                if (conLai > 0) {
                    if (dialogFactory == null) {
                        view.hienThiLoi("Lỗi hệ thống: dialogFactory chưa được khởi tạo.");
                        return;
                    }
                    boolean xacNhan = dialogFactory.showPaymentConfirmation(maDon, tongTien, daCoc, conLai);
                    if (!xacNhan) {
                        return; // Hủy cập nhật nếu không xác nhận thanh toán
                    }
                }
            }

            hoaDonMoi = orderService.chuyenTrangThaiDon(maDon, maTtMoi, getCurrentUserId(),
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

                    double tongTienHD = hoaDonMoi.getTongTienThanhToan() != null
                            ? hoaDonMoi.getTongTienThanhToan().doubleValue()
                            : 0.0;
                    view.inPhieuHoaDon(
                            "HÓA ĐƠN THANH TOÁN",
                            hoaDonMoi,
                            donHienTai,
                            items,
                            tatCaSanPham,
                            0.0,
                            tongTienHD, 0.0, false);
                } catch (Exception ex) {
                    view.hienThiLoi("Không thể tải chi tiết đơn hàng để in hóa đơn.");
                }
            }

            view.hienThiThongBaoTraCuu("Cập nhật thành công đơn #" + maDon);
            timKiemDonTheoDoi(lastSearchMaDon, lastSearchTenKhach, lastSearchNgay, lastSearchTu, lastSearchDen, lastSearchTrangThai);
        } catch (Exception e) {
            view.hienThiLoiTraCuu(e.getMessage());
        }
    }

    public void huyDonHang(String maDonStr) {
        // Bước 1: Validate maDon và load thông tin đơn (trên UI thread — nhanh)
        final int maDon;
        try {
            maDon = Integer.parseInt(maDonStr.trim());
        } catch (NumberFormatException e) {
            view.hienThiLoiTraCuu("Mã đơn không hợp lệ.");
            return;
        }

        if (dialogFactory == null) {
            view.hienThiLoi("Lỗi hệ thống: dialogFactory chưa được khởi tạo.");
            return;
        }

        // Bước 2: Load đơn hàng để lấy tiền cọc (hiển thị dialog)
        final DonDatHangDTO don;
        try {
            don = orderService.loadOrderById(maDon);
        } catch (Exception e) {
            view.hienThiLoiTraCuu("Không tải được thông tin đơn #" + maDon + ": " + e.getMessage());
            return;
        }

        // Bước 3: Hiển thị dialog xác nhận (blocking, trên UI thread)
        double daCoc = don.getTienDaCoc() != null ? don.getTienDaCoc().doubleValue() : 0.0;
        IDonHangDialogFactory.YeuCauHuyDonHang req = dialogFactory.showCancelOrderDialog(maDon, daCoc);
        if (!req.confirmed()) return;

        final String lyDoHuy = req.reason();
        final double soTienHoan = req.refundAmount();
        final int maNvHienTai = getCurrentUserId();
        final String tenTrangThai = don.getTenTrangThai();

        // Bước 4: Thực thi DB trên background Task (tránh UI freeze)
        javafx.concurrent.Task<Void> taskHuy = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                orderService.huyDonVaHoanCoc(maDon, lyDoHuy, maNvHienTai, tenTrangThai, soTienHoan);
                return null;
            }
        };

        taskHuy.setOnSucceeded(event -> javafx.application.Platform.runLater(() -> {
            String thongBao = soTienHoan > 0
                    ? "Đã hủy đơn #" + maDon + " và hoàn tiền: " + String.format("%,.0f", soTienHoan) + " đ"
                    : "Đã hủy đơn #" + maDon + " thành công.";
            view.hienThiThongBaoTraCuu(thongBao);
            timKiemDonTheoDoi(lastSearchMaDon, lastSearchTenKhach, lastSearchNgay,
                    lastSearchTu, lastSearchDen, lastSearchTrangThai);
        }));

        taskHuy.setOnFailed(event -> javafx.application.Platform.runLater(() -> {
            Throwable cause = taskHuy.getException();
            view.hienThiLoiTraCuu("Hủy đơn thất bại: " + (cause != null ? cause.getMessage() : "Lỗi không xác định"));
        }));

        new Thread(taskHuy, "thread-huy-don-" + maDon).start();
    }

    /**
     * Hủy hóa đơn bán lẻ đã hoàn thành.
     * Chỉ dành cho Quản lý và Thu ngân có quyền — DB procedure tự kiểm tra điều kiện.
     * Hoàn kho tự động; tiền mặt do quản lý xử lý ngoài hệ thống.
     */
    public void huyHoaDonBanLe(String maDonStr) {
        final int maDon;
        try {
            maDon = Integer.parseInt(maDonStr.trim());
        } catch (NumberFormatException e) {
            view.hienThiLoiTraCuu("Mã đơn không hợp lệ.");
            return;
        }

        if (dialogFactory == null) {
            view.hienThiLoi("Lỗi hệ thống: dialogFactory chưa được khởi tạo.");
            return;
        }

        // Dialog chạy trên UI thread (blocking, đúng)
        IDonHangDialogFactory.YeuCauHuyDonHang req = dialogFactory.showCancelOrderDialog(maDon, 0);
        if (!req.confirmed()) return;

        final String lyDo = req.reason();
        final int maNv = getCurrentUserId();

        // DB call chạy background
        javafx.concurrent.Task<Void> taskHuy = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                orderService.huyHoaDonBanLe(maDon, lyDo, maNv);
                return null;
            }
        };

        taskHuy.setOnSucceeded(event -> javafx.application.Platform.runLater(() -> {
            view.hienThiThongBaoTraCuu("Đã hủy hóa đơn bán lẻ #" + maDon + ". Kho hàng đã được hoàn trả.");
            timKiemDonTheoDoi(lastSearchMaDon, lastSearchTenKhach, lastSearchNgay,
                    lastSearchTu, lastSearchDen, lastSearchTrangThai);
        }));

        taskHuy.setOnFailed(event -> javafx.application.Platform.runLater(() -> {
            Throwable cause = taskHuy.getException();
            view.hienThiLoiTraCuu("Lỗi hủy hóa đơn: " + (cause != null ? cause.getMessage() : "Lỗi không xác định"));
        }));

        new Thread(taskHuy, "thread-huy-hd-" + maDon).start();
    }


    public void timKiemDonTheoDoi(String maDonSearch, String tenKhachSearch, LocalDate ngayNhan, LocalTime gioTu, LocalTime gioDen,
            String trangThaiFilter) {
        this.lastSearchMaDon = maDonSearch;
        this.lastSearchTenKhach = tenKhachSearch;
        this.lastSearchNgay = ngayNhan;
        this.lastSearchTu = gioTu;
        this.lastSearchDen = gioDen;
        this.lastSearchTrangThai = trangThaiFilter == null ? "ALL" : trangThaiFilter;
        try {
            List<DonDatHangDTO> dsDon = orderService.layDanhSachDonTheoDoi(maDonSearch, tenKhachSearch, ngayNhan, gioTu, gioDen,
                    this.lastSearchTrangThai);
            view.hienThiDanhSachDonTheoDoi(dsDon);
        } catch (Exception e) {
            view.hienThiLoiTraCuu(e.getMessage());
        }
    }

    public void timKiemDonTheoDoi(String maDonSearch, String tenKhachSearch, LocalDate ngayNhan, LocalTime gioTu, LocalTime gioDen) {
        timKiemDonTheoDoi(maDonSearch, tenKhachSearch, ngayNhan, gioTu, gioDen, "NOT_COMPLETED");
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

}
