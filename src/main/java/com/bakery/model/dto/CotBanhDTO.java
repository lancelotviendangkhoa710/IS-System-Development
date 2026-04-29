package com.bakery.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CotBanhDTO {
    private int maCot;
    private String tenCot;
    private BigDecimal phuPhi;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public CotBanhDTO() {}

    public CotBanhDTO(int maCot, String tenCot, BigDecimal phuPhi, LocalDateTime thoiDiemXoa, Integer maNX) {
        this.maCot = maCot;
        this.tenCot = tenCot;
        this.phuPhi = phuPhi;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaCot() { return maCot; }
    public void setMaCot(int maCot) { this.maCot = maCot; }

    public String getTenCot() { return tenCot; }
    public void setTenCot(String tenCot) { this.tenCot = tenCot; }

    public BigDecimal getPhuPhi() { return phuPhi; }
    public void setPhuPhi(BigDecimal phuPhi) { this.phuPhi = phuPhi; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
