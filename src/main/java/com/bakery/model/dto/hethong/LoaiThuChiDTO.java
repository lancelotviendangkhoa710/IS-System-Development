package com.bakery.model.dto.hethong;

import java.time.LocalDateTime;

public class LoaiThuChiDTO {
    private int maLoaiThuChi;
    private String tenLoaiThuChi;
    private String phanLoai;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public LoaiThuChiDTO() {}

    public LoaiThuChiDTO(int maLoaiThuChi, String tenLoaiThuChi, String phanLoai, LocalDateTime thoiDiemXoa, Integer maNX) {
        this.maLoaiThuChi = maLoaiThuChi;
        this.tenLoaiThuChi = tenLoaiThuChi;
        this.phanLoai = phanLoai;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaLoaiThuChi() { return maLoaiThuChi; }
    public void setMaLoaiThuChi(int maLoaiThuChi) { this.maLoaiThuChi = maLoaiThuChi; }

    public String getTenLoaiThuChi() { return tenLoaiThuChi; }
    public void setTenLoaiThuChi(String tenLoaiThuChi) { this.tenLoaiThuChi = tenLoaiThuChi; }

    public String getPhanLoai() { return phanLoai; }
    public void setPhanLoai(String phanLoai) { this.phanLoai = phanLoai; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
