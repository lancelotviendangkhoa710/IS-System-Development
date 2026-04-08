package QuanLySieuThi;

public abstract class HangHoa {
    protected String maHang;
    protected String tenHang;
    protected int soLuongTon;
    protected double donGia;

    public HangHoa() {}

    public HangHoa(String maHang, String tenHang, int soLuongTon, double donGia) {
        this.maHang = maHang;
        this.tenHang = tenHang;
        this.soLuongTon = soLuongTon;
        this.donGia = donGia;
    }

    public abstract double layThueVAT();
    public abstract String danhGiaMucDo();

    // Phương thức tính tiền chung
    public double tinhTien(int soLuongMua) {
        if (soLuongMua > soLuongTon) {
            System.out.println("Lỗi: Số lượng mua vượt quá số lượng tồn kho!");
            return 0;
        }
        this.soLuongTon -= soLuongMua; // Cập nhật lại số lượng tồn
        return soLuongMua * donGia * (1 + layThueVAT());
    }

    public void xuatThongTin() {
        System.out.printf("Mã: %-6s | Tên: %-15s | Tồn: %-4d | Giá: %,.1f", 
                          maHang, tenHang, soLuongTon, donGia);
    }

    public String getMaHang() { return maHang; }
    public String getTenHang() { return tenHang; }
    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }
}
