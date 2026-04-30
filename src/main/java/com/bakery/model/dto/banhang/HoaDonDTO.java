package com.bakery.model.dto.banhang;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HoaDonDTO {
    private int maHD;
    private Integer maDon;
    private int maCa;
    private LocalDateTime ngayXuatHd;
    private Double thueVAT;
    private BigDecimal tongTienThanhToan;
    private int maPTTT;
    private String loaiHD;
    private String trangThai;

    public HoaDonDTO() {}

    public HoaDonDTO(int maHD, Integer maDon, int maCa, LocalDateTime ngayXuatHd, Double thueVAT, BigDecimal tongTienThanhToan, int maPTTT, String loaiHD) {
        this.maHD = maHD;
        this.maDon = maDon;
        this.maCa = maCa;
        this.ngayXuatHd = ngayXuatHd;
        this.thueVAT = thueVAT;
        this.tongTienThanhToan = tongTienThanhToan;
        this.maPTTT = maPTTT;
        this.loaiHD = loaiHD;
        this.trangThai = "active";
    }

    public int getMaHD() { return maHD; }
    public void setMaHD(int maHD) { this.maHD = maHD; }

    public Integer getMaDon() { return maDon; }
    public void setMaDon(Integer maDon) { this.maDon = maDon; }

    public int getMaCa() { return maCa; }
    public void setMaCa(int maCa) { this.maCa = maCa; }

    public LocalDateTime getNgayXuatHd() { return ngayXuatHd; }
    public void setNgayXuatHd(LocalDateTime ngayXuatHd) { this.ngayXuatHd = ngayXuatHd; }

    public Double getThueVAT() { return thueVAT; }
    public void setThueVAT(Double thueVAT) { this.thueVAT = thueVAT; }

    public BigDecimal getTongTienThanhToan() { return tongTienThanhToan; }
    public void setTongTienThanhToan(BigDecimal tongTienThanhToan) { this.tongTienThanhToan = tongTienThanhToan; }

    public int getMaPTTT() { return maPTTT; }
    public void setMaPTTT(int maPTTT) { this.maPTTT = maPTTT; }

    public String getLoaiHD() { return loaiHD; }
    public void setLoaiHD(String loaiHD) { this.loaiHD = loaiHD; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public boolean isCancelled() { return "cancelled".equals(trangThai); }
}
