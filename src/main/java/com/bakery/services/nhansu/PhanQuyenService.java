package com.bakery.services.nhansu;

import com.bakery.model.dao.nhansu.PhanQuyenDAO;
import com.bakery.model.dto.nhansu.ChucNangDTO;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.enums.SystemModule;

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

    private final PhanQuyenDAO phanQuyenDAO;

    public PhanQuyenService() {
        this.phanQuyenDAO = new PhanQuyenDAO();
    }

    public boolean laAdmin(NhanVienDTO nhanVien) {
        if (nhanVien == null) {
            return false;
        }
        String tenVaiTro = chuanHoa(nhanVien.getTenVaiTro());
        return chuaMotTrong(tenVaiTro, "ADMIN", "QUAN TRI", "QUANTRI", "QUAN TRI VIEN", "QUANTRIVIEN");
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
        return "/fxml/MainMenuView.fxml";
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
        List<ChucNangDTO> danhSach = phanQuyenDAO.layDanhSachChucNangTheoVaiTro(nhanVien.getMaVaiTro());

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
        if (nhanVien == null) {
            return false;
        }
        return chuanHoa(nhanVien.getTenVaiTro()).contains(roleKey);
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
