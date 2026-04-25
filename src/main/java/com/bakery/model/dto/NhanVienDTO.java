package com.bakery.model.dto;

import java.time.LocalDate;

public class NhanVienDTO {
    private int maNV;
    private int maVaiTro;
    private String hoTen;
    private LocalDate ngaySinh;
    private String sdt;
    private String tenDangNhap;
    private String matKhau;
    private Integer trangThaiLamViec;

    public NhanVienDTO() {}

    public NhanVienDTO(int maNV, int maVaiTro, String hoTen, LocalDate ngaySinh, String sdt, String tenDangNhap, String matKhau, Integer trangThaiLamViec) {
        this.maNV = maNV;
        this.maVaiTro = maVaiTro;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.sdt = sdt;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.trangThaiLamViec = trangThaiLamViec;
    }

    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }

    public int getMaVaiTro() { return maVaiTro; }
    public void setMaVaiTro(int maVaiTro) { this.maVaiTro = maVaiTro; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public Integer getTrangThaiLamViec() { return trangThaiLamViec; }
    public void setTrangThaiLamViec(Integer trangThaiLamViec) { this.trangThaiLamViec = trangThaiLamViec; }
}
