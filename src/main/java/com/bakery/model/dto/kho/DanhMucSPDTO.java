package com.bakery.model.dto.kho;

import java.time.LocalDateTime;

public class DanhMucSPDTO {
    private int maDM;
    private String tenDM;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public DanhMucSPDTO() {}

    public DanhMucSPDTO(int maDM, String tenDM, LocalDateTime thoiDiemXoa, Integer maNX) {
        this.maDM = maDM;
        this.tenDM = tenDM;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaDM() { return maDM; }
    public void setMaDM(int maDM) { this.maDM = maDM; }

    public String getTenDM() { return tenDM; }
    public void setTenDM(String tenDM) { this.tenDM = tenDM; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
