package com.bakery.model.dto.kho;

import java.math.BigDecimal;

public class CTPhieuXuatTPDTO {
    private int maPX;
    private int maSP;
    private double soLuong;
    private BigDecimal donGiaVon;

    public CTPhieuXuatTPDTO() {}

    public CTPhieuXuatTPDTO(int maPX, int maSP, double soLuong, BigDecimal donGiaVon) {
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

    public BigDecimal getDonGiaVon() { return donGiaVon; }
    public void setDonGiaVon(BigDecimal donGiaVon) { this.donGiaVon = donGiaVon; }
}
