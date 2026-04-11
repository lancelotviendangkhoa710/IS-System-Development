package com.bakery.dto;

import java.time.LocalDateTime;

public class DonViTinhDTO {
    private int maDVT;
    private String tenDVT;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public DonViTinhDTO() {}

    public DonViTinhDTO(int maDVT, String tenDVT, LocalDateTime thoiDiemXoa, Integer maNX) {
        this.maDVT = maDVT;
        this.tenDVT = tenDVT;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaDVT() { return maDVT; }
    public void setMaDVT(int maDVT) { this.maDVT = maDVT; }

    public String getTenDVT() { return tenDVT; }
    public void setTenDVT(String tenDVT) { this.tenDVT = tenDVT; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }
    
    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
