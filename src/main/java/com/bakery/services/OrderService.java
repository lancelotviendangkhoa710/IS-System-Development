package com.bakery.services;

import com.bakery.dao.DanhMucSPDAO;
import com.bakery.dao.DonDatHangDAO;
import com.bakery.dao.HoaDonDAO;
import com.bakery.dao.KhachHangDAO;
import com.bakery.dao.SanPhamDAO;
import com.bakery.dto.CTDonHangDTO;
import com.bakery.dto.CTDonTuyChinhDTO;
import com.bakery.dto.DanhMucSPDTO;
import com.bakery.dto.DonDatHangDTO;
import com.bakery.dto.HoaDonDTO;
import com.bakery.dto.KhachHangDTO;
import com.bakery.dto.SanPhamDTO;
import com.bakery.dto.TrangThaiDonDTO;
import com.bakery.dto.YeuCauChiTietDonHangDTO;
import com.bakery.dto.YeuCauTaoDonHangDTO;

import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderService {
    private final DonDatHangDAO donDatHangDAO;
    private final HoaDonDAO hoaDonDAO;
    private final SanPhamDAO sanPhamDAO;
    private final DanhMucSPDAO danhMucSPDAO;
    private final KhachHangDAO khachHangDAO;

    public OrderService() {
        this.donDatHangDAO = new DonDatHangDAO();
        this.hoaDonDAO = new HoaDonDAO();
        this.sanPhamDAO = new SanPhamDAO();
        this.danhMucSPDAO = new DanhMucSPDAO();
        this.khachHangDAO = new KhachHangDAO();
    }

    public OrderService(DonDatHangDAO donDatHangDAO, HoaDonDAO hoaDonDAO, SanPhamDAO sanPhamDAO, DanhMucSPDAO danhMucSPDAO, KhachHangDAO khachHangDAO) {
        this.donDatHangDAO = donDatHangDAO;
        this.hoaDonDAO = hoaDonDAO;
        this.sanPhamDAO = sanPhamDAO;
        this.danhMucSPDAO = danhMucSPDAO;
        this.khachHangDAO = khachHangDAO;
    }

    public DonDatHangDTO loadOrderById(int maDon) throws Exception {
        return layTomTatDonHang(maDon);
    }

    public int submitNewOrder(YeuCauTaoDonHangDTO request) throws Exception {
        return taoDonHang(request);
    }

    public int taoDonHang(YeuCauTaoDonHangDTO request) throws Exception {
        kiemTraYeuCauDonHang(request);

        int maTrangThai = request.getMaTrangThai();
        if (maTrangThai <= 0) {
            maTrangThai = request.getTienDaCoc() > 0 ? layMaTrangThaiDaCoc() : layMaTrangThaiMoiDat();
        }

        DonDatHangDTO donDatHang = chuyenSangDonDatHangDTO(request, maTrangThai);
        List<CTDonHangDTO> dsCtDonHang = new ArrayList<>();
        List<CTDonTuyChinhDTO> dsCtTuyChinh = new ArrayList<>();
        chuyenDoiChiTietDonHang(request.getItems(), dsCtDonHang, dsCtTuyChinh);

        try {
            int maDonMoi = donDatHangDAO.taoDonHang(donDatHang, dsCtDonHang, dsCtTuyChinh);
            boolean tonTai = donDatHangDAO.tonTaiDonHang(maDonMoi);
            if (!tonTai) {
                throw new Exception("Tạo đơn thất bại: Không tìm thấy đơn hàng vừa tạo trong CSDL.");
            }
            return maDonMoi;
        } catch (SQLException e) {
            throw new Exception("Không tạo được đơn hàng: " + e.getMessage(), e);
        }
    }

    public void thanhToanTrucTiep(YeuCauTaoDonHangDTO request, double soTienKhachDua) throws Exception {
        int maTrangThaiHoanThanh = layMaTrangThaiHoanThanh();
        request.setMaTrangThai(maTrangThaiHoanThanh);

        double tongTien = request.getItems().stream()
                .mapToDouble(i -> i.getDonGia() * i.getSoLuong())
                .sum();
        request.setTienDaCoc(tongTien);

        int maDon = taoDonHang(request);

        HoaDonDTO hd = new HoaDonDTO();
        hd.setMaDon(maDon);
        hd.setMaCa(1);
        hd.setThueVAT(0.0);
        hd.setTongTienThanhToan(tongTien);
        hd.setMaPTTT(1);
        hd.setLoaiHD("BAN_LE");

        int maHD = hoaDonDAO.themHoaDonMoi(hd);
        if (maHD <= 0) {
            throw new Exception("Không thể tạo hóa đơn cho đơn hàng " + maDon);
        }

        thanhToanDon(maHD, request.getMaKH(), tongTien, soTienKhachDua);
    }

    public void thanhToanDon(int maHD, Integer maKH, double tongTienHoaDon, double soTienKhachDua) throws Exception {
        if (maHD <= 0) throw new IllegalArgumentException("Mã hóa đơn sai định dạng.");
        if (tongTienHoaDon <= 0) throw new IllegalArgumentException("Tổng tiền hóa đơn sai định dạng.");
        if (soTienKhachDua < tongTienHoaDon) throw new IllegalArgumentException("Số tiền khách đưa không đủ để thanh toán.");

        try {
            hoaDonDAO.thanhToanVaThangHang(maHD, maKH, tongTienHoaDon);
        } catch (SQLException e) {
            throw new Exception("Thanh toán thất bại: " + e.getMessage(), e);
        }
    }

    public void chuyenTrangThaiDon(int maDon, int maTrangThaiMoi, int maNvCapNhat, Integer hinhThucNhan,
            String tenTrangThaiHienTai, String tenTrangThaiMoi) throws Exception {
        if (maDon <= 0) throw new IllegalArgumentException("Mã đơn sai định dạng.");
        if (maTrangThaiMoi <= 0) throw new IllegalArgumentException("Mã trạng thái mới sai định dạng.");
        if (maNvCapNhat <= 0) throw new IllegalArgumentException("Mã nhân viên cập nhật sai định dạng.");
        if (tenTrangThaiHienTai == null || tenTrangThaiHienTai.trim().isEmpty()) throw new IllegalArgumentException("Trạng thái hiện tại chưa được nhập.");
        if (tenTrangThaiMoi == null || tenTrangThaiMoi.trim().isEmpty()) throw new IllegalArgumentException("Trạng thái mới chưa được nhập.");

        kiemTraChuyenTrangThai(tenTrangThaiHienTai, tenTrangThaiMoi);

        try {
            // 1. Cập nhật trạng thái
            donDatHangDAO.chuyenTrangThaiDon(maDon, maTrangThaiMoi, maNvCapNhat, hinhThucNhan);

            // 2. Kích hoạt xuất hóa đơn nếu chuyển sang "HOÀN THÀNH"
            if ("HOAN_THANH".equals(chuanHoaTrangThai(tenTrangThaiMoi))) {
                DonDatHangDTO donHang = donDatHangDAO.layTomTatDonHang(maDon);
                if (donHang != null) {
                    double tongTien = donHang.getTongTienHDBan();
                    double tienDaCoc = donHang.getTienDaCoc();

                    // Công thức: Tổng thanh toán = Tổng bill - Tiền đã cọc
                    double tongTienThanhToan = Math.max(0, tongTien - tienDaCoc);

                    HoaDonDTO hd = new HoaDonDTO();
                    hd.setMaDon(maDon);
                    hd.setMaCa(1);       // Hardcode ca 1 theo yêu cầu test
                    hd.setMaPTTT(1);     // Hardcode Tiền mặt (1) theo yêu cầu test
                    hd.setThueVAT(0.0);
                    hd.setTongTienThanhToan(tongTienThanhToan);
                    hd.setLoaiHD("DAT_HANG"); // Xác định xuất hóa đơn từ đơn đặt trước

                    int maHD = hoaDonDAO.themHoaDonMoi(hd);
                    if (maHD > 0) {
                        // Ghi nhận điểm thưởng tính trên TỔNG TRỊ GIÁ BILL, không phải chỉ riêng phần trả thêm
                        hoaDonDAO.thanhToanVaThangHang(maHD, donHang.getMaKH(), tongTien);
                    }
                }
            }
        } catch (SQLException e) {
            throw new Exception("Chuyển trạng thái thất bại: " + e.getMessage(), e);
        }
    }

    public void huyDonVaHoanCoc(int maDon, String lyDoHuy, int maNvCapNhat, String tenTrangThaiHienTai) throws Exception {
        if (maDon <= 0) throw new IllegalArgumentException("Mã đơn hủy sai định dạng.");
        if (maNvCapNhat <= 0) throw new IllegalArgumentException("Mã nhân viên cập nhật sai định dạng.");
        if (lyDoHuy == null || lyDoHuy.trim().isEmpty()) throw new IllegalArgumentException("Lý do hủy đơn chưa được nhập.");
        if (kiemTraTrangThaiCamHuy(tenTrangThaiHienTai)) throw new IllegalStateException("Đơn hàng đang ở trạng thái không cho phép hủy.");

        try {
            donDatHangDAO.huyDonVaHoanKho(maDon, lyDoHuy.trim(), maNvCapNhat);
        } catch (SQLException e) {
            throw new Exception("Hủy đơn thất bại: " + e.getMessage(), e);
        }
    }

    public String theoDoiDonHang(int maDon) throws Exception {
        if (maDon <= 0) throw new IllegalArgumentException("Mã đơn theo dõi không hợp lệ.");
        try {
            String tenTrangThai = donDatHangDAO.layTenTrangThaiDon(maDon);
            if (tenTrangThai == null || tenTrangThai.trim().isEmpty()) throw new Exception("Không tìm thấy đơn hàng với mã " + maDon + ".");
            return tenTrangThai;
        } catch (SQLException e) {
            throw new Exception("Không thể theo dõi đơn hàng: " + e.getMessage(), e);
        }
    }

    public DonDatHangDTO layTomTatDonHang(int maDon) throws Exception {
        if (maDon <= 0) throw new IllegalArgumentException("Mã đơn theo dõi không hợp lệ.");
        try {
            DonDatHangDTO tomTat = donDatHangDAO.layTomTatDonHang(maDon);
            if (tomTat == null) throw new Exception("Không tìm thấy đơn hàng với mã " + maDon + ".");
            return tomTat;
        } catch (SQLException e) {
            throw new Exception("Không thể lấy thông tin đơn hàng: " + e.getMessage(), e);
        }
    }

    public List<SanPhamDTO> layDanhSachSanPhamPOS() {
        return sanPhamDAO.layTatCaSanPhamDeBan();
    }

    public Map<Integer, String> layMapDanhMucSanPham() {
        Map<Integer, String> mapDanhMuc = new LinkedHashMap<>();
        List<DanhMucSPDTO> dsDanhMuc = danhMucSPDAO.layTatCaDanhMucConHoatDong();
        for (DanhMucSPDTO dm : dsDanhMuc) {
            mapDanhMuc.put(dm.getMaDM(), dm.getTenDM());
        }
        return mapDanhMuc;
    }

    public KhachHangDTO timKhachHangTheoSoDienThoai(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) return null;
        return khachHangDAO.timKhachHangBangSDT(sdt.trim());
    }

    public List<TrangThaiDonDTO> layDanhSachTrangThaiDon() throws Exception {
        try {
            return donDatHangDAO.layDanhSachTrangThaiDon();
        } catch (SQLException e) {
            throw new Exception("Không thể lấy danh sách trạng thái đơn: " + e.getMessage(), e);
        }
    }

    private int layMaTrangThaiDaCoc() throws Exception {
        List<TrangThaiDonDTO> dsTrangThai = layDanhSachTrangThaiDon();
        for (TrangThaiDonDTO trangThai : dsTrangThai) {
            String normalized = chuanHoaTrangThai(trangThai.getTenTrangThai());
            if ("DA_COC".equals(normalized)) return trangThai.getMaTrangThai();
        }
        return layMaTrangThaiMoiDat();
    }

    private int layMaTrangThaiHoanThanh() throws Exception {
        List<TrangThaiDonDTO> dsTrangThai = layDanhSachTrangThaiDon();
        for (TrangThaiDonDTO trangThai : dsTrangThai) {
            String normalized = chuanHoaTrangThai(trangThai.getTenTrangThai());
            if ("HOAN_THANH".equals(normalized)) return trangThai.getMaTrangThai();
        }
        throw new Exception("Không tìm thấy trạng thái HOÀN THÀNH.");
    }

    private int layMaTrangThaiMoiDat() throws Exception {
        List<TrangThaiDonDTO> dsTrangThai = layDanhSachTrangThaiDon();
        for (TrangThaiDonDTO trangThai : dsTrangThai) {
            String normalized = chuanHoaTrangThai(trangThai.getTenTrangThai());
            if ("MOI_DAT".equals(normalized) || "CHO_XU_LY".equals(normalized)) return trangThai.getMaTrangThai();
        }
        throw new Exception("Không tìm thấy trạng thái mặc định MOI_DAT/CHO_XU_LY.");
    }

    private void kiemTraYeuCauDonHang(YeuCauTaoDonHangDTO request) {
        if (request == null) throw new IllegalArgumentException("Yêu cầu tạo đơn hàng bị trống.");
        if (request.getMaNVLap() <= 0) throw new IllegalArgumentException("Mã nhân viên lập đơn không hợp lệ.");
        if (request.getTienDaCoc() < 0) throw new IllegalArgumentException("Tiền đặt cọc không được âm.");
        if (request.getHinhThucNhan() == null || (request.getHinhThucNhan() != 1 && request.getHinhThucNhan() != 2)) throw new IllegalArgumentException("Hình thức nhận chỉ được là Trực tiếp (1) hoặc Đặt hàng (2).");
        if (request.getHinhThucNhan() == 2 && (request.getDiaChiGiao() == null || request.getDiaChiGiao().trim().isEmpty())) throw new IllegalArgumentException("Đơn đặt hàng bắt buộc nhập địa chỉ giao.");
        if (request.getHinhThucNhan() == 1) request.setDiaChiGiao(null);
        if (request.getNgayGioNhanBanh() == null) throw new IllegalArgumentException("Ngày giờ nhận bánh bắt buộc nhập.");
        if (request.getNgayGioNhanBanh().isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Ngày giờ nhận bánh không được nằm trong quá khứ.");

        List<YeuCauChiTietDonHangDTO> items = request.getItems();
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Đơn hàng phải có ít nhất 1 sản phẩm.");
    }

    private DonDatHangDTO chuyenSangDonDatHangDTO(YeuCauTaoDonHangDTO request, int maTrangThai) {
        DonDatHangDTO donDatHang = new DonDatHangDTO();
        donDatHang.setNgayGioNhanBanh(request.getNgayGioNhanBanh());
        donDatHang.setMaKH(request.getMaKH());
        donDatHang.setMaNVLap(request.getMaNVLap());
        donDatHang.setMaTrangThai(maTrangThai);
        donDatHang.setTienDaCoc(request.getTienDaCoc());
        donDatHang.setHinhThucNhan(request.getHinhThucNhan());
        donDatHang.setDiaChiGiao(request.getDiaChiGiao());
        return donDatHang;
    }

    private void chuyenDoiChiTietDonHang(List<YeuCauChiTietDonHangDTO> items, List<CTDonHangDTO> dsCtDonHang, List<CTDonTuyChinhDTO> dsCtTuyChinh) {
        for (YeuCauChiTietDonHangDTO item : items) {
            if (item.isCustom()) {
                CTDonTuyChinhDTO ctTuyChinh = new CTDonTuyChinhDTO();
                ctTuyChinh.setMaSP(item.getMaSP()); ctTuyChinh.setSoLuong(item.getSoLuong()); ctTuyChinh.setDonGia(item.getDonGia());
                ctTuyChinh.setLoiChucTrenBanh(item.getGhiChu()); ctTuyChinh.setGhiChuThoBanh(item.getPhuKien());
                dsCtTuyChinh.add(ctTuyChinh);
            } else {
                CTDonHangDTO ctDonHang = new CTDonHangDTO();
                ctDonHang.setMaSP(item.getMaSP()); ctDonHang.setSoLuong(item.getSoLuong()); ctDonHang.setDonGia(item.getDonGia());
                dsCtDonHang.add(ctDonHang);
            }
        }
    }

       private void kiemTraChuyenTrangThai(String tenTrangThaiHienTai, String tenTrangThaiMoi) {
        String current = chuanHoaTrangThai(tenTrangThaiHienTai);
        String next = chuanHoaTrangThai(tenTrangThaiMoi);

        if (current.equals(next)) {
            throw new IllegalArgumentException("Trạng thái mới không được trùng trạng thái hiện tại.");
        }

        // SỬ DỤNG CHUỖI ĐÃ NORMALIZE ĐỂ SO SÁNH (MOI_DAT, DA_COC...)
        if ("MOI_DAT".equals(current) && Set.of("DA_COC", "DANG_SAN_XUAT", "HUY").contains(next)) return;
        if ("DA_COC".equals(current) && Set.of("DANG_SAN_XUAT", "HUY").contains(next)) return;
        if ("DANG_SAN_XUAT".equals(current) && Set.of("CHO_GIAO", "CHO_KHACH_LAY").contains(next)) return;
        if (Set.of("CHO_GIAO", "CHO_KHACH_LAY").contains(current) && "HOAN_THANH".equals(next)) return;

        throw new IllegalStateException("Không được phép nhảy cóc trạng thái đơn hàng.");
    }

    private boolean kiemTraTrangThaiCamHuy(String tenTrangThaiHienTai) {
        String current = chuanHoaTrangThai(tenTrangThaiHienTai);
        // RULE MỚI: CHỈ ĐƯỢC HỦY KHI ĐƠN Ở TRẠNG THÁI "MỚI ĐẶT" HOẶC "ĐÃ CỌC"
        return !("MOI_DAT".equals(current) || "DA_COC".equals(current) || "CHO_XU_LY".equals(current));
    }

    private String chuanHoaTrangThai(String rawStatus) {
        if (rawStatus == null) return "";
        String normalized = Normalizer.normalize(rawStatus.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").replace("đ", "d").replace("Đ", "D")
                .toUpperCase().replace(' ', '_');
        if (normalized.contains("KHACH") && normalized.contains("LAY")) return "CHO_KHACH_LAY";
        return normalized;
    }
}
