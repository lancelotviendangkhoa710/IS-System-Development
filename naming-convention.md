# Quy chuẩn đặt tên (Naming Convention) - Bakery Management System

Tài liệu này quy định các tiêu chuẩn đặt tên cho toàn bộ dự án để đảm bảo tính nhất quán, dễ đọc và chuyên nghiệp.

## 1. Nguyên tắc chung
*   Sử dụng **tiếng Việt không dấu** cho phần tên chính của mọi đối tượng (file, class, biến, hàm).
*   **Hậu tố (Suffix):** Được phép sử dụng tiếng Anh cho các hậu tố chuẩn kiến trúc để giữ tính chuyên nghiệp và dễ nhận diện tầng lớp.
*   **Không sử dụng ký tự đặc biệt:** Tránh sử dụng `_` (trừ hằng số) hoặc khoảng trắng.

## 2. Quy tắc chi tiết

### 2.1. Lớp và File (Classes & Files)
*   **Định dạng:** `PascalCase`.
*   **Cấu trúc:** `[TenChucNang][HauToChuan]`.
*   **Hậu tố được phép:**
    *   `Dialog`: Các cửa sổ thông báo/nhập liệu nhỏ.
    *   `View`: Giao diện chính (FXML hoặc Class View).
    *   `FXMLViewController`: Lớp điều khiển file FXML.
    *   `Controller`: Lớp điều phối giao diện (nếu không dùng FXML).
    *   `Service`: Tầng xử lý logic nghiệp vụ.
    *   `Presenter`: Tầng điều phối MVP.
    *   `Repository`: Tầng quản lý dữ liệu (nếu có).
    *   `Pop-up`: Các cửa sổ nổi.
    *   `DAO`: Tầng truy cập cơ sở dữ liệu.
    *   `DTO`: Đối tượng chuyển đổi dữ liệu.

**Ví dụ:**
*   ✅ Đúng: `KhachHangDialog`, `HoaDonService`, `NhanVienController`, `SanPhamDAO`, `BanHangPresenter`.
*   ❌ Sai: `CustomerDialog` (phần chính là tiếng Anh), `KhachHangDichVu` (không dùng hậu tố chuẩn), `KhachHang_Service` (dùng dấu gạch dưới).

### 2.2. Biến và Phương thức (Variables & Methods)
*   **Định dạng:** `camelCase`.
*   **Ngôn ngữ:** Tiếng Việt không dấu.
*   **Phương thức:** Bắt đầu bằng động từ.

**Ví dụ:**
*   ✅ Đúng: `tinhTienHoaDon()`, `moCaLamViec()`, `danhSachNhanVien`, `maKhachHang`.
*   ❌ Sai: `getInvoiceTotal()` (tiếng Anh), `TinhTien()` (PascalCase), `danh_sach_sp` (Snake Case).

### 2.3. Hằng số (Constants)
*   **Định dạng:** `UPPER_CASE`.
*   **Ngôn ngữ:** Tiếng Việt không dấu hoặc tiếng Anh chuyên ngành ngắn gọn.

**Ví dụ:**
*   ✅ Đúng: `MAX_SO_LUONG`, `LUONG_CO_BAN`.

### 2.4. JavaFX (UI Components)
*   **fx:id Prefix:** Bắt buộc sử dụng prefix viết thường cho các control.
    *   `btn`: Button
    *   `txt`: TextField / TextArea
    *   `lbl`: Label
    *   `tbl`: TableView
    *   `col`: TableColumn
    *   `cmb`: ComboBox
    *   `chk`: CheckBox
    *   `dp`: DatePicker

**Ví dụ:** `btnThanhToan`, `txtTenKhachHang`, `lblTongTien`.

## 3. Quy trình thực hiện Rename
1.  Sử dụng công cụ rename của IDE hoặc `gitnexus_rename` để đảm bảo cập nhật toàn bộ reference.
2.  Kiểm tra các liên kết trong file `.fxml` (fx:id và onAction) để không làm gãy UI.
3.  Cập nhật các chuỗi query SQL hoặc gọi Procedure nếu có liên quan đến tên biến/cột (nếu đổi tên cột DB).
4.  Chạy `gitnexus_detect_changes()` để kiểm tra phạm vi ảnh hưởng.
