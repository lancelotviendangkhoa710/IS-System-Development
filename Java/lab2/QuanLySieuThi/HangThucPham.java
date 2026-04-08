package QuanLySieuThi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HangThucPham extends HangHoa {
    private LocalDate ngaySanXuat;
    private LocalDate ngayHetHan;
    private String nhaCungCap;

    public HangThucPham() {}

    public HangThucPham(String maHang, String tenHang, int soLuongTon, double donGia, 
                        LocalDate ngaySanXuat, LocalDate ngayHetHan, String nhaCungCap) {
        super(maHang, tenHang, soLuongTon, donGia);
        this.ngaySanXuat = ngaySanXuat;
        setNgayHetHan(ngayHetHan);
        this.nhaCungCap = nhaCungCap;
    }

    public void setNgayHetHan(LocalDate ngayHetHan) {
        if (ngayHetHan.isBefore(this.ngaySanXuat)) {
            System.out.println("Cảnh báo: Ngày hết hạn không được trước ngày sản xuất! Hệ thống sẽ tự gán bằng ngày SX.");
            this.ngayHetHan = this.ngaySanXuat;
        } else {
            this.ngayHetHan = ngayHetHan;
        }
    }

    @Override
    public double layThueVAT() {
        return 0.05; // Thuế 5%
    }

    @Override
    public String danhGiaMucDo() {
        LocalDate homNay = LocalDate.now();
        if (homNay.isAfter(ngayHetHan) && soLuongTon > 2) {
            return "Khó bán";
        }
        return "Không đánh giá";
    }

    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.printf(" | HSD: %-10s | NCC: %s\n", ngayHetHan.format(fmt), nhaCungCap);
    }
}
