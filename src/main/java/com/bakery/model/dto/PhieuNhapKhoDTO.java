package com.bakery.model.dto;

import java.time.LocalDateTime;

public class PhieuNhapKhoDTO {
    private int maPN;
    private LocalDateTime ngayNhap;
    private int maNV;
    private int maNCC;
    private double tongTienNhap;

    public PhieuNhapKhoDTO() {}

    public PhieuNhapKhoDTO(int maPN, LocalDateTime ngayNhap, int maNV, int maNCC, double tongTienNhap) {
        this.maPN = maPN;
        this.ngayNhap = ngayNhap;
        this.maNV = maNV;
        this.maNCC = maNCC;
        this.tongTienNhap = tongTienNhap;
    }

    public int getMaPN() { return maPN; }
    public void setMaPN(int maPN) { this.maPN = maPN; }

    public LocalDateTime getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(LocalDateTime ngayNhap) { this.ngayNhap = ngayNhap; }

    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }

    public int getMaNCC() { return maNCC; }
    public void setMaNCC(int maNCC) { this.maNCC = maNCC; }
    
    public double getTongTienNhap() { return tongTienNhap; }
    public void setTongTienNhap(double tongTienNhap) { this.tongTienNhap = tongTienNhap; }
}
