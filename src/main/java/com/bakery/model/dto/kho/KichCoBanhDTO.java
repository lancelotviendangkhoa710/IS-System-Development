package com.bakery.model.dto.kho;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class KichCoBanhDTO {
    private int maKC;
    private String tenKC;
    private BigDecimal phuPhi;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public KichCoBanhDTO() {}

    public KichCoBanhDTO(int maKC, String tenKC, BigDecimal phuPhi, LocalDateTime thoiDiemXoa, Integer maNX) {
        this.maKC = maKC;
        this.tenKC = tenKC;
        this.phuPhi = phuPhi;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaKC() { return maKC; }
    public void setMaKC(int maKC) { this.maKC = maKC; }

    public String getTenKC() { return tenKC; }
    public void setTenKC(String tenKC) { this.tenKC = tenKC; }

    public BigDecimal getPhuPhi() { return phuPhi; }
    public void setPhuPhi(BigDecimal phuPhi) { this.phuPhi = phuPhi; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
