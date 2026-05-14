<div align="center">

# 🥐 H3K Bakery Management System

**A full-featured bakery management desktop application**  
*Hệ thống quản lý tiệm bánh toàn diện*

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.7-blue?logo=java)](https://openjfx.io/)
[![Oracle](https://img.shields.io/badge/Oracle-Database-red?logo=oracle)](https://www.oracle.com/database/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Academic-green)](#)

[🇬🇧 English](#-english) · [🇻🇳 Tiếng Việt](#-tiếng-việt)

</div>

---

## 🇬🇧 English

### 📖 Overview

H3K Bakery Management System is a desktop application built with **Java 21 + JavaFX** and **Oracle Database**, designed to manage all core operations of a modern bakery business:

| Module | Features |
|---|---|
| 👥 **Human Resources** | Staff management, role-based access control, shift tracking |
| 🛒 **Sales (POS)** | Retail orders, custom cake orders, invoices |
| 🧁 **Kitchen** | Production planning, KDS (Kitchen Display System) |
| 📦 **Inventory** | Raw materials, stock import/export, supplier management |
| 👤 **Customers** | Loyalty points, membership tiers, purchase history |
| 💰 **Finance** | Cash flow ledger, revenue & profit reports |
| 📊 **Reports** | Revenue trends, inventory status, shift reconciliation |

### 🏗 Architecture

```
MVP Pattern (Model – View – Presenter)
├── Model   → DTO + DAO + Service layers
├── View    → JavaFX FXML screens
└── Presenter → Business logic bridge
```

### ⚙️ Prerequisites

| Tool | Version | Download |
|---|---|---|
| JDK | 21+ | [Adoptium](https://adoptium.net/) |
| Maven | 3.8+ | [maven.apache.org](https://maven.apache.org/download.cgi) |
| Oracle Database | 19c+ | [oracle.com](https://www.oracle.com/database/technologies/xe-downloads.html) |

### 🚀 Setup Guide (for Instructors)

#### Step 1 — Clone the repository
```bash
git clone https://github.com/lancelotviendangkhoa710/IS-System-Development.git
cd IS-System-Development
```

#### Step 2 — Configure database connection

Create the file `src/main/resources/application.properties`:
```properties
db.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
db.username=YOUR_ORACLE_USERNAME
db.password=YOUR_ORACLE_PASSWORD
```
> ⚠️ This file is excluded from git for security. Create it manually.

#### Step 3 — Install database (one command)

Connect to Oracle using **SQL\*Plus** or **SQLcl**, then run the master script:

```bash
# SQL*Plus
sqlplus USERNAME/PASSWORD@localhost:1521/XEPDB1 @database/install.sql

# SQLcl (recommended)
sql USERNAME/PASSWORD@localhost:1521/XEPDB1 @database/install.sql
```

> This single script installs all **63 SQL files** in the correct order:  
> Tables → Constraints → Functions → Triggers → Procedures → Views → Sample Data

Alternatively, use **Oracle SQL Developer**:
1. Open SQL Developer → Connect to your schema
2. Open `database/install.sql`
3. Press **F5** to run as script

#### Step 4 — Build and run

```bash
# Build
mvn clean package -DskipTests

# Run
mvn javafx:run
```

### 👤 Default Login Accounts

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | *(see sample data)* |
| Manager | `quanly01` | *(see sample data)* |
| Cashier | `thungan01` | *(see sample data)* |
| Warehouse | `thukho01` | *(see sample data)* |
| Chef | `thobep01` | *(see sample data)* |

> Full credentials are inserted by `database/config/script_insert_data.sql`

### 📁 Project Structure

```
IS-System-Development/
├── src/main/java/com/bakery/
│   ├── main/           # App entry point
│   ├── model/          # DTO, DAO
│   ├── presenters/     # Business logic
│   ├── services/       # Service layer
│   └── views/          # JavaFX controllers
├── src/main/resources/
│   ├── fxml/           # UI layouts
│   └── css/            # Styling
├── database/
│   ├── install.sql     # ← Master install (run this)
│   ├── 01_tables/
│   ├── 02_constraints/
│   ├── 03_functions/
│   ├── 04_triggers/
│   ├── 05_procedures/
│   ├── 06_views/
│   └── config/         # Sample data
└── pom.xml
```

---

## 🇻🇳 Tiếng Việt

### 📖 Giới thiệu

**H3K Bakery Management System** là ứng dụng desktop quản lý tiệm bánh xây dựng bằng **Java 21 + JavaFX** và **Oracle Database**, phục vụ toàn bộ nghiệp vụ của một tiệm bánh hiện đại:

| Module | Chức năng |
|---|---|
| 👥 **Nhân sự** | Quản lý nhân viên, phân quyền theo vai trò, chấm công theo ca |
| 🛒 **Bán hàng (POS)** | Đơn hàng bán lẻ, đặt bánh tùy chỉnh, hóa đơn |
| 🧁 **Bếp** | Kế hoạch sản xuất, màn hình bếp (KDS) |
| 📦 **Kho** | Nguyên liệu, nhập/xuất kho, nhà cung cấp, kiểm kê |
| 👤 **Khách hàng** | Tích điểm, hạng thành viên, lịch sử mua hàng |
| 💰 **Tài chính** | Sổ quỹ thu chi, báo cáo doanh thu & lợi nhuận |
| 📊 **Báo cáo** | Xu hướng doanh thu, tồn kho, đối soát ca làm việc |

### ⚙️ Yêu cầu hệ thống

| Công cụ | Phiên bản | Tải về |
|---|---|---|
| JDK | 21+ | [Adoptium](https://adoptium.net/) |
| Maven | 3.8+ | [maven.apache.org](https://maven.apache.org/download.cgi) |
| Oracle Database | 19c+ (XE được hỗ trợ) | [oracle.com](https://www.oracle.com/database/technologies/xe-downloads.html) |

### 🚀 Hướng dẫn cài đặt (dành cho Giảng viên)

#### Bước 1 — Tải mã nguồn

```bash
git clone https://github.com/lancelotviendangkhoa710/IS-System-Development.git
cd IS-System-Development
```

#### Bước 2 — Cấu hình kết nối database

Tạo file `src/main/resources/application.properties`:
```properties
db.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
db.username=TÊN_USER_ORACLE
db.password=MẬT_KHẨU_ORACLE
```
> ⚠️ File này bị loại khỏi git vì lý do bảo mật. Vui lòng tạo thủ công.

#### Bước 3 — Cài đặt cơ sở dữ liệu (chỉ 1 lệnh)

Kết nối Oracle bằng **SQL\*Plus** hoặc **SQLcl**, sau đó chạy master script:

```bash
# Dùng SQL*Plus
sqlplus USERNAME/PASSWORD@localhost:1521/XEPDB1 @database/install.sql

# Dùng SQLcl (khuyến nghị)
sql USERNAME/PASSWORD@localhost:1521/XEPDB1 @database/install.sql
```

> Script này sẽ tự động cài đặt toàn bộ **63 file SQL** theo đúng thứ tự:  
> Bảng → Ràng buộc → Hàm → Trigger → Procedure → View → Dữ liệu mẫu

Hoặc dùng **Oracle SQL Developer** (giao diện đồ họa):
1. Kết nối tới schema Oracle của bạn
2. Mở file `database/install.sql`
3. Nhấn **F5** để chạy toàn bộ script

#### Bước 4 — Build và chạy ứng dụng

```bash
# Build
mvn clean package -DskipTests

# Chạy ứng dụng
mvn javafx:run
```

> Nếu gặp lỗi encoding trên Windows, thêm flag:  
> `mvn javafx:run -Dfile.encoding=UTF-8`

### 👤 Tài khoản đăng nhập mẫu

| Vai trò | Tài khoản | Mật khẩu |
|---|---|---|
| Admin | `admin` | *(xem file dữ liệu mẫu)* |
| Quản lý | `quanly01` | *(xem file dữ liệu mẫu)* |
| Thu ngân | `thungan01` | *(xem file dữ liệu mẫu)* |
| Thủ kho | `thukho01` | *(xem file dữ liệu mẫu)* |
| Thợ bếp | `thobep01` | *(xem file dữ liệu mẫu)* |

> Thông tin đầy đủ được tạo bởi `database/config/script_insert_data.sql`

### 🗂 Cấu trúc thư mục Database

```
database/
├── install.sql          ← CHẠY FILE NÀY để cài đặt toàn bộ
├── 01_tables/           Tạo bảng dữ liệu
├── 02_constraints/      Ràng buộc & Package lỗi
├── 03_functions/        Hàm nghiệp vụ
├── 04_triggers/         Trigger tự động
├── 05_procedures/       Stored Procedures
│   └── cud/             Thao tác Create/Update/Delete
├── 06_views/            View tổng hợp
└── config/
    └── script_insert_data.sql   Dữ liệu mẫu demo
```

### 🛠 Công nghệ sử dụng

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java 21 |
| UI Framework | JavaFX 21.0.7 + FXML |
| Database | Oracle Database 19c+ |
| Build Tool | Apache Maven 3.x |
| JDBC Driver | ojdbc8 21.9 |
| Báo cáo | JasperReports 6.21 |
| Excel Export | Apache POI 5.3 |
| Bảo mật | jBCrypt + JWT |

---

<div align="center">

Made with ❤️ by **H3K Team** — *IS-System-Development*

</div>
