package com.bakery.services.nhansu;

import com.bakery.model.dao.nhansu.PhanQuyenDAO;
import com.bakery.model.dto.nhansu.ChucNangDTO;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.enums.SystemModule;
import com.bakery.utils.SessionContext;

import java.text.Normalizer;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PhanQuyenService {
    private static final String ROLE_QUAN_LY = "QUAN LY";
    private static final String ROLE_THU_NGAN = "THU NGAN";
    private static final String ROLE_THO_BEP = "THO BEP";
    private static final String ROLE_THU_KHO = "THU KHO";

    public enum TinhNangHeThong {
        TONG_QUAN,
        TAI_KHOAN_CA_NHAN,
        BAN_HANG_POS,
        THEO_DOI_DON_HANG,
        KHACH_HANG,
        QUAN_LY_CA_LAM_VIEC,
        THU_CHI,
        KHO_TONG_QUAN,
        NHAP_KHO,
        XUAT_KHO,
        KIEM_KE_KHO,
        NHA_CUNG_CAP,
        SAN_PHAM,
        DANH_MUC_SAN_PHAM,
        NGUYEN_LIEU,
        CONG_THUC_SAN_XUAT,
        THANH_PHAN_BANH, // Quản lý thành phần bánh tùy chỉnh
        NHAN_SU,
        PHAN_QUYEN_TAI_KHOAN,
        PHAN_QUYEN_VAI_TRO,
        BAO_CAO_KINH_DOANH,
        NHAT_KY_HE_THONG,
        KDS_MAN_HINH_BEP,
        DON_HANG_BEP, // Quản lý đơn hàng bếp (chỉ bánh tùy chỉnh)
        CAU_HINH_GIOI_HAN_DON, // Cấu hình giới hạn nhận đơn
        KHOI_PHUC_DU_LIEU // Khôi phục / xóa vĩnh viễn dữ liệu soft-delete
    }

    /** Tính năng mặc định — mọi nhân viên đều có sau khi đăng nhập. */
    private static final EnumSet<TinhNangHeThong> TINH_NANG_MAC_DINH = EnumSet.of(
            TinhNangHeThong.TONG_QUAN,
            TinhNangHeThong.TAI_KHOAN_CA_NHAN);

    private final PhanQuyenDAO phanQuyenDAO;

    public PhanQuyenService() {
        this.phanQuyenDAO = new PhanQuyenDAO();
    }

    public boolean laAdmin(NhanVienDTO nhanVien) {
        if (nhanVien == null || nhanVien.getDanhSachTenVaiTro() == null) return false;
        // Neu da chon vai tro active → chi check vai tro do
        String tenHD = layTenVaiTroHoatDong(nhanVien);
        if (tenHD != null) {
            String chuan = chuanHoa(tenHD);
            return chuaMotTrong(chuan, "ADMIN", "QUAN TRI", "QUANTRI", "QUAN TRI VIEN", "QUANTRIVIEN");
        }
        // Chua chon → check tat ca (khong bao gom QUAN LY de tranh nham voi laQuanLy)
        for (String tenVT : nhanVien.getDanhSachTenVaiTro()) {
            String tenChuan = chuanHoa(tenVT);
            if (chuaMotTrong(tenChuan, "ADMIN", "QUAN TRI", "QUANTRI", "QUAN TRI VIEN", "QUANTRIVIEN")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lay ten vai tro dang hoat dong tu SessionContext.
     * Tra ve null neu chua chon (chi co 1 vai tro hoac chua set).
     */
    private String layTenVaiTroHoatDong(NhanVienDTO nhanVien) {
        if (nhanVien == null) return null;
        int activeId = SessionContext.getInstance().getActiveVaiTroId();
        if (activeId <= 0) return null;
        List<Integer> maList = nhanVien.getDanhSachMaVaiTro();
        List<String>  tenList = nhanVien.getDanhSachTenVaiTro();
        for (int i = 0; i < Math.min(maList.size(), tenList.size()); i++) {
            if (maList.get(i) == activeId) return tenList.get(i);
        }
        return null;
    }

    /** Lay danh sach maVaiTro chi gom vai tro dang hoat dong (neu da chon). */
    private List<Integer> layDanhSachMaVaiTroHieuLuc(NhanVienDTO nhanVien) {
        if (nhanVien == null) return List.of();
        int activeId = SessionContext.getInstance().getActiveVaiTroId();
        if (activeId > 0 && nhanVien.getDanhSachMaVaiTro().contains(activeId)) {
            return List.of(activeId);
        }
        return nhanVien.getDanhSachMaVaiTro();
    }

    public boolean laQuanLy(NhanVienDTO nhanVien) {
        String tenHD = layTenVaiTroHoatDong(nhanVien);
        if (tenHD != null) return chuanHoa(tenHD).contains(ROLE_QUAN_LY);
        return laVaiTro(nhanVien, ROLE_QUAN_LY);
    }

    public boolean laThuNgan(NhanVienDTO nhanVien) {
        String tenHD = layTenVaiTroHoatDong(nhanVien);
        if (tenHD != null) return chuanHoa(tenHD).contains(ROLE_THU_NGAN);
        return laVaiTro(nhanVien, ROLE_THU_NGAN);
    }

    public boolean laThoBep(NhanVienDTO nhanVien) {
        String tenHD = layTenVaiTroHoatDong(nhanVien);
        if (tenHD != null) return chuanHoa(tenHD).contains(ROLE_THO_BEP);
        return laVaiTro(nhanVien, ROLE_THO_BEP);
    }

    public boolean laThuKho(NhanVienDTO nhanVien) {
        String tenHD = layTenVaiTroHoatDong(nhanVien);
        if (tenHD != null) return chuanHoa(tenHD).contains(ROLE_THU_KHO);
        return laVaiTro(nhanVien, ROLE_THU_KHO);
    }

    public Set<TinhNangHeThong> layTinhNangDuocCap(NhanVienDTO nhanVien) {
        EnumSet<TinhNangHeThong> tinhNang = EnumSet.copyOf(TINH_NANG_MAC_DINH);
        if (nhanVien == null)
            return tinhNang;

        // Nguồn sự thật từ DB — VAITRO_CHUCNANG → SystemModule
        Set<SystemModule> modules = layModulesDuocCap(nhanVien);

        // Lấy danh sách ChucNangDTO để phân biệt POS vs Theo dõi đơn hàng
        boolean coChucNangPosThucSu = coChucNangPosThucSu(nhanVien);

        if (modules.contains(SystemModule.BAN_HANG)) {
            // BAN_HANG_POS chỉ cấp khi chức năng thực sự là POS, không phải chỉ "Theo dõi
            // đơn"
            if (coChucNangPosThucSu) {
                tinhNang.addAll(EnumSet.of(
                        TinhNangHeThong.BAN_HANG_POS,
                        TinhNangHeThong.THEO_DOI_DON_HANG,
                        TinhNangHeThong.QUAN_LY_CA_LAM_VIEC,
                        TinhNangHeThong.THU_CHI));
            } else {
                // Có MODULE=BAN_HANG nhưng chỉ là theo dõi đơn (ví dụ: Thợ bếp)
                tinhNang.add(TinhNangHeThong.THEO_DOI_DON_HANG);
            }
        }
        if (modules.contains(SystemModule.KHACH_HANG)) {
            tinhNang.add(TinhNangHeThong.KHACH_HANG);
        }
        if (modules.contains(SystemModule.KHO)) {
            tinhNang.addAll(EnumSet.of(
                    TinhNangHeThong.KHO_TONG_QUAN,
                    TinhNangHeThong.NHAP_KHO,
                    TinhNangHeThong.XUAT_KHO,
                    TinhNangHeThong.KIEM_KE_KHO,
                    TinhNangHeThong.NHA_CUNG_CAP,
                    TinhNangHeThong.SAN_PHAM,
                    TinhNangHeThong.DANH_MUC_SAN_PHAM,
                    TinhNangHeThong.NGUYEN_LIEU,
                    TinhNangHeThong.CONG_THUC_SAN_XUAT,
                    TinhNangHeThong.THANH_PHAN_BANH));
        }
        if (modules.contains(SystemModule.NHAN_SU)) {
            tinhNang.addAll(EnumSet.of(
                    TinhNangHeThong.NHAN_SU,
                    TinhNangHeThong.PHAN_QUYEN_TAI_KHOAN,
                    TinhNangHeThong.PHAN_QUYEN_VAI_TRO));
        }
        if (modules.contains(SystemModule.BAO_CAO)) {
            tinhNang.add(TinhNangHeThong.BAO_CAO_KINH_DOANH);
        }
        // Cấu hình giới hạn đơn + Lịch sử hệ thống + Khôi phục DL — chỉ dành cho Quản
        // lý
        if (laQuanLy(nhanVien) || laAdmin(nhanVien)) {
            tinhNang.add(TinhNangHeThong.CAU_HINH_GIOI_HAN_DON);
            tinhNang.add(TinhNangHeThong.NHAT_KY_HE_THONG);
            tinhNang.add(TinhNangHeThong.KHOI_PHUC_DU_LIEU);
        }
        if (modules.contains(SystemModule.NHA_BEP)) {
            // Tho Bep: full access bep + full access san pham (de cau hinh cong thuc)
            // + read-only kho (tab Nhap kho bi khoa boi KhoViewFXMLController)
            tinhNang.addAll(EnumSet.of(
                    TinhNangHeThong.KDS_MAN_HINH_BEP,
                    TinhNangHeThong.THEO_DOI_DON_HANG,
                    TinhNangHeThong.DON_HANG_BEP,
                    TinhNangHeThong.XUAT_KHO,
                    TinhNangHeThong.KHO_TONG_QUAN, // Xem kho read-only
                    TinhNangHeThong.SAN_PHAM, // Full access san pham
                    TinhNangHeThong.DANH_MUC_SAN_PHAM, // Full access danh muc sp
                    TinhNangHeThong.CONG_THUC_SAN_XUAT, // Cau hinh cong thuc
                    TinhNangHeThong.THANH_PHAN_BANH // Cau hinh thanh phan banh
            ));
        }

        return tinhNang;
    }

    /**
     * Kiểm tra user có chức năng POS thực sự (bán hàng tại quầy) không.
     * Phân biệt với "Theo dõi đơn hàng" (cùng MODULE=POS trong DB nhưng
     * không được phép mở màn hình BanHangView).
     *
     * Logic: đọc danh sách ChucNangDTO, tìm chức năng có tên chứa "POS",
     * "BÁN HÀNG", "LẬP HÓA ĐƠN" — không phải chỉ "THEO DÕI".
     */
    private boolean coChucNangPosThucSu(NhanVienDTO nhanVien) {
        if (nhanVien == null) return false;
        try {
            List<Integer> dsHieuLuc = layDanhSachMaVaiTroHieuLuc(nhanVien);
            if (dsHieuLuc.isEmpty()) return false;
            PhanQuyenDAO.RolePermissionInfo info = phanQuyenDAO.layPhanQuyenHopNhat(dsHieuLuc);
            if (info == null) return false;
            for (ChucNangDTO cn : info.getDanhSachChucNang()) {
                if (!cn.isCanView()) continue;
                String ten = chuanHoa(cn.getTenChucNang());
                if (chuaMotTrong(ten, "POS", "BAN HANG", "LAP HOA DON", "BILL", "CASHIER")) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("[PhanQuyenService] Loi kiem tra POS: " + e.getMessage());
        }
        return false;
    }

    public boolean coTinhNang(NhanVienDTO nhanVien, TinhNangHeThong tinhNang) {
        if (tinhNang == null) {
            return false;
        }
        return layTinhNangDuocCap(nhanVien).contains(tinhNang);
    }

    /**
     * Xác định màn hình trang chủ theo vai trò.
     * Nguyên tắc: vai trò có quyền CAO NHẤT luôn được ưu tiên (Admin > Quản lý >
     * các vai trò chuyên biệt).
     * Nếu nhân viên có đồng thời Thu Ngân + Quản lý → hiển thị màn hình Quản lý.
     */
    public String layManHinhTrangChu(NhanVienDTO nhanVien) {
        if (laAdmin(nhanVien) || laQuanLy(nhanVien)) {
            return "/fxml/hethong/MainView.fxml";
        }
        if (laThuNgan(nhanVien)) {
            return "/fxml/hethong/ThuNganDashboardView.fxml";
        }
        if (laThoBep(nhanVien)) {
            return "/fxml/hethong/ThoBepDashboardView.fxml";
        }
        if (laThuKho(nhanVien)) {
            return "/fxml/hethong/ThuKhoDashboardView.fxml";
        }
        return "/fxml/hethong/MainView.fxml";
    }

    /**
     * Xác định tiêu đề cửa sổ theo vai trò.
     * Cùng nguyên tắc "highest privilege wins" với layManHinhTrangChu.
     */
    public String layTieuDeTrangChu(NhanVienDTO nhanVien) {
        if (laAdmin(nhanVien) || laQuanLy(nhanVien)) {
            return "H3K Bakery - He Thong Quan Ly";
        }
        if (laThuNgan(nhanVien)) {
            return "H3K Bakery - Thu Ngan";
        }
        if (laThoBep(nhanVien)) {
            return "H3K Bakery - Tho Bep";
        }
        if (laThuKho(nhanVien)) {
            return "H3K Bakery - Thu Kho";
        }
        return "H3K Bakery - He Thong Quan Ly";
    }

    public Set<SystemModule> layModulesDuocCap(NhanVienDTO nhanVien) {
        if (nhanVien == null) {
            return EnumSet.noneOf(SystemModule.class);
        }

        Set<SystemModule> modules = EnumSet.noneOf(SystemModule.class);
        List<ChucNangDTO> danhSach;

        try {
            // Chi lay quyen cua vai tro dang hoat dong (active role)
            List<Integer> dsHieuLuc = layDanhSachMaVaiTroHieuLuc(nhanVien);
            PhanQuyenDAO.RolePermissionInfo info = dsHieuLuc.isEmpty()
                    ? null : phanQuyenDAO.layPhanQuyenHopNhat(dsHieuLuc);
            danhSach = info != null ? info.getDanhSachChucNang() : List.of();
        } catch (Exception e) {
            System.err.println("[PhanQuyenService] Loi khi lay quyen: " + e.getMessage());
            danhSach = List.of();
        }

        for (ChucNangDTO chucNang : danhSach) {
            if (chucNang.getModule() != null) {
                modules.add(chucNang.getModule());
                continue;
            }

            String normalized = chuanHoa(chucNang.getTenChucNang());

            if (chuaMotTrong(normalized, "POS", "BAN HANG", "LAP HOA DON", "DON HANG")) {
                modules.add(SystemModule.BAN_HANG);
            }
            if (chuaMotTrong(normalized, "INVENTORY", "KHO", "NGUYEN LIEU", "NHAP KHO", "XUAT KHO")) {
                modules.add(SystemModule.KHO);
            }
            if (chuaMotTrong(normalized, "STAFF", "NHAN SU", "NHAN VIEN", "PHAN QUYEN")) {
                modules.add(SystemModule.NHAN_SU);
            }
            if (chuaMotTrong(normalized, "REPORT", "BAO CAO", "DOANH THU", "LOI NHUAN")) {
                modules.add(SystemModule.BAO_CAO);
            }
            if (chuaMotTrong(normalized, "KDS", "KITCHEN", "BEP", "SAN XUAT")) {
                modules.add(SystemModule.NHA_BEP);
            }
            if (chuaMotTrong(normalized, "AUDIT", "NHAT KY", "LOG")) {
                modules.add(SystemModule.NHAT_KY);
            }
            if (chuaMotTrong(normalized, "CUSTOMER", "KHACH HANG", "CRM")) {
                modules.add(SystemModule.KHACH_HANG);
            }
        }
        return modules;
    }

    private boolean laVaiTro(NhanVienDTO nhanVien, String roleKey) {
        if (nhanVien == null || nhanVien.getDanhSachTenVaiTro() == null) {
            return false;
        }
        for (String tenVT : nhanVien.getDanhSachTenVaiTro()) {
            if (chuanHoa(tenVT).contains(roleKey)) {
                return true;
            }
        }
        return false;
    }

    private String chuanHoa(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "D")
                .toUpperCase(Locale.ROOT);
    }

    private boolean chuaMotTrong(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
