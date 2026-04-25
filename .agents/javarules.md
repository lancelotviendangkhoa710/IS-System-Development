# 4. QUY TẮC JAVA (BẮT BUỘC TUÂN THỦ)

## 4.1. QUY TẮC CHUNG & ĐẶT TÊN
- **Giao tiếp DB:** LUÔN sử dụng `PreparedStatement` (cho SQL thuần) hoặc `CallableStatement` (cho Procedure). TUYỆT ĐỐI KHÔNG nối chuỗi biến trực tiếp vào câu lệnh SQL (phòng chống SQL Injection).
- **Đặt tên:**
  + Biến/Hàm: `camelCase` (VD: `layDanhSachSanPham`, `maNhanVien`).
  + Lớp (Class): `PascalCase` (VD: `SanPhamDAO`, `CTPhieuNhapDTO`).
  + Hằng số: `UPPER_SNAKE_CASE` (VD: `MAX_SIZE`).
- **Xử lý Thời gian (Java 8+):** LUÔN dùng `java.time.LocalDate` (cho dữ liệu DATE) và `java.time.LocalDateTime` (cho dữ liệu TIMESTAMP). TUYỆT ĐỐI KHÔNG dùng `java.util.Date` hay `java.sql.Date` trong các class DTO/Model.

## 4.2. QUY TẮC TẦNG DTO / MODEL
- Mọi class DTO phải có đầy đủ:
  1. Các thuộc tính để ở mức `private`.
  2. Một Constructor rỗng (No-args constructor).
  3. Một Constructor đầy đủ tham số (All-args constructor).
  4. Đầy đủ Getter/Setter cho tất cả các thuộc tính.
- **Quy tắc kiểu dữ liệu Nullable:** Các cột DB cho phép `NULL` bắt buộc phải dùng Wrapper Class trong Java (`Integer`, `Double`, `Boolean`). Các cột `NOT NULL` dùng kiểu nguyên thủy (`int`, `double`, `boolean`).

## 4.3. QUY TẮC TẦNG DAO & QUẢN LÝ TÀI NGUYÊN
- **Chuỗi SQL:** Các từ khóa SQL (`SELECT`, `FROM`, `WHERE`...) phải VIẾT HOA. Nếu câu lệnh SQL dài, phải ngắt dòng bằng dấu `+` và căn lề cho dễ đọc.
- **Đóng luồng an toàn (Nested Try-with-resources):** Bắt buộc phải tách thành 2 khối `try` lồng nhau để đảm bảo thứ tự đóng tài nguyên.
  + Khối ngoài: Cho `Connection` và `PreparedStatement/CallableStatement`.
  + Khối trong: Cho `ResultSet`.
- **Trích xuất dữ liệu ResultSet:** Lấy dữ liệu bắt buộc gọi theo tên cột `rs.getInt("TEN_COT")`, KHÔNG ĐƯỢC gọi theo chỉ mục cột `rs.getInt(1)`.
- **Khởi tạo Collection:** Các hàm trả về danh sách (`List`) phải khởi tạo `List<T> list = new ArrayList<>();` ở đầu hàm và trả về `list` ở cuối hàm. Tuyệt đối không trả về `null`.

## 4.4. PATTERN XỬ LÝ DỮ LIỆU NULL TỪ RESULTSET (RẤT QUAN TRỌNG)
Agent BẮT BUỘC phải dùng kỹ thuật sau khi đọc dữ liệu từ `ResultSet` đổ vào `DTO`:

- **Đối với dữ liệu Ngày tháng (Date/Timestamp):** Phải kiểm tra khác null trước khi convert.

```java
if (rs.getTimestamp("THOIDIEMXOA") != null) {
    sp.setThoiDiemXoa(rs.getTimestamp("THOIDIEMXOA").toLocalDateTime());
}
```

- **Đối với biến số có thể Null (Wrapper Class như `Integer`/`Double`):** Phải đọc kiểu nguyên thủy trước, check `wasNull()` rồi mới Set. CẤM gán trực tiếp.

```java
int maNX = rs.getInt("MANX");
if (!rs.wasNull()) {
    sp.setMaNX(maNX);
}
```

## 4.5. PATTERN GỌI STORED PROCEDURE (CALLABLESTATEMENT)
Khi gọi Stored Procedure từ tầng DAO, bắt buộc tuân theo pattern sau:

- **Procedure chỉ có tham số IN** (thao tác DML không cần trả về giá trị):

```java
// Cú pháp: {CALL TEN_PROCEDURE(?, ?, ?)}
try (Connection con = DBConnect.getConnection();
     CallableStatement cs = con.prepareCall("{CALL PROC_NHANTIENCOC(?, ?, ?, ?)}")) {

    cs.setInt(1, maDon);
    cs.setBigDecimal(2, soTienCoc);
    cs.setInt(3, maCa);
    cs.setInt(4, maNhanVien);
    cs.execute();
}
```

- **Procedure có tham số OUT** (cần nhận giá trị trả về từ DB):

```java
try (Connection con = DBConnect.getConnection();
     CallableStatement cs = con.prepareCall("{CALL PROC_TAODON(?, ?, ?)}")) {

    cs.setInt(1, maKhachHang);
    cs.setInt(2, maNhanVien);
    // Đăng ký tham số OUT trước khi execute
    cs.registerOutParameter(3, Types.INTEGER);
    cs.execute();

    // Đọc giá trị trả về sau khi execute
    int maDonMoi = cs.getInt(3);
}
```

- **Gọi Function SQL** (trả về 1 giá trị duy nhất):

