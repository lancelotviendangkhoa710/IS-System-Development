package com.bakery.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DonDatHangDTO {
    private int maDon;
    private LocalDateTime ngayLap;
    private LocalDateTime ngayGioNhanBanh;
    private Integer maKH;
    private int maNVLap;
    private int maTrangThai;
    private BigDecimal tongTienHDBan;
    private BigDecimal tienDaCoc;
    private Integer phienBan;
    private Integer hinhThucNhan;
    private String diaChiGiao;

    public DonDatHangDTO() {
    }

    public DonDatHangDTO(int maDon, LocalDateTime ngayLap, LocalDateTime ngayGioNhanBanh, Integer maKH, int maNVLap,
            int maTrangThai, BigDecimal tongTienHDBan, BigDecimal tienDaCoc, Integer phienBan, Integer hinhThucNhan,
            String diaChiGiao) {
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

    public int getMaDon() {
        return maDon;
    }

    public void setMaDon(int maDon) {
        this.maDon = maDon;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public LocalDateTime getNgayGioNhanBanh() {
        return ngayGioNhanBanh;
    }

    public void setNgayGioNhanBanh(LocalDateTime ngayGioNhanBanh) {
        this.ngayGioNhanBanh = ngayGioNhanBanh;
    }

    public Integer getMaKH() {
        return maKH;
    }

    public void setMaKH(Integer maKH) {
        this.maKH = maKH;
    }

    public int getMaNVLap() {
        return maNVLap;
    }

    public void setMaNVLap(int maNVLap) {
        this.maNVLap = maNVLap;
    }

    public int getMaTrangThai() {
        return maTrangThai;
    }

    public void setMaTrangThai(int maTrangThai) {
        this.maTrangThai = maTrangThai;
    }

    public BigDecimal getTongTienHDBan() {
        return tongTienHDBan;
    }

    public void setTongTienHDBan(BigDecimal tongTienHDBan) {
        this.tongTienHDBan = tongTienHDBan;
    }

    public BigDecimal getTienDaCoc() {
        return tienDaCoc;
    }

    public void setTienDaCoc(BigDecimal tienDaCoc) {
        this.tienDaCoc = tienDaCoc;
    }

    public Integer getPhienBan() {
        return phienBan;
    }

    public void setPhienBan(Integer phienBan) {
        this.phienBan = phienBan;
    }

    public Integer getHinhThucNhan() {
        return hinhThucNhan;
    }

    public void setHinhThucNhan(Integer hinhThucNhan) {
        this.hinhThucNhan = hinhThucNhan;
    }

    public String getDiaChiGiao() {
        return diaChiGiao;
    }

    public void setDiaChiGiao(String diaChiGiao) {
        this.diaChiGiao = diaChiGiao;
    }

    private String tenTrangThai;

    public String getTenTrangThai() {
        return tenTrangThai;
    }

    public void setTenTrangThai(String tenTrangThai) {
        this.tenTrangThai = tenTrangThai;
    }
}
