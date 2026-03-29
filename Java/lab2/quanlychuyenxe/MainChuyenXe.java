package lab02.quanlychuyenxe;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MainChuyenXe {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        Scanner sc = new Scanner(System.in);
        
        System.out.println("--- BÀI 2: QUẢN LÝ CHUYẾN XE ---");
        QuanLyChuyenXe ql = new QuanLyChuyenXe();
        ChuyenXe[] ds = new ChuyenXe[20];
        
        System.out.println("1. NHẬP DANH SÁCH CHUYẾN XE");
        int n = ql.nhapDS(ds, sc);
        
        System.out.println("\n2. DANH SÁCH CHUYẾN XE VỪA NHẬP");
        ql.xuatDS(ds, n);
        
        System.out.println("\n3. TỔNG DOANH THU");
        ql.tinhTongDoanhThu(ds, n);
        
        System.out.println("\n4. CHUYẾN XE CÓ DOANH THU CAO NHẤT");
        ql.inDoanhThuCaoNhat(ds, n);
        
        sc.close();
    }
}