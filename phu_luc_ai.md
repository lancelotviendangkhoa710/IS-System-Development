# PHỤ LỤC: NHẬT KÝ SỬ DỤNG AI

Hệ thống Quản lý Tiệm bánh Chuyên nghiệp (BMS) sử dụng công nghệ AI để hỗ trợ phát triển. Tài liệu này ghi lại nhật ký sử dụng, đối chiếu kết quả do AI sinh ra và cách nhóm tối ưu hóa để đáp ứng các quy chuẩn của dự án.

## 1. Nhật ký tương tác và kiểm chứng

| Giai đoạn | Prompt chính của nhóm | Kết quả từ AI | Cách nhóm tối ưu & Kiểm chứng |
|---|---|---|---|
| **Cơ sở dữ liệu** | "Thiết kế bảng `KhachHang` tích điểm, tự thăng hạng VIP trên Oracle DB." | Sinh bảng `KhachHang` có cột điểm và Trigger thăng hạng gán cứng điểm. | Chuyển tên viết hoa (`KHACHHANG`). Tách bảng hạng thành viên riêng để tránh hardcode. |
| **Giao diện (UI)** | "Tạo giao diện bán hàng (POS) bằng JavaFX FXML có sidebar." | Dùng `AnchorPane` tọa độ cứng, nhúng mã CSS trực tiếp (inline style). | Đổi sang layout tự co giãn (`VBox`/`HBox`), gỡ inline style đưa vào `bakery.css` chung. |
| **Truy cập dữ liệu** | "Viết hàm Java lấy danh sách nguyên liệu sắp hết hạn theo quy tắc FEFO." | Select toàn bộ bảng rồi lọc và sắp xếp bằng Java Stream. | Đẩy logic sắp xếp xuống Database qua câu lệnh SQL tối ưu. Dùng Try-with-resources. |
| **Kiến trúc (MVP)** | "Viết logic tìm khách hàng theo số điện thoại khi click nút Tìm." | Viết trực tiếp kết nối DB và truy vấn SQL trong View Controller. | Tách thành View Interface, Presenter, Service và DAO theo đúng MVP. |

## 2. So sánh mã nguồn: Code AI sinh vs Code tối ưu bởi nhóm

### Ví dụ 1: Tầng dữ liệu Java (DAO) - Quản lý tài nguyên & Chống SQL Injection

*   **Vấn đề của code AI:** Nối chuỗi SQL gây lỗ hổng SQL Injection; đóng kết nối thủ công dễ rò rỉ bộ nhớ; trả về `null` thay vì danh sách rỗng.

**[MÃ NGUỒN DO AI SINH RA]**
```java
public List<SanPham> getSanPhamList(String ten) {
    List<SanPham> list = null; 
    try {
        Connection conn = DBConnect.getConnection();
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM SanPham WHERE TenSP LIKE '%" + ten + "%'";
        ResultSet rs = stmt.executeQuery(sql);
        list = new ArrayList<>();
        while(rs.next()) {
            SanPham sp = new SanPham();
            sp.setMaSP(rs.getInt("MaSP"));
            sp.setTenSP(rs.getString("TenSP"));
            list.add(sp);
        }
        conn.close(); // Dễ leak tài nguyên nếu lỗi xảy ra trước dòng này
    } catch (Exception e) { e.printStackTrace(); }
    return list;
}
```

**[MÃ NGUỒN SAU KHI NHÓM TỐI ƯU LẠI]**
```java
public List<SanPhamDTO> layDanhSachSanPhamTheoTen(String ten) {
    List<SanPhamDTO> danhSach = new ArrayList<>(); // Luôn trả về list rỗng
    String sql = "SELECT MASP, TENSP FROM SANPHAM WHERE TENSP LIKE ? AND THOIDIEMXOA IS NULL";
    
    // Tự động đóng tài nguyên bằng try-with-resources, chống SQL Injection
    try (Connection conn = DBConnect.layKetNoi();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, "%" + ten + "%");
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                SanPhamDTO sp = new SanPhamDTO();
                sp.setMaSP(rs.getInt("MASP"));
                sp.setTenSP(rs.getString("TENSP"));
                danhSach.add(sp);
            }
        }
    } catch (SQLException e) {
        System.err.println("Lỗi truy vấn SanPhamDAO: " + e.getMessage());
    }
    return danhSach;
}
```

### Ví dụ 2: Kiến trúc ứng dụng - Phân tách Fat Controller sang MVP chuẩn

*   **Vấn đề của code AI:** Viết trực tiếp logic kết nối CSDL và truy vấn trong file Controller của JavaFX (vi phạm MVP).

**[MÃ NGUỒN DO AI SINH RA]**
```java
public class BanHangViewFXMLController {
    @FXML private TextField txtSDT;
    @FXML private Label lblTenKhachHang;

    @FXML
    private void onTimKhachHang() {
        String sdt = txtSDT.getText();
        try {
            Connection conn = DBConnect.getConnection();
            String sql = "SELECT HoTen FROM KhachHang WHERE SDT = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { lblTenKhachHang.setText(rs.getString("HoTen")); }
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
```

**[MÃ NGUỒN SAU KHI NHÓM TỐI ƯU LẠI]**
```java
// 1. Interface đại diện cho View
public interface IBanHangView {
    String laySdtNhapVao();
    void hienThiTenKhachHang(String tenKhachHang);
}

// 2. Presenter trung gian xử lý logic
public class BanHangPresenter {
    private final IBanHangView view;
    private final KhachHangService service = new KhachHangService();

    public BanHangPresenter(IBanHangView view) { this.view = view; }

    public void xuLyTimKhachHang() {
        String sdt = view.laySdtNhapVao();
        if (sdt != null && !sdt.isEmpty()) {
            KhachHangDTO kh = service.timKhachHangTheoSdt(sdt);
            view.hienThiTenKhachHang(kh != null ? kh.getHoTen() : "Khách hàng vãng lai");
        }
    }
}

// 3. Controller của JavaFX chỉ làm nhiệm vụ giao tiếp UI
public class BanHangViewFXMLController implements IBanHangView {
    @FXML private TextField txtSDT;
    @FXML private Label lblTenKhachHang;
    private BanHangPresenter presenter;

    @FXML public void initialize() { this.presenter = new BanHangPresenter(this); }
    @FXML private void onTimKhachHang() { presenter.xuLyTimKhachHang(); }

    @Override public String laySdtNhapVao() { return txtSDT.getText(); }
    @Override public void hienThiTenKhachHang(String ten) { lblTenKhachHang.setText(ten); }
}
```

## 3. Quy trình và công cụ kiểm chứng

*   **Phân tích tĩnh (GitNexus):** Sử dụng đồ thị cuộc gọi để quét phân tích tác động tĩnh và khoanh vùng ảnh hưởng của mã nguồn mới sửa.
*   **Hậu kiểm MVP & SQL:** Chạy kiểm tra tự động cấu trúc phân tầng (View ↔ Presenter ↔ Service ↔ DAO) và rà soát chống nối chuỗi SQL.
