package com.bakery.model.dto.taichinh;

import java.time.LocalDateTime;

/**
 * DTO cho bảng PHIEUTHUCHI.
 * TRANGTHAI: 'active' | 'cancelled'
 */
public class PhieuThuChiDTO {

    private int           maPhieuTC;
    private LocalDateTime ngayTao;
    private int           maLoaiThuChi;
    private String        tenLoaiThuChi;  // JOIN từ LOAITHUCHI
    private String        phanLoai;       // 'Thu' | 'Chi' — JOIN từ LOAITHUCHI
    private double        soTien;
    private int           maNV;
    private String        tenNhanVien;    // JOIN từ NHANVIEN
    private Integer       maHD;           // nullable
    private Integer       maPieuNhap;     // nullable — MAPN
    private int           maCa;
    private String        ghiChu;
    private String        trangThai;      // 'active' | 'cancelled'

    public PhieuThuChiDTO() {}

    // ── Getters / Setters ────────────────────────────────────────────────

    public int getMaPhieuTC()                           { return maPhieuTC; }
    public void setMaPhieuTC(int v)                     { this.maPhieuTC = v; }

    public LocalDateTime getNgayTao()                   { return ngayTao; }
    public void setNgayTao(LocalDateTime v)             { this.ngayTao = v; }

    public int getMaLoaiThuChi()                        { return maLoaiThuChi; }
    public void setMaLoaiThuChi(int v)                  { this.maLoaiThuChi = v; }

    public String getTenLoaiThuChi()                    { return tenLoaiThuChi; }
    public void setTenLoaiThuChi(String v)              { this.tenLoaiThuChi = v; }

    public String getPhanLoai()                         { return phanLoai; }
    public void setPhanLoai(String v)                   { this.phanLoai = v; }

    public double getSoTien()                           { return soTien; }
    public void setSoTien(double v)                     { this.soTien = v; }

    public int getMaNV()                                { return maNV; }
    public void setMaNV(int v)                          { this.maNV = v; }

    public String getTenNhanVien()                      { return tenNhanVien; }
    public void setTenNhanVien(String v)                { this.tenNhanVien = v; }

    public Integer getMaHD()                            { return maHD; }
    public void setMaHD(Integer v)                      { this.maHD = v; }

    public Integer getMaPhieuNhap()                     { return maPieuNhap; }
    public void setMaPhieuNhap(Integer v)               { this.maPieuNhap = v; }

    public int getMaCa()                                { return maCa; }
    public void setMaCa(int v)                          { this.maCa = v; }

    public String getGhiChu()                           { return ghiChu; }
    public void setGhiChu(String v)                     { this.ghiChu = v; }

    public String getTrangThai()                        { return trangThai; }
    public void setTrangThai(String v)                  { this.trangThai = v; }
}
