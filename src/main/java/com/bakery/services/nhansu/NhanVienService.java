package com.bakery.services.nhansu;

import com.bakery.services.BaseService;

import com.bakery.model.dao.nhansu.NhanVienDAO;
import com.bakery.model.dao.nhansu.VaiTroDAO;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.dto.nhansu.VaiTroDTO;
import com.bakery.utils.UserSession;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NhanVienService extends BaseService {

    private final NhanVienDAO nhanVienDAO;
    private final VaiTroDAO vaiTroDAO;

    public NhanVienService() {
        this.nhanVienDAO = new NhanVienDAO();
        this.vaiTroDAO = new VaiTroDAO();
    }

    public NhanVienService(NhanVienDAO nhanVienDAO, VaiTroDAO vaiTroDAO) {
        this.nhanVienDAO = nhanVienDAO;
        this.vaiTroDAO = vaiTroDAO;
    }

    public int themNhanVien(NhanVienDTO nv) throws Exception {
        return nhanVienDAO.themNhanVien(nv);
    }

    public boolean suaNhanVien(NhanVienDTO nv) throws Exception {
        return nhanVienDAO.suaNhanVien(nv);
    }

    /** Cho thôi việc (soft): TRANGTHAILAMVIEC=0, TRANGTHAITK=0 — giữ lại toàn bộ lịch sử, ghi audit */
    public boolean thoiViec(int maNV) throws Exception {
        return nhanVienDAO.thoiViec(maNV, layMaNvHienTai());
    }

    /** Khôi phục nhân viên đã thôi việc: TRANGTHAILAMVIEC=1, TRANGTHAITK=1 + ghi audit */
    public boolean khoiPhucNhanVien(int maNV) throws Exception {
        return nhanVienDAO.khoiPhucNhanVien(maNV, layMaNvHienTai());
    }

    /** Xóa cứng khỏi DB — chỉ dùng cho admin khi cần dọn dữ liệu thử nghiệm */
    public boolean xoaHanNhanVien(int maNV) throws Exception {
        return nhanVienDAO.xoaNhanVien(maNV);
    }

    public List<NhanVienDTO> layTatCaNhanVien() throws Exception {
        return nhanVienDAO.layTatCaNhanVien();
    }

    public Map<Integer, String> layDanhSachVaiTro() throws Exception {
        List<VaiTroDTO> danhSach = vaiTroDAO.layDanhSachVaiTroDangHoatDong();
        Map<Integer, String> result = new LinkedHashMap<>();
        for (VaiTroDTO vt : danhSach) {
            result.put(vt.getMaVaiTro(), vt.getTenVaiTro());
        }
        return result;
    }

    public void capNhatVaiTro(int maNV, List<Integer> dsMaVT) throws Exception {
        nhanVienDAO.capNhatVaiTroNhanVien(maNV, dsMaVT);
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    /** Lấy mã NV đang đăng nhập từ session. */
    private int layMaNvHienTai() {
        NhanVienDTO user = UserSession.getCurrentUser();
        return (user != null) ? user.getMaNV() : 0;
    }
}

