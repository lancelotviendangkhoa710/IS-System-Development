package lab02.quanlychuyenxe;

import java.util.Scanner;

public class ChuyenNgoaiThanh extends ChuyenXe {
    private String noiDen; 
    private int soNgay; 

    public ChuyenNgoaiThanh() {
        super();
        this.noiDen = "";
        this.soNgay = 0;
    }

    public ChuyenNgoaiThanh(String MSChuyen, String tenTaiXe, String soXe, 
                            double khoiLuong, double doanhThu, String noiDen, int soNgay) {
        super(MSChuyen, tenTaiXe, soXe, khoiLuong, doanhThu);
        this.noiDen = noiDen;
        this.soNgay = soNgay;
    }

    @Override
    public void nhapThongTin(Scanner sc) {
        super.nhapThongTinChung(sc);
        System.out.print("Nhập nơi đến: ");
        this.noiDen = sc.nextLine();
        System.out.print("Nhập số ngày vận chuyển: ");
        this.soNgay = sc.nextInt();
        sc.nextLine(); 
    }

    @Override
    public void xuatThongTin() {
        super.xuatThongTinChung();
        System.out.println(" | Nơi đến: " + noiDen + " | Số ngày: " + soNgay);
    }
}