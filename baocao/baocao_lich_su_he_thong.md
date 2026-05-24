# Màn hình Lịch sử hệ thống

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `txtTimKiem` | TextField | | Nhập từ khóa tên nhân viên hoặc mô tả hành động để tìm kiếm nhật ký kiểm toán |
| 2 | `cbBoLoc` | ComboBox | | Lọc nhanh danh sách nhật ký thao tác theo từng Module chức năng cụ thể |
| 3 | Nút tải lại | Button | | Tải lại danh sách nhật ký kiểm toán mới nhất từ cơ sở dữ liệu |
| 4 | `tblAuditLog` | TableView | | Bảng hiển thị danh sách toàn bộ nhật ký thao tác kiểm toán của hệ thống |
| 5 | `colThoiGian` | TableColumn | | Hiển thị ngày và giờ cụ thể khi nhân viên thực hiện thao tác |
| 6 | `colNguoiDung` | TableColumn | | Hiển thị họ tên của nhân viên thực hiện thao tác |
| 7 | `colHanhDong` | TableColumn | | Hiển thị tên Module chức năng chịu tác động của thao tác kiểm toán |
| 8 | `colChiTiet` | TableColumn | | Hiển thị mô tả nội dung chi tiết cụ thể hành động của nhân viên |
| 9 | `colTrangThai` | TableColumn | | Hiển thị mã số định danh thực thể chịu tác động dưới dạng nhãn "ID: [mã]" hoặc "—" |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Nạp danh sách các Module chức năng vào bộ lọc ComboBox `cbBoLoc`.<br>- Gọi cơ sở dữ liệu để tải danh sách các bản ghi nhật ký kiểm toán mới nhất của hệ thống và cập nhật thông tin hiển thị lên bảng `tblAuditLog`. | |
| 2 | Nhập từ khóa tại ô `txtTimKiem` hoặc Thay đổi tiêu chí lọc `cbBoLoc` | - Thực hiện gọi cơ sở dữ liệu lọc danh sách các nhật ký thao tác khớp với từ khóa tìm kiếm và Module chức năng đã chọn, cập nhật kết quả hiển thị lên bảng `tblAuditLog`. | |
| 3 | Chọn button tải lại | - Xóa sạch từ khóa tìm kiếm trong `txtTimKiem`, làm trống lựa chọn bộ lọc `cbBoLoc`.<br>- Gọi cơ sở dữ liệu tải lại toàn bộ nhật ký kiểm toán mới nhất của hệ thống và làm mới thông tin trên bảng hiển thị `tblAuditLog`. | |
