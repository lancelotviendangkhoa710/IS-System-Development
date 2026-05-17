package com.bakery.model.dto.kho;

/** DTO đại diện một dòng công thức nguyên liệu của sản phẩm (Bill of Materials). */
public class CongThucDTO {
    private int maSP;
    private int maNL;
    private double soLuongTieuHao;
    // Thông tin JOIN từ NGUYENLIEU — chỉ dùng để hiển thị, không ghi DB
    private String tenNguyenLieu;
    private double donGia;
    private String tenDVT; // đơn vị tính từ DONVITINH

    public CongThucDTO() {}

    public CongThucDTO(int maSP, int maNL, double soLuongTieuHao) {
        this.maSP = maSP;
        this.maNL = maNL;
        this.soLuongTieuHao = soLuongTieuHao;
    }

    public int getMaSP() { return maSP; }
    public void setMaSP(int maSP) { this.maSP = maSP; }

    public int getMaNL() { return maNL; }
    public void setMaNL(int maNL) { this.maNL = maNL; }

    public double getSoLuongTieuHao() { return soLuongTieuHao; }
    public void setSoLuongTieuHao(double soLuongTieuHao) { this.soLuongTieuHao = soLuongTieuHao; }

    public String getTenNguyenLieu() { return tenNguyenLieu; }
    public void setTenNguyenLieu(String tenNguyenLieu) { this.tenNguyenLieu = tenNguyenLieu; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public String getTenDVT() { return tenDVT != null ? tenDVT : ""; }
    public void setTenDVT(String tenDVT) { this.tenDVT = tenDVT; }

    /** Thành tiền = định mức × đơn giá nguyên liệu */
    public double tinhThanhTien() { return soLuongTieuHao * donGia; }
}
