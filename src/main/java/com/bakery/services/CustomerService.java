package com.bakery.services;

import com.bakery.models.dao.KhachHangDAO;
import com.bakery.models.dto.KhachHangDTO;
import java.sql.SQLException;

public class CustomerService {

    private final KhachHangDAO khachHangDAO;

    public CustomerService() {
        this.khachHangDAO = new KhachHangDAO();
    }

    // ====== VALIDATION ======

    public void validateCustomerInput(KhachHangDTO kh) throws SQLException {
        if (kh == null) {
            throw new SQLException("Du lieu khach hang khong hop le");
        }

        String hoTen = kh.getHoTen() != null ? kh.getHoTen().trim() : "";
        String sdt = kh.getSdt() != null ? kh.getSdt().trim() : "";
        String diaChi = kh.getDiaChi() != null ? kh.getDiaChi().trim() : "";

        kh.setHoTen(hoTen);
        kh.setSdt(sdt);
        kh.setDiaChi(diaChi);

        if (hoTen.isEmpty()) {
            throw new SQLException("Ten khach hang khong duoc de trong");
        }

        if (hoTen.length() > 100) {
            throw new SQLException("Ten khach hang toi da 100 ky tu");
        }

        if (!sdt.matches("^\\d{10}$")) {
            throw new SQLException("SDT phai la 10 chu so");
        }

        if (diaChi.length() > 255) {
            throw new SQLException("Dia chi toi da 255 ky tu");
        }
    }

    // ====== TIM HOAC TAO MOI (voi restore) ======

    public KhachHangDTO timHoacTaoMoi(String sdt) throws SQLException {
        if (sdt == null || !sdt.matches("^\\d{10}$")) {
            throw new SQLException("SDT phai la 10 chu so");
        }

        // Buoc 1: Tim khach active
        KhachHangDTO khachActive = khachHangDAO.timKhachHangBangSDT(sdt);
        if (khachActive != null) {
            return khachActive;
        }

        // Buoc 2: Tim khach da xoa (de hoi restore)
        KhachHangDTO khachXoa = khachHangDAO.timKhachHangXoa(sdt);
        if (khachXoa != null) {
            return khachXoa;
        }

        // Buoc 3: Khong co, return null - Controller se tao moi
        return null;
    }

    // ====== RESTORE ======

    public void khoiPhucKhachHang(int maKH) throws SQLException {
        khachHangDAO.khoiPhucKhachHang(maKH);
    }

    // ====== THEM ======

    public int taoKhachMoi(KhachHangDTO kh) throws SQLException {
        validateCustomerInput(kh);

        KhachHangDTO existing = khachHangDAO.timKhachHangBangSDT(kh.getSdt());
        if (existing != null && existing.getThoiDiemXoa() == null) {
            throw new SQLException("SDT da ton tai trong he thong");
        }

        try {
            return khachHangDAO.themKhachHangMoi(kh);
        } catch (SQLException e) {
            throw new SQLException("Loi tao khach hang: " + e.getMessage(), e);
        }
    }

    // ====== SUA ======

    public void capNhatKhachHang(KhachHangDTO kh) throws SQLException {
        if (kh.getMaKH() <= 0) {
            throw new SQLException("Ma khach hang khong hop le");
        }

        validateCustomerInput(kh);

        KhachHangDTO existing = khachHangDAO.timKhachHangBangMaKH(kh.getMaKH());
        if (existing == null) {
            throw new SQLException("Khach hang khong ton tai");
        }

        try {
            khachHangDAO.suaKhachHang(kh);
        } catch (SQLException e) {
            throw new SQLException("Loi cap nhat khach hang: " + e.getMessage(), e);
        }
    }

    // ====== XOA ======

    public void xoaKhachHang(int maKH, int manvXoa) throws SQLException {
        if (maKH <= 0 || manvXoa <= 0) {
            throw new SQLException("Du lieu khong hop le");
        }

        KhachHangDTO existing = khachHangDAO.timKhachHangBangMaKH(maKH);
        if (existing == null) {
            throw new SQLException("Khach hang khong ton tai");
        }

        try {
            khachHangDAO.xoaKhachHang(maKH, manvXoa);
        } catch (SQLException e) {
            throw new SQLException("Loi xoa khach hang: " + e.getMessage(), e);
        }
    }

    // ====== TIM KIEM ======

    public KhachHangDTO layKHTheoMaKH(int maKH) throws SQLException {
        if (maKH <= 0) {
            throw new SQLException("Ma khach hang khong hop le");
        }

        KhachHangDTO kh = khachHangDAO.timKhachHangBangMaKH(maKH);
        if (kh == null) {
            throw new SQLException("Khach hang khong ton tai");
        }

        return kh;
    }

    public KhachHangDTO layKHTheoSDT(String sdt) throws SQLException {
        if (sdt == null || !sdt.matches("^\\d{10}$")) {
            throw new SQLException("SDT phai la 10 chu so");
        }

        return khachHangDAO.timKhachHangBangSDT(sdt);
    }

    // ====== CAP NHAT DIEM ======

    public void capNhatDiem(int maKH, int diemMoi) throws SQLException {
        if (maKH <= 0 || diemMoi < 0) {
            throw new SQLException("Du lieu khong hop le");
        }

        try {
            khachHangDAO.capNhatDiemTichLuy(maKH, diemMoi);
        } catch (SQLException e) {
            throw new SQLException("Loi cap nhat diem: " + e.getMessage(), e);
        }
    }
}
