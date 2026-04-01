package org.example;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Scanner;
import java.text.DecimalFormat;

public abstract class ChuyenXe {
    protected String maSoChuyen;
    protected String hoTenTX;
    protected int soXe;
    protected double khoiLuongHH;
    protected double doanhThu;

    public ChuyenXe() {
        maSoChuyen = hoTenTX = "";
        soXe = 0;
        khoiLuongHH = 0;
        doanhThu = 0;
    };

    public ChuyenXe(String maSoChuyen, String hoTenTX, int soXe, double khoiLuongHH, double doanhThu)
    {
        this.maSoChuyen = maSoChuyen;
        this.hoTenTX = hoTenTX;
        this.soXe = soXe;
        this.khoiLuongHH = khoiLuongHH;
        this.doanhThu = doanhThu;
    };

    public String getMaSoChuyen() {
        return maSoChuyen;
    };
    public void setMaSoChuyen(String maSoChuyen) {
        this.maSoChuyen = maSoChuyen;
    };

    public String getHoTenTX() {
        return hoTenTX;
    };

    public void setHoTenTX(String hoTenTX) {
        this.hoTenTX = hoTenTX;
    };

    public int getSoXe() {
        return soXe;
    };

    public void setSoXe(int soXe) {
        this.soXe = soXe;
    };

    public double getKhoiLuongHH() {
        return khoiLuongHH;
    };

    public void setKhoiLuongHH(double khoiLuongHH) {
        this.khoiLuongHH = khoiLuongHH;
    };

    public double getDoanhThu() {
        return doanhThu;
    };

    public void setDoanhThu(double doanhThu) {
        this.doanhThu = doanhThu;
    };

    void nhapChuyenXe(Scanner sc) {
        System.out.println("Nhập vào mã số chuyển: ");
        maSoChuyen = sc.nextLine();
        System.out.println("Nhập vào họ tên tài xế: ");
        hoTenTX = sc.nextLine();
        System.out.println("Nhập vào số xe: ");
        soXe = Integer.parseInt(sc.nextLine());
        System.out.println("Nhập vào khối lượng hàng hóa: ");
        khoiLuongHH = Double.parseDouble(sc.nextLine());
        System.out.println("Nhập vào doanh thu: ");
        doanhThu = Double.parseDouble(sc.nextLine());
    };

    public void xuatChuyenXe() {
        System.out.println("Mã số chuyển: " + maSoChuyen);
        System.out.println("Họ tên tài xế: " + hoTenTX);
        System.out.println("Số xe: " + soXe);
        System.out.println("Khối lượng hàng hóa: " + khoiLuongHH);
        DecimalFormat df = new DecimalFormat("###,###.###", new DecimalFormatSymbols(Locale.getDefault()));
        System.out.println("Doanh thu: " + df.format(doanhThu) + " vnd");
    };

}
