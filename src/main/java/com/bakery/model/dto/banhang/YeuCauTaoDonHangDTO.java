package com.bakery.model.dto.banhang;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class YeuCauTaoDonHangDTO {
    private LocalDateTime ngayGioNhanBanh;
    private Integer maKH;
    private int maNVLap;
    private int maTrangThai;
    private double tienDaCoc;
    private Integer hinhThucNhan;
    private String diaChiGiao;
    private List<YeuCauChiTietDonHangDTO> items;
    /** Bug 2 Fix: Mã phương thức thanh toán — 0 = chưa rõ, dùng layMaPTTTTienMat() khi cần. */
    private int maPTTT;

    public YeuCauTaoDonHangDTO() {
        this.items = new ArrayList<>();
    }

    public YeuCauTaoDonHangDTO(LocalDateTime ngayGioNhanBanh, Integer maKH, int maNVLap, int maTrangThai,
            double tienDaCoc, Integer hinhThucNhan, String diaChiGiao, List<YeuCauChiTietDonHangDTO> items) {
        this.ngayGioNhanBanh = ngayGioNhanBanh;
        this.maKH = maKH;
        this.maNVLap = maNVLap;
        this.maTrangThai = maTrangThai;
        this.tienDaCoc = tienDaCoc;
        this.hinhThucNhan = hinhThucNhan;
        this.diaChiGiao = diaChiGiao;
        this.items = items == null ? new ArrayList<>() : items;
    }

    public LocalDateTime getNgayGioNhanBanh() {
        return ngayGioNhanBanh;
    }

    public void setNgayGioNhanBanh(LocalDateTime ngayGioNhanBanh) {
        this.ngayGioNhanBanh = ngayGioNhanBanh;
    }

    public Integer getMaKH() {
        return maKH;
    }

    public void setMaKH(Integer maKH) {
        this.maKH = maKH;
    }

    public int getMaNVLap() {
        return maNVLap;
    }

    public void setMaNVLap(int maNVLap) {
        this.maNVLap = maNVLap;
    }

    public int getMaTrangThai() {
        return maTrangThai;
    }

    public void setMaTrangThai(int maTrangThai) {
        this.maTrangThai = maTrangThai;
    }

    public double getTienDaCoc() {
        return tienDaCoc;
    }

    public void setTienDaCoc(double tienDaCoc) {
        this.tienDaCoc = tienDaCoc;
    }

    public Integer getHinhThucNhan() {
        return hinhThucNhan;
    }

    public void setHinhThucNhan(Integer hinhThucNhan) {
        this.hinhThucNhan = hinhThucNhan;
    }

    public String getDiaChiGiao() {
        return diaChiGiao;
    }

    public void setDiaChiGiao(String diaChiGiao) {
        this.diaChiGiao = diaChiGiao;
    }

    /** Bug 2 Fix: getter/setter MAPTTT — 0 nghĩa là chưa set (dùng fallback layMaPTTTTienMat). */
    public int getMaPTTT() { return maPTTT; }
    public void setMaPTTT(int maPTTT) { this.maPTTT = maPTTT; }

    public List<YeuCauChiTietDonHangDTO> getItems() {
        return items;
    }

    public void setItems(List<YeuCauChiTietDonHangDTO> items) {
        this.items = items == null ? new ArrayList<>() : items;
    }
}
