package com.bakery.services;

import com.bakery.model.dao.PhanQuyenDAO;
import com.bakery.model.dto.ChucNangDTO;
import com.bakery.model.dto.NhanVienDTO;

import java.text.Normalizer;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AuthorizationService {
    public enum ModuleKey {
        POS,
        INVENTORY,
        STAFF,
        REPORTS,
        KDS,
        AUDIT_LOGS
    }

    private final PhanQuyenDAO phanQuyenDAO;

    public AuthorizationService() {
        this.phanQuyenDAO = new PhanQuyenDAO();
    }

    public boolean laAdmin(NhanVienDTO nhanVien) {
        if (nhanVien == null) {
            return false;
        }
        // Legacy mapping by role id.
        if (nhanVien.getMaVaiTro() == 1) {
            return true;
        }
        // Fallback by role name from DB (e.g. "Quản trị viên", "Admin"...).
        String tenVaiTro = chuanHoa(nhanVien.getTenVaiTro());
        return chuaMotTrong(tenVaiTro, "ADMIN", "QUAN TRI", "QUANTRI", "QUAN TRI VIEN", "QUANTRIVIEN");
    }

    public Set<ModuleKey> layModulesDuocCap(NhanVienDTO nhanVien) {
        if (nhanVien == null) {
            return EnumSet.noneOf(ModuleKey.class);
        }
        if (laAdmin(nhanVien)) {
            return EnumSet.allOf(ModuleKey.class);
        }

        Set<ModuleKey> modules = EnumSet.noneOf(ModuleKey.class);
        List<ChucNangDTO> danhSach = phanQuyenDAO.layDanhSachChucNangTheoVaiTro(nhanVien.getMaVaiTro());
        for (ChucNangDTO chucNang : danhSach) {
            String ten = chucNang.getTenChucNang();
            String normalized = chuanHoa(ten);
            if (chuaMotTrong(normalized, "POS", "BAN HANG", "LAP HOA DON", "DON HANG")) {
                modules.add(ModuleKey.POS);
            }
            if (chuaMotTrong(normalized, "INVENTORY", "KHO", "NGUYEN LIEU", "NHAP KHO", "XUAT KHO")) {
                modules.add(ModuleKey.INVENTORY);
            }
            if (chuaMotTrong(normalized, "STAFF", "NHAN SU", "NHAN VIEN", "PHAN QUYEN")) {
                modules.add(ModuleKey.STAFF);
            }
            if (chuaMotTrong(normalized, "REPORT", "BAO CAO", "DOANH THU", "LOI NHUAN")) {
                modules.add(ModuleKey.REPORTS);
            }
            if (chuaMotTrong(normalized, "KDS", "KITCHEN", "BEP", "SAN XUAT")) {
                modules.add(ModuleKey.KDS);
            }
            if (chuaMotTrong(normalized, "AUDIT", "NHAT KY", "LOG")) {
                modules.add(ModuleKey.AUDIT_LOGS);
            }
        }
        return modules;
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
