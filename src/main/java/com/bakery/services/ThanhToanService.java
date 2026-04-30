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
    private final QuanLyDonHangService quanLyDonHangService;

    public ThanhToanService() {
        this.hoaDonDAO = new HoaDonDAO();
        this.quanLyDonHangService = new QuanLyDonHangService();
    }

    public ThanhToanService(HoaDonDAO hoaDonDAO, QuanLyDonHangService quanLyDonHangService) {
        this.hoaDonDAO = hoaDonDAO;
        this.quanLyDonHangService = quanLyDonHangService;
    }

    public double tinhTienHoaDon(YeuCauTaoDonHangDTO request) {
        if (request == null || request.getItems() == null) return 0.0;
        return request.getItems().stream()
                .mapToDouble(i -> i.getDonGia() * i.getSoLuong())
                .sum();
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
        hd.setMaCa(1); // Mặc định hoặc lấy từ Session
        hd.setThueVAT(0.0);
        hd.setTongTienThanhToan(java.math.BigDecimal.valueOf(soTien));
        hd.setMaPTTT(1); // Mặc định Tiền mặt
        hd.setLoaiHD(loaiHD);
        return hd;
    }

    private int layMaTrangThaiHoanThanh() throws Exception {
        return quanLyDonHangService.layDanhSachTrangThaiDon().stream()
                .filter(tt -> "HOAN_THANH".equals(com.bakery.utils.StringUtil.chuanHoa(tt.getTenTrangThai())))
                .mapToInt(tt -> tt.getMaTrangThai())
                .findFirst()
                .orElseThrow(() -> new Exception("Không tìm thấy trạng thái HOÀN_THÀNH."));
    }
}
