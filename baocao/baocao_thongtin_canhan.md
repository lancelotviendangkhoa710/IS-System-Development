# Màn hình Thông tin cá nhân

> **Ghi chú:** Đây là hộp thoại (Dialog) được tạo động từ code, không có file FXML riêng.  
> Tên field dưới đây là tên biến cục bộ trong phương thức `onMoThongTinCaNhan()`.

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | *(headerText)* | Label | Không tương tác | Tiêu đề hướng dẫn "Cập nhật thông tin cá nhân. Mật khẩu mới là tùy chọn..." |
| 2 | `txtHoTen` | TextField | | Nhập họ tên người dùng |
| 3 | `txtSdt` | TextField | | Nhập số điện thoại |
| 4 | `txtEmail` | TextField | | Nhập địa chỉ email |
| 5 | `txtMatKhauMoi` | PasswordField | Tùy chọn, để trống nếu không muốn đổi | Nhập mật khẩu mới |
| 6 | `txtXacNhanMatKhau` | PasswordField | Phải trùng khớp với mật khẩu mới | Xác nhận mật khẩu mới |
| 7 | *(btnOK)* | Button | | Xác nhận lưu thông tin |
| 8 | *(btnCancel)* | Button | | Hủy và đóng hộp thoại |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Điền sẵn thông tin hiện tại của người dùng vào các ô Họ tên, Số điện thoại, Email. Các ô mật khẩu để trống. | |
| 2 | Chọn button OK | Lưu thông tin cá nhân đã chỉnh sửa.<br>- Nếu có nhập email → cập nhật cả email.<br>- Nếu có nhập mật khẩu mới → đổi mật khẩu.<br>- Nếu thành công → cập nhật tên hiển thị trên header, thông báo thành công.<br>- Nếu thất bại → hiển thị thông báo lỗi. | Cập nhật chạy ngầm, không làm đơ giao diện |
| 3 | Chọn button Cancel | Đóng hộp thoại, không lưu thay đổi. | |
