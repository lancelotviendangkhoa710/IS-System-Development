package com.bakery.model.dto.kho;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PhieuNhapKhoDTO {
    private int maPN;
    private LocalDateTime ngayNhap;
    private int maNV;
    private int maNCC;
    private BigDecimal tongTienNhap;

    public PhieuNhapKhoDTO() {}

    public PhieuNhapKhoDTO(int maPN, LocalDateTime ngayNhap, int maNV, int maNCC, BigDecimal tongTienNhap) {
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

    public BigDecimal getTongTienNhap() { return tongTienNhap; }
    public void setTongTienNhap(BigDecimal tongTienNhap) { this.tongTienNhap = tongTienNhap; }
}
