package com.bakery.model.dto.kho;

public class CTPhieuXuatNLDTO {
    private int maPX;
    private int maLo;
    private double soLuong;

    public CTPhieuXuatNLDTO() {}

    public CTPhieuXuatNLDTO(int maPX, int maLo, double soLuong) {
        this.maPX = maPX;
        this.maLo = maLo;
        this.soLuong = soLuong;
    }

    public int getMaPX() { return maPX; }
    public void setMaPX(int maPX) { this.maPX = maPX; }
    
    public int getMaLo() { return maLo; }
    public void setMaLo(int maLo) { this.maLo = maLo; }
    
    public double getSoLuong() { return soLuong; }
    public void setSoLuong(double soLuong) { this.soLuong = soLuong; }
}
