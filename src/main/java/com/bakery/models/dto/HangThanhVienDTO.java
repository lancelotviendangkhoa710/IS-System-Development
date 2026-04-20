package com.bakery.models.dto;
import java.time.LocalDateTime;
public class HangThanhVienDTO {
    private int maHang;
    private String tenHang;
    private int diemToiThieu;
    private double phanTramGiamGia;
    private LocalDateTime thoiDiemXoa;
    private int maNX;

    public HangThanhVienDTO() {}

    public HangThanhVienDTO(int maHang, String tenHang, int diemToiThieu, double phanTramGiamGia, LocalDateTime thoiDiemXoa, int maNX) {
        this.maHang = maHang;
        this.tenHang = tenHang;
        this.diemToiThieu = diemToiThieu;
        this.phanTramGiamGia = phanTramGiamGia;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaHang() { return maHang; }
    public void setMaHang(int maHang) { this.maHang = maHang; }

    public String getTenHang() { return tenHang; }
    public void setTenHang(String tenHang) { this.tenHang = tenHang; }

    public int getDiemToiThieu() { return diemToiThieu; }
    public void setDiemToiThieu(int diemToiThieu) { this.diemToiThieu = diemToiThieu; }

    public double getPhanTramGiamGia() { return phanTramGiamGia; }
    public void setPhanTramGiamGia(double phanTramGiamGia) { this.phanTramGiamGia = phanTramGiamGia; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public int getMaNX() { return maNX; }
    public void setMaNX(int maNX) { this.maNX = maNX; }
}