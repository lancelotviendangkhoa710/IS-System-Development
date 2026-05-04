package com.bakery.model.dto.nhansu;

import com.bakery.model.enums.SystemModule;

public class ChucNangDTO {
    private int maChucNang;
    private String tenChucNang;
    private String moTa;
    private SystemModule module;

    public ChucNangDTO() {}

    public ChucNangDTO(int maChucNang, String tenChucNang, String moTa, SystemModule module) {
        this.maChucNang = maChucNang;
        this.tenChucNang = tenChucNang;
        this.moTa = moTa;
        this.module = module;
    }

    public int getMaChucNang() { return maChucNang; }
    public void setMaChucNang(int maChucNang) { this.maChucNang = maChucNang; }

    public String getTenChucNang() { return tenChucNang; }
    public void setTenChucNang(String tenChucNang) { this.tenChucNang = tenChucNang; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public SystemModule getModule() { return module; }
    public void setModule(SystemModule module) { this.module = module; }
}