```java
// Cú pháp: {? = CALL TEN_FUNCTION(?)}
try (Connection con = DBConnect.getConnection();
     CallableStatement cs = con.prepareCall("{? = CALL FUNC_TINHDIEMDATHANG(?)}")) {

    cs.registerOutParameter(1, Types.NUMERIC); // Tham số trả về luôn ở vị trí 1
    cs.setInt(2, maDon);
    cs.execute();

    BigDecimal diemTichLuy = cs.getBigDecimal(1);
}
```

## 4.6. PATTERN BẮT LỖI NGHIỆP VỤ TỪ ORACLE
Oracle ném lỗi nghiệp vụ từ `RAISE_APPLICATION_ERROR` dưới dạng `SQLException` với mã lỗi âm. Toàn bộ mã lỗi của dự án nằm trong dải **`-20001` đến `-20599`** theo phân vùng của `PKG_ERROR_CODES` (xem `dbrules.md` mục 3.5). Phân biệt rõ 2 loại lỗi để xử lý đúng:

```java
catch (SQLException e) {
    // Lỗi nghiệp vụ từ RAISE_APPLICATION_ERROR — dải -20001 đến -20599
    // Phân vùng: -20001~-20099 Hệ thống | -20100~-20199 Nhân sự
    //            -20200~-20299 Sản phẩm  | -20300~-20399 Kho
    //            -20400~-20499 Đơn hàng  | -20500~-20599 Tài chính
    // → Message đã là tiếng Việt có dấu, hiển thị thẳng lên Alert cho người dùng
    if (e.getErrorCode() >= -20599 && e.getErrorCode() <= -20001) {
        // Tách lấy phần message tiếng Việt, bỏ phần "ORA-20xxx: " ở đầu
        String userMessage = e.getMessage().replaceAll("ORA-\\d+: ", "").trim();
        throw new RuntimeException(userMessage, e);
    }

    // Lỗi hệ thống Oracle (kết nối, cú pháp SQL, ...) → log ra console, ném lên để xử lý chung
    e.printStackTrace();
    throw new RuntimeException("Lỗi hệ thống, vui lòng thử lại: " + e.getMessage(), e);
}
```

**Quy tắc phối hợp ở tầng Presenter/Service:**
- Khi bắt được `RuntimeException` từ DAO, kiểm tra `e.getMessage()` để hiện `JOptionPane.showMessageDialog(...)` với nội dung thân thiện cho người dùng.
- Không bao giờ hiển thị stack trace hay mã lỗi Oracle thô lên giao diện.

## 4.7. PATTERN TRUYỀN DANH SÁCH (LIST) XUỐNG ORACLE
Khi cần truyền một danh sách đối tượng từ Java xuống Stored Procedure, bắt buộc serialize sang JSON bằng **Gson** và phía Oracle dùng `JSON_TABLE` để parse.

```java
import com.google.gson.Gson;

// Phía Java (tầng DAO): serialize List thành chuỗi JSON
Gson gson = new Gson();
String jsonDanhSach = gson.toJson(danhSachChiTiet); // VD: danhSachChiTiet là List<CTPhieuNhapDTO>

try (Connection con = DBConnect.getConnection();
     CallableStatement cs = con.prepareCall("{CALL PROC_NHAPKHO(?, ?)}")) {

    cs.setInt(1, maNhaCungCap);
    cs.setString(2, jsonDanhSach); // Truyền chuỗi JSON xuống tham số P_JSON_DS
    cs.execute();
}
```

```sql
-- Phía Oracle (Stored Procedure): parse JSON bằng JSON_TABLE
FOR ROW_CT IN (
    SELECT *
    FROM JSON_TABLE(P_JSON_DS, '$[*]'
        COLUMNS (
            MANL       NUMBER        PATH '$.maNL',
            SOLUONG    NUMBER(10, 2) PATH '$.soLuong',
            DONGIA     NUMBER(15, 2) PATH '$.donGia',
            HANSUDUNG  VARCHAR2(20)  PATH '$.hanSuDung'
        )
    )
) LOOP
    INSERT INTO CTPHIEUNHAP (...) VALUES (...);
END LOOP;
```

## 4.8. PATTERN XỬ LÝ BẤT ĐỒNG BỘ (SWINGWORKER)
Mọi thao tác gọi Service/DAO **BẮT BUỘC** phải bọc trong `SwingWorker<T, Void>`. Không được gọi DB trên Event Dispatch Thread (EDT).

```java
// Pattern chuẩn trong Presenter
private void thucHienThanhToan(DonHangDTO donHang) {
    // 1. Tạo SwingWorker bọc lệnh gọi Service
    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() throws Exception {
            // Chạy ngầm trên luồng nền — KHÔNG phải EDT
            donHangService.thanhToanVaThangHang(donHang);
            return null;
        }

        @Override
        protected void done() {
            // Luôn chạy trên EDT sau khi doInBackground() kết thúc
            // 2. Tắt loading overlay
            loadingOverlay.setVisible(false);

            try {
                // 3. Gọi get() để bắt Exception nếu doInBackground() thất bại
                get();

                // 4. Thành công → cập nhật UI
                AlertHelper.showInfo("Thanh toán thành công!");
                // Gọi JasperReports in hóa đơn ở đây

            } catch (ExecutionException ex) {
                // 5. Thất bại → hiện lỗi từ Exception (Message đã là tiếng Việt từ mục 4.6)
                AlertHelper.showError(ex.getCause().getMessage());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    };

    // 6. Hiện loading overlay rồi chạy worker trên luồng nền
    loadingOverlay.setVisible(true);
    worker.execute();
}
```
