package com.bakery.models.dto;

import java.time.LocalDateTime;

public class NguyenLieuDTO {
    private int maNL;
    private String tenNL;
    private String xuatXu;
    private int maDVT;
    private double giaVonTrungBinh;
    private double mucTonAnToan;
    private double soLuongTonTong;
    private int datChuanVSATTP;
    private int phienBan;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public NguyenLieuDTO() {}

    public NguyenLieuDTO(int maNL, String tenNL, String xuatXu, int maDVT, double giaVonTrungBinh, double mucTonAnToan, double soLuongTonTong, int datChuanVSATTP, int phienBan, LocalDateTime thoiDiemXoa, Integer maNX) {
        this.maNL = maNL;
        this.tenNL = tenNL;
        this.xuatXu = xuatXu;
        this.maDVT = maDVT;
        this.giaVonTrungBinh = giaVonTrungBinh;
        this.mucTonAnToan = mucTonAnToan;
        this.soLuongTonTong = soLuongTonTong;
        this.datChuanVSATTP = datChuanVSATTP;
        this.phienBan = phienBan;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaNL() { return maNL; }
    public void setMaNL(int maNL) { this.maNL = maNL; }

    public String getTenNL() { return tenNL; }
    public void setTenNL(String tenNL) { this.tenNL = tenNL; }

    public String getXuatXu() { return xuatXu; }
    public void setXuatXu(String xuatXu) { this.xuatXu = xuatXu; }

    public int getMaDVT() { return maDVT; }
    public void setMaDVT(int maDVT) { this.maDVT = maDVT; }

    public double getGiaVonTrungBinh() { return giaVonTrungBinh; }
    public void setGiaVonTrungBinh(double giaVonTrungBinh) { this.giaVonTrungBinh = giaVonTrungBinh; }

    public double getMucTonAnToan() { return mucTonAnToan; }
    public void setMucTonAnToan(double mucTonAnToan) { this.mucTonAnToan = mucTonAnToan; }

    public double getSoLuongTonTong() { return soLuongTonTong; }
    public void setSoLuongTonTong(double soLuongTonTong) { this.soLuongTonTong = soLuongTonTong; }

    public int getDatChuanVSATTP() { return datChuanVSATTP; }
    public void setDatChuanVSATTP(int datChuanVSATTP) { this.datChuanVSATTP = datChuanVSATTP; }

    public int getPhienBan() { return phienBan; }
    public void setPhienBan(int phienBan) { this.phienBan = phienBan; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }
    
    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
