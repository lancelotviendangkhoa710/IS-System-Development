package com.bakery.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PhieuThuChiDTO {
    private int maPhieuTC;
    private LocalDateTime ngayTao;
    private int maLoaiThuChi;
    private BigDecimal soTien;
    private int maNV;
    private Integer maHD;
    private Integer maPN;
    private int maCa;
    private String ghiChu;
    private String trangThai;
    private String tenLoaiThuChi;
    private String phanLoai;
    private String tenNhanVien;

    public PhieuThuChiDTO() {}

    public PhieuThuChiDTO(int maPhieuTC, LocalDateTime ngayTao, int maLoaiThuChi, BigDecimal soTien, int maNV, Integer maHD, Integer maPN, int maCa, String ghiChu) {
        this.maPhieuTC = maPhieuTC;
        this.ngayTao = ngayTao;
        this.maLoaiThuChi = maLoaiThuChi;
        this.soTien = soTien;
        this.maNV = maNV;
        this.maHD = maHD;
        this.maPN = maPN;
        this.maCa = maCa;
        this.ghiChu = ghiChu;
    }

    public int getMaPhieuTC() { return maPhieuTC; }
    public void setMaPhieuTC(int maPhieuTC) { this.maPhieuTC = maPhieuTC; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public int getMaLoaiThuChi() { return maLoaiThuChi; }
    public void setMaLoaiThuChi(int maLoaiThuChi) { this.maLoaiThuChi = maLoaiThuChi; }

    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }

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

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }

    public String getTenLoaiThuChi() { return tenLoaiThuChi; }
    public void setTenLoaiThuChi(String tenLoaiThuChi) { this.tenLoaiThuChi = tenLoaiThuChi; }

    public String getPhanLoai() { return phanLoai; }
    public void setPhanLoai(String phanLoai) { this.phanLoai = phanLoai; }
}
