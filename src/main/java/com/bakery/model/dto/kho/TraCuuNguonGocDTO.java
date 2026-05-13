package com.bakery.model.dto.kho;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO ánh xạ VW_TRACUUNGUONGOC.
 * Mỗi record = 1 lô nguyên liệu đã dùng trong 1 mẻ sản xuất.
 * Dùng cho màn hình tra cứu nguồn gốc.
 */
public class TraCuuNguonGocDTO {

    // Thông tin mẻ sản xuất
    private int maMe;
    private LocalDateTime ngaySanXuat;
    private double soLuongSanXuat;

    // Thông tin sản phẩm
    private int maSP;
    private String tenSP;

    // Thông tin nguyên liệu đã dùng
    private int maNL;
    private String tenNguyenLieu;
    private double soLuongDaDung;

    // Thông tin lô hàng
    private int maLo;
    private String maVachLo;
    private LocalDate nsxNguyenLieu;
    private LocalDate hanSuDung;
    private double giaNhap;

    // Thông tin phiếu nhập
    private int maPn;
    private LocalDateTime ngayNhap;

    // Thông tin nhà cung cấp
    private int maNCc;
    private String tenNCC;
    private String sdtNCC;
    private String diaChiNCC;

    public TraCuuNguonGocDTO() {}

    public int getMaMe() { return maMe; }
    public void setMaMe(int maMe) { this.maMe = maMe; }

    public LocalDateTime getNgaySanXuat() { return ngaySanXuat; }
    public void setNgaySanXuat(LocalDateTime ngaySanXuat) { this.ngaySanXuat = ngaySanXuat; }

    public double getSoLuongSanXuat() { return soLuongSanXuat; }
    public void setSoLuongSanXuat(double soLuongSanXuat) { this.soLuongSanXuat = soLuongSanXuat; }

    public int getMaSP() { return maSP; }
    public void setMaSP(int maSP) { this.maSP = maSP; }

    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }

    public int getMaNL() { return maNL; }
    public void setMaNL(int maNL) { this.maNL = maNL; }

    public String getTenNguyenLieu() { return tenNguyenLieu; }
    public void setTenNguyenLieu(String tenNguyenLieu) { this.tenNguyenLieu = tenNguyenLieu; }

    public double getSoLuongDaDung() { return soLuongDaDung; }
    public void setSoLuongDaDung(double soLuongDaDung) { this.soLuongDaDung = soLuongDaDung; }

    public int getMaLo() { return maLo; }
    public void setMaLo(int maLo) { this.maLo = maLo; }

    public String getMaVachLo() { return maVachLo; }
    public void setMaVachLo(String maVachLo) { this.maVachLo = maVachLo; }

    public LocalDate getNsxNguyenLieu() { return nsxNguyenLieu; }
    public void setNsxNguyenLieu(LocalDate nsxNguyenLieu) { this.nsxNguyenLieu = nsxNguyenLieu; }

    public LocalDate getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(LocalDate hanSuDung) { this.hanSuDung = hanSuDung; }

    public double getGiaNhap() { return giaNhap; }
    public void setGiaNhap(double giaNhap) { this.giaNhap = giaNhap; }

    public int getMaPn() { return maPn; }
    public void setMaPn(int maPn) { this.maPn = maPn; }

    public LocalDateTime getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(LocalDateTime ngayNhap) { this.ngayNhap = ngayNhap; }

    public int getMaNCC() { return maNCc; }
    public void setMaNCC(int maNCc) { this.maNCc = maNCc; }

    public String getTenNCC() { return tenNCC; }
    public void setTenNCC(String tenNCC) { this.tenNCC = tenNCC; }

    public String getSdtNCC() { return sdtNCC; }
    public void setSdtNCC(String sdtNCC) { this.sdtNCC = sdtNCC; }

    public String getDiaChiNCC() { return diaChiNCC; }
    public void setDiaChiNCC(String diaChiNCC) { this.diaChiNCC = diaChiNCC; }
}
