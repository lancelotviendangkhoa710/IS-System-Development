package QuanLyDienLuc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class QuanLyTienDien {
    private ArrayList<KhachHang> danhSach = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void nhapDanhSach() {
        System.out.println("--- NHẬP DANH SÁCH KHÁCH HÀNG (Nhấn Enter ở Mã KH để dừng) ---");
        while (true) {
            System.out.print("\nNhập Mã KH: ");
            String maKH = scanner.nextLine().trim();
            if (maKH.isEmpty()) {
                System.out.println("Đã kết thúc nhập liệu.");
                break;
            }

            System.out.print("Nhập Tên KH: ");
            String tenKH = scanner.nextLine();

            System.out.print("Nhập Ngày hóa đơn (dd/MM/yyyy): ");
            LocalDate ngayHoaDon = LocalDate.parse(scanner.nextLine(), formatter);

            // ---------------- KIỂM TRA RÀNG BUỘC ----------------
            boolean hopLe = true;
            Class<?> kieuKhachHangCu = null;

            for (KhachHang kh : danhSach) {
                if (kh.getMaKH().equals(maKH)) {
                    // Kiểm tra ngày hóa đơn
                    if (kh.getNgayHoaDon().equals(ngayHoaDon)) {
                        System.out.println(">> LỖI: Cùng một khách hàng không thể có 2 hóa đơn trong cùng một ngày!");
                        hopLe = false;
                        break;
                    }
                    // Ghi nhớ kiểu khách hàng (SinhHoat, KinhDoanh, SanXuat) để ép buộc phải nhập đúng kiểu
                    kieuKhachHangCu = kh.getClass();
                }
            }
            if (!hopLe) continue; // Bỏ qua lượt nhập này, nhập lại từ đầu

            System.out.print("Nhập Loại KH (1-Sinh Hoạt, 2-Kinh Doanh, 3-Sản Xuất): ");
            int loai = Integer.parseInt(scanner.nextLine());

            // Ràng buộc phải cùng loại điện sử dụng nếu mã KH đã tồn tại
            if (kieuKhachHangCu != null) {
                if ((loai == 1 && kieuKhachHangCu != KhachHangSinhHoat.class) ||
                    (loai == 2 && kieuKhachHangCu != KhachHangKinhDoanh.class) ||
                    (loai == 3 && kieuKhachHangCu != KhachHangSanXuat.class)) {
                    System.out.println(">> LỖI: Mã KH này đã đăng ký loại điện khác trước đó. Phải nhập cùng loại!");
                    continue;
                }
            }

            System.out.print("Nhập Số lượng điện: ");
            double soLuong = Double.parseDouble(scanner.nextLine());

            System.out.print("Nhập Đơn giá: ");
            double donGia = Double.parseDouble(scanner.nextLine());

            // Phân nhánh khởi tạo object
            if (loai == 1) {
                System.out.print("Nhập Định mức: ");
                double dinhMuc = Double.parseDouble(scanner.nextLine());
                danhSach.add(new KhachHangSinhHoat(maKH, tenKH, ngayHoaDon, soLuong, donGia, dinhMuc));
            } else if (loai == 2) {
                danhSach.add(new KhachHangKinhDoanh(maKH, tenKH, ngayHoaDon, soLuong, donGia));
            } else if (loai == 3) {
                System.out.print("Nhập loại điện (2 hoặc 3 pha): ");
                int loaiDien = Integer.parseInt(scanner.nextLine());
                danhSach.add(new KhachHangSanXuat(maKH, tenKH, ngayHoaDon, soLuong, donGia, loaiDien));
            } else {
                System.out.println("Loại KH không hợp lệ!");
            }
        }
    }

    public void xuatHoaDonTheoThang() {
        System.out.println("\n--- TÌM KIẾM HÓA ĐƠN THEO THÁNG/NĂM ---");
        System.out.print("Nhập tháng: ");
        int thang = Integer.parseInt(scanner.nextLine());
        System.out.print("Nhập năm: ");
        int nam = Integer.parseInt(scanner.nextLine());

        System.out.println("\nDANH SÁCH HÓA ĐƠN THÁNG " + thang + "/" + nam + ":");
        boolean coDuLieu = false;
        
        for (KhachHang kh : danhSach) {
            // Kiểm tra khớp tháng và năm
            if (kh.getNgayHoaDon().getMonthValue() == thang && kh.getNgayHoaDon().getYear() == nam) {
                kh.xuatThongTin();
                coDuLieu = true;
            }
        }

        if (!coDuLieu) {
            System.out.println("Không tìm thấy hóa đơn nào trong tháng " + thang + " năm " + nam);
        }
    }
}