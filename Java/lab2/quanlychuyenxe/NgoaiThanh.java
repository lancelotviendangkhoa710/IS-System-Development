package org.example;
import java.util.Scanner;
public class NgoaiThanh extends ChuyenXe {
    private String noiDen;
    private int soNgayVC;

    public NgoaiThanh () {
        super();
        noiDen = "";
        soNgayVC = 0;
    };

    public NgoaiThanh(String maSoChuyen, String hoTenTX, int soXe, double khoiLuongHH, double doanhThu, String noiDen, int soNgayVC) {
        super(maSoChuyen, hoTenTX, soXe, khoiLuongHH, doanhThu);
        this.noiDen = noiDen;
        this.soNgayVC = soNgayVC;
    };

    public String getNoiDen() {
        return noiDen;
    };

    public void setNoiDen(String noiDen) {
        this.noiDen = noiDen;
    };

    public int getSoNgayVC() {
        return soNgayVC;
    };

    public void setSoNgayVC(int soNgayVC) {
        this.soNgayVC = soNgayVC;
    };

    @Override
    void nhapChuyenXe(Scanner sc) {
        super.nhapChuyenXe(sc);
        System.out.println("Nhập vào nơi đến: ");
        noiDen = sc.nextLine();
        System.out.println("Nhập vào số ngày vận chuyển: ");
        soNgayVC = Integer.parseInt(sc.nextLine());
    };

    @Override
    public void xuatChuyenXe() {
        super.xuatChuyenXe();
        System.out.println("Nơi đến: " + noiDen);
        System.out.println("Số ngày vận chuyển: " + soNgayVC);
    };


}
