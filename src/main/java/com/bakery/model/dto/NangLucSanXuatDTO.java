package com.bakery.model.dto;

import java.time.LocalDate;

public class NangLucSanXuatDTO {
    private LocalDate ngaySanXuat;
    private int gioiHanSoBanh;
    private int soBanhDaNhan;

    public NangLucSanXuatDTO() {}

    public NangLucSanXuatDTO(LocalDate ngaySanXuat, int gioiHanSoBanh, int soBanhDaNhan) {
        this.ngaySanXuat = ngaySanXuat;
        this.gioiHanSoBanh = gioiHanSoBanh;
        this.soBanhDaNhan = soBanhDaNhan;
    }

    public LocalDate getNgaySanXuat() { return ngaySanXuat; }
    public void setNgaySanXuat(LocalDate ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }

    public int getGioiHanSoBanh() { return gioiHanSoBanh; }
    public void setGioiHanSoBanh(int gioiHanSoBanh) { this.gioiHanSoBanh = gioiHanSoBanh; }

    public int getSoBanhDaNhan() { return soBanhDaNhan; }
    public void setSoBanhDaNhan(int soBanhDaNhan) { this.soBanhDaNhan = soBanhDaNhan; }
}