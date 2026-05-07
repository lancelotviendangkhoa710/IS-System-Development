# 4. QUY TẮC JAVA (BẮT BUỘC TUÂN THỦ)

## 4.1. QUY TẮC CHUNG & ĐẶT TÊN
- **Giao tiếp DB:** LUÔN sử dụng `PreparedStatement` (cho SQL thuần) hoặc `CallableStatement` (cho Procedure). TUYỆT ĐỐI KHÔNG nối chuỗi biến trực tiếp vào câu lệnh SQL (phòng chống SQL Injection).
- **Đồng bộ Đặt tên (Naming Synchronization):**
  + Biến/Hàm: `camelCase` (VD: `layDanhSachSanPham`, `maNhanVien`).
  + Lớp (Class): `PascalCase` (VD: `SanPhamDAO`, `KhachHangDTO`).
  + Hằng số: `UPPER_SNAKE_CASE` (VD: `MAX_SIZE`).
  + **TUYỆT ĐỐI KHÔNG mix Vinglish (Ngoại trừ Động từ & Tên File):** Tên biến, hàm và các DTO/DAO phải thuần Việt không dấu (VD: `layDonHang`).
  + **NGOẠI LỆ HỢP LỆ (Prefixes):** Các tiền tố/động từ tiếng Anh thông dụng ĐƯỢC PHÉP đi kèm tên tiếng Việt: `get`, `set`, `is`, `add`, `update`, `delete`, `create`, `find` (VD: `getSanPham()`, `updateKhachHang()`). Các prefix chuẩn UI như `btn`, `lbl`, `txt` cũng hợp lệ (VD: `btnThem`, `lblTen`).
  + **NGOẠI LỆ HỢP LỆ (Tên File/Class hệ thống):** Tên File và Class thuộc tầng kiến trúc (View, Controller, Service tổng) ĐƯỢC PHÉP dùng tiếng Anh để giữ chuẩn Framework (VD: `LoginViewFXMLController`, `AuthService`, `OrderView`).
- **Xử lý Thời gian (Java 8+):** LUÔN dùng `java.time.LocalDate` (cho dữ liệu DATE) và `java.time.LocalDateTime` (cho dữ liệu TIMESTAMP). TUYỆT ĐỐI KHÔNG dùng `java.util.Date` hay `java.sql.Date` trong các class DTO/Model.

## 4.2. QUY TẮC GIAO DIỆN JAVAFX
- **FXML & Controller:** Mọi View phức tạp phải tách riêng file `.fxml` và Controller tương ứng. Sử dụng `@FXML` để bind các component và sự kiện.
- **Data Binding:** Ưu tiên sử dụng `ObservableList`, `Property` (StringProperty, IntegerProperty) để tự động cập nhật dữ liệu lên UI (TableView, ListView).
- **Format dữ liệu:** Sử dụng `NumberFormat` (cho tiền tệ) và `DateTimeFormatter` (cho ngày tháng) để hiển thị thông tin thống nhất.
- **Luồng xử lý (Threading):** Các tác vụ nặng (truy vấn DB, gọi API) BẮT BUỘC chạy trong `Task` hoặc `Service` của JavaFX để tránh treo UI (UI Freeze). Sử dụng `Platform.runLater()` nếu cần cập nhật UI từ luồng phụ.

## 4.3. QUY TẮC LUỒNG DỮ LIỆU MVP (SINGLE RESPONSIBILITY)
1. **View (FXML/Controller):** Bắt sự kiện, thu thập dữ liệu (TextField, ComboBox), gọi Presenter. TUYỆT ĐỐI KHÔNG chứa logic nghiệp vụ, tính toán, gọi DAO, hay định dạng inline CSS cứng. Tất cả thay đổi UI phải dùng CSS classes (Amber Palette).
2. **Presenter:** Đóng vai trò Orchestrator. Gọi Service để xử lý nghiệp vụ, nhận kết quả và ra lệnh cho View cập nhật UI qua Interface. Không phụ thuộc thư viện UI (JavaFX) để dễ mock test.
3. **Service:** Xử lý toàn bộ business logic cốt lõi (tính tiền, quy tắc nghiệp vụ). Không biết về View hay Presenter. Gọi DAO để tương tác DB.
4. **DAO:** Chỉ dùng SQL hoặc Stored Procedure. Tương tác trực tiếp qua JDBC.

## 4.4. QUY TẮC TẦNG DTO / MODEL
- Mọi class DTO phải có: 
  1. Các thuộc tính `private`.
  2. Constructor rỗng và Constructor đầy đủ tham số.
  3. Đầy đủ Getter/Setter.
  4. Tên class: `[TênBảng]DTO` (Tiếng Việt không dấu).
- **Nullable:** Các cột DB cho phép `NULL` phải dùng Wrapper Class (`Integer`, `Double`). Các cột `NOT NULL` dùng kiểu nguyên thủy (`int`, `double`).

## 4.5. QUY TẮC TẦNG DAO & QUẢN LÝ TÀI NGUYÊN
- **SQL Keywords:** Phải VIẾT HOA (`SELECT`, `INSERT`, `WHERE`).
- **Procedure:** Ưu tiên dùng Stored Procedure cho mọi thao tác CUD.
- **Resource Management:** Bắt buộc sử dụng **Try-with-resources** để đảm bảo đóng `Connection`, `Statement`, và `ResultSet` tự động.
- **Pattern xử lý Null từ ResultSet:** 
  ```java
  // Đối với Date/Timestamp
  if (rs.getTimestamp("THOIDIEM") != null) {
      dto.setThoiDiem(rs.getTimestamp("THOIDIEM").toLocalDateTime());
  }
  // Đối với Wrapper Class
  int val = rs.getInt("COT");
  if (!rs.wasNull()) {
      dto.setGiaTri(val);
  }
  ```
- **Collection:** Trả về `ArrayList` rỗng thay vì `null`.