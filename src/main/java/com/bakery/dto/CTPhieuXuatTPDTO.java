package com.bakery.dto;

public class CTPhieuXuatTPDTO {
    private int maPX;
    private int maSP;
    private double soLuong;
    private double donGiaVon;

    public CTPhieuXuatTPDTO() {}

    public CTPhieuXuatTPDTO(int maPX, int maSP, double soLuong, double donGiaVon) {
        this.maPX = maPX;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGiaVon = donGiaVon;
    }

    public int getMaPX() { return maPX; }
    public void setMaPX(int maPX) { this.maPX = maPX; }

    public int getMaSP() { return maSP; }
    public void setMaSP(int maSP) { this.maSP = maSP; }

    public double getSoLuong() { return soLuong; }
    public void setSoLuong(double soLuong) { this.soLuong = soLuong; }
    
    public double getDonGiaVon() { return donGiaVon; }
    public void setDonGiaVon(double donGiaVon) { this.donGiaVon = donGiaVon; }
}
