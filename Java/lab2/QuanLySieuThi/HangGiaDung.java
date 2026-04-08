package QuanLySieuThi;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class HangGiaDung extends HangHoa {
    private String nhaSanXuat;
    private LocalDate ngayNhap;
    private String loai;

    public HangGiaDung() {}

    public HangGiaDung(String maHang, String tenHang, int soLuongTon, double donGia, 
                       String nhaSanXuat, LocalDate ngayNhap, String loai) {
        super(maHang, tenHang, soLuongTon, donGia);
        this.nhaSanXuat = nhaSanXuat;
        this.ngayNhap = ngayNhap;
        this.loai = loai;
    }

    @Override
    public double layThueVAT() {
        return 0.10; // Thuế 10%
    }

    @Override
    public String danhGiaMucDo() {
        LocalDate homNay = LocalDate.now();
        long soNgayLuuKho = ChronoUnit.DAYS.between(ngayNhap, homNay); 
        
        if (soLuongTon > 10 && soNgayLuuKho > 20) {
            return "Bán chậm";
        }
        return "Không đánh giá";
    }

    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        System.out.printf(" | Loại: %-10s | NSX: %s\n", loai, nhaSanXuat);
    }
}