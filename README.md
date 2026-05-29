<div align="center">

<!-- Typing SVG Animation -->
<img src="https://readme-typing-svg.demolab.com?font=Outfit&weight=800&size=32&duration=3000&pause=1000&color=D85A30&center=true&vCenter=true&width=650&lines=%F0%9F%A5%90+H3K+Bakery+Management;H%E1%BB%87+Th%E1%BB%91ng+Qu%E1%BA%A3n+L%C3%BD+Ti%E1%BB%87m+B%C3%A1nh;Java+21+%2B+JavaFX+25+%2B+Oracle+12c;%F0%9F%9A%80+H%C6%B0%E1%BB%9Bng+D%E1%BA%ABn+C%C3%A0i+%C4%90%E1%BA%B7t+Chi+Ti%E1%BA%BFt" alt="H3K Bakery Animation" />

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-FF6B35?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/JavaFX-25-4A90E2?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/Oracle_DB-12c%2B-C74634?style=for-the-badge&logo=oracle&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-3.x-AA1B32?style=for-the-badge&logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/Ki%E1%BA%BFn%20Tr%C3%BAc-MVP-green?style=for-the-badge" />
</p>

---

*Hệ thống quản lý tiệm bánh ngọt chuyên nghiệp và toàn diện nhất.*

</div>

---

## 🥐 ✨ Giới Thiệu Chung

**H3K Bakery Management System** là giải pháp phần mềm Desktop chuyên nghiệp, phục vụ toàn diện hoạt động vận hành và kinh doanh của cửa hàng/chuỗi cửa hàng bánh ngọt. Dự án được phát triển tuân thủ nghiêm ngặt mô hình kiến trúc **MVP (Model - View - Presenter)**, mang lại khả năng phân tách trách nhiệm tối đa, vận hành mượt mà và dễ dàng bảo trì mở rộng.

*   **View (Giao diện):** Thiết kế hiện đại bằng JavaFX (FXML/CSS) sử dụng bảng màu **Amber** sang trọng.
*   **Presenter (Điều phối):** Xử lý luồng dữ liệu bất đồng bộ qua đa luồng (`Task`/`Service` JavaFX) giúp tránh treo đơ màn hình khi tương tác với CSDL nặng.
*   **Service (Nghiệp vụ):** Nơi chứa toàn bộ logic kiểm tra tính đúng đắn và quy tắc kinh doanh.
*   **DAO (Data Access Object):** Thực hiện kết nối dữ liệu an toàn thông qua Oracle Stored Procedures nhằm đảm bảo hiệu năng và tránh lỗi SQL Injection.

---

## 🚀 🛠️ Hướng Dẫn Cài Đặt & Khởi Chạy (Từng bước chi tiết)

Hệ thống được thiết kế tối ưu để cài đặt cực nhanh chỉ trong vài phút. Vui lòng thực hiện theo quy trình 4 bước dưới đây để thiết lập môi trường và khởi động ứng dụng:

```
[MÔI TRƯỜNG] ────► [CƠ SỞ DỮ LIỆU] ────► [CẤU HÌNH KẾT NỐI] ────► [KHỞI CHẠY]
(Java 21, Maven)   (Run install.sql)     (application.properties)  (mvn javafx:run)
```

