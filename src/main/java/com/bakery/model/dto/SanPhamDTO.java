package com.bakery.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SanPhamDTO {
    private int maSP;
    private int maDM;
    private String tenSP;
    private BigDecimal giaCoBan;
    private String hinhAnh;
    private Integer choPhepTuyChinh;
    private Integer thoiGianBaoQuan;
    private Double soLuongTon;
    private Integer phienBan;
    private LocalDateTime thoiDiemXoa;
    private Integer thoiGianChuanBi;
    private Integer maNX;

    public SanPhamDTO() {}

    public SanPhamDTO(int maSP, int maDM, String tenSP, BigDecimal giaCoBan, String hinhAnh, Integer choPhepTuyChinh, Integer thoiGianBaoQuan, Double soLuongTon, Integer phienBan, LocalDateTime thoiDiemXoa, Integer thoiGianChuanBi, Integer maNX) {
        this.maSP = maSP;
        this.maDM = maDM;
        this.tenSP = tenSP;
        this.giaCoBan = giaCoBan;
        this.hinhAnh = hinhAnh;
        this.choPhepTuyChinh = choPhepTuyChinh;
        this.thoiGianBaoQuan = thoiGianBaoQuan;
        this.soLuongTon = soLuongTon;
        this.phienBan = phienBan;
        this.thoiDiemXoa = thoiDiemXoa;
        this.thoiGianChuanBi = thoiGianChuanBi;
        this.maNX = maNX;
    }

    public int getMaSP() { return maSP; }
    public void setMaSP(int maSP) { this.maSP = maSP; }

    public int getMaDM() { return maDM; }
    public void setMaDM(int maDM) { this.maDM = maDM; }

    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }

    public BigDecimal getGiaCoBan() { return giaCoBan; }
    public void setGiaCoBan(BigDecimal giaCoBan) { this.giaCoBan = giaCoBan; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public Integer getChoPhepTuyChinh() { return choPhepTuyChinh; }
    public void setChoPhepTuyChinh(Integer choPhepTuyChinh) { this.choPhepTuyChinh = choPhepTuyChinh; }

    public Integer getThoiGianBaoQuan() { return thoiGianBaoQuan; }
    public void setThoiGianBaoQuan(Integer thoiGianBaoQuan) { this.thoiGianBaoQuan = thoiGianBaoQuan; }

    public Double getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Double soLuongTon) { this.soLuongTon = soLuongTon; }

    public Integer getPhienBan() { return phienBan; }
    public void setPhienBan(Integer phienBan) { this.phienBan = phienBan; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public Integer getThoiGianChuanBi() { return thoiGianChuanBi; }
    public void setThoiGianChuanBi(Integer thoiGianChuanBi) { this.thoiGianChuanBi = thoiGianChuanBi; }

    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
