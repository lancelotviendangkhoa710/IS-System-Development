package com.bakery.services;

import com.bakery.model.dao.NhanVienDAO;
import com.bakery.model.dto.NhanVienDTO;

public class AuthService {
    private final NhanVienDAO nhanVienDAO;

    public AuthService() {
        this.nhanVienDAO = new NhanVienDAO();
    }

    public NhanVienDTO dangNhap(String tenDangNhap, String matKhau) throws Exception {
        return nhanVienDAO.kiemTraDangNhap(tenDangNhap, matKhau);
    }
}
