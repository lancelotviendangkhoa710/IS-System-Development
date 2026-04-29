package com.bakery.model.dto;

import java.time.LocalDateTime;

public class NhaCungCapDTO {
    private int maNCC;
    private String tenNCC;
    private String sdt;
    private String diaChi;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public NhaCungCapDTO() {}

    public NhaCungCapDTO(int maNCC, String tenNCC, String sdt, String diaChi, LocalDateTime thoiDiemXoa, Integer maNX) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaNCC() { return maNCC; }
    public void setMaNCC(int maNCC) { this.maNCC = maNCC; }

    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }
    
    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
