package lab02.quanlychuyenxe;

import java.util.Scanner;

public class QuanLyChuyenXe {
    
    public int nhapDS(ChuyenXe[] ds, Scanner sc) {
        System.out.print("Nhập số lượng chuyến xe (tối đa 20): ");
        int n = sc.nextInt();
        sc.nextLine(); 

        if (n > 20) n = 20; 

        for (int i = 0; i < n; i++) {
            System.out.println("Nhập thông tin chuyến thứ " + (i + 1) + ":");
            System.out.print("Chọn loại chuyến (1: Nội thành, 2: Ngoại thành): ");
            int loai = sc.nextInt();
            sc.nextLine(); 

            if (loai == 1) {
                ds[i] = new ChuyenNoiThanh();
            } else {
                ds[i] = new ChuyenNgoaiThanh();
            }
            ds[i].nhapThongTin(sc); 
        }
        return n; 
    }
    
    public void xuatDS(ChuyenXe[] ds, int n) {
        for (int i = 0; i < n; ++i) {
            ds[i].xuatThongTin();
        }
    }
    
    public void tinhTongDoanhThu(ChuyenXe[] ds, int n) {
        double doanhThuNoi = 0, doanhThuNgoai = 0;
        for (int i = 0; i < n; ++i) {
            if (ds[i] instanceof ChuyenNoiThanh) {
                doanhThuNoi += ds[i].getDoanhThu();
            } else if (ds[i] instanceof ChuyenNgoaiThanh) {
                doanhThuNgoai += ds[i].getDoanhThu();
            }
        }
        
        System.out.println("Tổng doanh thu nội thành: " + doanhThuNoi);
        System.out.println("Tổng doanh thu ngoại thành: " + doanhThuNgoai);
    }
    
    public void inDoanhThuCaoNhat(ChuyenXe[] ds, int n) {
        ChuyenXe maxNoi = null, maxNgoai = null;
        
        for (int i = 0; i < n; ++i) {
            if (ds[i] instanceof ChuyenNoiThanh) {
                if (maxNoi == null || ds[i].getDoanhThu() > maxNoi.getDoanhThu()) {
                    maxNoi = ds[i];
                } 
            } else if (ds[i] instanceof ChuyenNgoaiThanh) {
                if (maxNgoai == null || ds[i].getDoanhThu() > maxNgoai.getDoanhThu()) {
                    maxNgoai = ds[i];
                } 
            }
        }
        System.out.print("Chuyến nội thành doanh thu cao nhất: ");
        if (maxNoi != null) maxNoi.xuatThongTin();
        else System.out.println("Chưa có dữ liệu");

        System.out.print("Chuyến ngoại thành doanh thu cao nhất: ");
        if (maxNgoai != null) maxNgoai.xuatThongTin();
        else System.out.println("Chưa có dữ liệu");
    }
}