package com.bakery.services.banhang;

import com.bakery.model.dto.banhang.YeuCauChiTietDonHangDTO;
import com.bakery.services.khachhang.KhachHangService;
import com.bakery.services.kho.SanPhamService;

import com.bakery.model.dto.banhang.CTDonHangDTO;
import com.bakery.model.dto.banhang.CTDonTuyChinhDTO;
import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.banhang.HoaDonDTO;
import com.bakery.model.dto.khachhang.KhachHangDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.model.dto.banhang.TrangThaiDonDTO;
import com.bakery.model.dto.banhang.YeuCauTaoDonHangDTO;
import com.bakery.model.dto.kho.KichCoBanhDTO;
import com.bakery.model.dto.kho.CotBanhDTO;
import com.bakery.model.dto.kho.NhanBanhDTO;
import com.bakery.model.dto.kho.KieuTrangTriDTO;
import com.bakery.model.dao.banhang.PhuongThucTTDAO;
import com.bakery.utils.StringUtil;

import java.time.LocalDate;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class DonHangService {
    private final QuanLyDonHangService donHangService;
    private final ThanhToanService thanhToanService;
    private final SanPhamService sanPhamService;
    private final KhachHangService khachHangService;
    private final TuyChinhBanhService tuyChinhBanhService;
    private final TheoDoiDonService theoDoiDonService;
    private final PhuongThucTTDAO phuongThucTTDAO;

    public DonHangService() {
        this.donHangService = new QuanLyDonHangService();
        this.thanhToanService = new ThanhToanService();
        this.sanPhamService = new SanPhamService();
        this.khachHangService = new KhachHangService();
        this.tuyChinhBanhService = new TuyChinhBanhService();
        this.theoDoiDonService = new TheoDoiDonService();
        this.phuongThucTTDAO = new PhuongThucTTDAO();
    }

    // =========================================================
    // 1. QUẢN LÝ ĐƠN HÀNG
    // =========================================================

    public int submitNewOrder(YeuCauTaoDonHangDTO request) throws Exception {
        return donHangService.taoDonHang(request);
    }

    public int taoDonHang(YeuCauTaoDonHangDTO request) throws Exception {
        return submitNewOrder(request);
    }

    public DonDatHangDTO loadOrderById(int maDon) throws Exception {
        return donHangService.layTomTatDonHang(maDon);
    }

    public DonDatHangDTO layTomTatDonHang(int maDon) throws Exception {
        return donHangService.layTomTatDonHang(maDon);
    }

    public List<CTDonHangDTO> layChiTietDonHang(int maDon) throws Exception {
        return donHangService.layChiTietDonHang(maDon);
    }

    public List<CTDonTuyChinhDTO> layChiTietTuyChinh(int maDon) throws Exception {
        return donHangService.layChiTietTuyChinh(maDon);
    }

    public List<TrangThaiDonDTO> layDanhSachTrangThaiDon() throws Exception {
        return donHangService.layDanhSachTrangThaiDon();
    }

    // =========================================================
    // 2. THANH TOÁN
    // =========================================================

    public HoaDonDTO thanhToanTrucTiep(YeuCauTaoDonHangDTO request, double soTienKhachDua) throws Exception {
        return thanhToanService.thanhToanTrucTiep(request, soTienKhachDua);
    }

    public double tinhTienHoaDon(YeuCauTaoDonHangDTO request) {
        return thanhToanService.tinhTienHoaDon(request);
    }

    public HoaDonDTO taoHoaDonDTO(int maDon, double soTien, String loaiHD) {
        return thanhToanService.taoHoaDonDTO(maDon, soTien, loaiHD);
    }

    /**
     * Trả về 0 nếu không tìm thấy (Service sử dụng fallback tiền mặt).
     */
    public int layMaPTTTTheoHinhThuc(String hinhThuc) {
        if (hinhThuc == null || hinhThuc.isBlank())
            return 0;
        try {
            // Thử tra theo tên chính xác trước
            int ma = phuongThucTTDAO.layMaTheoTen(hinhThuc);
            if (ma != -1)
                return ma;
            // Normalize: "TM" -> "Tiền mặt", "CK" -> "Chuyển khoản"
            String normalized = switch (hinhThuc.trim().toUpperCase()) {
                case "TM", "TIỀN MẶT" -> "Tiền mặt";
                case "CK", "CHUYỂN KHOẢN" -> "Chuyển khoản";
                default -> hinhThuc;
            };
            return phuongThucTTDAO.layMaTheoTen(normalized);
        } catch (Exception e) {
            System.err.println("[DonHangService] layMaPTTTTheoHinhThuc failed: " + e.getMessage());
        }
        return 0; // Fallback: ThanhToanService sẽ dùng layMaPTTTTienMat()
    }

    /** Cập nhật trạng thái đơn và tự động chốt hóa đơn nếu hoàn thành. */
    public HoaDonDTO chuyenTrangThaiDon(int maDon, int maTrangThaiMoi, int maNvCapNhat, int hinhThucNhan,
            String tenTrangThaiHienTai, String tenTrangThaiMoi) throws Exception {
        DonDatHangDTO donHang = donHangService.chuyenTrangThaiDon(maDon, maTrangThaiMoi, maNvCapNhat,
                hinhThucNhan, tenTrangThaiHienTai, tenTrangThaiMoi);

        if (donHang != null && "HOAN_THANH".equals(StringUtil.chuanHoa(tenTrangThaiMoi))) {
            return thanhToanService.chotHoaDonDatHang(donHang);
        }
        return null;
    }

    public void huyDonVaHoanCoc(int maDon, String lyDoHuy, int maNvCapNhat, String tenTrangThaiHienTai,
            double refundAmount, int maCa)
            throws Exception {
        // Guard: đơn đang sản xuất không được hủy
        if ("\u0110ang s\u1ea3n xu\u1ea5t".equalsIgnoreCase(tenTrangThaiHienTai)) {
            throw new Exception("Kh\u00f4ng th\u1ec3 h\u1ee7y \u0111\u01a1n #" + maDon
                    + " v\u00ec \u0111\u01a1n \u0111ang trong qu\u00e1 tr\u00ecnh s\u1ea3n xu\u1ea5t.");
        }
        donHangService.huyDonVaHoanCoc(maDon, lyDoHuy, maNvCapNhat, tenTrangThaiHienTai, refundAmount, maCa);
    }

    /** Hủy hóa đơn bán lẻ đã hoàn thành — hoàn kho, không hoàn tiền mặt. */
    public void huyHoaDonBanLe(int maDon, String lyDoHuy, int maNvCapNhat) throws Exception {
        donHangService.huyHoaDonBanLe(maDon, lyDoHuy, maNvCapNhat);
    }

    // 3. TRA CỨU SẢN PHẨM & KHÁCH HÀNG (Delegates to SanPham/KhachHang Service)

    public List<SanPhamDTO> layDanhSachSanPhamPOS() throws Exception {
        return sanPhamService.layDanhSachSanPhamPOS();
    }

    /** Kiểm tra tồn kho trước khi hiện dialog thanh toán (Fail-Fast). */
    public List<String> kiemTraTonKhoGioHang(List<YeuCauChiTietDonHangDTO> gioHang)
            throws Exception {
        return sanPhamService.kiemTraTonKhoGioHang(gioHang);
    }

    public Map<Integer, String> layMapDanhMucSanPham() throws Exception {
        return sanPhamService.layMapDanhMucSanPham();
    }

    public KhachHangDTO timKhachHangTheoSoDienThoai(String sdt) throws Exception {
        return khachHangService.timKhachHangTheoSoDienThoai(sdt);
    }

    // =========================================================
    // 4. TÙY CHỈNH BÁNH (Delegates to TuyChinhBanhService / SanPhamService)
    // =========================================================

    public double tinhGiaBanhTuyChinh(int maSP, Integer maKC, Integer maCot, Integer maNhan, Integer maTrangTri)
            throws Exception {
        return sanPhamService.tinhGiaBanhTuyChinh(maSP, maKC, maCot, maNhan, maTrangTri);
    }

    public List<KichCoBanhDTO> layDanhSachKichCo() throws Exception {
        return tuyChinhBanhService.layDanhSachKichCo();
    }

    public List<CotBanhDTO> layDanhSachCotBanh() throws Exception {
        return tuyChinhBanhService.layDanhSachCotBanh();
    }

    public List<NhanBanhDTO> layDanhSachNhanBanh() throws Exception {
        return tuyChinhBanhService.layDanhSachNhanBanh();
    }

    public List<KieuTrangTriDTO> layDanhSachKieuTrangTri() throws Exception {
        return tuyChinhBanhService.layDanhSachKieuTrangTri();
    }

    // =========================================================
    // 5. THEO DÕI ĐƠN HÀNG (Delegates to TheoDoiDonService)
    // =========================================================

    public List<DonDatHangDTO> layDanhSachDonTheoDoi(String maDonSearch, String tenKhachSearch, LocalDate ngayNhan,
            LocalTime gioTu, LocalTime gioDen, String trangThaiFilter) throws Exception {
        List<DonDatHangDTO> list = theoDoiDonService.layDanhSachDonTheoDoi(
                maDonSearch, tenKhachSearch, ngayNhan, gioTu, gioDen, trangThaiFilter);
        return list != null ? list : java.util.List.of();
    }

    /**
     * Lấy danh sách đơn có bánh tùy chỉnh chưa hoàn thành/hủy — dùng cho màn hình
     * bếp.
     * Delegate sang DonHangDAO qua TheoDoiDonService.
     */
    public List<DonDatHangDTO> layDonBepCoTuyChinhChuaHoanThanh() throws Exception {
        return theoDoiDonService.layDonCoTuyChinhChuaHoanThanh();
    }
}
