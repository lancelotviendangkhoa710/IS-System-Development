package com.bakery.model.dto.kho;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NguyenLieuDTO {
    private int maNL;
    private String tenNL;
    private String xuatXu;
    private int maDVT;
    private BigDecimal giaVonTrungBinh;
    private Double mucTonAnToan;
    private Double soLuongTonTong;
    private Integer datChuanVSATTP;
    private Integer phienBan;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public NguyenLieuDTO() {}

    public NguyenLieuDTO(int maNL, String tenNL, String xuatXu, int maDVT, BigDecimal giaVonTrungBinh, Double mucTonAnToan, Double soLuongTonTong, Integer datChuanVSATTP, Integer phienBan, LocalDateTime thoiDiemXoa, Integer maNX) {
        this.maNL = maNL;
        this.tenNL = tenNL;
        this.xuatXu = xuatXu;
        this.maDVT = maDVT;
        this.giaVonTrungBinh = giaVonTrungBinh;
        this.mucTonAnToan = mucTonAnToan;
        this.soLuongTonTong = soLuongTonTong;
        this.datChuanVSATTP = datChuanVSATTP;
        this.phienBan = phienBan;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaNL() { return maNL; }
    public void setMaNL(int maNL) { this.maNL = maNL; }

    public String getTenNL() { return tenNL; }
    public void setTenNL(String tenNL) { this.tenNL = tenNL; }

    public String getXuatXu() { return xuatXu; }
    public void setXuatXu(String xuatXu) { this.xuatXu = xuatXu; }

    public int getMaDVT() { return maDVT; }
    public void setMaDVT(int maDVT) { this.maDVT = maDVT; }

    public BigDecimal getGiaVonTrungBinh() { return giaVonTrungBinh; }
    public void setGiaVonTrungBinh(BigDecimal giaVonTrungBinh) { this.giaVonTrungBinh = giaVonTrungBinh; }

    public Double getMucTonAnToan() { return mucTonAnToan; }
    public void setMucTonAnToan(Double mucTonAnToan) { this.mucTonAnToan = mucTonAnToan; }

    public Double getSoLuongTonTong() { return soLuongTonTong; }
    public void setSoLuongTonTong(Double soLuongTonTong) { this.soLuongTonTong = soLuongTonTong; }

    public Integer getDatChuanVSATTP() { return datChuanVSATTP; }
    public void setDatChuanVSATTP(Integer datChuanVSATTP) { this.datChuanVSATTP = datChuanVSATTP; }

    public Integer getPhienBan() { return phienBan; }
    public void setPhienBan(Integer phienBan) { this.phienBan = phienBan; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
