package com.bakery.model.dto;

public class ChucNangDTO {
    private int maChucNang;
    private String tenChucNang;
    private String moTa;

    public ChucNangDTO() {}

    public ChucNangDTO(int maChucNang, String tenChucNang, String moTa) {
        this.maChucNang = maChucNang;
        this.tenChucNang = tenChucNang;
        this.moTa = moTa;
    }

    public int getMaChucNang() { return maChucNang; }
    public void setMaChucNang(int maChucNang) { this.maChucNang = maChucNang; }

    public String getTenChucNang() { return tenChucNang; }
    public void setTenChucNang(String tenChucNang) { this.tenChucNang = tenChucNang; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}