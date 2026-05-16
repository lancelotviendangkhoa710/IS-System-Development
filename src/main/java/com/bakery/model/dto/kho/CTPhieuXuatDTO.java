package com.bakery.model.dto.kho;

import java.math.BigDecimal;

/**
 * DTO chi tiết phiếu xuất kho — gộp cả nguyên liệu (NL) và thành phẩm (TP).
 * Loại phân biệt qua field {@code loai}: "NL" hoặc "TP".
 */
public class CTPhieuXuatDTO {

    /** "NL" = nguyên liệu / "TP" = thành phẩm */
    private String loai;

    /** Tên nguyên liệu hoặc tên sản phẩm */
    private String tenHang;

    /** Đơn vị tính (kg, cái, …) */
    private String donViTinh;

    /** Số lượng xuất */
    private double soLuong;

    /** Đơn giá vốn (chỉ có ở TP, NL thường = null) */
    private BigDecimal donGiaVon;

    // ── getters / setters ──────────────────────────────────────────────────

    public String getLoai()                    { return loai; }
    public void   setLoai(String loai)         { this.loai = loai; }

    public String getTenHang()                  { return tenHang; }
    public void   setTenHang(String tenHang)    { this.tenHang = tenHang; }

    public String getDonViTinh()                { return donViTinh; }
    public void   setDonViTinh(String dv)       { this.donViTinh = dv; }

    public double getSoLuong()                  { return soLuong; }
    public void   setSoLuong(double soLuong)    { this.soLuong = soLuong; }

    public BigDecimal getDonGiaVon()            { return donGiaVon; }
    public void       setDonGiaVon(BigDecimal d){ this.donGiaVon = d; }
}
