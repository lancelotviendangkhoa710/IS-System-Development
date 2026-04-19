package com.bakery.models.dto;

import java.time.LocalDateTime;

public class DonDatHangDTO {
    private int maDon;
    private LocalDateTime ngayLap;
    private LocalDateTime ngayGioNhanBanh;
    private Integer maKH;
    private int maNVLap;
    private int maTrangThai;
    private double tongTienHDBan;
    private double tienDaCoc;
    private int phienBan;
    private Integer hinhThucNhan;
    private String diaChiGiao;

    public DonDatHangDTO() {}

    public DonDatHangDTO(int maDon, LocalDateTime ngayLap, LocalDateTime ngayGioNhanBanh, Integer maKH, int maNVLap, int maTrangThai, double tongTienHDBan, double tienDaCoc, int phienBan, Integer hinhThucNhan, String diaChiGiao) {
        this.maDon = maDon;
        this.ngayLap = ngayLap;
        this.ngayGioNhanBanh = ngayGioNhanBanh;
        this.maKH = maKH;
        this.maNVLap = maNVLap;
        this.maTrangThai = maTrangThai;
        this.tongTienHDBan = tongTienHDBan;
        this.tienDaCoc = tienDaCoc;
        this.phienBan = phienBan;
        this.hinhThucNhan = hinhThucNhan;
        this.diaChiGiao = diaChiGiao;
    }

    public int getMaDon() { return maDon; }
    public void setMaDon(int maDon) { this.maDon = maDon; }

    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }

    public LocalDateTime getNgayGioNhanBanh() { return ngayGioNhanBanh; }
    public void setNgayGioNhanBanh(LocalDateTime ngayGioNhanBanh) { this.ngayGioNhanBanh = ngayGioNhanBanh; }

    public Integer getMaKH() { return maKH; }
    public void setMaKH(Integer maKH) { this.maKH = maKH; }

    public int getMaNVLap() { return maNVLap; }
    public void setMaNVLap(int maNVLap) { this.maNVLap = maNVLap; }

    public int getMaTrangThai() { return maTrangThai; }
    public void setMaTrangThai(int maTrangThai) { this.maTrangThai = maTrangThai; }

    public double getTongTienHDBan() { return tongTienHDBan; }
    public void setTongTienHDBan(double tongTienHDBan) { this.tongTienHDBan = tongTienHDBan; }

    public double getTienDaCoc() { return tienDaCoc; }
    public void setTienDaCoc(double tienDaCoc) { this.tienDaCoc = tienDaCoc; }

    public int getPhienBan() { return phienBan; }
    public void setPhienBan(int phienBan) { this.phienBan = phienBan; }

    public Integer getHinhThucNhan() { return hinhThucNhan; }
    public void setHinhThucNhan(Integer hinhThucNhan) { this.hinhThucNhan = hinhThucNhan; }

    public String getDiaChiGiao() { return diaChiGiao; }
    public void setDiaChiGiao(String diaChiGiao) { this.diaChiGiao = diaChiGiao; }
}