# 4. QUY TẮC JAVA (BẮT BUỘC TUÂN THỦ)

## 4.1. QUY TẮC CHUNG & ĐẶT TÊN
- **Giao tiếp DB:** LUÔN sử dụng `PreparedStatement` (cho SQL thuần) hoặc `CallableStatement` (cho Procedure). TUYỆT ĐỐI KHÔNG nối chuỗi biến trực tiếp vào câu lệnh SQL (phòng chống SQL Injection).
- **Đặt tên:**
  + Biến/Hàm: `camelCase` (VD: `layDanhSachSanPham`, `maNhanVien`).
  + Lớp (Class): `PascalCase` (VD: `SanPhamDAO`, `KhachHangDTO`).
  + Hằng số: `UPPER_SNAKE_CASE` (VD: `MAX_SIZE`).
- **Xử lý Thời gian (Java 8+):** LUÔN dùng `java.time.LocalDate` (cho dữ liệu DATE) và `java.time.LocalDateTime` (cho dữ liệu TIMESTAMP). TUYỆT ĐỐI KHÔNG dùng `java.util.Date` hay `java.sql.Date` trong các class DTO/Model.

## 4.2. QUY TẮC GIAO DIỆN JAVAFX
- **FXML & Controller:** Mọi View phức tạp phải tách riêng file `.fxml` và Controller tương ứng. Sử dụng `@FXML` để bind các component và sự kiện.
- **Data Binding:** Ưu tiên sử dụng `ObservableList`, `Property` (StringProperty, IntegerProperty) để tự động cập nhật dữ liệu lên UI (TableView, ListView).
- **Format dữ liệu:** Sử dụng `NumberFormat` (cho tiền tệ) và `DateTimeFormatter` (cho ngày tháng) để hiển thị thông tin thống nhất.
- **Luồng xử lý (Threading):** Các tác vụ nặng (truy vấn DB, gọi API) BẮT BUỘC chạy trong `Task` hoặc `Service` của JavaFX để tránh treo UI (UI Freeze). Sử dụng `Platform.runLater()` nếu cần cập nhật UI từ luồng phụ.

## 4.3. QUY TẮC LUỒNG DỮ LIỆU MVP
1. **View (FXML/Controller):** Bắt sự kiện người dùng, thu thập dữ liệu từ các Node (TextField, ComboBox), gọi Presenter để xử lý.
2. **Presenter:** Kiểm tra tính hợp lệ sơ bộ (Validate), gọi Service để xử lý nghiệp vụ. Cập nhật kết quả về View qua các phương thức Interface của View.
3. **Service:** Thực hiện logic nghiệp vụ (tính toán, quy tắc kho), gọi DAO để lưu vào DB. Trả kết quả về cho Presenter.
4. **DAO:** Chỉ thực hiện các lệnh SQL hoặc gọi Stored Procedure.

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