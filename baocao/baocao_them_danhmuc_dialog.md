# Màn hình Thêm danh mục mới

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `dialog` | TextInputDialog | | Hộp thoại nhập tên danh mục sản phẩm mới |
| 2 | `lblHeader` | Label | | Tiêu đề hướng dẫn Nhập tên danh mục sản phẩm |
| 3 | `lblContent` | Label | | Nhãn mô tả trường dữ liệu Tên danh mục |
| 4 | `txtInput` | TextField | Không được để trống | Ô nhập tên danh mục sản phẩm mới |
| 5 | `btnOK` | Button | | Xác nhận thực hiện thêm mới danh mục |
| 6 | `btnCancel` | Button | | Hủy bỏ thao tác và đóng hộp thoại |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Hiển thị hộp thoại nhập tên danh mục sản phẩm mới với ô nhập liệu trống. | |
| 2 | Chọn button `btnOK` | Kiểm tra giá trị đã nhập:<br>- Nếu tên danh mục không trống: Thực hiện thêm mới danh mục và đóng hộp thoại.<br>- Nếu tên danh mục trống: Hiển thị thông báo lỗi trên màn hình chính và đóng hộp thoại. | |
| 3 | Chọn button `btnCancel` | Hủy bỏ thao tác thêm mới và đóng hộp thoại. | |
