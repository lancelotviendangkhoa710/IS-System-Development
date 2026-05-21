package com.bakery.model.dto.banhang;

import java.math.BigDecimal;

public class CTDonTuyChinhDTO {
    private int maCTTC;
    private int maDon;
    private int maSP;
    private String tenSP;
    private int soLuong;
    private Integer maKC;
    private Integer maCot;
    private Integer maNhan;
    private Integer maTrangTri;
    private String loiChucTrenBanh;
    private String ghiChuThoBanh;
    private String hinhAnhThamKhao;
    private BigDecimal donGia;
    private BigDecimal donGiaVon;
    private Integer thoiGianChuanBi;

    public CTDonTuyChinhDTO() {}

    public CTDonTuyChinhDTO(int maCTTC, int maDon, int maSP, int soLuong, Integer maKC, Integer maCot, Integer maNhan, Integer maTrangTri, String loiChucTrenBanh, String ghiChuThoBanh, String hinhAnhThamKhao, BigDecimal donGia, BigDecimal donGiaVon, Integer thoiGianChuanBi) {
        this.maCTTC = maCTTC;
        this.maDon = maDon;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.maKC = maKC;
        this.maCot = maCot;
        this.maNhan = maNhan;
        this.maTrangTri = maTrangTri;
        this.loiChucTrenBanh = loiChucTrenBanh;
        this.ghiChuThoBanh = ghiChuThoBanh;
        this.hinhAnhThamKhao = hinhAnhThamKhao;
        this.donGia = donGia;
        this.donGiaVon = donGiaVon;
        this.thoiGianChuanBi = thoiGianChuanBi;
    }

    public int getMaCTTC() { return maCTTC; }
    public void setMaCTTC(int maCTTC) { this.maCTTC = maCTTC; }

    public int getMaDon() { return maDon; }
    public void setMaDon(int maDon) { this.maDon = maDon; }

    public int getMaSP() { return maSP; }
    public void setMaSP(int maSP) { this.maSP = maSP; }

    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public Integer getMaKC() { return maKC; }
    public void setMaKC(Integer maKC) { this.maKC = maKC; }

    public Integer getMaCot() { return maCot; }
    public void setMaCot(Integer maCot) { this.maCot = maCot; }

    public Integer getMaNhan() { return maNhan; }
    public void setMaNhan(Integer maNhan) { this.maNhan = maNhan; }

    public Integer getMaTrangTri() { return maTrangTri; }
    public void setMaTrangTri(Integer maTrangTri) { this.maTrangTri = maTrangTri; }

    public String getLoiChucTrenBanh() { return loiChucTrenBanh; }
    public void setLoiChucTrenBanh(String loiChucTrenBanh) { this.loiChucTrenBanh = loiChucTrenBanh; }

    public String getGhiChuThoBanh() { return ghiChuThoBanh; }
    public void setGhiChuThoBanh(String ghiChuThoBanh) { this.ghiChuThoBanh = ghiChuThoBanh; }

    public String getHinhAnhThamKhao() { return hinhAnhThamKhao; }
    public void setHinhAnhThamKhao(String hinhAnhThamKhao) { this.hinhAnhThamKhao = hinhAnhThamKhao; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }

    public BigDecimal getDonGiaVon() { return donGiaVon; }
    public void setDonGiaVon(BigDecimal donGiaVon) { this.donGiaVon = donGiaVon; }

    public Integer getThoiGianChuanBi() { return thoiGianChuanBi; }
    public void setThoiGianChuanBi(Integer thoiGianChuanBi) { this.thoiGianChuanBi = thoiGianChuanBi; }
}
