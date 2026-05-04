package com.bakery.model.dto.nhansu;

import java.time.LocalDateTime;

public class VaiTroDTO {
    private int maVaiTro;
    private String tenVaiTro;
    private String moTa;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public VaiTroDTO() {}

    public VaiTroDTO(int maVaiTro, String tenVaiTro, String moTa, LocalDateTime thoiDiemXoa, Integer maNX) {
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

    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
