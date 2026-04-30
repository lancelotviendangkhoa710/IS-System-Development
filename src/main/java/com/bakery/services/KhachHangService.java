package com.bakery.services;

import com.bakery.model.dao.KhachHangDAO;
import com.bakery.model.dto.KhachHangDTO;

/**
 * Service duy nhất chịu trách nhiệm tra cứu thông tin Khách hàng.
 * (SRP – Single Responsibility Principle)
 */
public class KhachHangService extends BaseService {

    private final KhachHangDAO khachHangDAO;

    public KhachHangService() {
        this.khachHangDAO = new KhachHangDAO();
    }

    public KhachHangService(KhachHangDAO khachHangDAO) {
        this.khachHangDAO = khachHangDAO;
    }

    /**
     * Tìm khách hàng theo số điện thoại.
     * Trả về null nếu không tìm thấy (khách vãng lai).
     */
    public KhachHangDTO timKhachHangTheoSoDienThoai(String sdt) throws Exception {
        if (sdt == null || sdt.trim().isEmpty())
            return null;
        return khachHangDAO.timKhachHangBangSDT(sdt.trim());
    }

    /**
     * Thêm khách hàng mới.
     * @return Mã khách hàng vừa tạo
     */
    public int themKhachHangMoi(KhachHangDTO kh) throws Exception {
        return khachHangDAO.themKhachHangMoi(kh);
    }

    /**
     * Cập nhật thông tin khách hàng.
     */
    public boolean capNhatKhachHang(KhachHangDTO kh) throws Exception {
        return khachHangDAO.capNhatKhachHang(kh);
    }
}
