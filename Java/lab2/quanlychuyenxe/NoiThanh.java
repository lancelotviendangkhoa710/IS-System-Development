package org.example;

import java.util.Scanner;

public class NoiThanh extends ChuyenXe {
    private int quangDuong;

    public NoiThanh() {
        super();
        quangDuong = 0;
    };

    public NoiThanh(String maSoChuyen, String hoTenTX, int soXe, double khoiLuongHH, double doanhThu, int quangDuong) {
        super(maSoChuyen, hoTenTX, soXe, khoiLuongHH, doanhThu);
        this.quangDuong = quangDuong;
    };

    public int getQuangDuong() {
        return quangDuong;
    };

    public void setQuangDuong(int quangDuong) {
        this.quangDuong = quangDuong;
    };

    @Override
    void nhapChuyenXe(Scanner sc) {
        super.nhapChuyenXe(sc);
        System.out.println("Nhập vào quang đường: ");
        quangDuong = Integer.parseInt(sc.nextLine());
    };

    @Override
    public void xuatChuyenXe() {
        super.xuatChuyenXe();
        System.out.println("Quảng đường: " + quangDuong);
    };

}
