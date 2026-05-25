<div align="center">

<!-- Typing SVG Animation -->
<img src="https://readme-typing-svg.demolab.com?font=Outfit&weight=800&size=32&duration=3000&pause=1000&color=D85A30&center=true&vCenter=true&width=650&lines=🥐+H3K+Bakery+Management;Hệ+Thống+Quản+Lý+Tiệm+Bánh;Java+21+%2B+JavaFX+%2B+Oracle+19c" alt="H3K Bakery Animation" />

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-FF6B35?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/JavaFX-21.0.7-4A90E2?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/Oracle_DB-19c%2B-C74634?style=for-the-badge&logo=oracle&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-3.x-AA1B32?style=for-the-badge&logo=apachemaven&logoColor=white" />
</p>

---

[🇻🇳 Tiếng Việt](#-tiếng-việt) • [🇬🇧 English](#-english)

</div>

---

## 🇻🇳 Tiếng Việt

### ✨ Giới thiệu ngắn
**H3K Bakery Management System** là hệ thống quản lý tiệm bánh chuyên nghiệp được thiết kế theo mô hình kiến trúc chuẩn **MVP (Model - View - Presenter)**. Dự án sử dụng **Java 21**, **JavaFX 21** cho giao diện Desktop mượt mà, và kết nối **Oracle Database** bảo mật thông qua Stored Procedures tối ưu hiệu năng.

Hệ thống quản lý toàn diện 4 luồng nghiệp vụ cốt lõi: Bán hàng tại quầy (POS), Điều phối bếp chế biến (KDS), Thủ kho kiểm soát nguyên liệu & thẻ kho, và Quản lý nhân viên & chấm công.

---

### 🚀 Hướng Dẫn Cài Đặt và Khởi Chạy (Dành cho Giảng viên)

Thực hiện theo 4 bước nhanh gọn dưới đây để thiết lập và chạy hệ thống:

#### 1. Chuẩn bị môi trường
*   **Java JDK 21+** (Khuyên dùng [Adoptium Temurin](https://adoptium.net/))
*   **Apache Maven 3.8+**
*   **Oracle Database 19c+** (Hỗ trợ tốt bản Oracle XE)

#### 2. Cấu hình Kết nối CSDL
Tạo file **`application.properties`** tại thư mục:
`src/main/resources/application.properties`

Nhập cấu hình thông tin kết nối database Oracle của bạn:
```properties
db.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
db.username=TÊN_USER_ORACLE
db.password=MẬT_KHẨU_ORACLE
```
> ⚠️ **Quan trọng:** File này nằm trong `.gitignore` để bảo mật, bạn **bắt buộc phải tự tạo bằng tay** thì ứng dụng mới kết nối được dữ liệu.

#### 3. Cài đặt Cương vị CSDL (Chỉ 1 lệnh)
Dự án có sẵn script master cài đặt tự động toàn bộ 63 file schema và dữ liệu mẫu theo đúng thứ tự.

*   **Cách 1 (Dòng lệnh):** Sử dụng **SQL*Plus** hoặc **SQLcl**:
    ```bash
    sql USERNAME/PASSWORD@localhost:1521/XEPDB1 @database/install.sql
    ```
*   **Cách 2 (Giao diện):** Mở **Oracle SQL Developer**, kéo file `database/install.sql` vào và nhấn phím **F5** (chạy script).

> *Script này tự động khởi tạo Bảng -> Ràng buộc -> Function -> Trigger -> Stored Procedure -> View -> Nạp dữ liệu mẫu demo.*

#### 4. Biên dịch và Khởi chạy
Mở Terminal/PowerShell tại thư mục gốc của dự án và chạy các lệnh:

```bash
# Bước A: Dọn dẹp và đóng gói dự án (bỏ qua chạy thử nghiệm để build nhanh hơn)
mvn clean package -DskipTests

# Bước B: Chạy ứng dụng JavaFX
mvn javafx:run
```
*(Nếu terminal Windows hiển thị sai mã hóa font chữ tiếng Việt, vui lòng chạy: `mvn javafx:run -Dfile.encoding=UTF-8`)*

---

### 👤 Tài khoản kiểm thử (Demo Account)
Dữ liệu mẫu cài đặt sẵn tài khoản có đầy đủ quyền quản trị để bạn dễ dàng chấm bài:

*   **Tài khoản:** `khoa`
*   **Mật khẩu:** `0710006`
*   **Vai trò:** **Quản lý (Manager)** - Có toàn quyền truy cập toàn bộ chức năng hệ thống (POS, Kho, Nhân sự, Bếp, Tài chính).
> 💡 *Bạn có thể vào module **"Nhân sự và phân quyền"** để tạo thêm các tài khoản Thu ngân, Thủ kho hoặc Đầu bếp mới tùy ý.*

---

### 🗂 Cấu trúc thư mục mã nguồn
```text
IS-System-Development/
├── database/                    <-- Kịch bản SQL cài đặt CSDL Oracle
│   ├── install.sql              <-- Script tổng chạy 1 lần duy nhất
│   └── [01-06]_.../             <-- Chia chi tiết các đối tượng DB
├── src/main/java/com/bakery/    <-- Source code Java theo chuẩn MVP
│   ├── views/                   <-- Tầng giao diện và Controller FXML
│   ├── presenters/              <-- Tầng điều phối logic
│   ├── services/                <-- Tầng nghiệp vụ xử lý logic
│   └── model/                   <-- Tầng dữ liệu (DAO tương tác DB, DTO POJO)
├── src/main/resources/          <-- Tài nguyên FXML, CSS và hình ảnh
├── pom.xml                      <-- File quản lý thư viện Maven
└── README.md                    <-- Hướng dẫn này
```

---
---

## 🇬🇧 English

### ✨ Short Introduction
**H3K Bakery Management System** is a professional desktop application designed using the standard **MVP (Model - View - Presenter)** architecture. Built with **Java 21**, **JavaFX 21** for a fluid GUI, and connected to **Oracle Database 19c+** through optimized Stored Procedures.

The system handles retail sales (POS), kitchen queue dispatching (KDS), warehouse inventory lot tracking, and staff attendance.

---

### 🚀 Installation Guide

#### 1. Prerequisites
*   **Java JDK 21+** ([Adoptium Temurin](https://adoptium.net/))
*   **Apache Maven 3.8+**
*   **Oracle Database 19c+**

#### 2. Database Connection Configuration
Create a new file **`application.properties`** at:
`src/main/resources/application.properties`

Add your Oracle credentials:
```properties
db.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
db.username=YOUR_ORACLE_USERNAME
db.password=YOUR_ORACLE_PASSWORD
```

#### 3. Run Database Script
*   **Option 1 (CLI):** Execute via **SQL*Plus** or **SQLcl**:
    ```bash
    sql USERNAME/PASSWORD@localhost:1521/XEPDB1 @database/install.sql
    ```
*   **Option 2 (GUI):** Open `database/install.sql` in **Oracle SQL Developer** and press **F5**.

#### 4. Build & Run
Run the following commands in the project root folder:

```bash
mvn clean package -DskipTests
mvn javafx:run
```

---

### 👤 Demo Account
The database seeding includes a manager account with all administrative privileges:

*   **Username:** `khoa`
*   **Password:** `0710006`
*   **Role:** **Manager** (Full access to POS, Warehouse, HR, Kitchen, Financials).

---

<div align="center">

### 🏆 H3K Team
Made with ❤️ by **H3K Team** · *IS-System-Development* · 2026

</div>
