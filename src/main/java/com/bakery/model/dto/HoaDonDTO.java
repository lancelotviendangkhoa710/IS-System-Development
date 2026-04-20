package com.bakery.model.dto;

import java.time.LocalDateTime;

public class HoaDonDTO {
    private int maHD;
    private Integer maDon;
    private int maCa;
    private LocalDateTime ngayXuatHd;
    private double thueVAT;
    private double tongTienThanhToan;
    private int maPTTT;
    private String loaiHD;

    public HoaDonDTO() {}

    public int getMaHD() { return maHD; }
    public void setMaHD(int maHD) { this.maHD = maHD; }

    public Integer getMaDon() { return maDon; }
    public void setMaDon(Integer maDon) { this.maDon = maDon; }

    public int getMaCa() { return maCa; }
    public void setMaCa(int maCa) { this.maCa = maCa; }

    public LocalDateTime getNgayXuatHd() { return ngayXuatHd; }
    public void setNgayXuatHd(LocalDateTime ngayXuatHd) { this.ngayXuatHd = ngayXuatHd; }

    public double getThueVAT() { return thueVAT; }
    public void setThueVAT(double thueVAT) { this.thueVAT = thueVAT; }

    public double getTongTienThanhToan() { return tongTienThanhToan; }
    public void setTongTienThanhToan(double tongTienThanhToan) { this.tongTienThanhToan = tongTienThanhToan; }

    public int getMaPTTT() { return maPTTT; }
    public void setMaPTTT(int maPTTT) { this.maPTTT = maPTTT; }

    public String getLoaiHD() { return loaiHD; }
    public void setLoaiHD(String loaiHD) { this.loaiHD = loaiHD; }
}