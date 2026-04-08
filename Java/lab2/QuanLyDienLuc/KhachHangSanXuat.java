package QuanLyDienLuc;

import java.time.LocalDate;

public class KhachHangSanXuat extends KhachHang {
    private int loaiDien; // 2 pha hoặc 3 pha

    public KhachHangSanXuat() {
    }

    public KhachHangSanXuat(String maKH, String tenKH, LocalDate ngayHoaDon, double soLuongDien, double donGia, int loaiDien) {
        super(maKH, tenKH, ngayHoaDon, soLuongDien, donGia);
        this.loaiDien = loaiDien;
    }

    @Override
    public double tinhThanhTien() {
        double tienGoc = soLuongDien * donGia;
        if (loaiDien == 2 && soLuongDien > 200) {
            return tienGoc * 0.98; // Giảm 2%
        } else if (loaiDien == 3 && soLuongDien > 150) {
            return tienGoc * 0.97; // Giảm 3%
        }
        return tienGoc;
    }

    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        System.out.printf(" | Điện: %d pha    | Thành tiền: %,.1f VNĐ\n", loaiDien, tinhThanhTien());
    }
}