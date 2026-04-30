package com.bakery.model.dto.kho;

public class CongThucDTO {
    private int maSP;
    private int maNL;
    private double soLuongTieuHao;

    public CongThucDTO() {}

    public CongThucDTO(int maSP, int maNL, double soLuongTieuHao) {
        this.maSP = maSP;
        this.maNL = maNL;
        this.soLuongTieuHao = soLuongTieuHao;
    }

    public int getMaSP() { return maSP; }
    public void setMaSP(int maSP) { this.maSP = maSP; }
    
    public int getMaNL() { return maNL; }
    public void setMaNL(int maNL) { this.maNL = maNL; }

    public double getSoLuongTieuHao() { return soLuongTieuHao; }
    public void setSoLuongTieuHao(double soLuongTieuHao) { this.soLuongTieuHao = soLuongTieuHao; }
}
