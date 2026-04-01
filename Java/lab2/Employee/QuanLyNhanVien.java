package org.example;

import java.util.ArrayList;
import java.util.Scanner;
import java.text.DecimalFormat;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class QuanLyNhanVien {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Employee> dsNV = new ArrayList<>();
        int n;
        System.out.println("=== Nhập vào số lượng nhân viên === ");
        n = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < n; i++)
        {
            System.out.println("Nhập vào tên nhân viên: ");
            String name = sc.nextLine();
            System.out.println("Nhập vào tuổi nhân viên: ");
            int age = Integer.parseInt(sc.nextLine());
            System.out.println("Nhập vào lương nhân viên: ");
            float salary = Float.parseFloat(sc.nextLine());
            dsNV.add(new Employee(name, age, salary));
        }

        System.out.println("\n=== Xuất danh sách nhân viên ===");
        for (Employee e : dsNV) {
            e.show();
            System.out.println();
        }

        int tuyChon;
        System.out.println("\n=== Chọn các tùy chọn ====");
        System.out.println("1. Tăng lương theo mặc định (10%)");
        System.out.println("2. Tăng theo tỉ lệ tùy chọn");
        System.out.println("3. Kết thúc chương trình");

        tuyChon = Integer.parseInt(sc.nextLine());

        if (tuyChon == 1) {
            for (Employee e : dsNV)
                e.addSalary();
            System.out.println("Danh sách các nhân viên sau khi tăng 10% lương");
            for (Employee e : dsNV) {
                e.show();
                System.out.println();
            }
        }
        else if (tuyChon == 2) {
            float tiLe;
            System.out.println("Nhập vào phần trăm tăng lương");
            tiLe = Float.parseFloat(sc.nextLine());
            for (Employee e : dsNV)
                e.addSalary(tiLe);
            System.out.println("Danh sách các nhân viên sau khi tăng " + tiLe + "% lương");
            for (Employee e : dsNV) {
                e.show();
                System.out.println();
            }
        }
        else {
            System.out.println("Kết thúc chương trình!");
        }
    }
}
