package lab02.quanlynhanvien;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class MainNhanVien {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        
        System.out.println("--- BÀI 1: QUẢN LÝ NHÂN VIÊN ---");
        Employee emp = new Employee("Nguyễn Văn A", 25, 10000000);
        
        System.out.println("Thông tin nhân viên ban đầu:");
        emp.show();
        
        System.out.println("\nSau khi tăng lương thêm 10% (addSalary không tham số):");
        emp.addSalary();
        emp.show();
        
        System.out.println("\nSau khi cộng thêm 5,000,000 (addSalary có tham số):");
        emp.addSalary(5000000f);
        emp.show();
    }
}