package com.bakery.model.dto;

import java.math.BigDecimal;

public class CTDonHangDTO {
    private int maCTHD;
    private int maDon;
    private int maSP;
    private int soLuong;
    private BigDecimal donGia;
    private double phanTramGiam;
    private BigDecimal donGiaVon;

    public CTDonHangDTO() {}

    public CTDonHangDTO(int maCTHD, int maDon, int maSP, int soLuong, BigDecimal donGia, double phanTramGiam, BigDecimal donGiaVon) {
        this.maCTHD = maCTHD;
        this.maDon = maDon;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.phanTramGiam = phanTramGiam;
        this.donGiaVon = donGiaVon;
    }

    public int getMaCTHD() { return maCTHD; }
    public void setMaCTHD(int maCTHD) { this.maCTHD = maCTHD; }

    public int getMaDon() { return maDon; }
    public void setMaDon(int maDon) { this.maDon = maDon; }

    public int getMaSP() { return maSP; }
    public void setMaSP(int maSP) { this.maSP = maSP; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }

    public double getPhanTramGiam() { return phanTramGiam; }
    public void setPhanTramGiam(double phanTramGiam) { this.phanTramGiam = phanTramGiam; }

    public BigDecimal getDonGiaVon() { return donGiaVon; }
    public void setDonGiaVon(BigDecimal donGiaVon) { this.donGiaVon = donGiaVon; }
}
