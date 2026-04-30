package com.bakery.model.dto.khachhang;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class KhachHangDTO {
    private int maKH;
    private String hoTen;
    private String sdt;
    private String diaChi;
    private LocalDate ngayDangKy;
    private Integer diemTichLuy;
    private Integer maHang;
    private String tenHang;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;
    private String tenNguoiXoa;

    public KhachHangDTO() {}

    public KhachHangDTO(int maKH, String hoTen, String sdt, String diaChi, LocalDate ngayDangKy, Integer diemTichLuy, Integer maHang, String tenHang, LocalDateTime thoiDiemXoa, Integer maNX, String tenNguoiXoa) {
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
        this.tenNguoiXoa = tenNguoiXoa;
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

    public Integer getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(Integer diemTichLuy) { this.diemTichLuy = diemTichLuy; }

    public Integer getMaHang() { return maHang; }
    public void setMaHang(Integer maHang) { this.maHang = maHang; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }

    public String getTenHang() { return tenHang; }
    public void setTenHang(String tenHang) { this.tenHang = tenHang; }

    public String getTenNguoiXoa() { return tenNguoiXoa; }
    public void setTenNguoiXoa(String tenNguoiXoa) { this.tenNguoiXoa = tenNguoiXoa; }
}
