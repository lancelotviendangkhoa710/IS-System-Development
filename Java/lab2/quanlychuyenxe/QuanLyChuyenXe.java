package org.example;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import static java.util.Arrays.sort;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class QuanLyChuyenXe {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<ChuyenXe> dsCX = new ArrayList<>();

        int nt, ngth;
        System.out.println("=== Nhập vào số chuyến xe nội thành === ");
        nt = Integer.parseInt(sc.nextLine());
        System.out.println("\n=== Nhập vào số chuyến xe ngoại thành ===");
        ngth = Integer.parseInt(sc.nextLine());

        if (nt + ngth > 20) {
            System.out.println("Tổng số chuyến xe không được vượt quá 20!");
            return;
        }

        for (int i = 0; i < nt; i++)
        {
            System.out.println("\n=== Nhập vào thông tin chuyến xe nội thành ===");
            NoiThanh chuyenxeNT = new NoiThanh();
            chuyenxeNT.nhapChuyenXe(sc);
            dsCX.add(chuyenxeNT);
        }

        for (int i = nt ; i < ngth + nt; i++)
        {
            System.out.println("\n=== Nhập vào thông tin chuyến xe ngoại thành ===");
            NgoaiThanh chuyenxeNgTh = new NgoaiThanh();
            chuyenxeNgTh.nhapChuyenXe(sc);
            dsCX.add(chuyenxeNgTh);
        }

        System.out.println("\n=== Danh sách chuyến xe nội thành ===");
        for (int i = 0; i < nt; i++) {
            dsCX.get(i).xuatChuyenXe();
            System.out.println();
        }

        System.out.println("\n=== Danh sách chuyến xe ngoại thành ===");
        for (int i = nt; i < ngth + nt; i++) {
            dsCX.get(i).xuatChuyenXe();
            System.out.println();
        }

        double tongDoanhThuNoiThanh = 0, tongDoanhThuNgoaiThanh = 0;
        for (int i = 0; i < nt; i++)
            tongDoanhThuNoiThanh += dsCX.get(i).getDoanhThu();
        for (int i = nt; i < ngth + nt; i++)
            tongDoanhThuNgoaiThanh += dsCX.get(i).getDoanhThu();

        DecimalFormat df = new DecimalFormat("###,###.###", new DecimalFormatSymbols(Locale.getDefault()));
        System.out.println("\nTổng doanh thu chuyến xe nội thành: " + df.format(tongDoanhThuNoiThanh) + " vnd");
        System.out.println("\nTổng doanh thu chuyến xe ngoại thành: " + df.format(tongDoanhThuNgoaiThanh) + " vnd");

        if (nt > 0) {
            ChuyenXe maxCXNT = dsCX.getFirst();
            for (int i = 1; i < nt; i++) {
                if (dsCX.get(i).getDoanhThu() > maxCXNT.getDoanhThu()) {
                    maxCXNT = dsCX.get(i);
                }
            }
            System.out.println("\n=== Chuyến xe nội thành có doanh thu cao nhất ===");
            maxCXNT.xuatChuyenXe();
            System.out.println();
        }
        else {
            System.out.printf("Không có chuyến xe nội thành nào!");
        }
        if (ngth > 0) {
            ChuyenXe maxCXNgTh = dsCX.get(nt);
            for (int i = nt + 1; i < ngth + nt; i++) {
                if (maxCXNgTh.getDoanhThu() < dsCX.get(i).getDoanhThu()) {
                    maxCXNgTh = dsCX.get(i);
                }
            }
            System.out.println("\n=== Chuyến xe ngoại thành có doanh thu cao nhất ===");
            maxCXNgTh.xuatChuyenXe();
            System.out.println();
        }
        else {
            System.out.println("Không có chuyến xe ngoại thành nào!");
        }

        sc.close();
    }
}
