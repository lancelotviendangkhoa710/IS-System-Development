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

    public DonHangService() {
        this.donHangService = new QuanLyDonHangService();
        this.thanhToanService = new ThanhToanService();
        this.sanPhamService = new SanPhamService();
        this.khachHangService = new KhachHangService();
        this.tuyChinhBanhService = new TuyChinhBanhService();
        this.theoDoiDonService = new TheoDoiDonService();
    }

    // =========================================================
    // 1. QUẢN LÝ ĐƠN HÀNG
    // =========================================================

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
            double refundAmount)
            throws Exception {
        donHangService.huyDonVaHoanCoc(maDon, lyDoHuy, maNvCapNhat, tenTrangThaiHienTai, refundAmount);
    }

    // =========================================================
    // 3. TRA CỨU SẢN PHẨM & KHÁCH HÀNG (Delegates to SanPham/KhachHang Service)
    // =========================================================

    public List<SanPhamDTO> layDanhSachSanPhamPOS() throws Exception {
        return sanPhamService.layDanhSachSanPhamPOS();
    }

    /** Kiểm tra tồn kho trước khi hiện dialog thanh toán (Fail-Fast). */
    public List<String> kiemTraTonKhoGioHang(List<com.bakery.model.dto.YeuCauChiTietDonHangDTO> gioHang)
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
}