### 1️⃣ Bước 1: Chuẩn bị môi trường lập trình
Đảm bảo máy tính của bạn đã được cài đặt sẵn các công cụ sau:
*   **Java JDK 21 hoặc cao hơn** (Khuyên dùng [Adoptium Temurin OpenJDK 21](https://adoptium.net/))
*   **Apache Maven 3.8 hoặc cao hơn**
*   **Hệ quản trị CSDL Oracle Database** (Hỗ trợ tốt nhất từ bản Oracle XE 11g, 12c, 19c đến 21c)

---

### 2️⃣ Bước 2: Thiết lập Cơ sở dữ liệu (Oracle DB)
Dự án đã tích hợp sẵn tập lệnh cài đặt tự động toàn bộ cấu trúc bảng, dữ liệu mẫu, hàm, trigger và thủ tục lưu trữ theo đúng trình tự liên kết.

*   **Cách 1: Sử dụng giao diện đồ họa (Khuyên dùng SQL Developer)**
    1. Mở công cụ **Oracle SQL Developer** và kết nối tới Database của bạn.
    2. Kéo thả file `database/install.sql` (nằm ở thư mục gốc của dự án) vào vùng làm việc.
    3. Nhấn phím **F5** (hoặc chọn biểu tượng *Run Script*) để tiến hành cài đặt tự động.
*   **Cách 2: Sử dụng dòng lệnh (Command Line - SQL*Plus / SQLcl)**
    ```bash
    sql USERNAME/PASSWORD@localhost:1521/XEPDB1 @database/install.sql
    ```
> ℹ️ *Tập lệnh `install.sql` sẽ tự động thực hiện: Khởi tạo bảng dữ liệu $\rightarrow$ Áp dụng ràng buộc $\rightarrow$ Tạo các Functions $\rightarrow$ Cài đặt Triggers $\rightarrow$ Biên dịch Stored Procedures $\rightarrow$ Tạo Views báo cáo $\rightarrow$ Đổ dữ liệu mẫu (Seeding).*

---

### 3️⃣ Bước 3: Cấu hình kết nối dữ liệu cho Ứng dụng
Tạo tệp cấu hình kết nối database cục bộ cho dự án:

1. Tạo mới một tệp tin có tên là **`application.properties`** tại đường dẫn chính xác sau:
   `src/main/resources/application.properties`
2. Nhập các thông tin kết nối CSDL Oracle của bạn vào tệp tin vừa tạo:
   ```properties
   db.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
   db.username=TÊN_TÀI_KHOẢN_ORACLE
   db.password=MẬT_KHẨU_TÀI_KHOẢN_ORACLE
   ```
> ⚠️ **LƯU Ý QUAN TRỌNG:** Tệp tin `application.properties` này đã được cấu hình ẩn trong `.gitignore` để tránh bị lộ mật khẩu cá nhân lên GitHub. Bạn **bắt buộc** phải tự tay tạo tệp tin này trước khi chạy phần mềm.

---

### 4️⃣ Bước 4: Biên dịch và Khởi chạy ứng dụng
Mở cửa sổ dòng lệnh (Terminal / Command Prompt / PowerShell) tại thư mục gốc của dự án và chạy lần lượt các lệnh sau:

*   **Biên dịch và đóng gói mã nguồn (Bỏ qua chạy test để build siêu tốc):**
    ```bash
    mvn clean package -DskipTests
    ```
*   **Khởi chạy giao diện ứng dụng JavaFX:**
    ```bash
    mvn javafx:run
    ```
*   💡 **Mẹo sửa lỗi hiển thị tiếng Việt trên Terminal Windows:** Nếu chạy trên Command Prompt của Windows bị lỗi mã hóa chữ tiếng Việt, hãy thêm thuộc tính mã hóa UTF-8 khi chạy:
    ```bash
    mvn javafx:run -Dfile.encoding=UTF-8
    ```

---

## 🔑 👤 Tài Khoản Trình Diễn (Demo Accounts)

Hệ thống đã nạp sẵn bộ dữ liệu giả lập mẫu với đầy đủ các phân quyền vai trò để thuận tiện nhất cho việc chấm bài và kiểm thử toàn bộ chức năng:

| Tài khoản Đăng nhập | Mật khẩu | Quyền truy cập | Phạm vi chức năng được sử dụng |
| :--- | :--- | :--- | :--- |
| **`khoa`** | `0710006` | **Quản lý (Manager)** | **Toàn quyền hệ thống** (POS, Kho, Nhân sự, Tài chính, Bếp) |
| **`thu_ngan`** | `123456` | **Thu ngân (Cashier)** | Lập hóa đơn bán lẻ tại quầy, quản lý đơn hàng |
| **`thu_kho`** | `123456` | **Thủ kho (Warehouse)** | Nhập/Xuất nguyên liệu, Thẻ kho, Kiểm kho, Nhà cung cấp |
| **`dau_bep`** | `123456` | **Đầu bếp (Chef/KDS)** | Tiếp nhận yêu cầu làm bánh tại bếp, cập nhật trạng thái chế biến |

---

## 🗂️ 📂 Cấu Trúc Tổ Chức Thư Mục

Cấu trúc thư mục được tổ chức theo chuẩn kiến trúc sạch, hỗ trợ phát triển theo mô hình MVP:

```text
IS-System-Development/
├── database/                    <-- Tập lệnh cài đặt CSDL Oracle 12c+
│   ├── install.sql              <-- Script tổng cài đặt tự động toàn bộ CSDL
│   ├── 01_tables/               <-- Khởi tạo cấu trúc bảng dữ liệu
│   ├── 02_constraints/          <-- Định nghĩa khóa ngoại, ràng buộc check
│   ├── 03_functions/            <-- Các hàm tính toán số liệu
│   ├── 04_triggers/             <-- Tự động hóa cập nhật logic ngầm (Kho, chấm công)
│   ├── 05_procedures/           <-- Thủ tục nghiệp vụ CUD bảo mật
│   └── 06_views/                <-- Views kết xuất dữ liệu thống kê báo cáo
├── src/main/java/com/bakery/    <-- Mã nguồn Java chính của ứng dụng
│   ├── main/                    <-- Lớp chạy ứng dụng (App.java)
│   ├── model/                   <-- Tầng lưu trữ dữ liệu (DAO kết nối CSDL, DTO thuần)
│   ├── presenters/              <-- Tầng Presenter điều hướng & trung chuyển
│   ├── services/                <-- Tầng nghiệp vụ xử lý logic độc lập
│   ├── utils/                   <-- Tiện ích dùng chung (Kết nối DB, tạo QR, mã hóa)
│   └── views/                   <-- Tầng View (Controllers của FXML và các Interfaces)
├── src/main/resources/          <-- Nơi chứa FXML, CSS và hình ảnh tài nguyên
├── pom.xml                      <-- Quản lý dependencies thư viện của Maven
└── README.md                    <-- Tài liệu hướng dẫn sử dụng này
```

---

## 🛡️ 🛠️ Các Lỗi Thường Gặp & Cách Khắc Phục (Troubleshooting)

*   **Lỗi `Connection Refused` hoặc không tìm thấy CSDL:**
    *   *Khắc phục:* Kiểm tra xem dịch vụ Oracle Service (ví dụ: `OracleServiceXE` hoặc listener `OracleOraDB19Home1TNSListener`) trên máy của bạn đã được bật (Running) chưa. Kiểm tra lại cổng `1521` và tên cổng SID/Service Name (thông thường là `XEPDB1` hoặc `XE`) trong tệp cấu hình `application.properties`.
*   **Lỗi giao diện bị lỗi font chữ hoặc vỡ bố cục:**
    *   *Khắc phục:* Hệ thống yêu cầu JDK 21+ để hỗ trợ render hoàn hảo các CSS Class Amber hiện đại. Hãy đảm bảo bạn không vô tình chạy bằng JDK 8 hoặc JDK 11 cũ.

---

<div align="center">

### 🏆 Đội Ngũ Phát Triển H3K
Được thực hiện với tất cả tâm huyết và sự chỉn chu bởi **H3K Team**
*Dự án thuộc Học phần Phát triển Hệ thống Thông tin — Năm học 2026*

</div>
