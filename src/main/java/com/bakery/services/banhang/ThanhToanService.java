package com.bakery.services.banhang;

import com.bakery.model.dao.banhang.HoaDonDAO;
import com.bakery.model.dao.banhang.PhuongThucTTDAO;
import com.bakery.model.dao.hethong.LoaiThuChiDAO;
import com.bakery.model.dao.hethong.PhieuThuChiDAO;
import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.banhang.HoaDonDTO;
import com.bakery.model.dto.banhang.YeuCauTaoDonHangDTO;
import com.bakery.utils.SessionContext;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Service duy nhất chịu trách nhiệm xử lý Thanh toán:
 * Thanh toán trực tiếp tại quầy, chốt hóa đơn đặt hàng.
 * (SRP – Single Responsibility Principle)
 */
public class ThanhToanService {

    /** Thuế VAT mặc định — đổi 1 chỗ áp dụng toàn service. */
    private static final double THUE_VAT = 0.085;

    private final HoaDonDAO hoaDonDAO;
    private final QuanLyDonHangService quanLyDonHangService;
    private final PhieuThuChiDAO phieuThuChiDAO;
    private final LoaiThuChiDAO loaiThuChiDAO;
    private final PhuongThucTTDAO phuongThucTTDAO;

    public ThanhToanService() {
        this.hoaDonDAO = new HoaDonDAO();
        this.quanLyDonHangService = new QuanLyDonHangService();
        this.phieuThuChiDAO = new PhieuThuChiDAO();
        this.loaiThuChiDAO = new LoaiThuChiDAO();
        this.phuongThucTTDAO = new PhuongThucTTDAO();
    }

    public ThanhToanService(HoaDonDAO hoaDonDAO, QuanLyDonHangService quanLyDonHangService,
            PhieuThuChiDAO phieuThuChiDAO) {
        this.hoaDonDAO = hoaDonDAO;
        this.quanLyDonHangService = quanLyDonHangService;
        this.phieuThuChiDAO = phieuThuChiDAO;
        this.loaiThuChiDAO = new LoaiThuChiDAO();
        this.phuongThucTTDAO = new PhuongThucTTDAO();
    }

    public double tinhTienHoaDon(YeuCauTaoDonHangDTO request) {
        if (request == null || request.getItems() == null)
            return 0.0;
        double subtotal = request.getItems().stream()
                .mapToDouble(i -> i.getDonGia() * i.getSoLuong())
                .sum();
        return subtotal * (1 + THUE_VAT);
    }

    /**
     * Tổng tiền gốc không nhân thuế — khớp đúng với TONGTIENHDBAN mà
     * PROC_TAODONHANG tính từ JSON.
     */
    private double tinhTienGoc(YeuCauTaoDonHangDTO request) {
        if (request == null || request.getItems() == null)
            return 0.0;
        return request.getItems().stream()
                .mapToDouble(i -> i.getDonGia() * i.getSoLuong())
                .sum();
    }

    /**
     * Tạo đơn hàng HOÀN_THÀNH và xuất hóa đơn bán lẻ ngay.
     * Dùng cho luồng thanh toán trực tiếp tại quầy.
     */
    public HoaDonDTO thanhToanTrucTiep(YeuCauTaoDonHangDTO request, double soTienKhachDua) throws Exception {
        // 1. Kiểm tra ca làm việc — FK_HD_CA yêu cầu MACA phải tồn tại
        if (!SessionContext.getInstance().isCaoDangMo())
            throw new IllegalStateException("Chưa mở ca làm việc. Vui lòng mở ca trước khi thực hiện thanh toán.");

        // 2. Tính tổng tiền từ giỏ hàng (có thuế 8.5% — dùng cho hiển thị & validate)
        double tongTien = tinhTienHoaDon(request);

        // 3. Validate số tiền khách đưa
        if (soTienKhachDua < tongTien)
            throw new IllegalArgumentException("Số tiền khách đưa không đủ để thanh toán.");

        // 3. Đặt trạng thái HOÀN_THÀNH
        int maTrangThaiHoanThanh = layMaTrangThaiHoanThanh();
        request.setMaTrangThai(maTrangThaiHoanThanh);


        // Thanh toán thẳng: TIENDACOC = 0 (không cọc, hóa đơn xử lý thanh toán)
        // tongTienGoc vẫn cần để tạo HoaDonDTO ở bước 5
        double tongTienGoc = tinhTienGoc(request);
        request.setTienDaCoc(0);

        // 4. Tạo đơn hàng
        int maDon = quanLyDonHangService.taoDonHang(request);

        // 5. Tạo hóa đơn bán lẻ
        // Bug 2 Fix: đọc MAPTTT từ request (do Presenter truyền từ hình thức thanh toán UI)
        int maPTTT = request.getMaPTTT() > 0 ? request.getMaPTTT() : layMaPTTTTienMat();
        HoaDonDTO hd = taoHoaDonDTO(maDon, tongTienGoc, tongTien, "BAN_LE", maPTTT);

        int maHD = hoaDonDAO.themHoaDonMoi(hd);
        if (maHD <= 0)
            throw new Exception("Không thể tạo hóa đơn cho đơn hàng " + maDon);

        // 6. Chốt hóa đơn & cộng điểm thành viên qua Procedure
        try {
            hoaDonDAO.thanhToanVaThangHang(maHD, request.getMaKH(), tongTien);

            // 6.1 Tạo phiếu thu đi kèm (Sổ quỹ)
            // Bug 2 Fix: CHỈ tạo phiếu thu khi thanh toán bằng tiền mặt
            if (isTienMat(hd.getMaPTTT())) {
                taoPhieuThuChiTuHoaDon(maHD, tongTien, "Thu tiền bán lẻ HD" + maHD);
            }

        } catch (SQLException e) {
            throw new Exception("Thanh toán thất bại: " + e.getMessage(), e);
        }

        // 7. Trả về hóa đơn đầy đủ để in
        HoaDonDTO hoaDonVuaTao = hoaDonDAO.layHoaDonTheoMa(maHD);
        if (hoaDonVuaTao == null)
            throw new Exception("Không thể tải thông tin hóa đơn vừa tạo.");
        return hoaDonVuaTao;
    }

