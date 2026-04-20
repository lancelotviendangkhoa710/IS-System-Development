package com.bakery.models.dto;

import java.time.LocalDateTime;

public class PhieuXuatKhoDTO {
    private int maPX;
    private LocalDateTime ngayXuat;
    private String lyDoXuat;
    private int maNV;

    public PhieuXuatKhoDTO() {}

    public PhieuXuatKhoDTO(int maPX, LocalDateTime ngayXuat, String lyDoXuat, int maNV) {
        this.maPX = maPX;
        this.ngayXuat = ngayXuat;
        this.lyDoXuat = lyDoXuat;
        this.maNV = maNV;
    }

    public int getMaPX() { return maPX; }
    public void setMaPX(int maPX) { this.maPX = maPX; }

    public LocalDateTime getNgayXuat() { return ngayXuat; }
    public void setNgayXuat(LocalDateTime ngayXuat) { this.ngayXuat = ngayXuat; }

    public String getLyDoXuat() { return lyDoXuat; }
    public void setLyDoXuat(String lyDoXuat) { this.lyDoXuat = lyDoXuat; }
    
    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }
}
