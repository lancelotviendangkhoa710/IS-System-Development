package com.bakery.model.dto.kho;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NhanBanhDTO {
    private int maNhan;
    private String tenNhan;
    private BigDecimal phuPhi;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public NhanBanhDTO() {}

    public NhanBanhDTO(int maNhan, String tenNhan, BigDecimal phuPhi, LocalDateTime thoiDiemXoa, Integer maNX) {
        this.maNhan = maNhan;
        this.tenNhan = tenNhan;
        this.phuPhi = phuPhi;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaNhan() { return maNhan; }
    public void setMaNhan(int maNhan) { this.maNhan = maNhan; }

    public String getTenNhan() { return tenNhan; }
    public void setTenNhan(String tenNhan) { this.tenNhan = tenNhan; }

    public BigDecimal getPhuPhi() { return phuPhi; }
    public void setPhuPhi(BigDecimal phuPhi) { this.phuPhi = phuPhi; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
