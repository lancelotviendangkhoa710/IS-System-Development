package com.bakery.model.dto;

import java.math.BigDecimal;

public class DoiSoatDTO {
    private int maCa;
    private BigDecimal tienKhaiBaoDauCa;
    private BigDecimal tongTienHeThong;
    private BigDecimal tienThucTeDem;
    private BigDecimal chenhLech;
    private String lyDoChenhLech;

    public DoiSoatDTO() {}

    public DoiSoatDTO(int maCa, BigDecimal tienKhaiBaoDauCa, BigDecimal tongTienHeThong, BigDecimal tienThucTeDem, BigDecimal chenhLech, String lyDoChenhLech) {
        this.maCa = maCa;
        this.tienKhaiBaoDauCa = tienKhaiBaoDauCa;
        this.tongTienHeThong = tongTienHeThong;
        this.tienThucTeDem = tienThucTeDem;
        this.chenhLech = chenhLech;
        this.lyDoChenhLech = lyDoChenhLech;
    }

    public int getMaCa() { return maCa; }
    public void setMaCa(int maCa) { this.maCa = maCa; }

    public BigDecimal getTienKhaiBaoDauCa() { return tienKhaiBaoDauCa; }
    public void setTienKhaiBaoDauCa(BigDecimal tienKhaiBaoDauCa) { this.tienKhaiBaoDauCa = tienKhaiBaoDauCa; }

    public BigDecimal getTongTienHeThong() { return tongTienHeThong; }
    public void setTongTienHeThong(BigDecimal tongTienHeThong) { this.tongTienHeThong = tongTienHeThong; }

    public BigDecimal getTienThucTeDem() { return tienThucTeDem; }
    public void setTienThucTeDem(BigDecimal tienThucTeDem) { this.tienThucTeDem = tienThucTeDem; }

    public BigDecimal getChenhLech() { return chenhLech; }
    public void setChenhLech(BigDecimal chenhLech) { this.chenhLech = chenhLech; }

    public String getLyDoChenhLech() { return lyDoChenhLech; }
    public void setLyDoChenhLech(String lyDoChenhLech) { this.lyDoChenhLech = lyDoChenhLech; }
}
