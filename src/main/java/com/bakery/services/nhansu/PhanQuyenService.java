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

/**
 * Dịch vụ xử lý phân quyền và kiểm tra quyền truy cập module.
 */
public class PhanQuyenService {
    private final PhanQuyenDAO phanQuyenDAO;

    public PhanQuyenService() {
        this.phanQuyenDAO = new PhanQuyenDAO();
    }

    /**
     * Kiểm tra xem nhân viên có quyền Admin hay không.
     */
    public boolean laAdmin(NhanVienDTO nhanVien) {
        if (nhanVien == null) {
            return false;
        }
        // Mapping theo mã vai trò (1 thường là Admin)
        if (nhanVien.getMaVaiTro() == 1) {
            return true;
        }
        // Kiểm tra theo tên vai trò nếu mã không khớp
        String tenVaiTro = chuanHoa(nhanVien.getTenVaiTro());
        return chuaMotTrong(tenVaiTro, "ADMIN", "QUAN TRI", "QUANTRI", "QUAN TRI VIEN", "QUANTRIVIEN");
    }

    /**
     * Lấy danh sách các Module mà nhân viên được phép truy cập.
     */
    public Set<SystemModule> layModulesDuocCap(NhanVienDTO nhanVien) {
        if (nhanVien == null) {
            return EnumSet.noneOf(SystemModule.class);
        }
        
        // Admin có quyền truy cập tất cả các module
        if (laAdmin(nhanVien)) {
            return EnumSet.allOf(SystemModule.class);
        }

        Set<SystemModule> modules = EnumSet.noneOf(SystemModule.class);
        List<ChucNangDTO> danhSach = phanQuyenDAO.layDanhSachChucNangTheoVaiTro(nhanVien.getMaVaiTro());
        
        for (ChucNangDTO chucNang : danhSach) {
            // Nếu DTO đã có Module được map từ database, sử dụng nó luôn
            if (chucNang.getModule() != null) {
                modules.add(chucNang.getModule());
                continue;
            }

            // Logic fallback: Tự động map dựa trên tên chức năng nếu cột MODULE trong DB bị trống
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
