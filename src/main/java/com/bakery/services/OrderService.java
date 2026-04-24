package com.bakery.services;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.CTDonTuyChinhDTO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.HoaDonDTO;
import com.bakery.model.dto.KhachHangDTO;
import com.bakery.model.dto.SanPhamDTO;
import com.bakery.model.dto.TrangThaiDonDTO;
import com.bakery.model.dto.YeuCauTaoDonHangDTO;
import com.bakery.model.dto.KichCoBanhDTO;
import com.bakery.model.dto.CotBanhDTO;
import com.bakery.model.dto.NhanBanhDTO;
import com.bakery.model.dto.KieuTrangTriDTO;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * OrderService hiện đóng vai trò là một Facade (Cổng giao tiếp chính)
 * cho màn hình POS, giúp Presenter không phải khởi tạo quá nhiều service lẻ.
 * Mọi logic nghiệp vụ thực tế đã được chuyển sang các Service chuyên biệt (SRP).
 */
public class OrderService {
    private final DonHangService donHangService;
    private final ThanhToanService thanhToanService;
    private final SanPhamService sanPhamService;
    private final KhachHangService khachHangService;
    private final TuyChinhBanhService tuyChinhBanhService;
    private final TheoDoiDonService theoDoiDonService;

    public OrderService() {
        this.donHangService = new DonHangService();
        this.thanhToanService = new ThanhToanService();
        this.sanPhamService = new SanPhamService();
        this.khachHangService = new KhachHangService();
        this.tuyChinhBanhService = new TuyChinhBanhService();
        this.theoDoiDonService = new TheoDoiDonService();
    }

    // =========================================================
    // 1. QUẢN LÝ ĐƠN HÀNG (Delegates to DonHangService)
    // =========================================================

    /** Tương thích với OrderPresenter cũ */
    public int submitNewOrder(YeuCauTaoDonHangDTO request) throws Exception {
        return donHangService.taoDonHang(request);
    }

    public int taoDonHang(YeuCauTaoDonHangDTO request) throws Exception {
        return donHangService.taoDonHang(request);
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
    // 2. THANH TOÁN (Delegates to ThanhToanService)
    // =========================================================

    public HoaDonDTO thanhToanTrucTiep(YeuCauTaoDonHangDTO request, double soTienKhachDua) throws Exception {
        return thanhToanService.thanhToanTrucTiep(request, soTienKhachDua);
    }

    /** Cập nhật trạng thái đơn và tự động chốt hóa đơn nếu hoàn thành. */
    public HoaDonDTO chuyenTrangThaiDon(int maDon, int maTrangThaiMoi, int maNvCapNhat, int hinhThucNhan,
            String tenTrangThaiHienTai, String tenTrangThaiMoi) throws Exception {
        DonDatHangDTO donHang = donHangService.chuyenTrangThaiDon(maDon, maTrangThaiMoi, maNvCapNhat, 
                hinhThucNhan, tenTrangThaiHienTai, tenTrangThaiMoi);
        
        if (donHang != null && "HOAN_THANH".equals(chuanHoaTrangThai(tenTrangThaiMoi))) {
            thanhToanService.chotHoaDonDatHang(donHang);
        }
        return null;
    }

    public void huyDonVaHoanCoc(int maDon, String lyDoHuy, int maNvCapNhat, String tenTrangThaiHienTai) throws Exception {
        donHangService.huyDonVaHoanCoc(maDon, lyDoHuy, maNvCapNhat, tenTrangThaiHienTai);
    }

    // =========================================================
    // 3. TRA CỨU SẢN PHẨM & KHÁCH HÀNG (Delegates to SanPham/KhachHang Service)
    // =========================================================

    public List<SanPhamDTO> layDanhSachSanPhamPOS() {
        return sanPhamService.layDanhSachSanPhamPOS();
    }

    public Map<Integer, String> layMapDanhMucSanPham() {
        return sanPhamService.layMapDanhMucSanPham();
    }

    public KhachHangDTO timKhachHangTheoSoDienThoai(String sdt) {
        return khachHangService.timKhachHangTheoSoDienThoai(sdt);
    }

    // =========================================================
    // 4. TÙY CHỈNH BÁNH (Delegates to TuyChinhBanhService / SanPhamService)
    // =========================================================

    public double tinhGiaBanhTuyChinh(int maSP, Integer maKC, Integer maCot, Integer maNhan, Integer maTrangTri) {
        return sanPhamService.tinhGiaBanhTuyChinh(maSP, maKC, maCot, maNhan, maTrangTri);
    }

    public List<KichCoBanhDTO> layDanhSachKichCo() {
        return tuyChinhBanhService.layDanhSachKichCo();
    }

    public List<CotBanhDTO> layDanhSachCotBanh() {
        return tuyChinhBanhService.layDanhSachCotBanh();
    }

    public List<NhanBanhDTO> layDanhSachNhanBanh() {
        return tuyChinhBanhService.layDanhSachNhanBanh();
    }

    public List<KieuTrangTriDTO> layDanhSachKieuTrangTri() {
        return tuyChinhBanhService.layDanhSachKieuTrangTri();
    }

    // =========================================================
    // 5. THEO DÕI ĐƠN HÀNG (Delegates to TheoDoiDonService)
    // =========================================================

    public String theoDoiDonHang(int maDon) throws Exception {
        return donHangService.theoDoiDonHang(maDon);
    }

    public List<DonDatHangDTO> layDanhSachDonTheoDoi(String maDonSearch, LocalDate ngayNhan, 
            LocalTime gioTu, LocalTime gioDen, String trangThaiFilter) throws Exception {
        return theoDoiDonService.layDanhSachDonTheoDoi(maDonSearch, ngayNhan, gioTu, gioDen, trangThaiFilter);
    }

    private String chuanHoaTrangThai(String rawStatus) {
        if (rawStatus == null) return "";
        return Normalizer.normalize(rawStatus.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d").replace("Đ", "D")
                .toUpperCase().replace(' ', '_');
    }
}
