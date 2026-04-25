package com.bakery.model.dto;

import java.time.LocalDateTime;

public class LichSuDonHangDTO {
    private int maLog;
    private int maDon;
    private Integer maTrangThaiCu;
    private int maTrangThaiMoi;
    private LocalDateTime thoiGianThayDoi;
    private int maNVCapNhat;
    private String ghiChu;

    public LichSuDonHangDTO() {}

    public LichSuDonHangDTO(int maLog, int maDon, Integer maTrangThaiCu, int maTrangThaiMoi, LocalDateTime thoiGianThayDoi, int maNVCapNhat, String ghiChu) {
        this.maLog = maLog;
        this.maDon = maDon;
        this.maTrangThaiCu = maTrangThaiCu;
        this.maTrangThaiMoi = maTrangThaiMoi;
        this.thoiGianThayDoi = thoiGianThayDoi;
        this.maNVCapNhat = maNVCapNhat;
        this.ghiChu = ghiChu;
    }

    public int getMaLog() { return maLog; }
    public void setMaLog(int maLog) { this.maLog = maLog; }

    public int getMaDon() { return maDon; }
    public void setMaDon(int maDon) { this.maDon = maDon; }

    public Integer getMaTrangThaiCu() { return maTrangThaiCu; }
    public void setMaTrangThaiCu(Integer maTrangThaiCu) { this.maTrangThaiCu = maTrangThaiCu; }

    public int getMaTrangThaiMoi() { return maTrangThaiMoi; }
    public void setMaTrangThaiMoi(int maTrangThaiMoi) { this.maTrangThaiMoi = maTrangThaiMoi; }

    public LocalDateTime getThoiGianThayDoi() { return thoiGianThayDoi; }
    public void setThoiGianThayDoi(LocalDateTime thoiGianThayDoi) { this.thoiGianThayDoi = thoiGianThayDoi; }

    public int getMaNVCapNhat() { return maNVCapNhat; }
    public void setMaNVCapNhat(int maNVCapNhat) { this.maNVCapNhat = maNVCapNhat; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}