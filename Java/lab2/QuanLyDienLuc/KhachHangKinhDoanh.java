package QuanLyDienLuc;

import java.time.LocalDate;

public class KhachHangKinhDoanh extends KhachHang {

    public KhachHangKinhDoanh() {
    }

    public KhachHangKinhDoanh(String maKH, String tenKH, LocalDate ngayHoaDon, double soLuongDien, double donGia) {
        super(maKH, tenKH, ngayHoaDon, soLuongDien, donGia);
    }

    @Override
    public double tinhThanhTien() {
        if (soLuongDien <= 400) {
            return soLuongDien * donGia;
        } else {
            // Phần trong định mức (400) tính giá gốc, phần vượt tính 105%
            return (400 * donGia) + ((soLuongDien - 400) * donGia * 1.05);
        }
    }

    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        System.out.printf(" | Loại: Kinh doanh | Thành tiền: %,.1f VNĐ\n", tinhThanhTien());
    }
}