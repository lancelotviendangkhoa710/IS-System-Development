package QuanLySieuThi;

public class HangDienMay extends HangHoa {
    private String thuongHieu;
    private String loaiMay;
    private int thoiGianBaoHanh; // Tính theo tháng

    public HangDienMay() {}

    public HangDienMay(String maHang, String tenHang, int soLuongTon, double donGia, 
                       String thuongHieu, String loaiMay, int thoiGianBaoHanh) {
        super(maHang, tenHang, soLuongTon, donGia);
        this.thuongHieu = thuongHieu;
        this.loaiMay = loaiMay;
        this.thoiGianBaoHanh = thoiGianBaoHanh;
    }

    @Override
    public double layThueVAT() {
        return 0.10; // Thuế 10%
    }

    @Override
    public String danhGiaMucDo() {
        if (soLuongTon < 3) {
            return "Bán được";
        }
        return "Không đánh giá";
    }

    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        System.out.printf(" | Hiệu: %-10s | Loại: %-10s | BH: %d tháng\n", 
                          thuongHieu, loaiMay, thoiGianBaoHanh);
    }

    public String getThuongHieu() { return thuongHieu; }
    public String getLoaiMay() { return loaiMay; }
}
