package com.bakery.model.dto;

import java.time.LocalDateTime;

public class CaLamViecDTO {
    private int maCa;
    private int maNV;
    private String maMayPOS;
    private LocalDateTime thoiGianMoCa;
    private LocalDateTime thoiGianDongCa;
    private String trangThai;

    public CaLamViecDTO() {}

    public CaLamViecDTO(int maCa, int maNV, String maMayPOS, LocalDateTime thoiGianMoCa, LocalDateTime thoiGianDongCa, String trangThai) {
        this.maCa = maCa;
        this.maNV = maNV;
        this.maMayPOS = maMayPOS;
        this.thoiGianMoCa = thoiGianMoCa;
        this.thoiGianDongCa = thoiGianDongCa;
        this.trangThai = trangThai;
    }

    public int getMaCa() { return maCa; }
    public void setMaCa(int maCa) { this.maCa = maCa; }

    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }

    public String getMaMayPOS() { return maMayPOS; }
    public void setMaMayPOS(String maMayPOS) { this.maMayPOS = maMayPOS; }

    public LocalDateTime getThoiGianMoCa() { return thoiGianMoCa; }
    public void setThoiGianMoCa(LocalDateTime thoiGianMoCa) { this.thoiGianMoCa = thoiGianMoCa; }

    public LocalDateTime getThoiGianDongCa() { return thoiGianDongCa; }
    public void setThoiGianDongCa(LocalDateTime thoiGianDongCa) { this.thoiGianDongCa = thoiGianDongCa; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
