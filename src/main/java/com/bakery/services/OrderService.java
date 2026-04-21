package com.bakery.services;

import com.bakery.model.dao.DanhMucSPDAO;
import com.bakery.model.dao.DonDatHangDAO;
import com.bakery.model.dao.HoaDonDAO;
import com.bakery.model.dao.KhachHangDAO;
import com.bakery.model.dao.SanPhamDAO;
import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.CTDonTuyChinhDTO;
import com.bakery.model.dto.DanhMucSPDTO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.HoaDonDTO;
import com.bakery.model.dto.KhachHangDTO;
import com.bakery.model.dto.SanPhamDTO;
import com.bakery.model.dto.TrangThaiDonDTO;
import com.bakery.model.dto.YeuCauChiTietDonHangDTO;
import com.bakery.model.dto.YeuCauChiTietDonTuyChinhDTO;
import com.bakery.model.dto.YeuCauTaoDonHangDTO;
import com.bakery.model.dao.KichCoBanhDAO;
import com.bakery.model.dao.CotBanhDAO;
import com.bakery.model.dao.NhanBanhDAO;
import com.bakery.model.dao.KieuTrangTriDAO;
import com.bakery.model.dto.KichCoBanhDTO;
import com.bakery.model.dto.CotBanhDTO;
import com.bakery.model.dto.NhanBanhDTO;
import com.bakery.model.dto.KieuTrangTriDTO;

