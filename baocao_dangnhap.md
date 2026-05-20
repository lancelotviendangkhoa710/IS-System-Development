# Màn hình Đăng nhập

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblBrandGiant` | Label | | Tên thương hiệu "H3K" |
| 2 | `lblBrandName` | Label | | Tên tiệm bánh "La Boulangerie H3K" |
| 3 | `txtTenDangNhap` | TextField | Không được để trống | Nhập tên đăng nhập |
| 4 | `txtMatKhau` | PasswordField | Không được để trống | Nhập mật khẩu |
| 5 | `btnToggleMatKhau` | Button | | Bật/ẩn hiển thị mật khẩu |
| 6 | `btnDangNhap` | Button | | Thực hiện đăng nhập |
| 7 | `lblThongBao` | Label | | Hiển thị thông báo lỗi hoặc thành công |
| 8 | *(btnQuenMatKhau)* | Button | | Chuyển sang màn hình quên mật khẩu |
| 9 | *(btnQuayLai)* | Button | | Quay lại màn hình chào mừng |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Thiết lập các control và thông tin hiển thị về trạng thái mặc định. | |
| 2 | Chọn button `btnDangNhap` | Thực hiện quy trình đăng nhập cho người dùng.<br>- Nếu tên đăng nhập hoặc mật khẩu để trống → hiển thị thông báo yêu cầu nhập đầy đủ.<br>- Nếu thông tin không đúng → hiển thị thông báo lỗi.<br>- Nếu hợp lệ → lưu phiên đăng nhập và chuyển vào màn hình chính. | |
| 3 | Chọn button `btnToggleMatKhau` | Chuyển đổi hiển thị mật khẩu giữa dạng ẩn và dạng văn bản thường. | |
| 4 | Chọn button Quên mật khẩu | Chuyển sang màn hình khôi phục mật khẩu. | |
