package com.bakery.model.dto.hethong;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho màn hình "Giám sát tiền mặt đóng ca" — chỉ đọc.
 */
public class GiamSatCaDTO {
    private int    maCa;
    private String hoTenNV;
    private String maMayPOS;
    private LocalDateTime thoiGianMoCa;
    private LocalDateTime thoiGianDongCa;
    private String trangThai;
    private BigDecimal tienKhaiBaoDauCa;
    private BigDecimal tongTienHeThong;
    private BigDecimal tienThucTeDem;
    private BigDecimal chenhLech;
    private String lyDoChenhLech;

    public GiamSatCaDTO() {}

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public int getMaCa()                         { return maCa; }
    public void setMaCa(int maCa)                { this.maCa = maCa; }

    public String getHoTenNV()                   { return hoTenNV; }
    public void setHoTenNV(String hoTenNV)       { this.hoTenNV = hoTenNV; }

    public String getMaMayPOS()                  { return maMayPOS; }
    public void setMaMayPOS(String maMayPOS)     { this.maMayPOS = maMayPOS; }

    public LocalDateTime getThoiGianMoCa()                     { return thoiGianMoCa; }
    public void setThoiGianMoCa(LocalDateTime v)               { this.thoiGianMoCa = v; }

    public LocalDateTime getThoiGianDongCa()                   { return thoiGianDongCa; }
    public void setThoiGianDongCa(LocalDateTime v)             { this.thoiGianDongCa = v; }

    public String getTrangThai()                 { return trangThai; }
    public void setTrangThai(String trangThai)   { this.trangThai = trangThai; }

    public BigDecimal getTienKhaiBaoDauCa()                        { return tienKhaiBaoDauCa; }
    public void setTienKhaiBaoDauCa(BigDecimal v)                  { this.tienKhaiBaoDauCa = v; }

    public BigDecimal getTongTienHeThong()                         { return tongTienHeThong; }
    public void setTongTienHeThong(BigDecimal v)                   { this.tongTienHeThong = v; }

    public BigDecimal getTienThucTeDem()                           { return tienThucTeDem; }
    public void setTienThucTeDem(BigDecimal v)                     { this.tienThucTeDem = v; }

    public BigDecimal getChenhLech()                               { return chenhLech; }
    public void setChenhLech(BigDecimal v)                         { this.chenhLech = v; }

    public String getLyDoChenhLech()                               { return lyDoChenhLech; }
    public void setLyDoChenhLech(String lyDoChenhLech)             { this.lyDoChenhLech = lyDoChenhLech; }

    /** Tiện ích: true nếu chênh lệch âm (thiếu tiền) */
    public boolean isAmTien() {
        return chenhLech != null && chenhLech.compareTo(BigDecimal.ZERO) < 0;
    }
}