import java.sql.SQLException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final DonDatHangDAO donDatHangDAO;
    private final HoaDonDAO hoaDonDAO;
    private final SanPhamDAO sanPhamDAO;
    private final DanhMucSPDAO danhMucSPDAO;
    private final KhachHangDAO khachHangDAO;
    private final KichCoBanhDAO kichCoBanhDAO;
    private final CotBanhDAO cotBanhDAO;
    private final NhanBanhDAO nhanBanhDAO;
    private final KieuTrangTriDAO kieuTrangTriDAO;

    public OrderService() {
        this.donDatHangDAO = new DonDatHangDAO();
        this.hoaDonDAO = new HoaDonDAO();
        this.sanPhamDAO = new SanPhamDAO();
        this.danhMucSPDAO = new DanhMucSPDAO();
        this.khachHangDAO = new KhachHangDAO();
        this.kichCoBanhDAO = new KichCoBanhDAO();
        this.cotBanhDAO = new CotBanhDAO();
        this.nhanBanhDAO = new NhanBanhDAO();
        this.kieuTrangTriDAO = new KieuTrangTriDAO();
    }

    public OrderService(DonDatHangDAO donDatHangDAO, HoaDonDAO hoaDonDAO, SanPhamDAO sanPhamDAO, DanhMucSPDAO danhMucSPDAO, KhachHangDAO khachHangDAO) {
        this.donDatHangDAO = donDatHangDAO;
        this.hoaDonDAO = hoaDonDAO;
        this.sanPhamDAO = sanPhamDAO;
        this.danhMucSPDAO = danhMucSPDAO;
        this.khachHangDAO = khachHangDAO;
        this.kichCoBanhDAO = new KichCoBanhDAO();
        this.cotBanhDAO = new CotBanhDAO();
        this.nhanBanhDAO = new NhanBanhDAO();
        this.kieuTrangTriDAO = new KieuTrangTriDAO();
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
                throw new Exception("Tao don that bai: khong tim thay don hang vua tao trong CSDL.");
            }
            return maDonMoi;
        } catch (SQLException e) {
            throw new Exception("Khong tao duoc don hang: " + e.getMessage(), e);
        }
    }

    public HoaDonDTO thanhToanTrucTiep(YeuCauTaoDonHangDTO request, double soTienKhachDua) throws Exception {
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
            throw new Exception("Khong the tao hoa don cho don hang " + maDon);
        }

        thanhToanDon(maHD, request.getMaKH(), tongTien, soTienKhachDua);

        HoaDonDTO hoaDonVuaTao = hoaDonDAO.layHoaDonTheoMa(maHD);
        if (hoaDonVuaTao == null) {
            throw new Exception("Khong the tai thong tin hoa don vua tao.");
        }
        return hoaDonVuaTao;
    }

    public void thanhToanDon(int maHD, Integer maKH, double tongTienHoaDon, double soTienKhachDua) throws Exception {
        if (maHD <= 0) throw new IllegalArgumentException("Ma hoa don sai dinh dang.");
        if (tongTienHoaDon <= 0) throw new IllegalArgumentException("Tong tien hoa don sai dinh dang.");
        if (soTienKhachDua < tongTienHoaDon) throw new IllegalArgumentException("So tien khach dua khong du de thanh toan.");

        try {
            hoaDonDAO.thanhToanVaThangHang(maHD, maKH, tongTienHoaDon);
        } catch (SQLException e) {
            throw new Exception("Thanh toan that bai: " + e.getMessage(), e);
        }
    }

    public void chuyenTrangThaiDon(int maDon, int maTrangThaiMoi, int maNvCapNhat, Integer hinhThucNhan,
            String tenTrangThaiHienTai, String tenTrangThaiMoi) throws Exception {
        if (maDon <= 0) throw new IllegalArgumentException("Ma don sai dinh dang.");
        if (maTrangThaiMoi <= 0) throw new IllegalArgumentException("Ma trang thai moi sai dinh dang.");
        if (maNvCapNhat <= 0) throw new IllegalArgumentException("Ma nhan vien cap nhat sai dinh dang.");
        if (tenTrangThaiHienTai == null || tenTrangThaiHienTai.trim().isEmpty()) throw new IllegalArgumentException("Trang thai hien tai chua duoc nhap.");
        if (tenTrangThaiMoi == null || tenTrangThaiMoi.trim().isEmpty()) throw new IllegalArgumentException("Trang thai moi chua duoc nhap.");

        String trangThaiHienTai = chuanHoaTrangThai(tenTrangThaiHienTai);
        String trangThaiMoi = chuanHoaTrangThai(tenTrangThaiMoi);
        if ("HOAN_THANH".equals(trangThaiHienTai)) {
            throw new IllegalStateException("Đơn hàng đã được cập nhật trạng thái.");
        }
        if (trangThaiHienTai.equals(trangThaiMoi)) {
            throw new IllegalArgumentException("Trạng thái mới không trùng với trạng thái hiện tại.");
        }

        try {
            donDatHangDAO.chuyenTrangThaiDon(maDon, maTrangThaiMoi, maNvCapNhat, hinhThucNhan);

            if ("HOAN_THANH".equals(chuanHoaTrangThai(tenTrangThaiMoi))) {
                DonDatHangDTO donHang = donDatHangDAO.layTomTatDonHang(maDon);
                if (donHang != null) {
                    double tongTien = donHang.getTongTienHDBan();
                    double tienDaCoc = donHang.getTienDaCoc();
                    double tongTienThanhToan = Math.max(0, tongTien - tienDaCoc);

                    HoaDonDTO hd = new HoaDonDTO();
                    hd.setMaDon(maDon);
                    hd.setMaCa(1);
                    hd.setMaPTTT(1);
                    hd.setThueVAT(0.0);
                    hd.setTongTienThanhToan(tongTienThanhToan);
                    hd.setLoaiHD("DAT_HANG");

                    int maHD = hoaDonDAO.themHoaDonMoi(hd);
                    if (maHD > 0) {
                        hoaDonDAO.thanhToanVaThangHang(maHD, donHang.getMaKH(), tongTien);
                    }
                }
            }
        } catch (SQLException e) {
            throw new Exception("Chuyen trang thai that bai: " + e.getMessage(), e);
        }
    }

    public void huyDonVaHoanCoc(int maDon, String lyDoHuy, int maNvCapNhat, String tenTrangThaiHienTai) throws Exception {
        if (maDon <= 0) throw new IllegalArgumentException("Ma don huy sai dinh dang.");
        if (maNvCapNhat <= 0) throw new IllegalArgumentException("Ma nhan vien cap nhat sai dinh dang.");
        if (lyDoHuy == null || lyDoHuy.trim().isEmpty()) throw new IllegalArgumentException("Ly do huy don chua duoc nhap.");
        if (kiemTraTrangThaiCamHuy(tenTrangThaiHienTai)) throw new IllegalStateException("Don hang dang o trang thai khong cho phep huy.");

        try {
            donDatHangDAO.huyDonVaHoanKho(maDon, lyDoHuy.trim(), maNvCapNhat);
        } catch (SQLException e) {
            throw new Exception("Huy don that bai: " + e.getMessage(), e);
        }
    }

    public String theoDoiDonHang(int maDon) throws Exception {
        if (maDon <= 0) throw new IllegalArgumentException("Ma don theo doi khong hop le.");
        try {
            String tenTrangThai = donDatHangDAO.layTenTrangThaiDon(maDon);
            if (tenTrangThai == null || tenTrangThai.trim().isEmpty()) throw new Exception("Khong tim thay don hang voi ma " + maDon + ".");
            return tenTrangThai;
        } catch (SQLException e) {
            throw new Exception("Khong the theo doi don hang: " + e.getMessage(), e);
        }
    }

    public DonDatHangDTO layTomTatDonHang(int maDon) throws Exception {
        if (maDon <= 0) throw new IllegalArgumentException("Ma don theo doi khong hop le.");
        try {
            DonDatHangDTO tomTat = donDatHangDAO.layTomTatDonHang(maDon);
            if (tomTat == null) throw new Exception("Khong tim thay don hang voi ma " + maDon + ".");
            return tomTat;
        } catch (SQLException e) {
            throw new Exception("Khong the lay thong tin don hang: " + e.getMessage(), e);
        }
    }

    public List<DonDatHangDTO> layDanhSachDonTheoDoi(LocalDate ngayNhan, Integer gioNhan) throws Exception {
        try {
            return donDatHangDAO.layDanhSachDonTheoDoi(ngayNhan, gioNhan);
        } catch (SQLException e) {
            throw new Exception("Khong the tai danh sach theo doi don: " + e.getMessage(), e);
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

    public double tinhGiaBanhTuyChinh(int maSP, Integer maKC, Integer maCot, Integer maNhan, Integer maTrangTri) {
        return sanPhamDAO.tinhGiaBanhTuyChinh(maSP, maKC, maCot, maNhan, maTrangTri);
    }

    public List<KichCoBanhDTO> layDanhSachKichCo() { return kichCoBanhDAO.layDanhSachPhuPhi(); }
    public List<CotBanhDTO> layDanhSachCotBanh() { return cotBanhDAO.layDanhSachPhuPhi(); }
    public List<NhanBanhDTO> layDanhSachNhanBanh() { return nhanBanhDAO.layDanhSachPhuPhi(); }
    public List<KieuTrangTriDTO> layDanhSachKieuTrangTri() { return kieuTrangTriDAO.layDanhSachPhuPhi(); }

    public List<TrangThaiDonDTO> layDanhSachTrangThaiDon() throws Exception {
        try {
            return donDatHangDAO.layDanhSachTrangThaiDon();
        } catch (SQLException e) {
            throw new Exception("Khong the lay danh sach trang thai don: " + e.getMessage(), e);
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
        throw new Exception("Khong tim thay trang thai HOAN_THANH.");
    }

    private int layMaTrangThaiMoiDat() throws Exception {
        List<TrangThaiDonDTO> dsTrangThai = layDanhSachTrangThaiDon();
        for (TrangThaiDonDTO trangThai : dsTrangThai) {
            String normalized = chuanHoaTrangThai(trangThai.getTenTrangThai());
            if ("MOI_DAT".equals(normalized) || "CHO_XU_LY".equals(normalized)) return trangThai.getMaTrangThai();
        }
        throw new Exception("Khong tim thay trang thai mac dinh MOI_DAT/CHO_XU_LY.");
    }

    private void kiemTraYeuCauDonHang(YeuCauTaoDonHangDTO request) {
        if (request == null) throw new IllegalArgumentException("Yêu câù tạo đơn hàng bị trống.");
        if (request.getMaNVLap() <= 0) throw new IllegalArgumentException("Ma nhan vien lap don khong hop le.");
        if (request.getTienDaCoc() < 0) throw new IllegalArgumentException("Tiền đặt cọc không được âm.");
        if (request.getHinhThucNhan() == null || (request.getHinhThucNhan() != 1 && request.getHinhThucNhan() != 2)) throw new IllegalArgumentException("Hinh thuc nhan chi duoc la Truc tiep (1) hoac Dat hang (2).");
        if (request.getHinhThucNhan() == 2 && (request.getDiaChiGiao() == null || request.getDiaChiGiao().trim().isEmpty())) throw new IllegalArgumentException("Don dat hang bat buoc nhap dia chi giao.");
        if (request.getHinhThucNhan() == 1) request.setDiaChiGiao(null);
        if (request.getNgayGioNhanBanh() == null) throw new IllegalArgumentException("Ngày giờ nhận bánh bắt buộc nhập.");
        if (request.getNgayGioNhanBanh().isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Ngày giờ nhận bánh không được nằm trong quá khứ.");

        List<YeuCauChiTietDonHangDTO> items = request.getItems();
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Don hang phai co it nhat 1 san pham.");
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
            if (item.isCustom() && item instanceof YeuCauChiTietDonTuyChinhDTO) {
                YeuCauChiTietDonTuyChinhDTO customItem = (YeuCauChiTietDonTuyChinhDTO) item;
                CTDonTuyChinhDTO ctTuyChinh = new CTDonTuyChinhDTO();
                ctTuyChinh.setMaSP(item.getMaSP()); ctTuyChinh.setSoLuong(item.getSoLuong()); ctTuyChinh.setDonGia(item.getDonGia());
                ctTuyChinh.setLoiChucTrenBanh(customItem.getLoiChucTrenBanh()); ctTuyChinh.setGhiChuThoBanh(customItem.getGhiChuThoBanh());
                ctTuyChinh.setMaKC(customItem.getMaKC());
                ctTuyChinh.setMaCot(customItem.getMaCot());
                ctTuyChinh.setMaNhan(customItem.getMaNhan());
                ctTuyChinh.setMaTrangTri(customItem.getMaTrangTri());
                dsCtTuyChinh.add(ctTuyChinh);
            } else if (item.isCustom()) {
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

    private boolean kiemTraTrangThaiCamHuy(String tenTrangThaiHienTai) {
        String current = chuanHoaTrangThai(tenTrangThaiHienTai);
        return !("MOI_DAT".equals(current) || "DA_COC".equals(current) || "CHO_XU_LY".equals(current));
    }

    private String chuanHoaTrangThai(String rawStatus) {
        if (rawStatus == null) return "";
        String normalized = Normalizer.normalize(rawStatus.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d").replace("Đ", "D")
                .replace("Ä‘", "d").replace("Ä", "D")
                .toUpperCase().replace(' ', '_');
        if (normalized.contains("KHACH") && normalized.contains("LAY")) return "CHO_KHACH_LAY";
        return normalized;
    }
}