    /**
     * Tạo và chốt hóa đơn khi đơn đặt hàng chuyển sang HOÀN_THÀNH.
     * Được gọi bởi TheoDoDonService sau khi chuyển trạng thái.
     */
    public HoaDonDTO chotHoaDonDatHang(DonDatHangDTO donHang) throws Exception {
        if (donHang == null)
            throw new IllegalArgumentException("Thông tin đơn hàng bị trống.");
        // Kiểm tra ca làm việc — FK_HD_CA yêu cầu MACA phải tồn tại
        if (!SessionContext.getInstance().isCaoDangMo())
            throw new IllegalStateException("Chưa mở ca làm việc. Vui lòng mở ca trước khi chốt hóa đơn.");

        double tongTienGoc = donHang.getTongTienHDBan() != null ? donHang.getTongTienHDBan().doubleValue() : 0.0;
        double tienCoc = donHang.getTienDaCoc() != null ? donHang.getTienDaCoc().doubleValue() : 0.0;
        double tongTienCoThue = tongTienGoc * (1 + THUE_VAT);
        double tongTienConLai = Math.max(0, tongTienCoThue - tienCoc);

        HoaDonDTO hd = taoHoaDonDTO(donHang.getMaDon(), tongTienGoc, tongTienConLai, "DAT_HANG");
        hd.setMaDon(donHang.getMaDon());

        try {
            int maHD = hoaDonDAO.themHoaDonMoi(hd);
            if (maHD > 0) {
                hoaDonDAO.thanhToanVaThangHang(maHD, donHang.getMaKH(), tongTienCoThue);

                // Tạo phiếu thu đi kèm (Sổ quỹ)
                // Bug 2 Fix: chỉ tạo phiếu thu khi tiền mặt
                if (isTienMat(hd.getMaPTTT())) {
                    taoPhieuThuChiTuHoaDon(maHD, tongTienConLai, "Thu tiền đơn hàng HD" + maHD);
                }

                return hoaDonDAO.layHoaDonTheoMa(maHD);
            }
            return null;
        } catch (SQLException e) {
            throw new Exception("Chốt hóa đơn đặt hàng thất bại: " + e.getMessage(), e);
        }
    }

    // =========================================================
    // PRIVATE – HỖ TRỢ NỘI BỘ
    // =========================================================

    /**
     * Tạo một đối tượng HoaDonDTO cơ bản.
     * 
     * @param maDon   Mã đơn hàng liên kết
     * @param tienGoc Tiền hàng trước thuế (dùng cho báo cáo doanh thu/lợi nhuận)
     * @param soTien  Tổng tiền thanh toán đã gồm thuế
     * @param loaiHD  Loại hóa đơn: BAN_LE, DAT_HANG
     */
    public HoaDonDTO taoHoaDonDTO(int maDon, double tienGoc, double soTien, String loaiHD) {
        return taoHoaDonDTO(maDon, tienGoc, soTien, loaiHD, layMaPTTTTienMat());
    }

    /**
     * Overload chính — nhận maPTTT rõ ràng từ request.
     * Bug 2 Fix: không hardcode tiền mặt — Presenter truyền đúng MAPTTT xuống.
     */
    public HoaDonDTO taoHoaDonDTO(int maDon, double tienGoc, double soTien, String loaiHD, int maPTTT) {
        HoaDonDTO hd = new HoaDonDTO();
        hd.setMaDon(maDon);
        hd.setMaCa(SessionContext.getInstance().getMaCa());
        hd.setThueVAT(THUE_VAT * 100);
        hd.setTienHangGoc(java.math.BigDecimal.valueOf(tienGoc));
        hd.setTongTienThanhToan(java.math.BigDecimal.valueOf(soTien));
        hd.setMaPTTT(maPTTT);
        hd.setLoaiHD(loaiHD);
        return hd;
    }

