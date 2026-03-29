package lab02.quanlychuyenxe;

import java.util.Scanner;

public class ChuyenNoiThanh extends ChuyenXe {
    private double quangDuong;
    
    public ChuyenNoiThanh() {
        super();
        this.quangDuong = 0;
    } 
    
    public ChuyenNoiThanh(String MSChuyen, String tenTaiXe, String soXe, 
                         double khoiLuong, double doanhThu, double quangDuong) {
        super(MSChuyen, tenTaiXe, soXe, khoiLuong, doanhThu);
        this.quangDuong = quangDuong;
    }
    
    @Override 
    public void nhapThongTin(Scanner sc) {
        super.nhapThongTinChung(sc); 
        System.out.print("Nhập quãng đường đi (km): ");
        this.quangDuong = sc.nextDouble();
        sc.nextLine(); 
    }
    
    @Override
    public void xuatThongTin() {
        super.xuatThongTinChung(); 
        System.out.println(" | Quãng đường: " + quangDuong + " km");
    }
}