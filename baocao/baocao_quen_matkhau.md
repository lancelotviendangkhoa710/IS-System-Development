# Màn hình Quên mật khẩu (Bước 1 — Nhập tên đăng nhập)

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblBrandGiant` | Label | | Tên thương hiệu "H3K" |
| 2 | `lblBrandName` | Label | | Tên tiệm bánh "La Boulangerie H3K" |
| 3 | `txtQmkTenDangNhap` | TextField | Không được để trống | Nhập tên đăng nhập để khôi phục mật khẩu |
| 4 | `btnGuiOtp` | Button | | Xác nhận gửi mã OTP qua email |
| 5 | `lblQmkThongBao1` | Label | | Hiển thị thông báo lỗi hoặc trạng thái gửi OTP |
| 6 | *(btnQuayLai)* | Button | | Quay lại giao diện trước đó |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Thiết lập các control và thông tin hiển thị về trạng thái mặc định, chạy hiệu ứng chữ tiêu đề H3K. | |
| 2 | Chọn button `btnGuiOtp` | Thực hiện gửi mã OTP đến email của tài khoản tương ứng.<br>- Nếu tên đăng nhập để trống → hiển thị thông báo lỗi.<br>- Nếu gửi thành công → chuyển sang bước nhập mã OTP.<br>- Nếu thất bại → hiển thị thông báo lỗi tương ứng. | Quá trình gửi mã chạy ngầm, không làm đơ giao diện |
| 3 | Chọn button Quay lại | Quay về màn hình chào mừng trước đó. | |