    /** Overload tương thích ngược: tienGoc = soTien (không tách biệt). */
    public HoaDonDTO taoHoaDonDTO(int maDon, double soTien, String loaiHD) {
        return taoHoaDonDTO(maDon, soTien, soTien, loaiHD);
    }

    /**
     * Tạo phiếu thu liên kết với hóa đơn vào bảng PHIEUTHUCHI.
     * Tự động upsert loại "Thu từ bán hàng" nếu chưa có trong LOAITHUCHI.
     * Nếu không thể lưu phiếu thu → chỉ log warning, không rollback thanh toán.
     */
    private void taoPhieuThuChiTuHoaDon(int maHD, double soTien, String ghiChu) {
        try {
            // Tìm loại thu "Thu từ bán hàng" — dò nhiều biến thể tên
            int maLoaiThu = phieuThuChiDAO.layMaLoaiTheoTen("Thu từ bán hàng");
            if (maLoaiThu == -1)
                maLoaiThu = phieuThuChiDAO.layMaLoaiTheoTen("Thu tu ban hang");
            if (maLoaiThu == -1)
                maLoaiThu = phieuThuChiDAO.layMaLoaiTheoTen("Bán hàng");
            if (maLoaiThu == -1)
                maLoaiThu = phieuThuChiDAO.layMaLoaiTheoTen("Ban hang");

            // Nếu vẫn không tìm thấy → auto-insert loại mặc định
            if (maLoaiThu == -1) {
                maLoaiThu = upsertLoaiThuBanHang();
            }

            // Nếu vẫn không có (DB lỗi) → skip, không crash
            if (maLoaiThu == -1) {
                System.err.println(
                        "[ThanhToanService] Khong the tao phieu thu: khong tim duoc LOAITHUCHI 'Thu tu ban hang'. Skip.");
                return;
            }

            com.bakery.model.dto.hethong.PhieuThuChiDTO ptc = new com.bakery.model.dto.hethong.PhieuThuChiDTO();
            ptc.setMaLoaiThuChi(maLoaiThu);
            ptc.setSoTien(BigDecimal.valueOf(soTien));
            ptc.setMaNV(SessionContext.getInstance().getMaNV());
            ptc.setMaHD(maHD);
            ptc.setMaCa(SessionContext.getInstance().getMaCa());
            ptc.setGhiChu(ghiChu);

            phieuThuChiDAO.taoPhieuThuChi(ptc);

        } catch (Exception e) {
            // Phiếu thu chỉ là ghi sổ — không được phép rollback thanh toán đã thành công
            System.err
                    .println("[ThanhToanService] Warning: Khong the tao phieu thu HD#" + maHD + ": " + e.getMessage());
        }
    }

    /**
     * Tự động INSERT loại "Thu từ bán hàng" vào LOAITHUCHI nếu chưa tồn tại.
     * Trả về mã mới hoặc -1 nếu thất bại.
     */
    private int upsertLoaiThuBanHang() {
        try {
            loaiThuChiDAO.them("Thu từ bán hàng", "Thu");
            return phieuThuChiDAO.layMaLoaiTheoTen("Thu từ bán hàng");
        } catch (Exception e) {
            System.err.println("[ThanhToanService] upsertLoaiThuBanHang failed: " + e.getMessage());
        }
        return -1;
    }

    private int layMaTrangThaiHoanThanh() throws Exception {
        return quanLyDonHangService.layDanhSachTrangThaiDon().stream()
                .filter(tt -> "HOAN_THANH".equals(com.bakery.utils.StringUtil.chuanHoa(tt.getTenTrangThai())))
                .mapToInt(tt -> tt.getMaTrangThai())
                .findFirst()
                .orElseThrow(() -> new Exception("Không tìm thấy trạng thái HOÀN_THÀNH."));
    }

    /**
     * Task 1.4: Tra cứu MAPTTT Tiền mặt động từ DB.
     * Fallback về 1 chỉ khi DB không có bản ghi PHUONGTHUCTT nào phù hợp.
     */
    private int layMaPTTTTienMat() {
        try {
            int ma = phuongThucTTDAO.layMaTheoTen("Tiền mặt");
            if (ma == -1) ma = phuongThucTTDAO.layMaTheoTen("tien mat");
            if (ma == -1) ma = phuongThucTTDAO.layMaTheoTen("mặt");
            if (ma != -1) return ma;
        } catch (Exception e) {
            System.err.println("[ThanhToanService] layMaPTTTTienMat fallback: " + e.getMessage());
        }
        return 1; // Fallback môi trường test
    }

    /**
     * Bug 2 Fix: Kiểm tra maPTTT có phải tiền mặt không — lazy cache 1 lần đầu.
     * Chỉ tạo phiếu thu khi thanh toán bằng tiền mặt thực sự.
     */
    private volatile int cachedMaPTTTTienMat = 0;

    private boolean isTienMat(int maPTTT) {
        if (cachedMaPTTTTienMat == 0) {
            cachedMaPTTTTienMat = layMaPTTTTienMat();
        }
        return maPTTT == cachedMaPTTTTienMat;
    }
}
