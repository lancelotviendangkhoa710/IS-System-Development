package com.bakery.services.banhang;

import com.bakery.model.dao.banhang.HoaDonDAO;
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

    private final HoaDonDAO hoaDonDAO;
    private final QuanLyDonHangService quanLyDonHangService;
    private final PhieuThuChiDAO phieuThuChiDAO;
    private final LoaiThuChiDAO loaiThuChiDAO;

    public ThanhToanService() {
        this.hoaDonDAO = new HoaDonDAO();
        this.quanLyDonHangService = new QuanLyDonHangService();
        this.phieuThuChiDAO = new PhieuThuChiDAO();
        this.loaiThuChiDAO = new LoaiThuChiDAO();
    }

    public ThanhToanService(HoaDonDAO hoaDonDAO, QuanLyDonHangService quanLyDonHangService,
            PhieuThuChiDAO phieuThuChiDAO) {
        this.hoaDonDAO = hoaDonDAO;
        this.quanLyDonHangService = quanLyDonHangService;
        this.phieuThuChiDAO = phieuThuChiDAO;
        this.loaiThuChiDAO = new LoaiThuChiDAO();
    }

    public double tinhTienHoaDon(YeuCauTaoDonHangDTO request) {
        if (request == null || request.getItems() == null)
            return 0.0;
        double subtotal = request.getItems().stream()
                .mapToDouble(i -> i.getDonGia() * i.getSoLuong())
                .sum();
        return subtotal * 1.085;
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


        double tongTienGoc = tinhTienGoc(request);
        request.setTienDaCoc(tongTienGoc);

        // 4. Tạo đơn hàng
        int maDon = quanLyDonHangService.taoDonHang(request);

        // 5. Tạo hóa đơn bán lẻ (tienGoc = tongTienGoc trước thuế)
        HoaDonDTO hd = taoHoaDonDTO(maDon, tongTienGoc, tongTien, "BAN_LE");

        int maHD = hoaDonDAO.themHoaDonMoi(hd);
        if (maHD <= 0)
            throw new Exception("Không thể tạo hóa đơn cho đơn hàng " + maDon);

        // 6. Chốt hóa đơn & cộng điểm thành viên qua Procedure
        try {
            hoaDonDAO.thanhToanVaThangHang(maHD, request.getMaKH(), tongTien);

            // 6.1 Tạo phiếu thu đi kèm (Sổ quỹ)
            taoPhieuThuChiTuHoaDon(maHD, tongTien, "Thu tiền bán lẻ HD" + maHD);

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

        double tongTien = donHang.getTongTienHDBan() != null ? donHang.getTongTienHDBan().doubleValue() : 0.0;
        double tienCoc = donHang.getTienDaCoc() != null ? donHang.getTienDaCoc().doubleValue() : 0.0;
        double tongTienConLai = Math.max(0, tongTien - tienCoc);

        // tienGoc = tongTien (đơn đặt hàng không áp thuế thêm tại đây)
        HoaDonDTO hd = taoHoaDonDTO(donHang.getMaDon(), tongTien, tongTienConLai, "DAT_HANG");
        hd.setMaDon(donHang.getMaDon());

        try {
            int maHD = hoaDonDAO.themHoaDonMoi(hd);
            if (maHD > 0) {
                hoaDonDAO.thanhToanVaThangHang(maHD, donHang.getMaKH(), tongTien);

                // Tạo phiếu thu đi kèm (Sổ quỹ)
                taoPhieuThuChiTuHoaDon(maHD, tongTienConLai, "Thu tiền đơn hàng HD" + maHD);

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
        HoaDonDTO hd = new HoaDonDTO();
        hd.setMaDon(maDon);
        hd.setMaCa(SessionContext.getInstance().getMaCa());
        hd.setThueVAT(8.5); // Mặc định 8.5%
        hd.setTienHangGoc(java.math.BigDecimal.valueOf(tienGoc));
        hd.setTongTienThanhToan(java.math.BigDecimal.valueOf(soTien));
        hd.setMaPTTT(1); // Mặc định Tiền mặt
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
}
