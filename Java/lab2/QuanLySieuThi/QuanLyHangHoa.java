package QuanLySieuThi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class QuanLyHangHoa {
    private ArrayList<HangHoa> danhSach = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Hàm kiểm tra trùng mã
    private boolean isTrungMa(String maHang) {
        for (HangHoa hh : danhSach) {
            if (hh.getMaHang().equals(maHang)) return true;
        }
        return false;
    }

    public void nhapHangHoa() {
        System.out.println("--- NHẬP HÀNG HÓA (Nhấn Enter ở Mã Hàng để thoát) ---");
        while (true) {
            System.out.print("\nNhập Mã Hàng: ");
            String maHang = scanner.nextLine().trim();
            if (maHang.isEmpty()) break;

            if (isTrungMa(maHang)) {
                System.out.println("LỖI: Mã hàng này đã tồn tại trong kho!");
                continue;
            }

            System.out.print("Tên hàng: ");
            String tenHang = scanner.nextLine();
            System.out.print("Số lượng tồn: ");
            int soLuong = Integer.parseInt(scanner.nextLine());
            System.out.print("Đơn giá: ");
            double donGia = Double.parseDouble(scanner.nextLine());

            System.out.print("Loại hàng (1-Điện máy, 2-Thực phẩm, 3-Gia dụng): ");
            int loai = Integer.parseInt(scanner.nextLine());

            if (loai == 1) {
                System.out.print("Thương hiệu: ");
                String thuongHieu = scanner.nextLine();
                System.out.print("Loại máy (laptop, đt...): ");
                String loaiMay = scanner.nextLine();
                System.out.print("Bảo hành (tháng): ");
                int bh = Integer.parseInt(scanner.nextLine());
                danhSach.add(new HangDienMay(maHang, tenHang, soLuong, donGia, thuongHieu, loaiMay, bh));
                
            } else if (loai == 2) {
                System.out.print("Ngày SX (dd/MM/yyyy): ");
                LocalDate nsx = LocalDate.parse(scanner.nextLine(), fmt);
                System.out.print("Ngày HH (dd/MM/yyyy): ");
                LocalDate nhh = LocalDate.parse(scanner.nextLine(), fmt);
                System.out.print("Nhà cung cấp: ");
                String ncc = scanner.nextLine();
                danhSach.add(new HangThucPham(maHang, tenHang, soLuong, donGia, nsx, nhh, ncc));
                
            } else if (loai == 3) {
                System.out.print("Nhà sản xuất: ");
                String nsx = scanner.nextLine();
                System.out.print("Ngày nhập (dd/MM/yyyy): ");
                LocalDate ngayNhap = LocalDate.parse(scanner.nextLine(), fmt);
                System.out.print("Loại (chén, nồi...): ");
                String loaiGD = scanner.nextLine();
                danhSach.add(new HangGiaDung(maHang, tenHang, soLuong, donGia, nsx, ngayNhap, loaiGD));
            } else {
                System.out.println("Loại không hợp lệ!");
            }
            System.out.println("=> Đã thêm thành công!");
        }
    }

    public void xuatDanhGiaBanBuon() {
        System.out.println("\n--- ĐÁNH GIÁ MỨC ĐỘ BÁN BUÔN ---");
        for (HangHoa hh : danhSach) {
            System.out.printf("Mã: %-6s | Tên: %-15s | Tồn: %-4d | Đánh giá: %s\n", 
                    hh.getMaHang(), hh.getTenHang(), hh.getSoLuongTon(), hh.danhGiaMucDo());
        }
    }

    public void inDienMayBanDuoc() {
        System.out.println("\n--- CÁC THƯƠNG HIỆU ĐIỆN MÁY BÁN ĐƯỢC ---");
        boolean coDuLieu = false;
        for (HangHoa hh : danhSach) {
            if (hh instanceof HangDienMay) {
                HangDienMay dm = (HangDienMay) hh;
                if (dm.danhGiaMucDo().equals("Bán được")) {
                    System.out.printf("Thương hiệu: %-10s | Tên hàng: %-15s | Loại máy: %s\n", 
                            dm.getThuongHieu(), dm.getTenHang(), dm.getLoaiMay());
                    coDuLieu = true;
                }
            }
        }
        if (!coDuLieu) System.out.println("Không có mặt hàng điện máy nào đạt tiêu chí 'Bán được'.");
    }
}
