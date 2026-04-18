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

    public int taoDonHang(YeuCauTaoDonHangDTO request) throws Exception {
        validateOrderRequest(request);

        int maTrangThai = request.getMaTrangThai();
        if (maTrangThai <= 0) {
            maTrangThai = layMaTrangThaiMoiDat();
        }

        DonDatHangDTO donDatHang = mapToDonDatHangDTO(request, maTrangThai);
        List<CTDonHangDTO> dsCtDonHang = new ArrayList<>();
        List<CTDonTuyChinhDTO> dsCtTuyChinh = new ArrayList<>();
        mapChiTietOrder(request.getItems(), dsCtDonHang, dsCtTuyChinh);

        try {
            int maDonMoi = donDatHangDAO.taoDonHang(donDatHang, dsCtDonHang, dsCtTuyChinh);
            boolean tonTai = donDatHangDAO.tonTaiDonHang(maDonMoi);
            if (!tonTai) {
                throw new Exception("Tao don that bai: Khong tim thay don hang vua tao trong CSDL.");
            }
            System.out.println("[SERVICE] Tao don hang thanh cong. MADON = " + maDonMoi);
            return maDonMoi;
        } catch (SQLException e) {
            throw new Exception("Khong tao duoc don hang: " + e.getMessage(), e);
        }
    }

    public void thanhToanDon(int maHD, Integer maKH, double tongTienHoaDon, double soTienKhachDua) throws Exception {
        if (maHD <= 0) {
            throw new IllegalArgumentException("Ma hoa don sai dinh dang (phai lon hon 0).");
        }
        if (tongTienHoaDon <= 0) {
            throw new IllegalArgumentException("Tong tien hoa don sai dinh dang (phai lon hon 0).");
        }
        if (soTienKhachDua < tongTienHoaDon) {
            throw new IllegalArgumentException("So tien khach dua khong du de thanh toan.");
        }

        try {
            hoaDonDAO.thanhToanVaThangHang(maHD, maKH, tongTienHoaDon);
            System.out.println("[SERVICE] Thanh toan don thanh cong. MAHD = " + maHD);
        } catch (SQLException e) {
            throw new Exception("Thanh toan that bai: " + e.getMessage(), e);
        }
    }

    public void chuyenTrangThaiDon(int maDon, int maTrangThaiMoi, int maNvCapNhat, Integer hinhThucNhan,
            String tenTrangThaiHienTai, String tenTrangThaiMoi) throws Exception {
        if (maDon <= 0) {
            throw new IllegalArgumentException("Ma don sai dinh dang.");
        }
        if (maTrangThaiMoi <= 0) {
            throw new IllegalArgumentException("Ma trang thai moi sai dinh dang.");
        }
        if (maNvCapNhat <= 0) {
            throw new IllegalArgumentException("Ma nhan vien cap nhat sai dinh dang.");
        }
        if (tenTrangThaiHienTai == null || tenTrangThaiHienTai.trim().isEmpty()) {
            throw new IllegalArgumentException("Trang thai hien tai chua duoc nhap.");
        }
        if (tenTrangThaiMoi == null || tenTrangThaiMoi.trim().isEmpty()) {
            throw new IllegalArgumentException("Trang thai moi chua duoc nhap.");
        }

        validateTrangThaiTransition(tenTrangThaiHienTai, tenTrangThaiMoi);

        try {
            donDatHangDAO.chuyenTrangThaiDon(maDon, maTrangThaiMoi, maNvCapNhat, hinhThucNhan);
        } catch (SQLException e) {
            throw new Exception("Chuyen trang thai that bai: " + e.getMessage(), e);
        }
    }

    public void huyDonVaHoanCoc(int maDon, String lyDoHuy, int maNvCapNhat, String tenTrangThaiHienTai)
            throws Exception {
        if (maDon <= 0) {
            throw new IllegalArgumentException("Ma don huy sai dinh dang.");
        }
        if (maNvCapNhat <= 0) {
            throw new IllegalArgumentException("Ma nhan vien cap nhat sai dinh dang.");
        }
        if (lyDoHuy == null || lyDoHuy.trim().isEmpty()) {
            throw new IllegalArgumentException("Ly do huy don chua duoc nhap.");
        }
        if (isTrangThaiCamHuy(tenTrangThaiHienTai)) {
            throw new IllegalStateException("Don hang dang o trang thai khong cho phep huy.");
        }

        try {
            donDatHangDAO.huyDonVaHoanKho(maDon, lyDoHuy.trim(), maNvCapNhat);
        } catch (SQLException e) {
            throw new Exception("Huy don that bai: " + e.getMessage(), e);
        }
    }

    public String theoDoiDonHang(int maDon) throws Exception {
        if (maDon <= 0) {
            throw new IllegalArgumentException("Ma don theo doi khong hop le.");
        }

        try {
            String tenTrangThai = donDatHangDAO.layTenTrangThaiDon(maDon);
            if (tenTrangThai == null || tenTrangThai.trim().isEmpty()) {
                throw new Exception("Khong tim thay don hang voi ma " + maDon + ".");
            }
            return tenTrangThai;
        } catch (SQLException e) {
            throw new Exception("Khong the theo doi don hang: " + e.getMessage(), e);
        }
    }

    public DonDatHangDTO layTomTatDonHang(int maDon) throws Exception {
        if (maDon <= 0) {
            throw new IllegalArgumentException("Ma don theo doi khong hop le.");
        }

        try {
            DonDatHangDTO tomTat = donDatHangDAO.layTomTatDonHang(maDon);
            if (tomTat == null) {
                throw new Exception("Khong tim thay don hang voi ma " + maDon + ".");
            }
            return tomTat;
        } catch (SQLException e) {
            throw new Exception("Khong the lay thong tin don hang: " + e.getMessage(), e);
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
        if (sdt == null || sdt.trim().isEmpty()) {
            return null;
        }
        return khachHangDAO.timKhachHangBangSDT(sdt.trim());
    }

    public List<TrangThaiDonDTO> layDanhSachTrangThaiDon() throws Exception {
        try {
            return donDatHangDAO.layDanhSachTrangThaiDon();
        } catch (SQLException e) {
            throw new Exception("Khong the lay danh sach trang thai don: " + e.getMessage(), e);
        }
    }

    private int layMaTrangThaiMoiDat() throws Exception {
        List<TrangThaiDonDTO> dsTrangThai = layDanhSachTrangThaiDon();
        for (TrangThaiDonDTO trangThai : dsTrangThai) {
            String normalized = normalizeStatus(trangThai.getTenTrangThai());
            if ("MOI_DAT".equals(normalized) || "CHO_XU_LY".equals(normalized)) {
                return trangThai.getMaTrangThai();
            }
        }
        throw new Exception("Khong tim thay trang thai mac dinh MOI_DAT/CHO_XU_LY trong TRANGTHAIDON.");
    }

    private void validateOrderRequest(YeuCauTaoDonHangDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Yeu cau tao don hang bi trong.");
        }
        if (request.getMaNVLap() <= 0) {
            throw new IllegalArgumentException("Ma nhan vien lap don khong hop le.");
        }
        if (request.getTienDaCoc() < 0) {
            throw new IllegalArgumentException("Tien dat coc khong duoc am.");
        }
        if (request.getHinhThucNhan() == null || (request.getHinhThucNhan() != 1 && request.getHinhThucNhan() != 2)) {
            throw new IllegalArgumentException("Hinh thuc nhan chi duoc la Truc tiep (1) hoac Dat hang (2).");
        }
        if (request.getHinhThucNhan() == 2
                && (request.getDiaChiGiao() == null || request.getDiaChiGiao().trim().isEmpty())) {
            throw new IllegalArgumentException("Don dat hang bat buoc nhap dia chi giao.");
        }
        if (request.getHinhThucNhan() == 1) {
            request.setDiaChiGiao(null);
        }
        if (request.getNgayGioNhanBanh() == null) {
            throw new IllegalArgumentException("Ngay gio nhan banh bat buoc nhap.");
        }
        if (request.getNgayGioNhanBanh().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Ngay gio nhan banh khong duoc nam trong qua khu.");
        }

        List<YeuCauChiTietDonHangDTO> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Don hang phai co it nhat 1 san pham.");
        }

        double tongTienCustom = 0.0;
        for (YeuCauChiTietDonHangDTO item : items) {
            validateOrderItem(item);
            if (item.isCustom()) {
                tongTienCustom += item.getDonGia() * item.getSoLuong();
            }
        }

        if (tongTienCustom > 0) {
            double tienCocToiThieu = tongTienCustom * 0.5;
            if (request.getTienDaCoc() < tienCocToiThieu) {
                throw new IllegalArgumentException("Tien dat coc cho banh tuy chinh phai >= 50% gia tri banh custom.");
            }
        }
    }

    private void validateOrderItem(YeuCauChiTietDonHangDTO item) {
        if (item == null) {
            throw new IllegalArgumentException("Chi tiet don hang khong hop le.");
        }
        if (item.getMaSP() <= 0) {
            throw new IllegalArgumentException("Ma san pham khong hop le.");
        }
        if (item.getSoLuong() <= 0) {
            throw new IllegalArgumentException("So luong phai la so nguyen duong.");
        }
        if (item.getDonGia() <= 0) {
            throw new IllegalArgumentException("Don gia phai lon hon 0.");
        }
    }

    private DonDatHangDTO mapToDonDatHangDTO(YeuCauTaoDonHangDTO request, int maTrangThai) {
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

    private void mapChiTietOrder(List<YeuCauChiTietDonHangDTO> items, List<CTDonHangDTO> dsCtDonHang,
            List<CTDonTuyChinhDTO> dsCtTuyChinh) {
        for (YeuCauChiTietDonHangDTO item : items) {
            if (item.isCustom()) {
                CTDonTuyChinhDTO ctTuyChinh = new CTDonTuyChinhDTO();
                ctTuyChinh.setMaSP(item.getMaSP());
                ctTuyChinh.setSoLuong(item.getSoLuong());
                ctTuyChinh.setDonGia(item.getDonGia());
                ctTuyChinh.setLoiChucTrenBanh(item.getGhiChu());
                ctTuyChinh.setGhiChuThoBanh(item.getPhuKien());
                dsCtTuyChinh.add(ctTuyChinh);
            } else {
                CTDonHangDTO ctDonHang = new CTDonHangDTO();
                ctDonHang.setMaSP(item.getMaSP());
                ctDonHang.setSoLuong(item.getSoLuong());
                ctDonHang.setDonGia(item.getDonGia());
                dsCtDonHang.add(ctDonHang);
            }
        }
    }

    private void validateTrangThaiTransition(String tenTrangThaiHienTai, String tenTrangThaiMoi) {
        String current = normalizeStatus(tenTrangThaiHienTai);
        String next = normalizeStatus(tenTrangThaiMoi);

        if (current.equals(next)) {
            throw new IllegalArgumentException("Trang thai moi khong duoc trung trang thai hien tai.");
        }

        if ("MOI_DAT".equals(current) && Set.of("DA_COC", "DANG_SAN_XUAT").contains(next)) {
            return;
        }
        if ("DA_COC".equals(current) && "DANG_SAN_XUAT".equals(next)) {
            return;
        }
        if ("DANG_SAN_XUAT".equals(current) && Set.of("CHO_GIAO", "CHO_KHACH_LAY").contains(next)) {
            return;
        }
        if (Set.of("CHO_GIAO", "CHO_KHACH_LAY").contains(current) && "HOAN_THANH".equals(next)) {
            return;
        }
        if (!"HOAN_THANH".equals(current) && "HUY".equals(next)) {
            return;
        }

        throw new IllegalStateException("Khong duoc phep nhay coc trang thai don hang.");
    }

    private boolean isTrangThaiCamHuy(String tenTrangThaiHienTai) {
        String current = normalizeStatus(tenTrangThaiHienTai);
        return "CHO_GIAO".equals(current) || "HOAN_THANH".equals(current);
    }

    private String normalizeStatus(String rawStatus) {
        if (rawStatus == null) {
            return "";
        }

        String normalized = Normalizer.normalize(rawStatus.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase()
                .replace(' ', '_');

        if (normalized.contains("KHACH") && normalized.contains("LAY")) {
            return "CHO_KHACH_LAY";
        }

        return normalized;
    }
}
