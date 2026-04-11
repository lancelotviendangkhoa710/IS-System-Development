package com.bakery.dto;

import java.time.LocalDateTime;

public class PhuongThucTTDTO {
    private int maPTTT;
    private String tenPTTT;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public PhuongThucTTDTO() {}

    public int getMaPTTT() { return maPTTT; }
    public void setMaPTTT(int maPTTT) { this.maPTTT = maPTTT; }

    public String getTenPTTT() { return tenPTTT; }
    public void setTenPTTT(String tenPTTT) { this.tenPTTT = tenPTTT; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}