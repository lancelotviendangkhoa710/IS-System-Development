package org.example;

import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class QuanLyXe {
    public static void main(String[] args) {
        ArrayList<Xe> dsXe = new ArrayList<>();
        //Nhập danh sách xe
        System.out.println("=== Nhập danh sách xe ===");
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Nhập tên chủ xe: ");
            String tenChuXe = sc.nextLine();
            if (tenChuXe.isBlank())
                break;
            System.out.println("Nhập loại xe: ");
            String loaiXe = sc.nextLine();
            System.out.println("Nhập trị giá xe: ");
            double triGiaXe = Double.parseDouble(sc.nextLine());
            System.out.println("Nhập dung tích xi lanh: ");
            double dungTichXiLanh = Double.parseDouble(sc.nextLine());
            dsXe.add(new Xe(tenChuXe, loaiXe, triGiaXe, dungTichXiLanh));
        }

        System.out.println("=== Xuất danh sách xe ==");
        //Xuất thông tin xe
        for (Xe xe : dsXe)
            xe.xuatThongTin();

        //Nhập tên chủ xe và loại xe vào, xuất hiện thuế phải đóng của chủ xe.
        System.out.println("=== Nhập tên chủ xe và loại xe vào, xuất hiện thuế phải đóng của chủ xe ===");
        String tenCX;
        String loaiXe;

        System.out.println("Nhập vào tên chủ xe: ");
        tenCX = sc.nextLine();
        System.out.println("Nhập vào loại xe: ");
        loaiXe = sc.nextLine();

        boolean timThay = false;
        for (Xe xe : dsXe) {
            if (xe.getTenChuXe().equalsIgnoreCase(tenCX) && xe.getLoaiXe().equals(loaiXe)) {
                xe.xuatThueXe();
                timThay = true;
                break;
            }
        }

        if (!timThay) {
            System.out.println("Không tìm thấy xe phù hợp");
        }
    }
}
