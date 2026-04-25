# Hướng dẫn Cài đặt & Chạy Dự án (La Boulangerie Management System)

## 1. Yêu cầu môi trường (Prerequisites)
- **JDK 25** (Hoặc tối thiểu JDK 21).
- **Maven 3.8+** (Dùng để quản lý thư viện `pom.xml`).
- **Oracle Database 12c+** (Dùng Oracle XE hoặc Enterprise).
- **IDE Khuyến nghị:** IntelliJ IDEA.

## 2. Chuẩn bị Cơ sở dữ liệu (Database Setup)
- Chạy các script tạo bảng (`01_tables`) và insert dữ liệu mẫu. (Đảm bảo bảng `DONDATHANG` có cột `SDTGIAO VARCHAR2(20)`).
- **QUAN TRỌNG:** Phải chạy (compile) toàn bộ các script có trong thư mục `database/03_functions`, `database/04_triggers`, và `database/05_procedures`. Hệ thống đẩy logic tính toán nặng xuống Oracle, nếu bạn quên chạy Procedure, ứng dụng sẽ lỗi.

## 3. Build Project & Tải thư viện
Hệ thống sử dụng các thư viện chính: `javafx-controls`, `javafx-fxml`, `ojdbc8` (cho Oracle), `pdfbox` và `jasperreports` (cho in hóa đơn).
- Mở Terminal/Command Prompt tại thư mục chứa file `pom.xml` và chạy:
```bash
mvn clean compile
```
- Nếu dùng **IntelliJ**, hãy mở tab **Maven** ở cột bên phải -> Chọn biểu tượng **Reload All Maven Projects** để IDE tự động tải thư viện.

## 4. Chạy ứng dụng
- **Main Class khởi chạy:** `com.bakery.main.App`
- Trong IntelliJ: Mở file `src/main/java/com/bakery/main/App.java` -> Click chuột phải chọn **Run 'App.main()'**.
- Nếu chạy bằng lệnh: `mvn javafx:run`

---

## 5. CÁC LỖI THƯỜNG GẶP VÀ CÁCH KHẮC PHỤC (Troubleshooting)

Nếu team bạn cài đặt không thành công hoặc không chạy được, hãy đối chiếu với các trường hợp sau:

### 5.1. Bị báo lỗi đỏ code diện rộng (Không tìm thấy thư viện)
- **Nguyên nhân:** Maven chưa tải được thư viện từ `pom.xml` về máy.
- **Cách xử lý:** 
  - Đảm bảo máy có mạng Internet.
  - Kiểm tra xem máy đã cài Maven chưa.
  - Trong IntelliJ, click chuột phải vào file `pom.xml` -> **Maven** -> **Reload project**.

### 5.2. Lỗi `ORA-06550: line X, column Y: PLS-00201: identifier must be declared` (Lỗi gọi Procedure)
- **Nguyên nhân:** Bạn mới chỉ tạo Bảng (Tables) mà **chưa tạo Function / Procedure / Trigger** dưới Oracle Database.
- **Cách xử lý:** Mở SQL Developer hoặc DataGrip, truy cập vào thư mục `database/`, mở lần lượt các file trong `03_functions`, `04_triggers`, `05_procedures` và chạy toàn bộ mã PL/SQL.

### 5.3. Lỗi "JavaFX runtime components are missing" hoặc "Unsupported Class Version Error"
- **Nguyên nhân:** Phiên bản JDK máy bạn đang sử dụng thấp hơn phiên bản được định nghĩa trong `pom.xml` (Yêu cầu Java 21 hoặc 25).
- **Cách xử lý:** 
  - Trong IntelliJ, vào **File -> Project Structure -> Project**.
  - Sửa `SDK` và `Language level` thành Java 21 hoặc 25.

### 5.4. Lỗi "OJDBC Driver not found" hoặc không kết nối được Database
- **Nguyên nhân:** Thiếu thư viện `ojdbc8` hoặc Oracle Service chưa được bật.
- **Cách xử lý:**
  - Mở `services.msc` của Windows, tìm và **Start** các dịch vụ có tên `OracleServiceXE` và `Oracle...TNSListener`.
  - Cập nhật lại username và password Oracle trong file code chứa cấu hình (thường là `DBConnect`).

### 5.5. Lỗi `NullPointerException` (Không tìm thấy giao diện)
- **Nguyên nhân:** IDE quên không copy file `.fxml` và `.css` từ thư mục `src/main/resources/` sang thư mục build thực thi.
- **Cách xử lý:** Mở tab Maven -> Chạy chuỗi lệnh `clean` sau đó chạy `compile` lại để ép hệ thống đóng gói các file giao diện vào thư mục `target`.
