package org.example;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static boolean kiemTraHinhChuNhat(double chieuDai, double chieuRong) {
        if (chieuDai > 0 && chieuRong > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        double chieuDai, chieuRong;
        System.out.println("Nhập vào chiều dài: ");
        chieuDai = sc.nextDouble();
        System.out.println("Nhập vào chiều rộng: ");
        chieuRong = sc.nextDouble();

        while(!kiemTraHinhChuNhat(chieuDai, chieuRong))
        {
            System.out.println("Chiều dài hoặc chiều rộng nhập vào không thỏa mãn để tạo thành hình chữ nhật");
            System.out.println("Vui lòng nhập lại!");
            System.out.println("Nhập vào chiều dài: ");
            chieuDai = sc.nextDouble();
            System.out.println("Nhập vào chiều rộng: ");
            chieuRong = sc.nextDouble();
        }

        HinhChuNhat HCN = new HinhChuNhat(chieuDai, chieuRong);
        System.out.println(HCN.toString());
    }
}
