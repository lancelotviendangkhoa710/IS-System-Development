package com.bakery.model.dto;

import java.time.LocalDateTime;

public class PhieuThuChiDTO {
    private int maPhieuTC;
    private LocalDateTime ngayTao;
    private int maLoaiThuChi;
    private double soTien;
    private int maNV;
    private Integer maHD;
    private Integer maPN;
    private int maCa;
    private String ghiChu;

    public PhieuThuChiDTO() {}

    public int getMaPhieuTC() { return maPhieuTC; }
    public void setMaPhieuTC(int maPhieuTC) { this.maPhieuTC = maPhieuTC; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public int getMaLoaiThuChi() { return maLoaiThuChi; }
    public void setMaLoaiThuChi(int maLoaiThuChi) { this.maLoaiThuChi = maLoaiThuChi; }

    public double getSoTien() { return soTien; }
    public void setSoTien(double soTien) { this.soTien = soTien; }

    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }

    public Integer getMaHD() { return maHD; }
    public void setMaHD(Integer maHD) { this.maHD = maHD; }

    public Integer getMaPN() { return maPN; }
    public void setMaPN(Integer maPN) { this.maPN = maPN; }

    public int getMaCa() { return maCa; }
    public void setMaCa(int maCa) { this.maCa = maCa; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
