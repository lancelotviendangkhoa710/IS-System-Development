package com.bakery.services;

import com.bakery.model.dao.HoaDonDAO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.HoaDonDTO;
import com.bakery.model.dto.YeuCauTaoDonHangDTO;

import java.sql.SQLException;

/**
 * Service duy nhất chịu trách nhiệm xử lý Thanh toán:
 * Thanh toán trực tiếp tại quầy, chốt hóa đơn đặt hàng.
 * (SRP – Single Responsibility Principle)
 */
public class ThanhToanService {

    private final HoaDonDAO hoaDonDAO;
    private final DonHangService donHangService;

    public ThanhToanService() {
        this.hoaDonDAO = new HoaDonDAO();
        this.donHangService = new DonHangService();
    }

    public ThanhToanService(HoaDonDAO hoaDonDAO, DonHangService donHangService) {
        this.hoaDonDAO = hoaDonDAO;
        this.donHangService = donHangService;
    }

    /**
     * Tạo đơn hàng HOÀN_THÀNH và xuất hóa đơn bán lẻ ngay.
     * Dùng cho luồng thanh toán trực tiếp tại quầy.
     */
    public HoaDonDTO thanhToanTrucTiep(YeuCauTaoDonHangDTO request, double soTienKhachDua) throws Exception {
        // 1. Tính tổng tiền từ giỏ hàng
        double tongTien = request.getItems().stream()
                .mapToDouble(i -> i.getDonGia() * i.getSoLuong())
                .sum();

        // 2. Validate số tiền khách đưa
        if (soTienKhachDua < tongTien)
            throw new IllegalArgumentException("Số tiền khách đưa không đủ để thanh toán.");

        // 3. Đặt trạng thái HOÀN_THÀNH và ghi nhận toàn bộ tiền là đã cọc
        int maTrangThaiHoanThanh = layMaTrangThaiHoanThanh();
        request.setMaTrangThai(maTrangThaiHoanThanh);
        request.setTienDaCoc(tongTien);

        // 4. Tạo đơn hàng
        int maDon = donHangService.taoDonHang(request);

        // 5. Tạo hóa đơn bán lẻ
        HoaDonDTO hd = new HoaDonDTO();
        hd.setMaDon(maDon);
        hd.setMaCa(1);
        hd.setThueVAT(0.0);
        hd.setTongTienThanhToan(java.math.BigDecimal.valueOf(tongTien));
        hd.setMaPTTT(1);
        hd.setLoaiHD("BAN_LE");

        int maHD = hoaDonDAO.themHoaDonMoi(hd);
        if (maHD <= 0)
            throw new Exception("Không thể tạo hóa đơn cho đơn hàng " + maDon);

        // 6. Chốt hóa đơn & cộng điểm thành viên qua Procedure
        try {
            hoaDonDAO.thanhToanVaThangHang(maHD, request.getMaKH(), tongTien);
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

        HoaDonDTO hd = new HoaDonDTO();
        hd.setMaDon(donHang.getMaDon());
        hd.setMaCa(1);
        hd.setMaPTTT(1);
        hd.setThueVAT(0.0);
        hd.setTongTienThanhToan(java.math.BigDecimal.valueOf(tongTienConLai));
        hd.setLoaiHD("DAT_HANG");

        try {
            int maHD = hoaDonDAO.themHoaDonMoi(hd);
            if (maHD > 0) {
                hoaDonDAO.thanhToanVaThangHang(maHD, donHang.getMaKH(), tongTien);
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

    private int layMaTrangThaiHoanThanh() throws Exception {
        return donHangService.layDanhSachTrangThaiDon().stream()
                .filter(tt -> "HOAN_THANH".equals(chuanHoaTrangThai(tt.getTenTrangThai())))
                .mapToInt(tt -> tt.getMaTrangThai())
                .findFirst()
                .orElseThrow(() -> new Exception("Không tìm thấy trạng thái HOÀN_THÀNH."));
    }

    private String chuanHoaTrangThai(String raw) {
        if (raw == null) return "";
        return java.text.Normalizer.normalize(raw.trim(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d").replace("Đ", "D")
                .toUpperCase().replace(' ', '_');
    }
}
