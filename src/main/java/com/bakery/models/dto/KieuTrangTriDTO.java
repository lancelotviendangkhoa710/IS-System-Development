package com.bakery.models.dto;

import java.time.LocalDateTime;

public class KieuTrangTriDTO {
    private int maTrangTri;
    private String tenTrangTri;
    private double phuPhi;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public KieuTrangTriDTO() {}

    public KieuTrangTriDTO(int maTrangTri, String tenTrangTri, double phuPhi, LocalDateTime thoiDiemXoa, Integer maNX) {
        this.maTrangTri = maTrangTri;
        this.tenTrangTri = tenTrangTri;
        this.phuPhi = phuPhi;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaTrangTri() { return maTrangTri; }
    public void setMaTrangTri(int maTrangTri) { this.maTrangTri = maTrangTri; }

    public String getTenTrangTri() { return tenTrangTri; }
    public void setTenTrangTri(String tenTrangTri) { this.tenTrangTri = tenTrangTri; }

    public double getPhuPhi() { return phuPhi; }
    public void setPhuPhi(double phuPhi) { this.phuPhi = phuPhi; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }
    
    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
