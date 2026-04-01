package org.example;

public class XeTai extends Xe{
        public XeTai() {
            super();
        }

        public XeTai(double quangDuong, double luongXang, double luongHang) {
            super(quangDuong, luongXang, luongHang);
        }

        @Override
        public void choXeChay(double quangDuong) {
            double mucTieuTonChuanTrenQD = 0.2;
            double mucTieuTonChuanTrenLH = 0.0001;
            double luongXangTieuTon = (mucTieuTonChuanTrenQD + mucTieuTonChuanTrenLH * getLuongHang()) * quangDuong;
            double luongXangConLai = getLuongXang() - luongXangTieuTon;
            if (luongXangConLai < 0) {
                System.out.println("Xe không đủ xăng để chạy hết quãng đường!");
                setLuongXang(0);
            } else {
                setLuongXang(luongXangConLai);
            }
        }

        @Override
        public void inThongTin() {
            System.out.println("Xe tải: ");
            super.inThongTin();
        }
}
