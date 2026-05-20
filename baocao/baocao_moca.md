# Màn hình Xác nhận Mở Ca

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblHoTen` | Label | | Hiển thị họ tên nhân viên thu ngân đang đăng nhập |
| 2 | `cbMayPOS` | ComboBox | Không được để trống | Chọn máy POS làm việc hiện tại |
| 3 | `tfTienDauCa` | TextField | Định dạng số tự nhiên | Nhập số tiền mặt đầu ca có trong két để đối soát |
| 4 | `lblThongBao` | Label | | Hiển thị thông báo lỗi hoặc thông tin khi thực hiện mở ca |
| 5 | `btnBatDau` | Button | Vô hiệu hóa khi đang xử lý mở ca | Xác nhận mở ca và bắt đầu phiên làm việc |
| 6 | *(btnDangXuat)* | Button | | Đăng xuất tài khoản khỏi hệ thống |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải họ tên nhân viên đang đăng nhập và gán vào `lblHoTen`. Nạp danh sách máy POS sẵn có ("POS-01", "POS-02", "POS-03") vào ComboBox `cbMayPOS` và chọn máy đầu tiên. Kích hoạt bộ tự động định dạng tiền tệ khi gõ cho ô nhập tiền `tfTienDauCa`. | |
| 2 | Nhập tiền vào ô `tfTienDauCa` | Tự động định dạng ngăn cách hàng phần nghìn bằng dấu chấm (Ví dụ: `500000` hiển thị thành `500.000`) khi người dùng nhập số. | |
| 3 | Chọn button `btnBatDau` (Bắt đầu làm việc) | Thực hiện quy trình mở ca.<br>- Nếu máy POS để trống → hiển thị lỗi "Vui lòng chọn máy POS."<br>- Nếu tiền nhập có ký tự đặc biệt không hợp lệ → hiển thị lỗi "Số tiền không hợp lệ."<br>- Gọi dịch vụ mở ca qua `service.moCa(...)` chạy ngầm.<br>- Khi thành công → ghi nhận ca làm việc vào session hệ thống và chuyển hướng đến màn hình chính. | Quy trình mở ca được chạy ngầm trên Thread phụ |
| 4 | Chọn button Đăng xuất | Kết thúc phiên làm việc hiện tại của nhân viên, xóa thông tin session và điều hướng quay lại màn hình Đăng nhập. | |
