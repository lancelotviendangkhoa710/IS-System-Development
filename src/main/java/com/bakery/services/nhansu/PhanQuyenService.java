package com.bakery.services.nhansu;

import com.bakery.model.dao.nhansu.PhanQuyenDAO;
import com.bakery.model.dto.nhansu.ChucNangDTO;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.enums.SystemModule;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
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
        NHAN_SU,
        PHAN_QUYEN_TAI_KHOAN,
        PHAN_QUYEN_VAI_TRO,
        BAO_CAO_KINH_DOANH,
        NHAT_KY_HE_THONG,
        KDS_MAN_HINH_BEP
    }

    private static final EnumSet<TinhNangHeThong> TINH_NANG_MAC_DINH = EnumSet.of(
            TinhNangHeThong.TONG_QUAN,
            TinhNangHeThong.TAI_KHOAN_CA_NHAN
    );

    private static final EnumSet<TinhNangHeThong> TINH_NANG_QUAN_LY = EnumSet.of(
            TinhNangHeThong.BAN_HANG_POS,
            TinhNangHeThong.THEO_DOI_DON_HANG,
            TinhNangHeThong.KHACH_HANG,
            TinhNangHeThong.QUAN_LY_CA_LAM_VIEC,
            TinhNangHeThong.THU_CHI,
            TinhNangHeThong.KHO_TONG_QUAN,
            TinhNangHeThong.NHAP_KHO,
            TinhNangHeThong.XUAT_KHO,
            TinhNangHeThong.KIEM_KE_KHO,
            TinhNangHeThong.NHA_CUNG_CAP,
            TinhNangHeThong.SAN_PHAM,
            TinhNangHeThong.DANH_MUC_SAN_PHAM,
            TinhNangHeThong.NGUYEN_LIEU,
            TinhNangHeThong.CONG_THUC_SAN_XUAT,
            TinhNangHeThong.NHAN_SU,
            TinhNangHeThong.PHAN_QUYEN_TAI_KHOAN,
            TinhNangHeThong.PHAN_QUYEN_VAI_TRO,
            TinhNangHeThong.BAO_CAO_KINH_DOANH,
            TinhNangHeThong.NHAT_KY_HE_THONG,
            TinhNangHeThong.KDS_MAN_HINH_BEP
    );

    private static final EnumSet<TinhNangHeThong> TINH_NANG_THU_NGAN = EnumSet.of(
            TinhNangHeThong.BAN_HANG_POS,
            TinhNangHeThong.THEO_DOI_DON_HANG,
            TinhNangHeThong.KHACH_HANG,
            TinhNangHeThong.QUAN_LY_CA_LAM_VIEC,
            TinhNangHeThong.THU_CHI
    );

    private static final EnumSet<TinhNangHeThong> TINH_NANG_THU_KHO = EnumSet.of(
            TinhNangHeThong.KHO_TONG_QUAN,
            TinhNangHeThong.NHAP_KHO,
            TinhNangHeThong.XUAT_KHO,
            TinhNangHeThong.KIEM_KE_KHO,
            TinhNangHeThong.NHA_CUNG_CAP,
            TinhNangHeThong.NGUYEN_LIEU
    );

    private static final EnumSet<TinhNangHeThong> TINH_NANG_THO_BEP = EnumSet.of(
            TinhNangHeThong.THEO_DOI_DON_HANG,
            TinhNangHeThong.KDS_MAN_HINH_BEP,
            TinhNangHeThong.XUAT_KHO,
            TinhNangHeThong.KHO_TONG_QUAN
    );

    private final PhanQuyenDAO phanQuyenDAO;

    public PhanQuyenService() {
        this.phanQuyenDAO = new PhanQuyenDAO();
    }

    public boolean laAdmin(NhanVienDTO nhanVien) {
        if (nhanVien == null || nhanVien.getDanhSachTenVaiTro() == null) {
            return false;
        }
        for (String tenVT : nhanVien.getDanhSachTenVaiTro()) {
            String tenChuan = chuanHoa(tenVT);
            if (chuaMotTrong(tenChuan, "ADMIN", "QUAN TRI", "QUANTRI", "QUAN TRI VIEN", "QUANTRIVIEN", "QUAN LY")) {
                return true;
            }
        }
        return false;
    }

    public boolean laQuanLy(NhanVienDTO nhanVien) {
        return laVaiTro(nhanVien, ROLE_QUAN_LY);
    }

    public boolean laThuNgan(NhanVienDTO nhanVien) {
        return laVaiTro(nhanVien, ROLE_THU_NGAN);
    }

    public boolean laThoBep(NhanVienDTO nhanVien) {
        return laVaiTro(nhanVien, ROLE_THO_BEP);
    }

    public boolean laThuKho(NhanVienDTO nhanVien) {
        return laVaiTro(nhanVien, ROLE_THU_KHO);
    }

    public Set<TinhNangHeThong> layTinhNangDuocCap(NhanVienDTO nhanVien) {
        EnumSet<TinhNangHeThong> tinhNang = EnumSet.copyOf(TINH_NANG_MAC_DINH);
        if (nhanVien == null) {
            return tinhNang;
        }

        Set<String> tapVaiTro = new LinkedHashSet<>();
        if (nhanVien.getDanhSachTenVaiTro() != null) {
            tapVaiTro.addAll(nhanVien.getDanhSachTenVaiTro());
        }
        if (nhanVien.getTenVaiTro() != null && !nhanVien.getTenVaiTro().isBlank()) {
            tapVaiTro.add(nhanVien.getTenVaiTro());
        }

        List<String> dsVaiTroPhang = new ArrayList<>();
        for (String vaiTro : tapVaiTro) {
            if (vaiTro == null || vaiTro.isBlank()) {
                continue;
            }
            String[] roleChunks = vaiTro.split("\\+");
            for (String roleChunk : roleChunks) {
                if (roleChunk != null && !roleChunk.isBlank()) {
                    dsVaiTroPhang.add(roleChunk.trim());
                }
            }
        }

        for (String vaiTro : dsVaiTroPhang) {
            String roleKey = chuanHoa(vaiTro);
            if (ROLE_QUAN_LY.equals(roleKey)) {
                tinhNang.addAll(TINH_NANG_QUAN_LY);
            } else if (ROLE_THU_NGAN.equals(roleKey)) {
                tinhNang.addAll(TINH_NANG_THU_NGAN);
            } else if (ROLE_THU_KHO.equals(roleKey)) {
                tinhNang.addAll(TINH_NANG_THU_KHO);
            } else if (ROLE_THO_BEP.equals(roleKey)) {
                tinhNang.addAll(TINH_NANG_THO_BEP);
            }
        }

        if (laAdmin(nhanVien)) {
            tinhNang.addAll(TINH_NANG_QUAN_LY);
        }
        return tinhNang;
    }

    public boolean coTinhNang(NhanVienDTO nhanVien, TinhNangHeThong tinhNang) {
        if (tinhNang == null) {
            return false;
        }
        return layTinhNangDuocCap(nhanVien).contains(tinhNang);
    }

    public String layManHinhTrangChu(NhanVienDTO nhanVien) {
        if (laThuNgan(nhanVien)) {
            return "/fxml/ThuNganDashboardView.fxml";
        }
        if (laThoBep(nhanVien)) {
            return "/fxml/ThoBepDashboardView.fxml";
        }
        if (laThuKho(nhanVien)) {
            return "/fxml/ThuKhoDashboardView.fxml";
        }
        return "/fxml/MainView.fxml";
    }

    public String layTieuDeTrangChu(NhanVienDTO nhanVien) {
        if (laThuNgan(nhanVien)) {
            return "H3K Bakery - Thu ngan";
        }
        if (laThoBep(nhanVien)) {
            return "H3K Bakery - Tho bep";
        }
        if (laThuKho(nhanVien)) {
            return "H3K Bakery - Thu kho";
        }
        return "H3K Bakery - He thong quan ly";
    }

    public Set<SystemModule> layModulesDuocCap(NhanVienDTO nhanVien) {
        if (nhanVien == null) {
            return EnumSet.noneOf(SystemModule.class);
        }

        Set<SystemModule> modules = EnumSet.noneOf(SystemModule.class);
        List<ChucNangDTO> danhSach;
        
        try {
            // Sử dụng logic hợp nhất quyền từ tất cả vai trò của nhân viên
            PhanQuyenDAO.RolePermissionInfo info = phanQuyenDAO.layPhanQuyenHopNhat(nhanVien.getDanhSachMaVaiTro());
            danhSach = info != null ? info.getDanhSachChucNang() : List.of();
        } catch (Exception e) {
            System.err.println("[PhanQuyenService] Lỗi khi hợp nhất quyền: " + e.getMessage());
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
