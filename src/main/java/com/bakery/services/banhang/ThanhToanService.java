package com.bakery.services.banhang;

import com.bakery.model.dao.banhang.HoaDonDAO;
import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.banhang.HoaDonDTO;
import com.bakery.model.dto.banhang.YeuCauTaoDonHangDTO;
import com.bakery.model.dto.hethong.PhieuThuChiDTO;
import com.bakery.model.dao.hethong.PhieuThuChiDAO;
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

    public ThanhToanService() {
        this.hoaDonDAO = new HoaDonDAO();
        this.quanLyDonHangService = new QuanLyDonHangService();
        this.phieuThuChiDAO = new PhieuThuChiDAO();
    }

    public ThanhToanService(HoaDonDAO hoaDonDAO, QuanLyDonHangService quanLyDonHangService, PhieuThuChiDAO phieuThuChiDAO) {
        this.hoaDonDAO = hoaDonDAO;
        this.quanLyDonHangService = quanLyDonHangService;
        this.phieuThuChiDAO = phieuThuChiDAO;
    }

    public double tinhTienHoaDon(YeuCauTaoDonHangDTO request) {
        if (request == null || request.getItems() == null) return 0.0;
        double subtotal = request.getItems().stream()
                .mapToDouble(i -> i.getDonGia() * i.getSoLuong())
                .sum();
        return subtotal * 1.085; // Mặc định tính thêm 8.5% (Thuế/Phụ phí)
    }

    /**
     * Tạo đơn hàng HOÀN_THÀNH và xuất hóa đơn bán lẻ ngay.
     * Dùng cho luồng thanh toán trực tiếp tại quầy.
     */
    public HoaDonDTO thanhToanTrucTiep(YeuCauTaoDonHangDTO request, double soTienKhachDua) throws Exception {
        // 1. Tính tổng tiền từ giỏ hàng
        double tongTien = tinhTienHoaDon(request);

        // 2. Validate số tiền khách đưa
        if (soTienKhachDua < tongTien)
            throw new IllegalArgumentException("Số tiền khách đưa không đủ để thanh toán.");

        // 3. Đặt trạng thái HOÀN_THÀNH và ghi nhận toàn bộ tiền là đã cọc
        int maTrangThaiHoanThanh = layMaTrangThaiHoanThanh();
        request.setMaTrangThai(maTrangThaiHoanThanh);
        request.setTienDaCoc(tongTien);

        // 4. Tạo đơn hàng
        int maDon = quanLyDonHangService.taoDonHang(request);

        // 5. Tạo hóa đơn bán lẻ
        HoaDonDTO hd = taoHoaDonDTO(maDon, tongTien, "BAN_LE");

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

        double tongTien = donHang.getTongTienHDBan() != null ? donHang.getTongTienHDBan().doubleValue() : 0.0;
        double tienCoc = donHang.getTienDaCoc() != null ? donHang.getTienDaCoc().doubleValue() : 0.0;
        double tongTienConLai = Math.max(0, tongTien - tienCoc);

        HoaDonDTO hd = taoHoaDonDTO(donHang.getMaDon(), tongTienConLai, "DAT_HANG");
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
     * Dùng cho việc chuẩn bị dữ liệu trước khi lưu hoặc hiển thị.
     */
    public HoaDonDTO taoHoaDonDTO(int maDon, double soTien, String loaiHD) {
        HoaDonDTO hd = new HoaDonDTO();
        hd.setMaDon(maDon);
        hd.setMaCa(SessionContext.getInstance().getMaCa()); 
        hd.setThueVAT(8.5); // Mặc định 8.5%
        hd.setTongTienThanhToan(java.math.BigDecimal.valueOf(soTien));
        hd.setMaPTTT(1); // Mặc định Tiền mặt
        hd.setLoaiHD(loaiHD);
        return hd;
    }

    /**
     * Tạo phiếu thu chi liên kết với hóa đơn.
     */
    private void taoPhieuThuChiTuHoaDon(int maHD, double soTien, String ghiChu) throws Exception {
        int maLoaiThu = phieuThuChiDAO.layMaLoaiTheoTen("Bán hàng");
        if (maLoaiThu == -1) maLoaiThu = 1; // Fallback

        PhieuThuChiDTO ptc = new PhieuThuChiDTO();
        ptc.setMaLoaiThuChi(maLoaiThu);
        ptc.setSoTien(BigDecimal.valueOf(soTien));
        ptc.setMaNV(SessionContext.getInstance().getMaNV());
        ptc.setMaHD(maHD);
        ptc.setMaCa(SessionContext.getInstance().getMaCa());
        ptc.setGhiChu(ghiChu);

        phieuThuChiDAO.taoPhieuThuChi(ptc);
    }

    private int layMaTrangThaiHoanThanh() throws Exception {
        return quanLyDonHangService.layDanhSachTrangThaiDon().stream()
                .filter(tt -> "HOAN_THANH".equals(com.bakery.utils.StringUtil.chuanHoa(tt.getTenTrangThai())))
                .mapToInt(tt -> tt.getMaTrangThai())
                .findFirst()
                .orElseThrow(() -> new Exception("Không tìm thấy trạng thái HOÀN_THÀNH."));
    }
}
