<div align="center">

<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=700&size=28&duration=3000&pause=1000&color=D85A30&center=true&vCenter=true&width=600&lines=🥐+H3K+Bakery+Management;Hệ+thống+quản+lý+tiệm+bánh;Java+21+%2B+JavaFX+%2B+Oracle" alt="Typing SVG" />

<br/>

<p>
  <img src="https://img.shields.io/badge/Java-21-FF6B35?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/JavaFX-21.0.7-4A90E2?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/Oracle-Database-C74634?style=for-the-badge&logo=oracle&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-3.x-AA1B32?style=for-the-badge&logo=apachemaven&logoColor=white" />
  <img src="https://img.shields.io/badge/License-Academic-27AE60?style=for-the-badge" />
</p>

<p>
  <img src="https://img.shields.io/github/last-commit/lancelotviendangkhoa710/IS-System-Development?style=flat-square&color=D85A30" />
  <img src="https://img.shields.io/github/repo-size/lancelotviendangkhoa710/IS-System-Development?style=flat-square&color=185FA5" />
  <img src="https://img.shields.io/github/languages/top/lancelotviendangkhoa710/IS-System-Development?style=flat-square&color=F1C40F" />
</p>

---

> 🥐 **H3K Bakery Management System** là hệ thống phần mềm quản lý tiệm bánh toàn diện,  
> được phát triển bởi nhóm **H3K** trong khuôn khổ môn học *Phát triển Hệ thống Thông tin*.  
> Ứng dụng desktop sử dụng **Java 21 + JavaFX 21** làm giao diện, kết nối với **Oracle Database**  
> theo kiến trúc phân tầng **MVP (Model – View – Presenter)**.

