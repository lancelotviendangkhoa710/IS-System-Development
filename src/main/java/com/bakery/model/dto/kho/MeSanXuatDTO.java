package com.bakery.model.dto.kho;

import java.time.LocalDateTime;

/**
 * DTO ánh xạ bảng MESANXUAT.
 * Mỗi record = 1 lần xuất kho để làm bánh (1 mẻ sản xuất).
 */
public class MeSanXuatDTO {

    private int maMe;
    private int maSP;
    private String tenSP;
    private double soLuongSanXuat;
    private LocalDateTime ngaySanXuat;
    private int maNV;
    private String tenNhanVien;
    private int maPX;

    public MeSanXuatDTO() {}

    public MeSanXuatDTO(int maMe, int maSP, String tenSP, double soLuongSanXuat,
                        LocalDateTime ngaySanXuat, int maNV, String tenNhanVien, int maPX) {
        this.maMe = maMe;
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.soLuongSanXuat = soLuongSanXuat;
        this.ngaySanXuat = ngaySanXuat;
        this.maNV = maNV;
        this.tenNhanVien = tenNhanVien;
        this.maPX = maPX;
    }

    public int getMaMe() { return maMe; }
    public void setMaMe(int maMe) { this.maMe = maMe; }

    public int getMaSP() { return maSP; }
    public void setMaSP(int maSP) { this.maSP = maSP; }

    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }

    public double getSoLuongSanXuat() { return soLuongSanXuat; }
    public void setSoLuongSanXuat(double soLuongSanXuat) { this.soLuongSanXuat = soLuongSanXuat; }

    public LocalDateTime getNgaySanXuat() { return ngaySanXuat; }
    public void setNgaySanXuat(LocalDateTime ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }

    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }

    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }

    public int getMaPX() { return maPX; }
    public void setMaPX(int maPX) { this.maPX = maPX; }
}
