package com.bakery.model.dto.banhang;

public class YeuCauChiTietDonHangDTO {
    private int maSP;
    private int soLuong;
    private double donGia;
    private boolean custom;
    private String ghiChu;
    private String phuKien;

    public YeuCauChiTietDonHangDTO() {
    }

    public YeuCauChiTietDonHangDTO(int maSP, int soLuong, double donGia, boolean custom, String ghiChu,
            String phuKien) {
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.custom = custom;
        this.ghiChu = ghiChu;
        this.phuKien = phuKien;
    }

    public int getMaSP() {
        return maSP;
    }

    public void setMaSP(int maSP) {
        this.maSP = maSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getPhuKien() {
        return phuKien;
    }

    public void setPhuKien(String phuKien) {
        this.phuKien = phuKien;
    }
}
