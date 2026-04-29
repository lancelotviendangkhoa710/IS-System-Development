package com.bakery.services;

import com.bakery.model.dao.NhanVienDAO;
import com.bakery.model.dao.VaiTroDAO;
import com.bakery.model.dto.NhanVienDTO;
import com.bakery.model.dto.VaiTroDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service quản lý nghiệp vụ Nhân viên.
 * Đảm bảo tuân thủ MVP/Service pattern.
 */
public class NhanVienService {

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

    /**
     * Thêm nhân viên mới vào hệ thống.
     * @param nv DTO chứa thông tin nhân viên
     * @return Mã nhân viên vừa tạo
     * @throws Exception nếu có lỗi DB (trùng lặp, vi phạm ràng buộc)
     */
    public int themNhanVien(NhanVienDTO nv) throws Exception {
        return nhanVienDAO.themNhanVien(nv);
    }

    public boolean suaNhanVien(NhanVienDTO nv) throws Exception {
        return nhanVienDAO.suaNhanVien(nv);
    }

    public boolean xoaNhanVien(int maNV) throws Exception {
        return nhanVienDAO.xoaNhanVien(maNV);
    }

    public List<NhanVienDTO> layTatCaNhanVien() throws Exception {
        return nhanVienDAO.layTatCaNhanVien();
    }

    /**
     * Lấy danh sách vai trò hiện có (dùng VaiTroDAO thay vì NhanVienDAO).
     */
    public Map<Integer, String> layDanhSachVaiTro() throws Exception {
        List<VaiTroDTO> danhSach = vaiTroDAO.layDanhSachVaiTroDangHoatDong();
        Map<Integer, String> result = new LinkedHashMap<>();
        for (VaiTroDTO vt : danhSach) {
            result.put(vt.getMaVaiTro(), vt.getTenVaiTro());
        }
        return result;
    }
}
