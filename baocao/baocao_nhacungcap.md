# Màn hình Quản lý Nhà cung cấp

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `txtTimKiem` | TextField | | Nhập từ khóa (tên hoặc số điện thoại) để lọc nhanh nhà cung cấp |
| 2 | `onTaiLai` | Button | | Tải lại danh sách nhà cung cấp mới nhất từ cơ sở dữ liệu và xóa trắng ô tìm kiếm |
| 3 | `onThem` | Button | | Mở hộp thoại (Dialog) để nhập thông tin thêm mới nhà cung cấp |
| 4 | `tvNhaCungCap` | TableView | | Bảng hiển thị danh sách toàn bộ các nhà cung cấp |
| 5 | `colMaNCC` | TableColumn | | Hiển thị mã định danh duy nhất của nhà cung cấp |
| 6 | `colTenNCC` | TableColumn | | Hiển thị tên đầy đủ của nhà cung cấp |
| 7 | `colSdt` | TableColumn | | Hiển thị số điện thoại liên hệ của nhà cung cấp |
| 8 | `colDiaChi` | TableColumn | | Hiển thị địa chỉ trụ sở của nhà cung cấp |
| 9 | `colHanhDong` | TableColumn | | Cột chứa các nút chức năng tương tác trực tiếp trên từng hàng |
| 10 | `btnSua` | Button (trong cột) | | Mở hộp thoại chỉnh sửa thông tin cho nhà cung cấp tại hàng tương ứng |
| 11 | `btnXoa` | Button (trong cột) | | Thực hiện ngừng giao dịch (xóa mềm) với nhà cung cấp tại hàng tương ứng |
| 12 | `lblThongBao` | Label | | Hiển thị trạng thái hoạt động hoặc thông báo lỗi/thành công ở góc dưới màn hình |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Thiết lập các liên kết cột dữ liệu trên bảng `tvNhaCungCap`.<br>- Tự động tải danh sách nhà cung cấp thông qua luồng chạy ngầm (Thread) để tránh đơ giao diện.<br>- Lắng nghe sự kiện double-click vào dòng trên bảng để mở nhanh cửa sổ sửa. | |
| 2 | Nhập từ khóa tại ô `txtTimKiem` | - Thực hiện bắt sự kiện nhả phím (Key Released) để tự động lọc danh sách nhà cung cấp có tên hoặc số điện thoại chứa từ khóa vừa nhập thông qua luồng chạy ngầm. | |
| 3 | Chọn button `Tải lại` | - Xóa sạch từ khóa tìm kiếm trong ô `txtTimKiem` và tải lại toàn bộ danh sách nhà cung cấp từ CSDL. | |
| 4 | Chọn button `Thêm mới` | - Mở cửa sổ Modal Dialog (`NhaCungCapDialog.fxml`) ở chế độ Thêm mới.<br>- Nếu người dùng điền đầy đủ thông tin hợp lệ và bấm Lưu → Thực hiện gọi dịch vụ thêm nhà cung cấp mới vào CSDL, hiển thị thông báo thành công và reload lại danh sách. | Cửa sổ Dialog ngăn tương tác với màn hình chính cho tới khi đóng lại |
| 5 | Chọn button `Sửa` (hoặc double-click dòng) | - Mở cửa sổ Modal Dialog (`NhaCungCapDialog.fxml`) đồng thời nạp dữ liệu cũ của nhà cung cấp đã chọn lên các ô nhập liệu.<br>- Nếu người dùng cập nhật thông tin và bấm Lưu → Gửi yêu cầu cập nhật xuống CSDL qua luồng chạy ngầm, hiển thị thông báo thành công và reload lại danh sách. | |
| 6 | Chọn button `Xóa` | - Hiển thị hộp thoại cảnh báo (Confirmation Alert) xác nhận việc ngừng giao dịch với nhà cung cấp.<br>- Nếu người dùng chọn OK → Thực hiện gọi dịch vụ xóa mềm nhà cung cấp theo mã nhân viên đang đăng nhập, hiển thị thông báo thành công và tải lại danh sách. | Lịch sử nhập kho của nhà cung cấp bị xóa vẫn được giữ nguyên trong cơ sở dữ liệu |
