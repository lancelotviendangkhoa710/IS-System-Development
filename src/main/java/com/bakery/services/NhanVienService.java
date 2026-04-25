package com.bakery.services;

import com.bakery.model.dao.NhanVienDAO;
import com.bakery.model.dto.NhanVienDTO;

public class NhanVienService {

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    /**
     * Xác thực thông tin đăng nhập.
     * Ném Exception với message tiếng Việt nếu thất bại — Controller hiển thị thẳng lên UI.
     */
    public NhanVienDTO dangNhap(String tenDangNhap, String matKhau) throws Exception {
        if (tenDangNhap == null || tenDangNhap.isBlank())
            throw new Exception("Tên đăng nhập không được để trống.");
        if (matKhau == null || matKhau.isEmpty())
            throw new Exception("Mật khẩu không được để trống.");
        return nhanVienDAO.kiemTraDangNhap(tenDangNhap.trim(), matKhau);
    }
}
