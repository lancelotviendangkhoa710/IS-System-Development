package QuanLyDienLuc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class KhachHang {
    protected String maKH;
    protected String tenKH;
    protected LocalDate ngayHoaDon;
    protected double soLuongDien;
    protected double donGia;

    // Constructor không tham số
    public KhachHang() {
    }

    // Constructor đầy đủ
    public KhachHang(String maKH, String tenKH, LocalDate ngayHoaDon, double soLuongDien, double donGia) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.ngayHoaDon = ngayHoaDon;
        this.soLuongDien = soLuongDien;
        this.donGia = donGia;
    }

    public abstract double tinhThanhTien();

    public void xuatThongTin() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.printf("Mã KH: %-10s | Tên KH: %-15s | Ngày HĐ: %-12s | SL Điện: %-6.1f | Đơn giá: %-8.1f",
                maKH, tenKH, ngayHoaDon.format(formatter), soLuongDien, donGia);
    }

    public String getMaKH() { return maKH; }
    public String getTenKH() { return tenKH; }
    public LocalDate getNgayHoaDon() { return ngayHoaDon; }
}