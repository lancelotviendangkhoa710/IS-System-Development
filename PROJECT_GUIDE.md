# H3K Bakery Management System - Project Guide

## 1. Tiến độ dự án (Use Cases Status)

Tổng số Use Case định nghĩa: **36**. Hiện tại đã triển khai xong phần khung và logic cốt lõi cho **22/36** Use Case.

### ✅ Các Use Case đã hoàn thành (Implemented)
| ID | Tên Use Case | Ghi chú |
|:---|:---|:---|
| UC01 | Đăng nhập | Hoàn thiện, có phân quyền. |
| UC03 | Quản lý nhân viên | CRUD nhân viên, vai trò. |
| UC04 | Tra cứu nhân viên | Tìm kiếm theo tên/SĐT. |
| UC08 | Quản lý khách hàng | Quản lý thông tin khách hàng. |
| UC09 | Tra cứu khách hàng | Tìm kiếm khách hàng nhanh. |
| UC10 | Xem lịch sử mua hàng | Tích hợp trong View Khách hàng. |
| UC11 | Cấu hình hạng thành viên | Thiết lập mức điểm và % giảm giá. |
| UC12 | Lập hóa đơn bán lẻ | POS bán hàng trực tiếp. |
| UC13 | Lập đơn đặt bánh tùy chỉnh | Chọn cốt bánh, nhân, kích cỡ, trang trí. |
| UC14 | Cập nhật trạng thái đơn | Chuyển trạng thái: Mới đặt -> Đã cọc -> Hoàn thành. |
| UC15 | Tra cứu danh sách đơn | Bộ lọc theo ngày, trạng thái, mã đơn. |
| UC17 | Đối soát tiền cuối ca | Kiểm tiền mặt thực tế vs Hệ thống. |
| UC18 | Quản lý danh mục SP | CRUD loại sản phẩm (Cake, Bread...). |
| UC19 | Quản lý sản phẩm | CRUD thông tin sản phẩm, giá bán. |
| UC20 | Tra cứu sản phẩm | Tìm kiếm nhanh trên POS và Quản lý. |
| UC21 | Quản lý công thức | Định lượng nguyên liệu cho từng loại bánh. |
| UC22 | Tra cứu công thức | Xem thành phần cấu tạo bánh. |
| UC25 | Quản lý nguyên liệu | Quản lý danh mục vật tư trong kho. |
| UC32 | Quản lý nhà cung cấp | Thông tin đối tác nhập hàng. |
| UC33 | Tra cứu nhà cung cấp | Tìm kiếm đối tác cung ứng. |
| UC34 | Báo cáo lợi nhuận | Thống kê theo thời gian. |
| UC35 | Báo cáo doanh thu | Biểu đồ doanh thu hàng ngày/tháng. |

### ⏳ Các Use Case đang chờ hoặc chưa triển khai (Pending/Backlog)
- **Hệ thống:** UC02 (Đổi mật khẩu), UC05 (Phân quyền chi tiết), UC06 (Nhật ký hoạt động), UC07 (Khôi phục dữ liệu).
- **Bán hàng:** UC16 (Hủy đơn hoàn cọc - đang làm dở).
- **Sản xuất:** UC23 (Dự báo số lượng bánh), UC24 (Giới hạn nhận đơn).
- **Kho:** UC26-UC28 (Lập phiếu Nhập/Xuất/Hủy), UC30-UC31 (Thẻ kho, Truy vết), UC36 (Báo cáo tồn kho).

---

## 2. Hướng dẫn chạy Project (Team Setup Guide)

### Yêu cầu hệ thống
- **Database:** Oracle Database (19c hoặc 21c).
- **Build Tool:** Maven.

### Bước 1: Thiết lập Database
1. Mở công cụ quản lý Oracle (SQL Developer hoặc SQL*Plus).
2. Tạo User và cấp quyền (Ví dụ: `BAKERY_MANAGER`).
3. Truy cập thư mục `/database/`.
4. Chạy file **`run_all.sql`** để khởi tạo toàn bộ cấu trúc và dữ liệu mẫu.
   - Lệnh: `@run_all.sql`
   - *Lưu ý:* File này sẽ tự động gọi các script con trong các thư mục 01..06 và config.

### Bước 2: Cấu hình Project
1. Copy file cấu hình (nếu chưa có): `src/main/resources/application.properties`.
2. Chỉnh sửa thông tin kết nối cho khớp với local:
   ```properties
   db.url=jdbc:oracle:thin:@localhost:1521:orcl
   db.user=BAKERY_MANAGER - mỗi người mỗi khác
   db.password=Admin123
   ```

### Bước 3: Chạy ứng dụng
1. Mở Project trong IntelliJ IDEA hoặc Antigravity.
2. Đảm bảo đã add JavaFX Library vào Project Structure.
3.Drop database cũ và Chạy file run_all.sql trong thư mục /database/ để tạo database.
4. Chạy class App.java


