package com.bakery.model.dto.nhansu;


public class VaiTroChucNangDTO {
    private int maVaiTro;
    private int maChucNang;

    public VaiTroChucNangDTO() {}

    public VaiTroChucNangDTO(int maVaiTro, int maChucNang) {
        this.maVaiTro = maVaiTro;
        this.maChucNang = maChucNang;
    }

    public int getMaVaiTro() { return maVaiTro; }
    public void setMaVaiTro(int maVaiTro) { this.maVaiTro = maVaiTro; }

    public int getMaChucNang() { return maChucNang; }
    public void setMaChucNang(int maChucNang) { this.maChucNang = maChucNang; }
}
