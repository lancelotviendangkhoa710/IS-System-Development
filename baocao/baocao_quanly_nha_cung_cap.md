# Báo cáo mô tả màn hình — Quản lý Nhà Cung Cấp

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabTheKho` | Tab | | Chuyển sang màn hình Thẻ kho |
| 2 | `tabNhaCungCap` | Tab | | Tab đang được chọn trong TabPane kho hàng |
| 3 | `tabKiemKeKho` | Tab | | Chuyển sang màn hình Kiểm kê kho |
| 4 | `tabNhapKho` | Tab | | Chuyển sang màn hình Nhập kho |
| 5 | `tabTruyXuatNguonGoc` | Tab | | Chuyển sang màn hình Truy xuất nguồn gốc |
| 6 | `txtTimKiem` | TextField | | Tìm kiếm nhà cung cấp theo tên hoặc số điện thoại |
| 7 | `btnTaiLai` | Button | | Tải lại danh sách nhà cung cấp từ cơ sở dữ liệu |
| 8 | `btnThemMoi` | Button | | Mở dialog thêm nhà cung cấp mới |
| 9 | `tblNhaCungCap` | TableView | | Bảng danh sách nhà cung cấp |
| 10 | `colMaNCC` | TableColumn | | Mã nhà cung cấp |
| 11 | `colTenNhaCungCap` | TableColumn | | Tên nhà cung cấp |
| 12 | `colSoDienThoai` | TableColumn | | Số điện thoại nhà cung cấp |
| 13 | `colDiaChi` | TableColumn | | Địa chỉ nhà cung cấp |
| 14 | `colHanhDong` | TableColumn | | Cột chứa các button thao tác trên từng dòng |
| 15 | `btnSua` | Button (mỗi dòng) | | Mở dialog chỉnh sửa thông tin nhà cung cấp tương ứng |
| 16 | `btnXoa` | Button (mỗi dòng) | | Xóa nhà cung cấp tương ứng khỏi hệ thống |
| 17 | `lblTrangThai` | Label | | Hiển thị trạng thái tải dữ liệu và tổng số nhà cung cấp |

---

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải toàn bộ danh sách nhà cung cấp từ cơ sở dữ liệu, hiển thị vào `tblNhaCungCap`. Cập nhật `lblTrangThai` số lượng bản ghi. | |
| 2 | Nhập văn bản vào `txtTimKiem` | Lọc danh sách `tblNhaCungCap` theo tên hoặc số điện thoại khớp với từ khóa đã nhập. | |
| 3 | Chọn button `btnTaiLai` | Tải lại toàn bộ danh sách nhà cung cấp từ cơ sở dữ liệu. Cập nhật `tblNhaCungCap` và `lblTrangThai`. | |
| 4 | Chọn button `btnThemMoi` | Mở dialog thêm nhà cung cấp mới. Sau khi xác nhận → thêm bản ghi vào cơ sở dữ liệu và làm mới `tblNhaCungCap`. | |
| 5 | Chọn button `btnSua` trên một dòng | Mở dialog chỉnh sửa với thông tin nhà cung cấp của dòng đó. Sau khi xác nhận → cập nhật bản ghi và làm mới `tblNhaCungCap`. | |
| 6 | Chọn button `btnXoa` trên một dòng | Hiển thị hộp thoại xác nhận xóa.<br>- Nếu xác nhận → xóa nhà cung cấp khỏi cơ sở dữ liệu, làm mới `tblNhaCungCap`, cập nhật `lblTrangThai`.<br>- Nếu hủy → đóng hộp thoại, không thay đổi dữ liệu. | |
