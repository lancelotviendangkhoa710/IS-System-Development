package QuanLyDienLuc;

import java.time.LocalDate;

public class KhachHangSinhHoat extends KhachHang {
    private double dinhMuc;

    public KhachHangSinhHoat() {
    }

    public KhachHangSinhHoat(String maKH, String tenKH, LocalDate ngayHoaDon, double soLuongDien, double donGia, double dinhMuc) {
        super(maKH, tenKH, ngayHoaDon, soLuongDien, donGia);
        this.dinhMuc = dinhMuc;
    }

    @Override
    public double tinhThanhTien() {
        if (soLuongDien <= dinhMuc) {
            return soLuongDien * donGia;
        } else {
            return (dinhMuc * donGia) + ((soLuongDien - dinhMuc) * donGia * 2);
        }
    }

    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        System.out.printf(" | Định mức: %-6.1f | Thành tiền: %,.1f VNĐ\n", dinhMuc, tinhThanhTien());
    }
}