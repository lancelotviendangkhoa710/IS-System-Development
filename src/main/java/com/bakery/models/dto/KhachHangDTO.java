package com.bakery.models.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class KhachHangDTO {
    private int maKH;
    private String hoTen;
    private String sdt;
    private String diaChi;
    private LocalDate ngayDangKy;
    private int diemTichLuy;
    private int maHang;
    private String tenHang;
    private LocalDateTime thoiDiemXoa;
    private int maNX;
    private String tenNguoiXoa;

    public KhachHangDTO() {}

    public KhachHangDTO(int maKH, String hoTen, String sdt, String diaChi, LocalDate ngayDangKy, int diemTichLuy, int maHang, String tenHang, LocalDateTime thoiDiemXoa, int maNX) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.ngayDangKy = ngayDangKy;
        this.diemTichLuy = diemTichLuy;
        this.maHang = maHang;
        this.tenHang = tenHang;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaKH() { return maKH; }
    public void setMaKH(int maKH) { this.maKH = maKH; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public LocalDate getNgayDangKy() { return ngayDangKy; }
    public void setNgayDangKy(LocalDate ngayDangKy) { this.ngayDangKy = ngayDangKy; }

    public int getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(int diemTichLuy) { this.diemTichLuy = diemTichLuy; }

    public int getMaHang() { return maHang; }
    public void setMaHang(int maHang) { this.maHang = maHang; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public int getMaNX() { return maNX; }
    public void setMaNX(int maNX) { this.maNX = maNX; }

    public String getTenHang() { return tenHang; }
    public void setTenHang(String tenHang) { this.tenHang = tenHang; }

    public String getTenNguoiXoa() { return tenNguoiXoa; }
    public void setTenNguoiXoa(String tenNguoiXoa) { this.tenNguoiXoa = tenNguoiXoa; }
}