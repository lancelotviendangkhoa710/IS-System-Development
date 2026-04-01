package org.example;

public class HinhChuNhat {
    private double chieuDai;
    private double chieuRong;

    public HinhChuNhat() {
        this.chieuDai = chieuRong = 0;
    }

    public HinhChuNhat(double cd, double cr) {
        this.chieuDai = cd;
        this.chieuRong = cr;
    }

    public double getChieuDai() { return chieuDai; }
    public double getChieuRong() { return chieuRong; }

    public void setChieuDai(double cd) { this.chieuDai = cd; }
    public void setChieuRong(double cr) { this.chieuRong = cr; }

    public double tinhDienTich() {
        return chieuDai * chieuRong;
    }

    public double tinhChuVi() {
        return (chieuDai + chieuRong) * 2;
    }

    @Override
    public String toString() {
        return "Chieu dai: " + chieuDai + ", chieu rong: " + chieuRong + ", dien tich: "
                + tinhDienTich() + ", chu vi: " + tinhChuVi();
    }

}
