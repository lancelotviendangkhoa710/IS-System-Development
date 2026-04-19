package com.bakery.models.dto;

import java.time.LocalDateTime;

public class VaiTroDTO {
    private int maVaiTro;
    private String tenVaiTro;
    private String moTa;
    private LocalDateTime thoiDiemXoa;
    private int maNX;

    public VaiTroDTO() {}

    public VaiTroDTO(int maVaiTro, String tenVaiTro, String moTa, LocalDateTime thoiDiemXoa, int maNX) {
        this.maVaiTro = maVaiTro;
        this.tenVaiTro = tenVaiTro;
        this.moTa = moTa;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaVaiTro() { return maVaiTro; }
    public void setMaVaiTro(int maVaiTro) { this.maVaiTro = maVaiTro; }

    public String getTenVaiTro() { return tenVaiTro; }
    public void setTenVaiTro(String tenVaiTro) { this.tenVaiTro = tenVaiTro; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public int getMaNX() { return maNX; }
    public void setMaNX(int maNX) { this.maNX = maNX; }
}
