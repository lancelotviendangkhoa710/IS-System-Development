package com.bakery.model.dto.kho;

/** DTO một dòng nguyên liệu trong kế hoạch xuất kho sản xuất. */
public class KeHoachXuatKhoDTO {
    private int maNL;
    private String tenNguyenLieu;
    private String donViTinh;
    private double soLuongTonKho;   // Tồn kho hiện tại
    private double soLuongTieuHao;  // Định mức / 1 cái bánh
    private double soLuongCanXuat;  // = soLuongTieuHao * soCaiSanXuat

    public KeHoachXuatKhoDTO() {}

    public KeHoachXuatKhoDTO(int maNL, String tenNguyenLieu, String donViTinh,
                              double soLuongTonKho, double soLuongTieuHao) {
        this.maNL = maNL;
        this.tenNguyenLieu = tenNguyenLieu;
        this.donViTinh = donViTinh;
        this.soLuongTonKho = soLuongTonKho;
        this.soLuongTieuHao = soLuongTieuHao;
    }

    /** Tính soLuongCanXuat dựa trên số cái kế hoạch */
    public void tinhCanXuat(double soCaiSanXuat) {
        this.soLuongCanXuat = soLuongTieuHao * soCaiSanXuat;
    }

    /** True nếu kho không đủ cho kế hoạch */
    public boolean isThieuKho() {
        return soLuongCanXuat > soLuongTonKho;
    }

    public int getMaNL() { return maNL; }
    public void setMaNL(int maNL) { this.maNL = maNL; }

    public String getTenNguyenLieu() { return tenNguyenLieu; }
    public void setTenNguyenLieu(String tenNguyenLieu) { this.tenNguyenLieu = tenNguyenLieu; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    public double getSoLuongTonKho() { return soLuongTonKho; }
    public void setSoLuongTonKho(double soLuongTonKho) { this.soLuongTonKho = soLuongTonKho; }

    public double getSoLuongTieuHao() { return soLuongTieuHao; }
    public void setSoLuongTieuHao(double soLuongTieuHao) { this.soLuongTieuHao = soLuongTieuHao; }

    public double getSoLuongCanXuat() { return soLuongCanXuat; }
    public void setSoLuongCanXuat(double soLuongCanXuat) { this.soLuongCanXuat = soLuongCanXuat; }
}
