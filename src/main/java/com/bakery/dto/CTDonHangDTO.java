package com.bakery.dto;

public class CTDonHangDTO {
    private int maCTHD;
    private int maDon;
    private int maSP;
    private int soLuong;
    private double donGia;
    private double phanTramGiam;
    private double donGiaVon;

    public CTDonHangDTO() {}

    public CTDonHangDTO(int maCTHD, int maDon, int maSP, int soLuong, double donGia, double phanTramGiam, double donGiaVon) {
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

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public double getPhanTramGiam() { return phanTramGiam; }
    public void setPhanTramGiam(double phanTramGiam) { this.phanTramGiam = phanTramGiam; }

    public double getDonGiaVon() { return donGiaVon; }
    public void setDonGiaVon(double donGiaVon) { this.donGiaVon = donGiaVon; }
}