# Màn hình Quản lý nhân sự (Tab 1 — Quản lý nhân viên)

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabNhanVien` | Tab | | Tab chuyển đến phân hệ Quản lý nhân viên |
| 2 | `tabPhanQuyen` | Tab | | Tab chuyển đến phân hệ Nhân viên & Vai trò |
| 3 | `tabVaiTro` | Tab | | Tab chuyển đến phân hệ Quyền vai trò |
| 4 | `txtTimKiem` | TextField | | Nhập tên hoặc số điện thoại để tìm kiếm nhân viên |
| 5 | `cmbLocTrangThai` | ComboBox | | Lọc nhân viên theo trạng thái làm việc (Tất cả, Đang làm việc, Đã thôi việc) |
| 6 | *(btnLamMoi)* | Button | | Tải lại danh sách nhân viên từ CSDL |
| 7 | `btnThemMoi` | Button | | Mở hộp thoại thêm nhân viên mới |
| 8 | `tblNhanVien` | TableView | | Hiển thị danh sách nhân viên trong hệ thống |
| 9 | `colMaNV` | TableColumn | | Cột ID nhân viên |
| 10 | `colHoTen` | TableColumn | | Cột họ tên nhân viên |
| 11 | `colSdt` | TableColumn | | Cột số điện thoại |
| 12 | `colVaiTro` | TableColumn | | Cột vai trò làm việc |
| 13 | `colTenDangNhap` | TableColumn | | Cột tên đăng nhập |
| 14 | `colTrangThai` | TableColumn | | Cột trạng thái làm việc (Đang làm việc / Đã thôi việc) |
| 15 | `colHanhDong` | TableColumn | | Cột nút chức năng thao tác trên từng dòng |
| 16 | `btnSua` | Button | | Nút Sửa thông tin nhân viên trên từng dòng TableView |
| 17 | `btnThoiViec` | Button | Vô hiệu hóa đối với nhân viên đã thôi việc | Nút cho thôi việc nhân viên trên từng dòng TableView |
| 18 | `lblThongBao` | Label | | Hiển thị tổng số lượng nhân viên đã tải hoặc thông báo lỗi |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải danh sách nhân viên từ CSDL và sắp xếp theo ID tăng dần để hiển thị lên bảng `tblNhanVien`. Cập nhật số lượng nhân viên vào nhãn trạng thái `lblThongBao`. Kích hoạt bộ lọc tự động và cơ chế làm mới tự động sau mỗi 30 giây. | |
| 2 | Nhập từ khóa tại ô `txtTimKiem` hoặc chọn lọc tại `cmbLocTrangThai` | Thực hiện lọc dữ liệu trên bảng hiển thị.<br>- Chỉ hiển thị các nhân viên có Họ tên, Số điện thoại hoặc Tên đăng nhập chứa từ khóa tìm kiếm (không phân biệt hoa thường).<br>- Lọc theo trạng thái làm việc tương ứng với mục được chọn (Tất cả, Đang làm việc, Đã thôi việc). | Việc lọc diễn ra tức thời trên giao diện |
| 3 | Chọn button `Làm mới` | Gọi tải lại danh sách nhân viên mới nhất từ CSDL. | |
| 4 | Chọn button `btnThemMoi` | Mở hộp thoại `ThemNhanVienDialog.fxml` ở chế độ Thêm mới nhân viên. | Hộp thoại dạng Modal chặn tương tác màn hình chính |
| 5 | Chọn button `btnSua` (hoặc Double-click dòng trên TableView) | Mở hộp thoại `ThemNhanVienDialog.fxml` và truyền thông tin nhân viên đã chọn để chỉnh sửa. | |
| 6 | Chọn button `btnThoiViec` | Hiển thị hộp thoại cảnh báo xác nhận cho thôi việc.<br>- Nếu chọn Đồng ý → Gọi dịch vụ `nhanVienService.thoiViec(maNV)` để cập nhật trạng thái làm việc của nhân viên về 0 (Đã thôi việc) trong CSDL, khóa tài khoản và tải lại danh sách.<br>- Nếu chọn Hủy → Đóng hộp thoại và không thực hiện gì. | Cập nhật được thực hiện ngầm để tránh đơ ứng dụng |
