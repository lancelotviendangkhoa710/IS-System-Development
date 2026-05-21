# Màn hình Kiểm kê kho — Quản lý nguyên liệu

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `btnLamMoi` | Button | | Tải lại danh sách nguyên liệu từ cơ sở dữ liệu |
| 2 | `btnThemMoi` | Button | Chỉ hiển thị với Quản lý, Admin, Thủ kho | Mở hộp thoại thêm nguyên liệu mới kèm nhập kho lần đầu |
| 3 | `txtTimKiem` | TextField | | Ô tìm kiếm lọc danh sách nguyên liệu theo tên |
| 4 | `tblNguyenLieu` | TableView | | Bảng danh sách tất cả nguyên liệu trong hệ thống |
| 5 | `colSoLuongTon` | TableColumn | | Cột số lượng tồn của nguyên liệu (kèm đơn vị tính) |
| 6 | `colTenNL` | TableColumn | | Cột tên nguyên liệu |
| 7 | `colXuatXu` | TableColumn | | Cột xuất xứ của nguyên liệu |
| 8 | `colDVT` | TableColumn | | Cột đơn vị tính của nguyên liệu |
| 9 | `colMucTon` | TableColumn | | Cột mức tồn an toàn của nguyên liệu (kèm đơn vị tính) |
| 10 | `vboxChiTiet` | VBox | Chỉ hiển thị khi đã chọn dòng trên bảng | Panel chi tiết bên phải để sửa thông tin nguyên liệu đang chọn |
| 11 | `txtTenNL` | TextField | Không được để trống | Ô nhập tên nguyên liệu cần sửa |
| 12 | `txtXuatXu` | TextField | | Ô nhập xuất xứ của nguyên liệu |
| 13 | `cmbDonViTinh` | ComboBox | Bắt buộc chọn | Ô chọn đơn vị tính của nguyên liệu |
| 14 | `txtMucTonAnToan` | TextField | Chỉ Quản lý và Admin mới được sửa | Ô nhập mức tồn kho an toàn của nguyên liệu |
| 15 | `btnLuuThayDoi` | Button | Chỉ hiển thị với Quản lý, Admin, Thủ kho | Lưu thông tin đã chỉnh sửa của nguyên liệu đang chọn |
| 16 | `btnXoa` | Button | Chỉ hiển thị với Quản lý, Admin, Thủ kho | Xóa nguyên liệu đang chọn khỏi hệ thống |
| 17 | `lblThongBao` | Label | | Hiển thị thông báo trạng thái thành công hoặc thông báo lỗi ở cuối màn hình |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải danh sách nguyên liệu lên `tblNguyenLieu`. Ẩn `vboxChiTiet`. Áp dụng phân quyền: ẩn các nút thêm, sửa, xóa nếu vai trò không có quyền CUD. Bắt đầu tự động làm mới dữ liệu mỗi 10 giây. | |
| 2 | Nhập text vào `txtTimKiem` | Lọc danh sách nguyên liệu trên `tblNguyenLieu` theo từ khóa đã nhập. | |
| 3 | Chọn dòng trên `tblNguyenLieu` | Hiển thị `vboxChiTiet` và điền thông tin của nguyên liệu đang chọn vào các ô `txtTenNL`, `txtXuatXu`, `cmbDonViTinh`, `txtMucTonAnToan`. | |
| 4 | Chọn button `btnThemMoi` | Mở hộp thoại Thêm nguyên liệu mới để nhập thông tin nguyên liệu và nhập kho lần đầu. | |
| 5 | Chọn button `btnLuuThayDoi` | Kiểm tra và lưu thông tin đã chỉnh sửa của nguyên liệu đang chọn vào cơ sở dữ liệu. Hiển thị thông báo kết quả và làm mới bảng. | |
| 6 | Chọn button `btnXoa` | Xóa nguyên liệu đang chọn khỏi hệ thống và làm mới bảng. Hiển thị thông báo kết quả. | |
| 7 | Chọn button `btnLamMoi` | Tải lại toàn bộ danh sách nguyên liệu từ cơ sở dữ liệu và xóa dữ liệu đang nhập trên form. | |
