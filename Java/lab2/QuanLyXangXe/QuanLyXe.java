package org.example;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class QuanLyXe {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Xe> dsXe = new ArrayList<>();
        int n;
        System.out.println("Nhập vào số lượng xe: ");
        n = sc.nextInt();
        for (int i = 0; i < n; i++) {
            double quangDuong, luongXang, luongHang;
            int loaiXe;
            System.out.println("Nhập vào quãng đường(km): ");
            quangDuong = sc.nextDouble();
            System.out.println("Nhập vào lượng xăng(lít): ");
            luongXang = sc.nextDouble();
            System.out.println("Nhập vào lượng hàng(kg): ");
            luongHang = sc.nextDouble();
            System.out.println("Chọn loại xe: 1. Xe máy, 2. Xe tải");
            loaiXe = sc.nextInt();
            if (loaiXe == 1) {
                XeMay xm = new XeMay(quangDuong, luongXang, luongHang);
                dsXe.add(xm);
            } else if (loaiXe == 2) {
                XeTai xt = new XeTai(quangDuong, luongXang, luongHang);
                dsXe.add(xt);
            } else continue;
        }

        System.out.println("===Danh sach cac xe===");
        for (Xe t : dsXe) {
            t.inThongTin();
            System.out.println();
        }

        int chonXe;
        System.out.println("===Hãy chọn 1 xe để thao tác===");
        System.out.println("===Các xe từ 1 --> " + n + ", hãy nhập vào 1 số để chọn===");
        chonXe = sc.nextInt();
        while (chonXe < 1 || chonXe > n) {
            System.out.println("Chọn sai rồi, chọn lại đê");
            chonXe = sc.nextInt();
        }
        chonXe -= 1;
        while (true) {
            System.out.println("===Danh sách các tùy chọn với các xe===");
            System.out.println("1. Thêm một lượng hàng lên xe.\n" +
                    "2. Bớt một lượng hàng xuống xe.\n" +
                    "3. Đổ một lượng xăng vào xe.\n" +
                    "4. Cho xe chạy một đoạn đường.\n" +
                    "5. Kiểm tra xem xe đã hết xăng chưa.\n" +
                    "6. Cho biết lượng xăng còn trong xe.\n" +
                    "7. Thoát");
            int tuyChon;
            tuyChon = sc.nextInt();
            if (tuyChon == 1) {
                double lh;
                System.out.printf("Nhập vào lượng hàng cần thêm: ");
                lh = sc.nextDouble();
                dsXe.get(chonXe).themLuongHang(lh);
                System.out.printf("Đã thêm thành công! Lượng hàng hiện tại là: " + dsXe.get(chonXe).getLuongHang());
            } else if (tuyChon == 2) {
                double lh;
                System.out.printf("Nhập vào lượng hàng cần bớt: ");
                lh = sc.nextDouble();
                if (lh > dsXe.get(chonXe).getLuongHang()) {
                    System.out.println("Bớt lố quá rồi, chỉnh lại lượng hàng = 0");
                    dsXe.get(chonXe).setLuongHang(0);
                } else {
                    dsXe.get(chonXe).botLuongHang(lh);
                    System.out.printf("Đã bớt thành công! Lượng hàng hiện tại là: " + dsXe.get(chonXe).getLuongHang());
                }
            } else if (tuyChon == 3) {
                double lx;
                System.out.printf("Nhập vào lượng xăng cần đổ: ");
                lx = sc.nextDouble();
                dsXe.get(chonXe).doXang(lx);
                System.out.printf("Đã đổ xăng thành công! Lượng xăng hiện tại là: " + dsXe.get(chonXe).getLuongXang());
            } else if (tuyChon == 4) {
                double qd;
                System.out.printf("Nhập vào quãng đường muốn cho xe chạy: ");
                qd = sc.nextDouble();
                dsXe.get(chonXe).choXeChay(qd);
                System.out.printf("Đã cho xe chạy thành công! Lượng xăng hiện tại là: " + dsXe.get(chonXe).getLuongXang());
            } else if (tuyChon == 5) {
                System.out.println((dsXe.get(chonXe).kiemTraXang()) ? "Hết xăng" : "Còn xăng");
            } else if (tuyChon == 6) {
                System.out.println(dsXe.get(chonXe).choBietLuongXang());
            } else {
                System.out.println("Thoát!");
                break;
            }
        }
    }
}
