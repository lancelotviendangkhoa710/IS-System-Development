package com.bakery.model.dto.kho;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CTPhieuNhapDTO {
    private int maLo;
    private int maPN;
    private int maNL;
    private double soLuong;
    private BigDecimal donGia;
    private double soLuongConLai;
    private LocalDate ngaySanXuat;
    private LocalDate hanSuDung;
    private String maVachLo;

    public CTPhieuNhapDTO() {}

    public CTPhieuNhapDTO(int maLo, int maPN, int maNL, double soLuong, BigDecimal donGia, double soLuongConLai, LocalDate ngaySanXuat, LocalDate hanSuDung, String maVachLo) {
        this.maLo = maLo;
        this.maPN = maPN;
        this.maNL = maNL;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.soLuongConLai = soLuongConLai;
        this.ngaySanXuat = ngaySanXuat;
        this.hanSuDung = hanSuDung;
        this.maVachLo = maVachLo;
    }

    public int getMaLo() { return maLo; }
    public void setMaLo(int maLo) { this.maLo = maLo; }

    public int getMaPN() { return maPN; }
    public void setMaPN(int maPN) { this.maPN = maPN; }

    public int getMaNL() { return maNL; }
    public void setMaNL(int maNL) { this.maNL = maNL; }

    public double getSoLuong() { return soLuong; }
    public void setSoLuong(double soLuong) { this.soLuong = soLuong; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }

    public double getSoLuongConLai() { return soLuongConLai; }
    public void setSoLuongConLai(double soLuongConLai) { this.soLuongConLai = soLuongConLai; }

    public LocalDate getNgaySanXuat() { return ngaySanXuat; }
    public void setNgaySanXuat(LocalDate ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }

    public LocalDate getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(LocalDate hanSuDung) { this.hanSuDung = hanSuDung; }

    public String getMaVachLo() { return maVachLo; }
    public void setMaVachLo(String maVachLo) { this.maVachLo = maVachLo; }
}
