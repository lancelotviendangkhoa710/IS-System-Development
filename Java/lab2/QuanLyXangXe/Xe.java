package org.example;

public abstract class Xe {
    private double quangDuong;
    private double luongXang;
    private double luongHang;

    public Xe() {
        quangDuong = luongXang = luongHang = 0;
    }

    public Xe(double quangDuong, double luongXang, double luongHang) {
        this.quangDuong = quangDuong;
        this.luongXang = luongXang;
        this.luongHang = luongHang;
    }

    public double getQuangDuong() { return quangDuong; }
    public double getLuongXang() { return luongXang; }
    public double getLuongHang() { return luongHang; }

    public void setQuangDuong(double quangDuong) { this.quangDuong = quangDuong; }
    public void setLuongXang(double luongXang) { this.luongXang = luongXang; }
    public void setLuongHang(double luongHang) { this.luongHang = luongHang; }

    public void doXang(double luongXang) {
        this.luongXang += luongXang;
    }

    public void themLuongHang(double luongHang) {
        this.luongHang += luongHang;
    }

    public void botLuongHang(double luongHang) {
        this.luongHang -= luongHang;
    }

    public abstract void choXeChay(double quangDuong);

    public boolean kiemTraXang() {
        return this.luongXang <= 0;
    }

    public String choBietLuongXang() {
        if (kiemTraXang()) {
            return "Xe đã hết xăng!";
        }
        else {
            return "Xe còn " + luongXang + " lít xăng!";
        }
    }

    public void inThongTin() {
        System.out.println("Quãng đường: " + quangDuong + ", lượng xăng: " + luongXang + ", lượng hàng: " + luongHang);
    }


}
