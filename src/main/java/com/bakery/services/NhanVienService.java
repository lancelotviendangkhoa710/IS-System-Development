package com.bakery.services;

import com.bakery.model.dao.NhanVienDAO;
import com.bakery.model.dto.NhanVienDTO;

import java.sql.SQLException;
import java.util.Map;

/**
 * Service quản lý nghiệp vụ Nhân viên.
 * Đảm bảo tuân thủ MVP/Service pattern.
 */
public class NhanVienService {

    private final NhanVienDAO nhanVienDAO;

    public NhanVienService() {
        this.nhanVienDAO = new NhanVienDAO();
    }

    public NhanVienService(NhanVienDAO nhanVienDAO) {
        this.nhanVienDAO = nhanVienDAO;
    }

    /**
     * Thêm nhân viên mới vào hệ thống.
     * @param nv DTO chứa thông tin nhân viên
     * @return Mã nhân viên vừa tạo
     * @throws SQLException nếu có lỗi DB (trùng lặp, vi phạm ràng buộc)
     */
    public int themNhanVien(NhanVienDTO nv) throws SQLException {
        // Có thể thêm logic kiểm tra nghiệp vụ ở đây nếu cần
        return nhanVienDAO.themNhanVien(nv);
    }

    /**
     * Lấy danh sách vai trò hiện có.
     */
    public Map<Integer, String> layDanhSachVaiTro() {
        return nhanVienDAO.layDanhSachVaiTro();
    }
}