[🇬🇧 English](#-english) · [🇻🇳 Tiếng Việt](#-tiếng-việt)

</div>

---

## 🇬🇧 English

### ✨ What is H3K Bakery?

H3K Bakery Management System is a **production-grade desktop application** built to handle every operational aspect of a modern bakery business. Whether it's ringing up customers at the POS counter, tracking raw material stock in the warehouse, or managing chef production queues in the kitchen — the system ties everything together in one unified platform.

The application supports **4 distinct roles** (Manager, Cashier, Warehouse Staff, Chef), each with tailored views and permissions. Data flows through a strict **MVP architecture**: Views only handle UI events, Presenters orchestrate business logic, Services apply domain rules, and DAOs communicate exclusively with Oracle through Stored Procedures.

---

### 🗂 Module Breakdown

<table>
<tr>
<td width="50%">

#### 🛒 Sales — POS Module
- Process retail orders in real time with a product grid
- Create **custom cake orders** with size, flavor, and decoration options
- Auto-calculate totals, apply loyalty discounts, and generate PDF invoices
- Support cash and split-payment transactions

#### 👥 Human Resources
- Manage full employee profiles (name, role, department, salary grade)
- Role-based access control — each employee sees only their module
- Shift scheduling and **attendance tracking** (check-in/check-out)
- Salary calculation based on working hours and position grade

</td>
<td width="50%">

#### 🧁 Kitchen — KDS (Kitchen Display System)
- Receive orders from the POS in real time
- Display production queues on a dedicated kitchen screen
- Chefs update order status (Pending → In Progress → Done)
- Link each order to a **production recipe (BOM)** for material tracking

#### 📦 Inventory & Warehouse
- Track all raw materials with current stock levels
- Record **stock imports** from suppliers with lot traceability
- Auto-deduct materials when kitchen marks orders complete
- Alert when stock falls below minimum threshold

</td>
</tr>
<tr>
<td width="50%">

#### 👤 Customer Management
- Maintain a customer database with purchase history
- **Loyalty point** accumulation on every transaction
- Membership tiers (Bronze → Silver → Gold) with tier-based discounts
- View per-customer spending analytics

</td>
<td width="50%">

#### 💰 Finance & Reporting
- Cash flow ledger: record income and expense entries per shift
- Generate **revenue & profit reports** by day, week, or month
- Export reports to **PDF** (JasperReports) or **Excel** (Apache POI)
- Shift reconciliation: cash-in vs. system totals at shift close

</td>
</tr>
</table>

---

### 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                   MVP Pattern Architecture                  │
├────────────────┬────────────────────┬───────────────────────┤
│   View Layer   │  Presenter Layer   │    Model Layer        │
│  (JavaFX /     │  (Business Logic   │  DTO  · DAO ·         │
│   FXML)        │   Orchestrator)    │  Service · Oracle DB  │
├────────────────┴────────────────────┴───────────────────────┤
│  🔐 Security: JWT session tokens + jBCrypt password hashing  │
│  🔄 Concurrency: Oracle FOR UPDATE locks + setAutoCommit(F) │
│  📡 Threading: JavaFX Task/Service for heavy operations      │
└─────────────────────────────────────────────────────────────┘
```

### ⚙️ Prerequisites

| Tool | Version | Download |
|---|---|---|
| JDK | 21+ | [Adoptium Temurin](https://adoptium.net/) |
| Maven | 3.8+ | [maven.apache.org](https://maven.apache.org/download.cgi) |
| Oracle Database | 19c+ (XE supported) | [oracle.com](https://www.oracle.com/database/technologies/xe-downloads.html) |

### 🚀 Quick Start

#### Step 1 — Clone

```bash
git clone https://github.com/lancelotviendangkhoa710/IS-System-Development.git
cd IS-System-Development
```

#### Step 2 — Configure database

Create `src/main/resources/application.properties`:

```properties
db.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
db.username=YOUR_ORACLE_USERNAME
db.password=YOUR_ORACLE_PASSWORD
```

> ⚠️ This file is gitignored for security. Create it manually.

#### Step 3 — Install database (single command)

```bash
# SQL*Plus
sqlplus USERNAME/PASSWORD@localhost:1521/XEPDB1 @database/install.sql

# SQLcl (recommended)
sql USERNAME/PASSWORD@localhost:1521/XEPDB1 @database/install.sql
```

> Installs all **63 SQL files** in the correct order:  
> Tables → Constraints → Functions → Triggers → Procedures → Views → Sample Data

Or via **Oracle SQL Developer**: open `database/install.sql` → press **F5**.

#### Step 4 — Build & Run

```bash
mvn clean package -DskipTests
mvn javafx:run
```

### 👤 Default Login Accounts

> Credentials are seeded by `database/config/script_insert_data.sql`

| Role | Username | Password |
|---|---|---|
| 🧑‍💼 Manager | `quanly01` | *(see sample data)* |
| 💵 Cashier | `thungan01` | *(see sample data)* |
| 📦 Warehouse | `thukho01` | *(see sample data)* |
| 👨‍🍳 Chef | `thobep01` | *(see sample data)* |

### 📁 Project Structure

```
IS-System-Development/
├── src/main/java/com/bakery/
│   ├── main/           # App entry point
│   ├── model/          # DTO, DAO
│   ├── presenters/     # Business logic bridge
│   ├── services/       # Service layer
│   └── views/          # JavaFX FXML controllers
├── src/main/resources/
│   ├── fxml/           # UI layouts
│   └── css/            # Styling (bakery.css)
├── database/
│   ├── install.sql     ← Master install script
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

### ✨ Giới thiệu tổng quan

**H3K Bakery Management System** là ứng dụng desktop quản lý tiệm bánh toàn diện, được xây dựng bằng **Java 21 + JavaFX** và **Oracle Database**. Dự án được phát triển trong khuôn khổ môn học *Phát triển Hệ thống Thông tin*, nhằm giải quyết bài toán quản lý nghiệp vụ thực tế của một tiệm bánh quy mô vừa.

Hệ thống hỗ trợ **4 vai trò người dùng** với giao diện và quyền hạn riêng biệt. Toàn bộ logic nghiệp vụ được tổ chức theo kiến trúc **MVP (Model – View – Presenter)**: View chỉ xử lý sự kiện UI, Presenter điều phối logic, Service thực thi nghiệp vụ, DAO tương tác Oracle qua Stored Procedure — đảm bảo tách biệt rõ ràng giữa các tầng.

---

### 🗂 Chi tiết từng module

<table>
<tr>
<td width="50%">

#### 🛒 Bán hàng — POS
- Xử lý đơn hàng bán lẻ trực tiếp tại quầy với giao diện lưới sản phẩm
- Tạo **đơn bánh tùy chỉnh** (size, hương vị, trang trí) theo yêu cầu khách
- Tự động tính tổng tiền, áp dụng chiết khấu thành viên, xuất hóa đơn PDF
- Hỗ trợ thanh toán tiền mặt, tính tiền thối tự động

#### 👥 Nhân sự & Chấm công
- Quản lý hồ sơ nhân viên: họ tên, vai trò, phòng ban, bậc lương
- Phân quyền theo vai trò — mỗi nhân viên chỉ thấy module của mình
- Lên ca làm việc và **chấm công** (giờ vào/ra theo ca)
- Tính lương dựa trên số giờ làm và bậc lương

</td>
<td width="50%">

#### 🧁 Bếp — KDS (Kitchen Display System)
- Nhận đơn hàng từ POS theo thời gian thực
- Hiển thị hàng đợi sản xuất trên màn hình bếp riêng biệt
- Thợ bếp cập nhật trạng thái đơn: Chờ → Đang làm → Hoàn thành
- Liên kết mỗi đơn với **công thức (BOM)** để trừ nguyên liệu tự động

#### 📦 Kho & Nguyên liệu
- Theo dõi tồn kho tất cả nguyên liệu với số lượng hiện tại
- Ghi nhận **phiếu nhập kho** từ nhà cung cấp (có truy xuất lô hàng)
- Tự động trừ nguyên liệu khi bếp hoàn thành đơn sản xuất
- Cảnh báo khi tồn kho dưới mức tối thiểu

</td>
</tr>
<tr>
<td width="50%">

#### 👤 Khách hàng & Thành viên
- Quản lý cơ sở dữ liệu khách hàng và lịch sử mua hàng
- **Tích điểm** sau mỗi giao dịch, dùng điểm đổi ưu đãi
- Hệ thống hạng thành viên (Đồng → Bạc → Vàng) kèm chiết khấu theo hạng
- Xem thống kê chi tiêu theo từng khách hàng

</td>
<td width="50%">

#### 💰 Tài chính & Báo cáo
- Sổ quỹ: ghi nhận thu/chi theo từng ca làm việc
- Báo cáo **doanh thu & lợi nhuận** theo ngày, tuần, tháng
- Xuất báo cáo ra **PDF** (JasperReports) hoặc **Excel** (Apache POI)
- Đối soát ca: so sánh tiền mặt thực tế với tổng hệ thống khi đóng ca

</td>
</tr>
</table>

---

### 🎨 Giao diện ứng dụng

```
🎨 Design System — Amber Palette
├── Màu chính     #D85A30  (Amber / btn-primary)
├── Màu phụ       #185FA5  (Blue  / sidebar / btn-secondary)
├── Nền ứng dụng  #F1EFE8  (Warm cream)
├── Card          #FFFFFF  (White)
└── Text          #2C2C2A  (Near-black)
```

| Màn hình | Đặc điểm giao diện |
|---|---|
| 🔐 **Đăng nhập** | Glassmorphism card trên nền hình ảnh tiệm bánh |
| 🏠 **Menu chính** | AppShell với sidebar navigation + header động |
| 🛒 **Bán hàng** | Product grid + giỏ hàng real-time + dialog thanh toán |
| 🧁 **Bếp (KDS)** | Card-based order board, cập nhật trạng thái trực tiếp |
| 📊 **Báo cáo** | Biểu đồ JasperReports, xuất PDF/Excel một click |

---

### 🏗 Kiến trúc hệ thống

```
┌─────────────────────────────────────────────────────────────┐
│                   Kiến trúc MVP phân tầng                   │
├────────────────┬────────────────────┬───────────────────────┤
│   View Layer   │  Presenter Layer   │    Model Layer        │
│  (JavaFX /     │  (Điều phối       │  DTO  · DAO ·         │
│   FXML)        │   nghiệp vụ)      │  Service · Oracle DB  │
├────────────────┴────────────────────┴───────────────────────┤
│  🔐 Bảo mật: JWT session + jBCrypt mã hóa mật khẩu          │
│  🔄 Đồng bộ: Oracle FOR UPDATE + setAutoCommit(false)        │
│  📡 Luồng: JavaFX Task/Service cho tác vụ nặng               │
└─────────────────────────────────────────────────────────────┘
```

---

### ⚙️ Yêu cầu hệ thống

| Công cụ | Phiên bản | Tải về |
|---|---|---|
| JDK | 21+ | [Adoptium Temurin](https://adoptium.net/) |
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

```bash
# Dùng SQL*Plus
sqlplus USERNAME/PASSWORD@localhost:1521/XEPDB1 @database/install.sql

# Dùng SQLcl (khuyến nghị)
sql USERNAME/PASSWORD@localhost:1521/XEPDB1 @database/install.sql
```

> Script tự động cài đặt toàn bộ **63 file SQL** theo đúng thứ tự:  
> Bảng → Ràng buộc → Hàm → Trigger → Procedure → View → Dữ liệu mẫu

Hoặc dùng **Oracle SQL Developer**: mở `database/install.sql` → nhấn **F5**.

#### Bước 4 — Build và chạy ứng dụng

```bash
# Build
mvn clean package -DskipTests

# Chạy ứng dụng
mvn javafx:run
```

> 💡 Nếu gặp lỗi encoding trên Windows:  
> `mvn javafx:run -Dfile.encoding=UTF-8`

---

### 👤 Tài khoản đăng nhập mẫu

> 📋 Thông tin đầy đủ được tạo bởi `database/config/script_insert_data.sql`

| Vai trò | Tài khoản | Mật khẩu |
|---|---|---|
| 🧑‍💼 **Quản lý** | `quanly01` | *(xem file dữ liệu mẫu)* |
| 💵 **Thu ngân** | `thungan01` | *(xem file dữ liệu mẫu)* |
| 📦 **Thủ kho** | `thukho01` | *(xem file dữ liệu mẫu)* |
| 👨‍🍳 **Thợ bếp** | `thobep01` | *(xem file dữ liệu mẫu)* |

---

### 🗂 Cấu trúc thư mục Database

```
database/
├── install.sql              ← CHẠY FILE NÀY để cài đặt toàn bộ
├── 01_tables/               Tạo bảng dữ liệu
├── 02_constraints/          Ràng buộc & Package lỗi
├── 03_functions/            Hàm nghiệp vụ
├── 04_triggers/             Trigger tự động
├── 05_procedures/           Stored Procedures
│   └── cud/                 Thao tác Create/Update/Delete
├── 06_views/                View tổng hợp
└── config/
    └── script_insert_data.sql   Dữ liệu mẫu demo
```

### 🛠 Công nghệ sử dụng

<div align="center">

| Thành phần | Công nghệ | Badge |
|---|---|---|
| Ngôn ngữ | Java 21 | ![Java](https://img.shields.io/badge/Java-21-FF6B35?style=flat-square&logo=openjdk) |
| UI Framework | JavaFX 21.0.7 + FXML | ![JavaFX](https://img.shields.io/badge/JavaFX-21.0.7-4A90E2?style=flat-square) |
| Database | Oracle Database 19c+ | ![Oracle](https://img.shields.io/badge/Oracle-19c%2B-C74634?style=flat-square&logo=oracle) |
| Build Tool | Apache Maven 3.x | ![Maven](https://img.shields.io/badge/Maven-3.x-AA1B32?style=flat-square&logo=apachemaven) |
| JDBC Driver | ojdbc8 21.9 | ![JDBC](https://img.shields.io/badge/ojdbc8-21.9-blue?style=flat-square) |
| Báo cáo | JasperReports 6.21 | ![Jasper](https://img.shields.io/badge/JasperReports-6.21-green?style=flat-square) |
| Excel Export | Apache POI 5.3 | ![POI](https://img.shields.io/badge/Apache_POI-5.3-orange?style=flat-square) |
| Bảo mật | jBCrypt + JWT | ![Security](https://img.shields.io/badge/Security-jBCrypt%2BJWT-purple?style=flat-square) |

</div>

---

<div align="center">

### 🏆 H3K Team

```
    ██╗  ██╗██████╗ ██╗  ██╗
    ██║  ██║╚════██╗██║ ██╔╝
    ███████║ █████╔╝█████╔╝ 
    ██╔══██║ ╚═══██╗██╔═██╗ 
    ██║  ██║██████╔╝██║  ██╗
    ╚═╝  ╚═╝╚═════╝ ╚═╝  ╚═╝
```

Made with ❤️ by **H3K Team** · *IS-System-Development* · 2026

[![GitHub](https://img.shields.io/badge/GitHub-lancelotviendangkhoa710-181717?style=for-the-badge&logo=github)](https://github.com/lancelotviendangkhoa710/IS-System-Development)

</div>
