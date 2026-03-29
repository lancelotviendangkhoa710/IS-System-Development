package lab02.quanlychuyenxe;

import java.util.Scanner;

public abstract class ChuyenXe {
    protected String MSChuyen;
    protected String tenTaiXe;
    protected String soXe;
    protected double khoiLuong;
    protected double doanhThu;
    
    public ChuyenXe() {
        this.MSChuyen = "";
        this.tenTaiXe = "";
        this.soXe = "";
        this.khoiLuong = 0;
        this.doanhThu = 0;
    }
    
    public ChuyenXe(String MSChuyen, String tenTaiXe, String soXe, double khoiLuong, double doanhThu) {
        this.MSChuyen = MSChuyen;
        this.tenTaiXe = tenTaiXe;
        this.soXe = soXe;
        this.khoiLuong = khoiLuong;
        this.doanhThu = doanhThu;
    }
    
    public String getMSChuyen() { return MSChuyen; }
    public void setMSChuyen(String MSChuyen) { this.MSChuyen = MSChuyen; }

    public String getTenTaiXe() { return tenTaiXe; }
    public void setTenTaiXe(String tenTaiXe) { this.tenTaiXe = tenTaiXe; }

    public String getSoXe() { return soXe; }
    public void setSoXe(String soXe) { this.soXe = soXe; }

    public double getKhoiLuong() { return khoiLuong; }
    public void setKhoiLuong(double khoiLuong) { this.khoiLuong = khoiLuong; }

    public double getDoanhThu() { return doanhThu; }
    public void setDoanhThu(double doanhThu) { this.doanhThu = doanhThu; }
    
    public void nhapThongTinChung(Scanner sc) {
        System.out.print("Nhập mã số chuyển: ");
        this.MSChuyen = sc.nextLine();
        System.out.print("Nhập họ và tên tài xế: ");
        this.tenTaiXe = sc.nextLine();
        System.out.print("Nhập số xe: ");
        this.soXe = sc.nextLine();
        System.out.print("Nhập khối lượng hàng hóa (tấn): ");
        this.khoiLuong = sc.nextDouble();
        System.out.print("Nhập doanh thu: ");
        this.doanhThu = sc.nextDouble();
        sc.nextLine(); 
    }
    
    public void xuatThongTinChung() {
        System.out.print("Mã số: " + MSChuyen + " | Tài xế: " + tenTaiXe + 
                         " | Số xe: " + soXe + " | Khối lượng: " + khoiLuong + 
                         " | Doanh thu: " + doanhThu);
    }   
    
    public abstract void nhapThongTin(Scanner sc);
    public abstract void xuatThongTin();
}