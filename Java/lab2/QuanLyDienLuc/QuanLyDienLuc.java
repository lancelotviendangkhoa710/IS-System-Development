package QuanLyDienLuc;

public class QuanLyDienLuc {
    public static void main(String[] args) {
        QuanLyTienDien ql = new QuanLyTienDien();
        
        // 1. Thực hiện nhập danh sách (Kèm xử lý các ngoại lệ trùng lặp)
        ql.nhapDanhSach();
        
        // 2. Yêu cầu xuất hóa đơn theo Tháng / Năm
        ql.xuatHoaDonTheoThang();
    }
}