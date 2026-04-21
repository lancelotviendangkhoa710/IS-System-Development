# Hướng dẫn cài đặt và chạy chức năng Bán hàng (Order Module)

Dưới đây là hướng dẫn chi tiết về cấu hình, điều kiện môi trường và cách để build cũng như chạy được module Bán hàng (Order) này:

## 1. Điều kiện môi trường (Prerequisites)
Để hệ thống có thể biên dịch và chạy đúng, máy tính của bạn cần được cài đặt các công cụ sau:
*   **Java Development Kit (JDK):** Yêu cầu bắt buộc là **JDK 21 trở lên** (project hiện tại đang config `source` và `target` là Java 25 trong `pom.xml`, nên tốt nhất là cài JDK 25).
*   **Maven:** Công cụ quản lý thư viện và build project (khuyên dùng bản 3.8+).
*   **Database:** Oracle Database 12c trở lên. (Do các procedure thao tác dữ liệu được thiết kế riêng cho Oracle PL/SQL).
*   **IDE:** IntelliJ IDEA (khuyên dùng) hoặc Eclipse, VS Code có cài đặt Java Extension Pack.

## 2. Cấu hình Dependencies & Module
Hệ thống sử dụng **Java Module System** (`module-info.java`), do đó cấu hình thư viện cần phải khớp cả trong Maven lẫn Module.

**Các thư viện chính (`pom.xml`):**
*   `flatlaf` & `flatlaf-extras` (version 3.6): Framework giao diện để tạo UI hiện đại, bo góc chuẩn FlatLaf.
*   `ojdbc8`: Driver kết nối Oracle Database.
*   `pdfbox` / `jasperreports`: Xử lý in ấn hóa đơn nhiệt.
*   `javafx` (controls, fxml, graphics): Nếu dự án có dùng song song JavaFX ở một vài view khác.

**Cấu hình Module (`module-info.java`):**
```java
module com.bakery {
    requires java.desktop;          // Bắt buộc cho Java Swing (OrderViewPanel)
    requires java.sql;              // Kết nối JDBC
    requires com.formdev.flatlaf;   // Giao diện FlatLaf
    requires com.formdev.flatlaf.extras;

    opens com.bakery.views to java.desktop; // Mở module để Swing load được các icon/resource
    exports com.bakery.main;
}
```

## 3. Hướng dẫn Build (Biên dịch)
**Cách 1: Sử dụng Terminal / Command Prompt**
Mở terminal tại thư mục gốc của project (chứa file `pom.xml`), chạy lệnh:
```bash
mvn clean compile
```

**Cách 2: Sử dụng IntelliJ IDEA**
1. Mở project trong IntelliJ.
2. Đợi IDE tải xong các thư viện (Maven Sync).
3. Bấm vào icon chiếc búa **Build Project** (Ctrl + F9).

## 4. Hướng dẫn Chạy (Run) ứng dụng thử nghiệm (Smoke Test)
Để kiểm tra riêng module Giao diện Bán hàng (OrderViewPanel) mà không cần chạy toàn bộ hệ thống lớn, bạn có thể chạy file Test App.

**Bước 1:** Mở file `src/main/java/com/bakery/main/OrderUiSmokeTestApp.java`.
**Bước 2:** Chạy file này.
*   **Trong IntelliJ:** Click chuột phải vào file -> chọn **Run 'OrderUiSmokeTestApp.main()'**.

> **⚠️ Lỗi phổ biến & Cách khắc phục:**
> 
> **Lỗi 1: `java.lang.module.FindException: Module com.bakery not found`**
> *   **Nguyên nhân:** Lỗi này xảy ra khi IDE cố gắng chạy file dưới dạng Java Module (`-m com.bakery/com.bakery...`) nhưng thư mục `target/classes` chưa được build đúng cấu trúc module.
> *   **Khắc phục (IntelliJ):** 
>     1. Chọn Run -> Edit Configurations...
>     2. Tại cấu hình `OrderUiSmokeTestApp`, tìm phần **Modify options**.
>     3. Chọn **Disable "Use --module-path"** (hoặc chọn classpath mode thay vì module mode).
>     4. Chạy lại.
> 
> **Lỗi 2: Không load được ảnh bánh (`No Image`)**
> *   Đảm bảo các file ảnh (ví dụ: `cake1.jpg`, `cake_vani_16.png`) đã được copy vào đúng thư mục `src/main/resources/images/products/` hoặc `src/main/resources/`.

## 5. Kiến trúc luồng chạy của màn hình này
Khi bạn chạy `OrderUiSmokeTestApp`, kiến trúc **MVP (Model - View - Presenter)** sẽ được kích hoạt như sau:
1. `OrderViewPanel` (View) được khởi tạo để vẽ giao diện Swing.
2. `OrderService` (Model/Service) được khởi tạo để chứa logic tính toán và gọi DB.
3. `OrderPresenter` được khởi tạo, làm cầu nối ghép View và Service lại với nhau.
4. Lệnh `presenter.taiDuLieuBanDau()` được gọi để load danh sách sản phẩm mẫu và giỏ hàng trống lên màn hình.
